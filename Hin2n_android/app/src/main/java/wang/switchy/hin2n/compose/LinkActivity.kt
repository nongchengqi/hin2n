package wang.switchy.hin2n.compose

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import wang.switchy.hin2n.Hin2nApplication
import wang.switchy.hin2n.compose.home.HomeViewModel
import wang.switchy.hin2n.model.EdgeStatus
import wang.switchy.hin2n.model.N2NSettingInfo
import wang.switchy.hin2n.receiver.ObjectBox
import wang.switchy.hin2n.service.N2NService
import wang.switchy.hin2n.service.VpnReceiverService
import wang.switchy.hin2n.storage.model.N2NSettingModel
import wang.switchy.hin2n.storage.model.N2NSettingModel_

class LinkActivity : AppCompatActivity() {
    private var selectConnectId: Long = -1L
    private var mCurrentSettingInfo: N2NSettingModel? = null
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                doLink()
            }
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(Intent(this, VpnReceiverService::class.java))
        val config = intent.getStringExtra("config")
        if (config.isNullOrEmpty()) {
            finish()
        } else {
            try {
                mCurrentSettingInfo = Gson().fromJson(config, N2NSettingModel::class.java)
                if (mCurrentSettingInfo?.uuid.isNullOrEmpty()) {
                    mCurrentSettingInfo?.let { selectConnectId = ObjectBox.getSettingBox().put(it) }
                } else {
                    val query = ObjectBox.getSettingBox().query(N2NSettingModel_.uuid.equal(mCurrentSettingInfo!!.uuid)).build()
                    query.findFirst()?.let {
                        mCurrentSettingInfo!!.id = it.id
                        selectConnectId = it.id
                    }
                    mCurrentSettingInfo?.let { selectConnectId = ObjectBox.getSettingBox().put(it) }
                    query.close()
                }
                if (HomeViewModel.hasVpnPermission()) {
                    doLink()
                    finish()
                } else {
                    launcher.launch(HomeViewModel.getPermissionIntent())
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                finish()
            }
        }
    }

    private fun doLink() {
        val currentId =
            Hin2nApplication.instance.getSharedPreferences("Hin2n", Context.MODE_PRIVATE)
                .getLong("current_setting_id", -1)
        if (currentId == selectConnectId) {
            val status =
                if (N2NService.INSTANCE == null) EdgeStatus.RunningStatus.DISCONNECT else N2NService.INSTANCE.currentStatus
            if (N2NService.INSTANCE != null && status != EdgeStatus.RunningStatus.DISCONNECT && status != EdgeStatus.RunningStatus.FAILED) {
                handleStop()
            } else {
                handleConnect()
            }
        } else {
            handleStop()
            handleConnect()
        }
    }

    private fun handleConnect() {
        mCurrentSettingInfo = ObjectBox.getSettingBox().get(selectConnectId)
        if (mCurrentSettingInfo == null) {
            Toast.makeText(
                Hin2nApplication.instance,
                "连接失败：配置异常，请重试",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val mHin2nEdit = Hin2nApplication.instance.getSharedPreferences(
                "Hin2n",
                Context.MODE_PRIVATE
            ).edit()
            mHin2nEdit.putLong("current_setting_id", mCurrentSettingInfo!!.id)
            mHin2nEdit.apply()
        }
        val intent = Intent(Hin2nApplication.instance, N2NService::class.java)
        val bundle = Bundle()
        val n2NSettingInfo = N2NSettingInfo(mCurrentSettingInfo)
        bundle.putParcelable("n2nSettingInfo", n2NSettingInfo)
        intent.putExtra("Setting", bundle)
        Hin2nApplication.instance.startService(intent)
    }

    private fun handleStop() {
        if (N2NService.INSTANCE != null) {
            N2NService.INSTANCE.stop(null)
        }
    }
}