package wang.switchy.hin2n.compose.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.VpnService
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wang.switchy.hin2n.Hin2nApplication
import wang.switchy.hin2n.compose.AppColor
import wang.switchy.hin2n.compose.BaseViewModel
import wang.switchy.hin2n.compose.Page
import wang.switchy.hin2n.compose.PageRouter
import wang.switchy.hin2n.event.ConnectingEvent
import wang.switchy.hin2n.event.ErrorEvent
import wang.switchy.hin2n.event.LogChangeEvent
import wang.switchy.hin2n.event.StartEvent
import wang.switchy.hin2n.event.StopEvent
import wang.switchy.hin2n.event.SupernodeDisconnectEvent
import wang.switchy.hin2n.model.EdgeStatus.RunningStatus
import wang.switchy.hin2n.model.N2NSettingInfo
import wang.switchy.hin2n.receiver.ObjectBox
import wang.switchy.hin2n.receiver.VpnEventReceiver
import wang.switchy.hin2n.service.N2NService
import wang.switchy.hin2n.service.VpnReceiverService
import wang.switchy.hin2n.service.VpnReceiverService.Companion.sendBroadcastGetState
import wang.switchy.hin2n.storage.model.N2NSettingModel
import wang.switchy.hin2n.tool.IOUtils
import wang.switchy.hin2n.tool.N2nTools

class HomeViewModel : BaseViewModel<HomeViewAction>() {
    companion object {
        fun getPermissionIntent(): Intent? = VpnService.prepare(Hin2nApplication.instance)
        fun hasVpnPermission(): Boolean {
            return if (N2NService.INSTANCE == null) {
                getPermissionIntent() == null
            } else {
                true
            }
        }
    }

    private var mCurrentSettingInfo: N2NSettingModel? = null
    private var logTxtPath: String = ""
    val configList = mutableStateListOf<ConfigExt>()
    var viewState by mutableStateOf(
        HomeViewState(
            selectId = 0L,
            connectState = ConnectState.Normal,
            connectId = -0L
        )
    )
    private var selectConnectId = -1L

    init {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        registerReceiverCallback()
        refreshList()
        viewModelScope.launch(Dispatchers.IO) {
            val n2nSp: SharedPreferences =
                Hin2nApplication.instance.getSharedPreferences("Hin2n", Context.MODE_PRIVATE)
            logTxtPath = n2nSp.getString("current_log_path", "")!!
            withContext(Dispatchers.Main) {
                selectConnectId = n2nSp.getLong("current_setting_id", -1L)
                if (selectConnectId == -1L) {
                    if (configList.isNotEmpty()) {
                        viewState = viewState.copy(selectId = configList.first().config.id)
                    }
                } else {
                    viewState = viewState.copy(selectId = selectConnectId)
                }
            }
        }

    }

    private fun registerReceiverCallback() {
        VpnReceiverService.updateReceiverCallback { action, data ->
            when (action) {
                VpnEventReceiver.ACTION_GET_STATE -> {
                    val remoteData = when (viewState.connectState) {
                        ConnectState.ConnectFail -> "connect_fail"
                        ConnectState.Connected -> "connected"
                        ConnectState.NoConfig -> "disconnect"
                        ConnectState.Normal -> "disconnect"
                        ConnectState.Connecting -> "connecting"
                    }
                    VpnReceiverService.sendBroadcast(
                        action = VpnEventReceiver.ACTION_GET_STATE,
                        data = remoteData
                    )
                }

                VpnEventReceiver.ACTION_GET_CONFIG -> {
                    val remoteData =
                        if (mCurrentSettingInfo == null) "" else Gson().toJson(mCurrentSettingInfo)
                    VpnReceiverService.sendBroadcast(
                        action = VpnEventReceiver.ACTION_GET_CONFIG,
                        data = remoteData
                    )
                }
            }
        }
    }

    private fun refreshList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = ObjectBox.getSettingBox().all.map {
                ConfigExt(config = it)
            }
            withContext(Dispatchers.Main) {

                configList.clear()
                configList.addAll(list)
            }
        }
    }

    private fun refreshConnectState() {
        val n2nSp: SharedPreferences =
            Hin2nApplication.instance.getSharedPreferences("Hin2n", Context.MODE_PRIVATE)
        logTxtPath = n2nSp.getString("current_log_path", "") ?: ""
        val currentSettingId = n2nSp.getLong("current_setting_id", -1)
        if (currentSettingId != -1L) {
            mCurrentSettingInfo = ObjectBox.getSettingBox().get(currentSettingId)
            viewState = if (mCurrentSettingInfo != null) {
                //可以连接状态
                if (N2NService.INSTANCE == null) {
                    viewState.copy(
                        connectState = ConnectState.Normal,
                        connectId = mCurrentSettingInfo?.id ?: -1L
                    )
                } else {
                    when (N2NService.INSTANCE.currentStatus) {
                        RunningStatus.CONNECTED -> {
                            viewState.copy(
                                connectState = ConnectState.Connected,
                                connectId = mCurrentSettingInfo?.id ?: -1L
                            )
                        }

                        RunningStatus.SUPERNODE_DISCONNECT -> {
                            viewState.copy(
                                connectState = ConnectState.ConnectFail,
                                connectId = mCurrentSettingInfo?.id ?: -1L
                            )
                        }

                        RunningStatus.CONNECTING -> {
                            viewState.copy(
                                connectState = ConnectState.Connecting,
                                connectId = mCurrentSettingInfo?.id ?: -1L
                            )
                        }

                        else -> {
                            viewState.copy(
                                connectState = ConnectState.Normal,
                                connectId = mCurrentSettingInfo?.id ?: -1L
                            )
                        }
                    }
                }
            } else {
                viewState.copy(connectState = ConnectState.NoConfig, connectId = -1L)
            }
        } else {
            viewState = viewState.copy(connectState = ConnectState.NoConfig, connectId = -1L)
        }
    }

    override fun dispatchAction(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.OnItemClick -> {
                viewState = viewState.copy(selectId = action.item.config.id)

            }

            HomeViewAction.RefreshList -> {
                refreshList()
            }

            HomeViewAction.RefreshState -> {
                refreshConnectState()
            }

            is HomeViewAction.StartConnect -> {
                val currentId =
                    Hin2nApplication.instance.getSharedPreferences("Hin2n", Context.MODE_PRIVATE)
                        .getLong("current_setting_id", -1)
                if (currentId == selectConnectId) {
                    val status =
                        if (N2NService.INSTANCE == null) RunningStatus.DISCONNECT else N2NService.INSTANCE.currentStatus
                    if (N2NService.INSTANCE != null && status != RunningStatus.DISCONNECT && status != RunningStatus.FAILED) {
                        handleStop()
                    } else {
                        handleConnect()
                    }
                } else {
                    handleStop()
                    handleConnect()
                }

            }

            is HomeViewAction.UpdateConnectId -> {
                selectConnectId = action.id
                viewState = viewState.copy(connectId = action.id)
            }

            is HomeViewAction.DeleteItem -> {
                if (viewState.connectId == action.id && viewState.connectState == ConnectState.Connected) {
                    Toast.makeText(
                        Hin2nApplication.instance,
                        "此配置连接中，请断开连接后再删除",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    configList.find { it.config.id == action.id }?.let {
                        configList.remove(it)
                        ObjectBox.getSettingBox().remove(it.config.id)
                    }

                }
            }

            is HomeViewAction.ToEditPage -> {
                if (viewState.connectId == action.id && viewState.connectState == ConnectState.Connected) {
                    Toast.makeText(
                        Hin2nApplication.instance,
                        "此配置连接中，请断开连接后再编辑",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    PageRouter.routerTo(Page.Add, params = "${action.id}")
                }
            }

            is HomeViewAction.ShowNoNetDialog -> {
                viewState = viewState.copy(showNoNetDialog = action.show)
            }
        }
    }

    private fun handleConnect() {
        if (!N2nTools.checkNetworkAvailable()) {
            dispatchAction(HomeViewAction.ShowNoNetDialog(true))
            return
        }
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
            refreshConnectState()
        }
        val intent = Intent(Hin2nApplication.instance, N2NService::class.java)
        val bundle = Bundle()
        val n2NSettingInfo = N2NSettingInfo(mCurrentSettingInfo)
        bundle.putParcelable("n2nSettingInfo", n2NSettingInfo)
        intent.putExtra("Setting", bundle)
        Hin2nApplication.instance.startService(intent)
    }

    private fun handleStop() {
        viewState = viewState.copy(connectState = ConnectState.Normal)
        if (N2NService.INSTANCE != null) {
            N2NService.INSTANCE.stop(null)
        }
    }

    fun getConnectColor(state: ConnectState): Color {
        return when (state) {
            ConnectState.ConnectFail -> AppColor.errorColor
            ConnectState.Connected -> AppColor.mainColor
            ConnectState.NoConfig -> AppColor.colorGreyAa
            ConnectState.Normal -> AppColor.colorGreyAa
            ConnectState.Connecting -> AppColor.colorGreyAa
        }
    }

    fun getConnectText(state: ConnectState): String {
        return when (state) {
            ConnectState.ConnectFail -> "连接"
            ConnectState.Connected -> "断开"
            ConnectState.NoConfig -> "连接"
            ConnectState.Normal -> "连接"
            ConnectState.Connecting -> "连接中"
        }
    }

    fun getConnectTips(state: ConnectState): String {
        return when (state) {
            ConnectState.ConnectFail -> "连接失败"
            ConnectState.Connected -> "已连接"
            ConnectState.NoConfig -> "未连接"
            ConnectState.Normal -> "未连接"
            ConnectState.Connecting -> "连接中"
        }
    }

    override fun onCleared() {
        super.onCleared()
        VpnReceiverService.updateReceiverCallback(null)
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStartEvent(event: StartEvent?) {
        refreshConnectState()
        sendBroadcast()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStopEvent(event: StopEvent?) {
        refreshConnectState()
        sendBroadcast()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(event: ErrorEvent?) {
        refreshConnectState()
        sendBroadcast()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnectingEvent(event: ConnectingEvent?) {
        refreshConnectState()
        sendBroadcast()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSupernodeDisconnectEvent(event: SupernodeDisconnectEvent?) {
        refreshConnectState()
        sendBroadcast()
    }

    private fun  sendBroadcast(){
        //sendBroadcastGetState()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogChangeEvent(event: LogChangeEvent) {
        logTxtPath = event.txtPath
    }

    fun showLog() {
        IOUtils.readTxtLimit("logTxtPath", 1024 * 5)
    }

}

data class HomeViewState(
    val selectId: Long,
    val connectState: ConnectState,
    val connectId: Long,
    val showNoNetDialog: Boolean = false
)

sealed class ConnectState {
    object NoConfig : ConnectState()
    object Normal : ConnectState()
    object Connected : ConnectState()
    object Connecting : ConnectState()
    object ConnectFail : ConnectState()
}

sealed class HomeViewAction {
    object RefreshList : HomeViewAction()
    object RefreshState : HomeViewAction()
    class OnItemClick(val item: ConfigExt) : HomeViewAction()
    object StartConnect : HomeViewAction()
    class UpdateConnectId(val id: Long) : HomeViewAction()
    class DeleteItem(val id: Long) : HomeViewAction()
    class ToEditPage(val id: Long) : HomeViewAction()
    class ShowNoNetDialog(val show: Boolean) : HomeViewAction()

}

data class ConfigExt(val config: N2NSettingModel)