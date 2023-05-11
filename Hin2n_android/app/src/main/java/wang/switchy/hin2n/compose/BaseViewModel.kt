package wang.switchy.hin2n.compose

import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T> : ViewModel() {
    abstract fun dispatchAction(action: T)
}