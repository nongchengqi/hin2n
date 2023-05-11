package wang.switchy.hin2n.compose

import wang.switchy.hin2n.Hin2nApplication

fun Int.toResString(vararg params: Any): String {
    return Hin2nApplication.instance.getString(this, *params)
}
