package wang.switchy.hin2n.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import wang.switchy.hin2n.compose.add.AddView
import wang.switchy.hin2n.compose.home.HomeView

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootView() {
    val ctrl = rememberAnimatedNavController()
    DisposableEffect(Unit) {
        onDispose {
            PageRouter.dispose()
        }
    }
    LaunchedEffect(Unit) {
        PageRouter.bindRouter(ctrl)
    }
    AnimatedNavHost(
        ctrl,
        modifier = Modifier.fillMaxSize(),
        startDestination = Page.Home.name
    ) {
        composable(Page.Home.name) {
            HomeView()
        }
        composable(Page.Add.name) {
            AddView()
        }
    }
}