package wang.switchy.hin2n.compose.add

import android.text.TextUtils
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import wang.switchy.hin2n.Hin2nApplication
import wang.switchy.hin2n.R
import wang.switchy.hin2n.compose.BaseViewModel
import wang.switchy.hin2n.compose.PageRouter
import wang.switchy.hin2n.compose.toResString
import wang.switchy.hin2n.model.EdgeCmd
import wang.switchy.hin2n.receiver.ObjectBox
import wang.switchy.hin2n.storage.model.N2NSettingModel

class AddViewModel : BaseViewModel<AddViewAction>() {
    companion object {
        var isSaveSuccess = false
    }

    var model by mutableStateOf(N2NSettingModelKt())
    var errorState by mutableStateOf(AddErrorState())
    val encryptionModeList: Array<String> =
        Hin2nApplication.instance.resources.getStringArray(R.array.encryption_modes)
    val traceLevelList = arrayOf(
        R.string.trace_level_error.toResString(),
        R.string.trace_level_warn.toResString(),
        R.string.trace_level_normal.toResString(),
        R.string.trace_level_info.toResString(),
        R.string.trace_level_debug.toResString()
    )
    private var hasError = false

    override fun dispatchAction(action: AddViewAction) {
        if (hasError && action !is AddViewAction.SaveClicked) {
            errorState = AddErrorState()
        }
        hasError = false
        when (action) {
            is AddViewAction.NameChanged -> {
                model = model.copy(name = action.value)
            }

            is AddViewAction.SuperNodeChanged -> {
                model = model.copy(superNode = action.value)
            }

            is AddViewAction.CommunityChanged -> {
                model = model.copy(community = action.value)
            }

            is AddViewAction.DeviceDescMaskChanged -> {
                model = model.copy(devDesc = action.value)
            }

            is AddViewAction.DnsIpChanged -> {
                model = model.copy(dnsServer = action.value)
            }

            is AddViewAction.EncryptKeyChanged -> {
                model = model.copy(password = action.value)
            }

            is AddViewAction.GatewayIpChanged -> {
                model = model.copy(gatewayIp = action.value)
            }

            is AddViewAction.IpAddressChanged -> {
                model = model.copy(ip = action.value)
            }

            is AddViewAction.LocalPortChanged -> {
                model = model.copy(localPort = action.value)
            }

            is AddViewAction.MacChanged -> {
                model = model.copy(macAddr = action.value)
            }

            is AddViewAction.MtuChanged -> {
                model = model.copy(mtu = action.value)
            }

            is AddViewAction.SubNetMaskChanged -> {
                model = model.copy(netmask = action.value)
            }

            is AddViewAction.SuperNode2Changed -> {
                model = model.copy(superNodeBackup = action.value)
            }

            is AddViewAction.AllowRoutingChanged -> {
                model = model.copy(allowRouting = action.value)
            }

            is AddViewAction.DropMuticastChanged -> {
                model = model.copy(dropMuticast = action.value)
            }

            is AddViewAction.HeaderEncChanged -> {
                model = model.copy(headerEnc = action.value)
            }

            is AddViewAction.EncModeChanged -> {
                model = model.copy(encryptionMode = action.value)
            }

            is AddViewAction.TraceLevelChanged -> {
                model = model.copy(traceLevel = traceLevelList.indexOf(action.value))
            }

            is AddViewAction.IPModeChanged -> {
                model = model.copy(ipMode = if (action.value) 1 else 0)
            }

            AddViewAction.SaveClicked -> {
                val check = checkValues()
                if (!check) {
                    Toast.makeText(Hin2nApplication.instance, errorState.msg, Toast.LENGTH_LONG)
                        .show()
                    hasError = true
                } else {
                    ObjectBox.getSettingBox().put(parseKtToN2NSettingModel(model))
                    isSaveSuccess = true
                    Toast.makeText(Hin2nApplication.instance, "保存成功", Toast.LENGTH_LONG)
                        .show()
                    PageRouter.back()
                }
            }
        }

    }

    fun initDate(params: String?) {
        val id = params?.toLongOrNull()
        if (id != null) {
            val storeData = ObjectBox.getSettingBox().get(id)
            if (storeData != null) {
                model = parseN2NSettingModelToKt(storeData)
            }
        }
    }

    private fun checkValues(): Boolean {
        /**
         * 基础配置判空
         *
         * 判空的状态恢复有问题，后续有时间再改吧
         */
        if (model.name.isEmpty()) {
            errorState = errorState.copy(name = true, msg = "配置名称不能为空")
            return false

        }
        if (model.community.isEmpty()) {
            errorState = errorState.copy(community = true, msg = "社区名称不能为空")
            return false

        }
        if (model.superNode.isEmpty()) {
            errorState = errorState.copy(superNode = true, msg = "超级节点不能为空")
            return false

        }

        if (model.encryptionMode.isEmpty()) {
            errorState = errorState.copy(encryptionKey = true, msg = "加密密钥不能为空")
            return false

        }
        if (!EdgeCmd.checkSupernode(model.superNode)) {
            errorState = errorState.copy(superNode = true, msg = "超级节点格式错误")
            return false
        }

        if (!EdgeCmd.checkCommunity(model.community)) {
            errorState = errorState.copy(community = true, msg = "社区名称格式错误")
            return false
        }

        if (!EdgeCmd.checkEncKey(model.encryptionMode)) {
            errorState = errorState.copy(encryptionKey = true, msg = "加密密钥格式错误")
            return false
        }
        if (model.ipMode == 0) {
            if (model.ip.isEmpty()) {
                errorState = errorState.copy(ip = true, msg = "IP地址不能为空")
                return false
            }
            if (!EdgeCmd.checkIPV4(model.ip)) {
                errorState = errorState.copy(ip = true, msg = "IP地址格式错误")
                return false
            }
        }

        // netmask => v1, v2, v2s
        if (!EdgeCmd.checkIPV4Mask(if (TextUtils.isEmpty(model.netmask)) "255.255.255.0" else model.netmask)
        ) {
            errorState = errorState.copy(subNetMask = true, msg = "子网掩码格式错误")
            return false
        }

        if (model.gatewayIp.isNotEmpty() &&
            !EdgeCmd.checkIPV4(model.gatewayIp)
        ) {
            errorState = errorState.copy(gatewayIp = true, msg = "网关IP格式错误")
            return false
        }

        if (model.dnsServer.isNotEmpty() &&
            !EdgeCmd.checkIPV4(model.dnsServer)
        ) {
            errorState = errorState.copy(dns = true, msg = "DNS服务器格式错误")
            return false
        }
        /**
         * 高级配置参数检查
         */
        val ver: Int = model.version
        // backup supernode => v2, v2s
        if ((ver == 1 || ver == 2) && model.superNodeBackup.isNotEmpty() && !EdgeCmd.checkSupernode(
                model.superNodeBackup
            )
        ) {
            // return false
        }
        // mtu => v1, v2, v2s
        if (model.mtu.isNotEmpty() && !EdgeCmd.checkMtu(model.mtu.toInt())
        ) {
            errorState = errorState.copy(mtu = true, msg = "最大传输单元值错误")
            return false
        }

        // holePunchInterval => v2s
        if (ver == 2 && model.holePunchInterval.isNotEmpty() && !EdgeCmd.checkInt(
                model.holePunchInterval.toInt(), 10, 120
            )
        ) {
            // return false
        }
        // localIP => v2s
        if (ver == 2) {

        }

        // localPort => v1, v2, v2s
        if (!TextUtils.isEmpty(model.localPort) && !EdgeCmd.checkInt(
                model.localPort.toInt(), 0, 65535
            )
        ) {
            errorState = errorState.copy(localPort = true, msg = "本地端口号错误")
            return false
        }
        // macAddr => v1, v2, v2s
        if (!TextUtils.isEmpty(model.macAddr) && !EdgeCmd.checkMacAddr(model.macAddr)
        ) {
            errorState = errorState.copy(mac = true, msg = "MAC地址格式错误")
            return false
        }
        return true
    }

}

fun parseN2NSettingModelToKt(data: N2NSettingModel): N2NSettingModelKt {
    return N2NSettingModelKt(
        id = data.id,
        version = data.version,
        name = data.name,
        ipMode = data.ipMode,
        ip = data.ip,
        netmask = data.netmask,
        community = data.community,
        password = data.password,
        devDesc = data.devDesc,
        superNode = data.superNode,
        moreSettings = data.moreSettings,
        superNodeBackup = data.superNodeBackup,
        macAddr = data.macAddr,
        mtu = data.mtu.toString(),
        localIP = data.localIP,
        holePunchInterval = data.holePunchInterval.toString(),
        resoveSupernodeIP = data.resoveSupernodeIP,
        localPort = data.localPort.toString(),
        allowRouting = data.allowRouting,
        dropMuticast = data.dropMuticast,
        useHttpTunnel = data.useHttpTunnel,
        traceLevel = data.traceLevel,
        isSelcected = data.isSelcected,
        gatewayIp = data.gatewayIp,
        dnsServer = data.dnsServer,
        encryptionMode = data.encryptionMode,
        headerEnc = data.headerEnc
    )
}

fun parseKtToN2NSettingModel(data: N2NSettingModelKt): N2NSettingModel {
    return N2NSettingModel(
        if (data.id == -1L) null else data.id,
        data.version,
        data.name,
        data.ipMode,
        data.ip,
        data.netmask,
        data.community,
        data.password,
        data.devDesc,
        data.superNode,
        data.moreSettings,
        data.superNodeBackup,
        data.macAddr,
        data.mtu.toIntOrNull() ?: 1386,
        data.localIP,
        data.holePunchInterval.toIntOrNull() ?: 0,
        data.resoveSupernodeIP,
        data.localPort.toIntOrNull() ?: 0,
        data.allowRouting,
        data.dropMuticast,
        data.useHttpTunnel,
        data.traceLevel,
        data.isSelcected,
        data.gatewayIp,
        data.dnsServer,
        data.encryptionMode,
        data.headerEnc
    )
}

data class AddErrorState(
    val name: Boolean = false,
    val superNode: Boolean = false,
    val community: Boolean = false,
    val encryptionKey: Boolean = false,
    val ip: Boolean = false,
    val subNetMask: Boolean = false,
    val devDesc: Boolean = false,
    val superNode2: Boolean = false,
    val localPort: Boolean = false,
    val mtu: Boolean = false,
    val gatewayIp: Boolean = false,
    val dns: Boolean = false,
    val mac: Boolean = false,
    val msg: String = ""
)

data class N2NSettingModelKt(
    val id: Long = -1,
    val version: Int = 3,
    val name: String = "",
    val ipMode: Int = 0,
    val ip: String = "",
    val netmask: String = R.string.item_default_netmask.toResString(),
    val community: String = "",
    val password: String = "",
    val devDesc: String = "",
    val superNode: String = R.string.item_default_supernode_v3.toResString(),
    val moreSettings: Boolean = false,
    val superNodeBackup: String = "",
    val macAddr: String = EdgeCmd.getRandomMac(),
    val mtu: String = R.string.item_default_mtu.toResString(), //int
    val localIP: String = "",
    val holePunchInterval: String = R.string.item_default_holepunchinterval.toResString(), //int
    val resoveSupernodeIP: Boolean = false,
    val localPort: String = R.string.item_default_localport.toResString(), //int
    val allowRouting: Boolean = false,
    val dropMuticast: Boolean = true,
    val useHttpTunnel: Boolean = false,
    val traceLevel: Int = 3,
    val isSelcected: Boolean = false,
    val gatewayIp: String = "",
    val dnsServer: String = "",
    val encryptionMode: String = "AES-CBC",
    val headerEnc: Boolean = false
)

sealed class AddViewAction {
    class NameChanged(val value: String) : AddViewAction()
    class SuperNodeChanged(val value: String) : AddViewAction()
    class CommunityChanged(val value: String) : AddViewAction()
    class EncryptKeyChanged(val value: String) : AddViewAction()
    class IpAddressChanged(val value: String) : AddViewAction()
    class SubNetMaskChanged(val value: String) : AddViewAction()
    class DeviceDescMaskChanged(val value: String) : AddViewAction()
    class SuperNode2Changed(val value: String) : AddViewAction()
    class MtuChanged(val value: String) : AddViewAction()
    class LocalPortChanged(val value: String) : AddViewAction()
    class GatewayIpChanged(val value: String) : AddViewAction()
    class DnsIpChanged(val value: String) : AddViewAction()
    class MacChanged(val value: String) : AddViewAction()
    class AllowRoutingChanged(val value: Boolean) : AddViewAction()
    class DropMuticastChanged(val value: Boolean) : AddViewAction()
    class HeaderEncChanged(val value: Boolean) : AddViewAction()
    class EncModeChanged(val value: String) : AddViewAction()
    class TraceLevelChanged(val value: String) : AddViewAction()
    class IPModeChanged(val value: Boolean) : AddViewAction()
    object SaveClicked : AddViewAction()
}