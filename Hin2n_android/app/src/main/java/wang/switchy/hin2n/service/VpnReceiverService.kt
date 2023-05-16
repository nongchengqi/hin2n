package wang.switchy.hin2n.service

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import wang.switchy.hin2n.Hin2nApplication
import wang.switchy.hin2n.model.EdgeStatus
import wang.switchy.hin2n.receiver.ObjectBox
import wang.switchy.hin2n.receiver.VpnEventReceiver


class VpnReceiverService : android.app.Service() {
    companion object {
        private var receiverCallback: ((String, String?) -> Unit)? = null
        fun updateReceiverCallback(callback: ((String, String?) -> Unit)?) {
            receiverCallback = callback
        }

        fun sendBroadcast(action: String, data: String?) {
            Intent().also { intent ->
                intent.action = VpnEventReceiver.REMOTE_ACTION
                intent.putExtra("action", action)
                data?.let {
                    intent.putExtra("data", it)
                }
                Hin2nApplication.instance.sendBroadcast(intent)
            }
        }

        fun sendBroadcastGetState() {
            Intent().also { intent ->
                intent.action = VpnEventReceiver.LOCAL_ACTION
                intent.putExtra("action", VpnEventReceiver.ACTION_GET_STATE)
                Hin2nApplication.instance.sendBroadcast(intent)
            }
        }
    }

    private val vpnEventReceiver = VpnEventReceiver { action, data ->
        if (receiverCallback == null) {
            when (action) {
                VpnEventReceiver.ACTION_GET_STATE -> {
                    val remoteData = if (N2NService.INSTANCE == null) {
                        "disconnect"
                    } else {
                        when (N2NService.INSTANCE.currentStatus) {
                            EdgeStatus.RunningStatus.CONNECTING -> "connecting"
                            EdgeStatus.RunningStatus.CONNECTED -> "connected"
                            EdgeStatus.RunningStatus.SUPERNODE_DISCONNECT -> "disconnect"
                            EdgeStatus.RunningStatus.DISCONNECT -> "disconnect"
                            EdgeStatus.RunningStatus.FAILED -> "connect_fail"
                            else -> "unknown"
                        }
                    }
                    sendBroadcast(action = VpnEventReceiver.ACTION_GET_STATE, data = remoteData)
                }

                VpnEventReceiver.ACTION_GET_CONFIG -> {
                    MainScope().launch(Dispatchers.IO) {
                        val currentId =
                            Hin2nApplication.instance.getSharedPreferences(
                                "Hin2n",
                                Context.MODE_PRIVATE
                            )
                                .getLong("current_setting_id", -1)
                        val remoteData = if (currentId == -1L) {
                            ""
                        } else {
                            try {
                                Gson().toJson(ObjectBox.getSettingBox().get(currentId))
                            } catch (e: Throwable) {
                                ""
                            }
                        }
                        sendBroadcast(
                            action = VpnEventReceiver.ACTION_GET_CONFIG,
                            data = remoteData
                        )
                    }
                }
            }
        } else {
            receiverCallback?.invoke(action, data)
        }


    }
    private val filter = IntentFilter().apply {
        addAction(VpnEventReceiver.LOCAL_ACTION)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver()
        Log.e("TTAG", "registerReceiver:onDestroy")
    }

    private fun registerReceiver() {
        Log.e("TTAG", "registerReceiver:")
        try {
            registerReceiver(vpnEventReceiver, filter)
        } catch (e: Throwable) {
            e.printStackTrace()
            Log.e("TTAG", "registerReceiver:Throwable")
        }
    }

    private fun unregisterReceiver() {
        kotlin.runCatching {
            unregisterReceiver(vpnEventReceiver)
        }
    }

}