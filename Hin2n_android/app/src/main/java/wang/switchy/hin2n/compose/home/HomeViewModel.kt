package wang.switchy.hin2n.compose.home

import androidx.compose.runtime.mutableStateListOf
import wang.switchy.hin2n.compose.BaseViewModel
import wang.switchy.hin2n.model.N2NSettingInfo

class HomeViewModel : BaseViewModel<HomeViewAction>() {
    val configList = mutableStateListOf<ConfigExt>()
    override fun dispatchAction(action: HomeViewAction) {
    }

}

sealed class HomeViewAction {

}

data class ConfigExt(val config: N2NSettingInfo)