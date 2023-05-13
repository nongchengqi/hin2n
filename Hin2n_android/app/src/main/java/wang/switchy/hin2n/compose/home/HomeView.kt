package wang.switchy.hin2n.compose.home

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import wang.switchy.hin2n.compose.AppBar
import wang.switchy.hin2n.compose.AppColor
import wang.switchy.hin2n.compose.Page
import wang.switchy.hin2n.compose.PageRouter
import wang.switchy.hin2n.compose.SimpleDialog
import wang.switchy.hin2n.compose.add.AddViewModel
import wang.switchy.hin2n.compose.rippleClickable

@Composable
fun HomeView(viewModel: HomeViewModel = viewModel()) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (AddViewModel.isSaveSuccess) {
                    AddViewModel.isSaveSuccess = false
                    viewModel.dispatchAction(HomeViewAction.RefreshList)
                }
                viewModel.dispatchAction(HomeViewAction.RefreshState)
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
    Dialogs(viewModel)
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
                        PageRouter.routerTo(Page.Add, params = "null")
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
        } else {
            ItemListView(viewModel)
        }
    }
}

@Composable
fun Dialogs(viewModel: HomeViewModel) {
    if (viewModel.viewState.showNoNetDialog) {
        SimpleDialog("提示", "无网络连接，请检查后重试") {
            viewModel.dispatchAction(HomeViewAction.ShowNoNetDialog(false))
        }
    }
}

@Composable
fun ItemListView(viewModel: HomeViewModel) {
    LazyColumn(
        Modifier.fillMaxSize().background(AppColor.colorGreyEf),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(viewModel.configList, key = { it.config.id }) {
            ItemView(it, viewModel) {
                viewModel.dispatchAction(HomeViewAction.OnItemClick(it))
            }
        }
    }
}

@Composable
fun ItemView(item: ConfigExt, viewModel: HomeViewModel, onClick: () -> Unit) {
    val selected =
        remember { derivedStateOf { viewModel.viewState.selectId == item.config.id } }
    val connected =
        remember { derivedStateOf { viewModel.viewState.connectId == item.config.id } }

    val permissionState =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK)
                viewModel.dispatchAction(HomeViewAction.StartConnect)
        }
    Column(
        Modifier.animateContentSize().fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f).padding(vertical = 8.dp)) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = item.config.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = item.config.superNode,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = AppColor.colorGreyAa
                )
            }

            if (connected.value) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp)
                        .align(Alignment.CenterVertically),
                    text = viewModel.getConnectTips(viewModel.viewState.connectState),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = viewModel.getConnectColor(viewModel.viewState.connectState)
                )
            }
        }
        if (selected.value) {
            Row(
                Modifier.fillMaxWidth().height(56.dp)
            ) {
                IconTextButton(
                    modifier = Modifier.fillMaxHeight().padding(vertical = 8.dp)
                        .background(
                            color = if (connected.value) viewModel.getConnectColor(viewModel.viewState.connectState) else AppColor.colorGreyAa,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .clip(RoundedCornerShape(28.dp)).clickable {
                            viewModel.dispatchAction(HomeViewAction.UpdateConnectId(item.config.id))
                            if (viewModel.hasVpnPermission()
                            ) {
                                viewModel.dispatchAction(HomeViewAction.StartConnect)
                            } else {
                                permissionState.launch(viewModel.getPermissionIntent())
                            }
                        },
                    icon = Icons.Default.Link,
                    text = if (connected.value) viewModel.getConnectText(viewModel.viewState.connectState) else "连接"
                )
                Spacer(Modifier.weight(1f))

                Image(
                    Icons.Default.Delete,
                    contentDescription = "icon",
                    modifier = Modifier.align(Alignment.CenterVertically).size(40.dp).clip(
                        RoundedCornerShape(20.dp)
                    ).clickable {
                        viewModel.dispatchAction(HomeViewAction.DeleteItem(item.config.id))
                    }.padding(4.dp)
                        .background(AppColor.colorGreyEf, shape = RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    colorFilter = ColorFilter.tint(AppColor.errorColor),
                )
                Spacer(Modifier.width(20.dp))
                Image(
                    Icons.Default.Edit,
                    contentDescription = "icon",
                    modifier = Modifier.align(Alignment.CenterVertically).size(40.dp).clip(
                        RoundedCornerShape(20.dp)
                    ).clickable {
                        viewModel.dispatchAction(HomeViewAction.ToEditPage(item.config.id))
                    }.padding(4.dp)
                        .background(AppColor.colorGreyEf, shape = RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    colorFilter = ColorFilter.tint(AppColor.mainColor),
                )
            }
        }
    }
}

@Composable
fun IconTextButton(modifier: Modifier, icon: ImageVector, text: String) {
    Box(modifier) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
            Image(
                icon,
                contentDescription = "icon",
                modifier = Modifier.align(Alignment.CenterVertically).fillMaxHeight()
                    .padding(vertical = 4.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp).align(Alignment.CenterVertically),
                text = text,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
        }
    }
}