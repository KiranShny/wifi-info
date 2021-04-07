package io.github.kiranshny.wifiinfo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.ScanResult.*
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

internal const val SCAN_RESULTS = "wifi_scan_results"
internal const val NOT_AVAILABLE = "N/A"

class WiFiDetailsActivity : AppCompatActivity() {
    companion object {
        fun create(context: Context, scanResult: ScanResult): Intent {
            return Intent(context, WiFiDetailsActivity::class.java)
                .putExtra(SCAN_RESULTS, scanResult)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        extractData()
    }

    @SuppressLint("SetTextI18n")
    private fun extractData() {
        intent?.getParcelableExtra<ScanResult>(SCAN_RESULTS)?.let { scanResult ->
            supportActionBar?.title = getSSID(scanResult)
            supportActionBar?.subtitle = scanResult.BSSID
            findViewById<TextView>(R.id.ssidTv).text = spannable {
                text("SSID: ") + bold(getSSID(scanResult))
            }
            findViewById<TextView>(R.id.bssidTv).text = spannable {
                text("BSSID: ") + bold(scanResult.BSSID)
            }
            findViewById<TextView>(R.id.rssiTv).text = spannable {
                text("RSSI: ") + bold(scanResult.level.toString())
            }
            findViewById<TextView>(R.id.frequencyTv).text = spannable {
                text("Frequency: ") + bold(scanResult.frequency.toString())
            }
            findViewById<TextView>(R.id.centerFrequency0Tv).text = spannable {
                text("Canter Frequency 0: ") + bold(getCenterFrequency0(scanResult))
            }
            findViewById<TextView>(R.id.centerFrequency1Tv).text = spannable {
                text("Canter Frequency 1: ") + bold(getCenterFrequency1(scanResult))
            }
            findViewById<TextView>(R.id.capabilitiesTv).text = spannable {
                text("Capabilities: ") + bold(scanResult.capabilities)

            }
            findViewById<TextView>(R.id.channelWidthTv).text = spannable {
                text("Channel Width: ") + bold(getChannelWidth(scanResult))
            }
            findViewById<TextView>(R.id.operatorNameTv).text = spannable {
                text("Operator friendly name: ") + bold(getOperatorFriendlyName(scanResult))
            }
            findViewById<TextView>(R.id.venueNameTv).text = spannable {
                text("Venue name: ") + bold(getVenueName(scanResult))
            }
            findViewById<TextView>(R.id.wiFiStandardTv).text = spannable {
                text("WiFi Standard: ") + bold(getWiFiStandard(scanResult))
            }
            findViewById<TextView>(R.id.is802responderTv).text = spannable {
                text("Is 802.11mc Responder: ") + bold(isWiFi802Responder(scanResult))
            }
            findViewById<TextView>(R.id.isPasspointNetworkTv).text = spannable {
                text("Is Passpoint Network: ") + bold(isWiFiPassPointNetwork(scanResult))
            }
            findViewById<TextView>(R.id.timeSinceBootTv).text = spannable {
                text("Timestamp in μs (since boot): ") + bold(scanResult.timestamp.toString())
            }
        }
    }

    private fun getSSID(scanResult: ScanResult): String {
        return try {
            return if (scanResult.SSID.isNullOrEmpty()) "Hidden SSID" else scanResult.SSID
        } catch (e: RuntimeException) {
            NOT_AVAILABLE
        } catch (e: NoSuchMethodError) {
            NOT_AVAILABLE
        }
    }

    private fun getCenterFrequency1(scanResult: ScanResult): String {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                scanResult.centerFreq1.toString()
            } else {
                NOT_AVAILABLE
            }
        } catch (e: RuntimeException) {
            NOT_AVAILABLE
        } catch (e: NoSuchMethodError) {
            NOT_AVAILABLE
        }
    }

    private fun getCenterFrequency0(scanResult: ScanResult): String {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                scanResult.centerFreq0.toString()
            } else {
                NOT_AVAILABLE
            }
        } catch (e: RuntimeException) {
            NOT_AVAILABLE
        } catch (e: NoSuchMethodError) {
            NOT_AVAILABLE
        }
    }

    private fun getVenueName(scanResult: ScanResult): String {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                scanResult.venueName.toString()
            } else {
                NOT_AVAILABLE
            }
        } catch (e: RuntimeException) {
            NOT_AVAILABLE
        } catch (e: NoSuchMethodError) {
            NOT_AVAILABLE
        }
    }

    private fun isWiFiPassPointNetwork(scanResult: ScanResult): String {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                scanResult.isPasspointNetwork.toString()
            } else {
                NOT_AVAILABLE
            }
        } catch (e: RuntimeException) {
            NOT_AVAILABLE
        } catch (e: NoSuchMethodError) {
            NOT_AVAILABLE
        }
    }

    private fun isWiFi802Responder(scanResult: ScanResult): String {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                scanResult.is80211mcResponder.toString()
            } else {
                NOT_AVAILABLE
            }
        } catch (e: RuntimeException) {
            NOT_AVAILABLE
        } catch (e: NoSuchMethodError) {
            NOT_AVAILABLE
        }
    }

    private fun getWiFiStandard(scanResult: ScanResult): String {
        return try {
            when (scanResult.wifiStandard) {
                WIFI_STANDARD_UNKNOWN -> "WIFI_STANDARD_UNKNOWN"
                WIFI_STANDARD_LEGACY -> "WIFI_STANDARD_LEGACY"
                WIFI_STANDARD_11N -> "WIFI_STANDARD_11N"
                WIFI_STANDARD_11AC -> "WIFI_STANDARD_11AC"
                WIFI_STANDARD_11AX -> "WIFI_STANDARD_11AX"
                else -> "Unknown Type: " + scanResult.wifiStandard.toString()
            }
        } catch (e: RuntimeException) {
            NOT_AVAILABLE
        } catch (e: NoSuchMethodError) {
            NOT_AVAILABLE
        }
    }

    private fun getOperatorFriendlyName(scanResult: ScanResult): String {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                scanResult.operatorFriendlyName.toString()
            } else {
                NOT_AVAILABLE
            }
        } catch (e: RuntimeException) {
            NOT_AVAILABLE
        } catch (e: NoSuchMethodError) {
            NOT_AVAILABLE
        }
    }

    private fun getChannelWidth(result: ScanResult): String {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                when (result.channelWidth) {
                    CHANNEL_WIDTH_20MHZ -> "20MHZ"
                    CHANNEL_WIDTH_40MHZ -> "40MHZ"
                    CHANNEL_WIDTH_80MHZ -> "80MHZ"
                    CHANNEL_WIDTH_160MHZ -> "160MHZ"
                    CHANNEL_WIDTH_80MHZ_PLUS_MHZ -> "80MHZ PLUS MHZ"
                    else -> "Unknown Type: " + result.channelWidth.toString()
                }
            } else {
                NOT_AVAILABLE
            }
        } catch (e: RuntimeException) {
            NOT_AVAILABLE
        } catch (e: NoSuchMethodError) {
            NOT_AVAILABLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}