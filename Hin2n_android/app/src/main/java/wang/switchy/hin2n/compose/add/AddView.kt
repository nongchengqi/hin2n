package wang.switchy.hin2n.compose.add

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import wang.switchy.hin2n.compose.AppBar
import wang.switchy.hin2n.compose.AppColor
import wang.switchy.hin2n.compose.Page
import wang.switchy.hin2n.compose.PageRouter
import wang.switchy.hin2n.compose.TitleButton
import wang.switchy.hin2n.compose.rippleClickable

@Composable
fun AddView(viewModel: AddViewModel = viewModel(), params: String? = null) {
    LaunchedEffect(Unit) {
        viewModel.initDate(params)
    }
    Column(Modifier.fillMaxSize()) {
        AppBar(if (viewModel.model.id == -1L) "新建配置" else "更新配置", showBack = true)
        Box(Modifier.fillMaxWidth()) {

            Column(
                Modifier.animateContentSize().fillMaxSize().background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    viewModel.model.name,
                    onValueChange = {
                        viewModel.dispatchAction(AddViewAction.NameChanged(it))
                    },
                    label = {
                        Text("配置名称")
                    },
                    isError = viewModel.errorState.name,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = AppColor.mainColor,
                        focusedBorderColor = AppColor.mainColor,
                        focusedLabelColor = AppColor.mainColor

                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    viewModel.model.superNode,
                    onValueChange = {
                        viewModel.dispatchAction(AddViewAction.SuperNodeChanged(it))
                    },
                    label = {
                        Text("超级节点")
                    },
                    isError = viewModel.errorState.superNode,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = AppColor.mainColor,
                        focusedBorderColor = AppColor.mainColor,
                        focusedLabelColor = AppColor.mainColor

                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )

                OutlinedTextField(
                    viewModel.model.community,
                    onValueChange = {
                        viewModel.dispatchAction(AddViewAction.CommunityChanged(it))
                    },
                    label = {
                        Text("社区名称")
                    },
                    isError = viewModel.errorState.community,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = AppColor.mainColor,
                        focusedBorderColor = AppColor.mainColor,
                        focusedLabelColor = AppColor.mainColor

                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
                OutlinedTextField(
                    viewModel.model.password,
                    onValueChange = {
                        viewModel.dispatchAction(AddViewAction.EncryptKeyChanged(it))
                    },
                    label = {
                        Text("加密密钥")
                    },
                    isError = viewModel.errorState.encryptionKey,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = AppColor.mainColor,
                        focusedBorderColor = AppColor.mainColor,
                        focusedLabelColor = AppColor.mainColor

                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
                OutlinedTextField(
                    viewModel.model.ip,
                    onValueChange = {
                        viewModel.dispatchAction(AddViewAction.IpAddressChanged(it))
                    },
                    label = {
                        Text("IP地址")
                    },
                    isError = viewModel.errorState.ip,
                    enabled = viewModel.model.ipMode == 0,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = AppColor.mainColor,
                        focusedBorderColor = AppColor.mainColor,
                        focusedLabelColor = AppColor.mainColor

                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
                Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    Checkbox(viewModel.model.ipMode == 1, onCheckedChange = {
                        viewModel.dispatchAction(AddViewAction.IPModeChanged(it))
                    }, colors = CheckboxDefaults.colors(checkedColor = AppColor.mainColor))
                    Text(
                        "从超级节点获取IP地址",
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }

                OutlinedTextField(
                    viewModel.model.netmask,
                    onValueChange = {
                        viewModel.dispatchAction(AddViewAction.SubNetMaskChanged(it))
                    },
                    label = {
                        Text("子网掩码")
                    },
                    isError = viewModel.errorState.subNetMask,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = AppColor.mainColor,
                        focusedBorderColor = AppColor.mainColor,
                        focusedLabelColor = AppColor.mainColor

                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )

                OutlinedTextField(
                    viewModel.model.devDesc,
                    onValueChange = {
                        viewModel.dispatchAction(AddViewAction.DeviceDescMaskChanged(it))
                    },
                    label = {
                        Text("设备描述符")
                    },
                    isError = false,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = AppColor.mainColor,
                        focusedBorderColor = AppColor.mainColor,
                        focusedLabelColor = AppColor.mainColor

                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )

                val showMoreSetting = remember { mutableStateOf(false) }
                Row(Modifier.align(Alignment.CenterHorizontally).rippleClickable(radius = 25.dp) {
                    showMoreSetting.value = !showMoreSetting.value
                }.padding(vertical = 24.dp)) {
                    Text(
                        "更多设置",
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center,
                        color = AppColor.mainColor
                    )
                    Image(
                        Icons.Default.ExpandLess,
                        contentDescription = "more setting",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 4.dp)
                            .size(24.dp).rotate(if (showMoreSetting.value) 180f else 0f),
                        colorFilter = ColorFilter.tint(AppColor.mainColor)
                    )

                }

                if (showMoreSetting.value) {

                    val showEncType = remember { mutableStateOf(false) }
                    val showTraceLevelType = remember { mutableStateOf(false) }
                    Row(
                        Modifier.fillMaxWidth().border(
                            width = 1.dp,
                            color = Color.Gray.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(4.dp)
                        ).padding(vertical = 12.dp)
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "加密方式",
                                fontSize = 12.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = AppColor.mainColor
                            )
                            Text(
                                viewModel.model.encryptionMode,
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth().rippleClickable(radius = 20.dp) {
                                    showEncType.value = true
                                }.padding(vertical = 6.dp),
                                textAlign = TextAlign.Center,
                            )
                            DropdownMenuView(
                                expanded = showEncType.value,
                                items = viewModel.encryptionModeList,
                                onItemClick = {
                                    viewModel.dispatchAction(AddViewAction.EncModeChanged(it))
                                    showEncType.value = false
                                }) {
                                showEncType.value = false
                            }
                        }
                        Spacer(
                            Modifier.height(20.dp).width(1.dp).background(AppColor.mainColor)
                                .align(Alignment.CenterVertically)
                        )
                        Column(
                            Modifier.weight(1f)
                        ) {
                            Text(
                                "日志级别",
                                fontSize = 12.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = AppColor.mainColor
                            )
                            Text(
                                viewModel.traceLevelList[viewModel.model.traceLevel],
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth().rippleClickable(radius = 20.dp) {
                                    showTraceLevelType.value = true
                                }.padding(vertical = 6.dp),
                                textAlign = TextAlign.Center,
                            )
                            DropdownMenuView(
                                expanded = showTraceLevelType.value,
                                items = viewModel.traceLevelList,
                                onItemClick = {
                                    viewModel.dispatchAction(AddViewAction.TraceLevelChanged(it))
                                    showTraceLevelType.value = false
                                }) {
                                showTraceLevelType.value = false
                            }
                        }

                    }

                    OutlinedTextField(
                        viewModel.model.superNodeBackup,
                        onValueChange = {
                            viewModel.dispatchAction(AddViewAction.SuperNode2Changed(it))
                        },
                        label = {
                            Text("超级节点2")
                        },
                        isError = viewModel.errorState.superNode2,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = AppColor.mainColor,
                            focusedBorderColor = AppColor.mainColor,
                            focusedLabelColor = AppColor.mainColor

                        ),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        viewModel.model.mtu,
                        onValueChange = {
                            viewModel.dispatchAction(AddViewAction.MtuChanged(it))
                        },
                        label = {
                            Text("最大传输单元(MTU)")
                        },
                        isError = viewModel.errorState.mtu,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = AppColor.mainColor,
                            focusedBorderColor = AppColor.mainColor,
                            focusedLabelColor = AppColor.mainColor

                        ),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    )

                    OutlinedTextField(
                        viewModel.model.localPort,
                        onValueChange = {
                            viewModel.dispatchAction(AddViewAction.LocalPortChanged(it))
                        },
                        label = {
                            Text("本地端口")
                        },
                        isError = viewModel.errorState.localPort,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = AppColor.mainColor,
                            focusedBorderColor = AppColor.mainColor,
                            focusedLabelColor = AppColor.mainColor

                        ),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    )

                    OutlinedTextField(
                        viewModel.model.gatewayIp,
                        onValueChange = {
                            viewModel.dispatchAction(AddViewAction.GatewayIpChanged(it))
                        },
                        label = {
                            Text("网关IP")
                        },
                        isError = viewModel.errorState.gatewayIp,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = AppColor.mainColor,
                            focusedBorderColor = AppColor.mainColor,
                            focusedLabelColor = AppColor.mainColor

                        ),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    )

                    OutlinedTextField(
                        viewModel.model.dnsServer,
                        onValueChange = {
                            viewModel.dispatchAction(AddViewAction.DnsIpChanged(it))
                        },
                        label = {
                            Text("DNS服务器")
                        },
                        isError = viewModel.errorState.dns,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = AppColor.mainColor,
                            focusedBorderColor = AppColor.mainColor,
                            focusedLabelColor = AppColor.mainColor

                        ),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    )

                    OutlinedTextField(
                        viewModel.model.macAddr,
                        onValueChange = {
                            viewModel.dispatchAction(AddViewAction.MacChanged(it))
                        },
                        label = {
                            Text("MAC地址")
                        },
                        isError = viewModel.errorState.mac,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            cursorColor = AppColor.mainColor,
                            focusedBorderColor = AppColor.mainColor,
                            focusedLabelColor = AppColor.mainColor

                        ),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    )

                    Row(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                        Checkbox(viewModel.model.allowRouting, onCheckedChange = {
                            viewModel.dispatchAction(AddViewAction.AllowRoutingChanged(it))
                        }, colors = CheckboxDefaults.colors(checkedColor = AppColor.mainColor))
                        Text(
                            "启用数据包转发",
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            textAlign = TextAlign.Center,
                        )
                    }

                    Row(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                        Checkbox(viewModel.model.dropMuticast, onCheckedChange = {
                            viewModel.dispatchAction(AddViewAction.DropMuticastChanged(it))
                        }, colors = CheckboxDefaults.colors(checkedColor = AppColor.mainColor))
                        Text(
                            "允许MAC地址组播",
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Row(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                        Checkbox(viewModel.model.headerEnc, onCheckedChange = {
                            viewModel.dispatchAction(AddViewAction.HeaderEncChanged(it))
                        }, colors = CheckboxDefaults.colors(checkedColor = AppColor.mainColor))
                        Text(
                            "启用完整标头加密",
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(Modifier.height(72.dp))
                }
            }
            TitleButton(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp),
                title = "保存"
            ) {
                viewModel.dispatchAction(AddViewAction.SaveClicked)
            }
        }
    }
}

@Composable
fun DropdownMenuView(
    expanded: Boolean = true,
    items: Array<String>,
    onItemClick: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismissRequest) {
        items.forEach {
            DropdownMenuItem(onClick = {
                onItemClick(it)
            }) {
                Text(
                    it,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }

}