package wang.switchy.hin2n.compose.home

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import wang.switchy.hin2n.Hin2nApplication
import wang.switchy.hin2n.compose.BaseViewModel
import wang.switchy.hin2n.compose.Page
import wang.switchy.hin2n.compose.PageRouter
import wang.switchy.hin2n.event.ConnectingEvent
import wang.switchy.hin2n.event.ErrorEvent
import wang.switchy.hin2n.event.LogChangeEvent
import wang.switchy.hin2n.event.StartEvent
import wang.switchy.hin2n.event.StopEvent
import wang.switchy.hin2n.event.SupernodeDisconnectEvent
import wang.switchy.hin2n.receiver.ObjectBox
import wang.switchy.hin2n.storage.model.N2NSettingModel
import wang.switchy.hin2n.tool.IOUtils

class HomeViewModel : BaseViewModel<HomeViewAction>() {
    private var logTxtPath: String = ""
    val configList = mutableStateListOf<ConfigExt>()
    var viewState by mutableStateOf(HomeViewState(selectId = 0L))

    init {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        refreshList()
        viewModelScope.launch(Dispatchers.IO) {
            val n2nSp: SharedPreferences =
                Hin2nApplication.instance.getSharedPreferences("Hin2n", Context.MODE_PRIVATE)
            logTxtPath = n2nSp.getString("current_log_path", "")!!
        }
    }

    private fun refreshList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = ObjectBox.getSettingBox().all.map {
                ConfigExt(config = it)
            }
            withContext(Dispatchers.Main) {
                if (list.isNotEmpty()) {
                    viewState = viewState.copy(selectId = list.first().config.id)
                }
                configList.clear()
                configList.addAll(list)
            }
        }
    }

    private fun refreshConnectState() {

    }

    override fun dispatchAction(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.OnItemClick -> {
                viewState = viewState.copy(selectId = action.item.config.id)
                PageRouter.routerTo(Page.Add, params = "${action.item.config.id}")
            }

            HomeViewAction.RefreshList -> {
                refreshList()
            }

            HomeViewAction.RefreshState -> {
                refreshConnectState()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStartEvent(event: StartEvent?) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStopEvent(event: StopEvent?) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(event: ErrorEvent?) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConnectingEvent(event: ConnectingEvent?) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSupernodeDisconnectEvent(event: SupernodeDisconnectEvent?) {

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogChangeEvent(event: LogChangeEvent) {
        logTxtPath = event.txtPath
    }

    fun showLog() {
        IOUtils.readTxtLimit("logTxtPath", 1024 * 5)
    }

}

data class HomeViewState(val selectId: Long)
sealed class HomeViewAction {
    object RefreshList : HomeViewAction()
    object RefreshState : HomeViewAction()

    class OnItemClick(val item: ConfigExt) : HomeViewAction()

}

data class ConfigExt(val config: N2NSettingModel)