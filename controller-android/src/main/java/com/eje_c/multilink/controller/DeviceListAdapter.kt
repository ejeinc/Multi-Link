package com.eje_c.multilink.controller

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.eje_c.multilink.controller.db.DeviceEntity

/**
 * Adapter for [DeviceEntity].
 */
class DeviceListAdapter : RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    var list: List<DeviceEntity> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = list[position]
        holder.text.text = "${device.imei} ${device.name}"
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    class ViewHolder(container: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(container.context).inflate(android.R.layout.simple_list_item_1, container, false)) {
        val text: TextView = itemView.findViewById(android.R.id.text1)
    }
}