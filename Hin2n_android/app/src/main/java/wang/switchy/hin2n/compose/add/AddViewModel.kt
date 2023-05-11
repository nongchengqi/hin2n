package wang.switchy.hin2n.compose.add

import wang.switchy.hin2n.compose.BaseViewModel

class AddViewModel : BaseViewModel<AddViewAction>() {
    override fun dispatchAction(action: AddViewAction) {
    }

}

sealed class AddViewAction {

}