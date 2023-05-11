package wang.switchy.hin2n.compose.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import wang.switchy.hin2n.compose.AppBar
import wang.switchy.hin2n.compose.AppColor
import wang.switchy.hin2n.compose.Page
import wang.switchy.hin2n.compose.PageRouter
import wang.switchy.hin2n.compose.rippleClickable

@Composable
fun HomeView(viewModel: HomeViewModel = viewModel()) {
    Column(Modifier.fillMaxSize()) {
        AppBar("众鼎互联", showBack = false, rightIcon = {
            Image(
                Icons.Default.Add,
                contentDescription = "add",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 12.dp)
                    .size(36.dp)
                    .rippleClickable(radius = 18.dp) {
                        PageRouter.routerTo(Page.Add)
                    }
                    .padding(4.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        })
        if (viewModel.configList.isEmpty()) {
            Box(Modifier.fillMaxSize()) {
                Column(Modifier.align(Alignment.Center)) {
                    Image(
                        Icons.Default.TipsAndUpdates,
                        contentDescription = "add",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(120.dp)
                            .padding(4.dp),
                        colorFilter = ColorFilter.tint(AppColor.colorGreyAa)
                    )
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp),
                        text = "暂无配置，请点击右上角添加",
                        fontSize = 14.sp,
                        color = AppColor.colorGreyAa
                    )
                }
            }
        }
    }

}