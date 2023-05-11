package wang.switchy.hin2n.compose

import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import java.lang.ref.SoftReference

object PageRouter {
    private var routerRef = SoftReference<NavHostController>(null)
    private val router get() = routerRef.get()
    private var waitBindCallback: (() -> Unit)? = null
    fun bindRouter(ctrl: NavHostController) {
        routerRef = SoftReference(ctrl)
        waitBindCallback?.invoke()
    }

    fun dispose() {
        routerRef.clear()
    }

    fun routerTo(
        page: Page,
        launchSingleTop: Boolean = true,
        params: String? = null,
        replace: Boolean = false
    ) {
        if (router == null) {
            waitBindCallback = {
                safeRouteTo(page, launchSingleTop, params, replace)
                waitBindCallback = null
            }
        } else {
            safeRouteTo(page, launchSingleTop, params, replace)
        }
    }

    private fun safeRouteTo(
        page: Page,
        launchSingleTop: Boolean = true,
        params: String? = null,
        replace: Boolean = false
    ) {
        val destination = if (params == null) {
            page.name
        } else {
            "${page.name}/$params"
        }
        if (launchSingleTop) {
            val pre = router?.backQueue?.firstOrNull {
                it.destination.route?.substringBefore("/") == destination.substringBefore("/")
            }
            if (pre != null) {
                router?.popBackStack(pre.destination.id, true)
            }
        }
        if (replace) {
            val pre = router?.backQueue?.lastOrNull()
            if (pre != null) {
                router?.popBackStack(pre.destination.id, true)
            }
        }
        router?.navigate(destination, navOptions {
            this.launchSingleTop = launchSingleTop
            this.restoreState = true
        })
    }

    fun back() {
        router?.navigateUp()
    }
}


sealed class Page(val name: String) {
    object Add : Page("add")
    object Home : Page("home")
}