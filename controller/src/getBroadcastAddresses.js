const ip = require('ip');
const os = require('os');

/**
 * 現在接続しているのネットワークのブロードキャストアドレスを取得する。複数のネットワークに属している場合は結果の配列の要素が2個以上になることもある。
 * @return {String[]} ブロードキャストアドレスの配列
 */
module.exports = function getBroadcastAddresses() {
  let ifaces = os.networkInterfaces();

  let results = []

  Object.keys(ifaces).forEach(function (ifname) {

    if (/Emulator/.test(ifname)) return

    ifaces[ifname].forEach(function (iface) {
      if ('IPv4' !== iface.family || iface.internal !== false) {
        // skip over internal (i.e. 127.0.0.1) and non-ipv4 addresses
        return;
      }

      let broadcastAddress = ip.or(iface.address, ip.not(iface.netmask))
      results.push(broadcastAddress)
    });
  });

  return results
}
