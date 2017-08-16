package com.eje_c.multilink.udp

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.concurrent.Executors

/**
 * UDP Socket. It provides API to use UDP in simple way.
 */
class UdpSocket(port: Int = 0, receiveBufferCapacity: Int = 128 * 1024) {

    private val channel = DatagramChannel.open()
    private val executor = Executors.newCachedThreadPool()
    private var released: Boolean = false

    /**
     * Callback for receiving data.
     */
    var onReceive: ((ByteBuffer, SocketAddress) -> Unit)? = null

    init {
        channel.socket().reuseAddress = true
        channel.socket().broadcast = true
        channel.socket().bind(InetSocketAddress(port))
        channel.configureBlocking(false)

        // Start background receiving data thread
        executor.submit {

            val selector = Selector.open()
            channel.register(selector, SelectionKey.OP_READ)

            try {

                // Stop when release() is called
                while (!released) {

                    // Block until some data is coming from remote
                    selector.select()
                    val iterator = selector.selectedKeys().iterator()

                    while (iterator.hasNext()) {
                        val key = iterator.next()
                        iterator.remove()

                        if (!key.isValid) {
                            continue
                        }

                        if (key.isReadable) {

                            // Read packet
                            val buffer = ByteBuffer.allocate(receiveBufferCapacity)
                            val remote = channel.receive(buffer)
                            buffer.flip()

                            // Process data
                            try {
                                onReceive?.invoke(buffer, remote)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }

                    Thread.yield()
                }

            } catch (e: Exception) {
                // When channel is closed or something wrong
                e.printStackTrace()
            }
        }
    }

    /**
     * Close socket and shutdown background receiving thread.
     */
    fun release() {

        released = true
        channel.close()
        executor.shutdown()
    }

    /**
     * Send [data] to [remote]. Sub set of [data] can be sent with specifying [offset] and [length].
     */
    fun send(data: ByteArray, remote: SocketAddress, offset: Int = 0, length: Int = data.size - offset) {

        val buffer = ByteBuffer.wrap(data, offset, length)

        executor.submit {
            channel.send(buffer, remote)
        }

    }

    /**
     * Send [data] with all local network hosts. Sub set of [data] can be sent with specifying [offset] and [length].
     */
    fun broadcast(data: ByteArray, port: Int, offset: Int = 0, length: Int = data.size - offset) {

        val remote = InetSocketAddress(getBroadcastAddress(), port)

        send(data, remote, offset, length)

    }

    /**
     * Process for all elements in [Iterator] and remove them all.
     */
    fun <T> MutableIterator<T>.consumeAll(callback: (T) -> Unit) {

        while (hasNext()) {

            val element = next()
            remove()

            callback(element)
        }
    }

    companion object {

        /**
         * Get current network's broadcast address. Returns null if device is offline.
         */
        fun getBroadcastAddress(): InetAddress? {
            NetworkInterface.getNetworkInterfaces().iterator().forEach { networkInterface ->
                if (!networkInterface.isLoopback) {
                    networkInterface.interfaceAddresses.forEach { interfaceAddress ->
                        if (interfaceAddress.broadcast != null) {
                            return interfaceAddress.broadcast
                        }
                    }
                }
            }

            return null
        }

    }
}