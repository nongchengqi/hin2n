package wang.switchy.hin2n.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class VpnEventReceiver(private val onReceive: (String, String?) -> Unit) : BroadcastReceiver() {
    companion object {
        const val LOCAL_ACTION = "com.evan.n2n.ipc_local"
        const val REMOTE_ACTION = "com.evan.n2n.ipc_remote"

        const val ACTION_GET_STATE = "get_state"
        const val ACTION_GET_CONFIG = "get_config"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val action = it.getStringExtra("action")
            if (!action.isNullOrEmpty()) {
                onReceive.invoke(action, it.getStringExtra("data"))
            }
        }

    }
}