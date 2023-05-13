package wang.switchy.hin2n.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

object AppColor {
    val mainColor = Color(0xff04BE02)
    val colorGreyAa = Color(0xffaaaaaa)
    val colorGreyEf = Color(0xffefefef)
    val errorColor = Color(0xffff3333)
}


@Composable
fun AppBar(
    title: String?,
    showBack: Boolean = false,
    backClick: (() -> Unit)? = null,
    rightIcon: (@Composable RowScope.() -> Unit)? = null,
    style: TextStyle? = null,
    isDark: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = AppColor.mainColor)
            .statusBarsPadding()
            .height(56.dp)
    ) {
        Row(Modifier.fillMaxHeight()) {
            if (showBack) {
                Image(
                    Icons.Default.ArrowBack,
                    contentDescription = "back",
                    colorFilter = ColorFilter.tint(color = if (isDark) Color.Black else Color.White),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 12.dp)
                        .size(36.dp)
                        .rippleClickable(radius = 18.dp) {
                            if (backClick == null) {
                                PageRouter.back()
                            } else {
                                backClick.invoke()
                            }
                        }
                        .padding(4.dp)
                )
            } else {
                Spacer(
                    Modifier
                        .padding(horizontal = 12.dp)
                        .width(48.dp)
                )
            }
            Text(
                text = title ?: "",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f),
                style = style ?: TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    color = if (isDark) Color.Black else Color.White
                ),
                maxLines = 1
            )
            if (rightIcon != null) {
                rightIcon()
            } else {
                Spacer(
                    Modifier
                        .padding(horizontal = 12.dp)
                        .width(48.dp)
                )
            }
        }
    }
}

fun Modifier.rippleClickable(
    radius: Dp = Dp.Unspecified,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    color: Color = Color.Gray,
    onClick: () -> Unit
): Modifier = composed {
    val rippleIndication = rememberRipple(radius = radius, color = color)
    val source = remember { MutableInteractionSource() }
    clickable(source, rippleIndication, enabled, onClickLabel, role, onClick)
}

@Composable
fun TitleButton(
    modifier: Modifier = Modifier,
    title: String, onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = AppColor.mainColor),
        contentPadding = PaddingValues(
            top = 14.dp,
            bottom = 14.dp
        )
    ) {
        Text(
            title,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SimpleDialog(
    title: String,
    desc: String,
    confirmButtonTitle: String = "确定",
    cancelButtonTitle: String = "取消",
    confirmTextStyle: TextStyle = TextStyle(
        fontSize = 15.sp,
        color = Color.White,
        fontWeight = FontWeight.Medium
    ),
    cancelTextStyle: TextStyle = TextStyle(
        fontSize = 15.sp,
        color = MaterialTheme.colors.onBackground.copy(
            alpha = 0.5f
        ),
        fontWeight = FontWeight.Medium
    ),
    onClick: (Boolean) -> Unit
) {
    AlertDialog(
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 17.sp,
                    color = MaterialTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(
                text = desc,
                style = MaterialTheme.typography.body1
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        onDismissRequest = {
            onClick(false)
        },
        properties = DialogProperties(
            dismissOnClickOutside = false
        ),
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .width(110.dp)
                        .height(44.dp),
                    shape = RoundedCornerShape(corner = CornerSize(22.dp)),
                    elevation = null,
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = MaterialTheme.colors.onBackground.copy(
                            alpha = 0.08f
                        ),
                    ),
                    onClick = {
                        onClick(false)
                    }) {
                    Text(
                        text = cancelButtonTitle,
                        style = cancelTextStyle
                    )
                }
                Spacer(modifier = Modifier.size(15.dp))
                Button(
                    modifier = Modifier
                        .width(110.dp)
                        .height(44.dp),
                    shape = RoundedCornerShape(corner = CornerSize(22.dp)),
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = AppColor.mainColor,
                    ),
                    onClick = { onClick(true) }
                ) {
                    Text(
                        text = confirmButtonTitle,
                        style = confirmTextStyle
                    )
                }
            }
        },
    )
}