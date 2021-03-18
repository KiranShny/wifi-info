package io.github.kiranshny.wifiinfo

import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WiFiAdapter : RecyclerView.Adapter<WiFiAdapter.WiFiViewHolder>() {

    private val data: MutableList<ScanResult>  = mutableListOf()

    fun setData(results: List<ScanResult>) {
        data.clear()
        data.addAll(results)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WiFiViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_available_wifis, parent, false)
        return WiFiViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WiFiViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class WiFiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView= itemView.findViewById(R.id.ssidTv)
        val cardSubTitle: TextView= itemView.findViewById(R.id.bssidTv)
        val cardCounter: TextView= itemView.findViewById(R.id.rssiTv)

        fun bind(scanResult: ScanResult) {
            cardTitle.text = scanResult.SSID
            cardSubTitle.text = scanResult.BSSID
            cardCounter.text = scanResult.level.toString()
        }
    }
}