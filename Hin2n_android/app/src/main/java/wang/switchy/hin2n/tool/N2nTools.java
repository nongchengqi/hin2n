package wang.switchy.hin2n.tool;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.VpnService;
import android.util.TypedValue;

import java.net.InetAddress;

import wang.switchy.hin2n.Hin2nApplication;

/**
 * Created by janiszhang on 2018/5/23.
 */

public class N2nTools {
    public static final String MetaUmengAppKey = "UMENG_APPKEY";
    public static final String MetaUmengChannel = "UMENG_CHANNEL";
    public static final String MetaBuglyAppId = "BUGLY_APPID";
    public static final String MetaShareWxAppId = "SHARE_WX_APPID";
    public static final String MetaShareWxAppSecret = "SHARE_WX_APPSECRET";
    public static final String MetaShareWbAppId = "SHARE_WB_APPID";
    public static final String MetaShareWbAppSecret = "SHARE_WB_APPSECRET";


    public static int dp2px(Context context, int dp) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static String getMetaData(Context context, String key) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString(key, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int getIpAddrPrefixLength(String netmask) {
        try {
            byte[] byteAddr = InetAddress.getByName(netmask).getAddress();
            int prefixLength = 0;
            for (int i = 0; i < byteAddr.length; i++) {
                for (int j = 0; j < 8; j++) {
                    if ((byteAddr[i] << j & 0xFF) != 0) {
                        prefixLength++;
                    } else {
                        return prefixLength;
                    }
                }
            }
            return prefixLength;
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getRoute(String ipAddr, int prefixLength) {
        byte[] arr = {(byte) 0x00, (byte) 0x80, (byte) 0xC0, (byte) 0xE0, (byte) 0xF0, (byte) 0xF8, (byte) 0xFC, (byte) 0xFE, (byte) 0xFF};

        if (prefixLength > 32 || prefixLength < 0) {
            return "";
        }
        try {
            byte[] byteAddr = InetAddress.getByName(ipAddr).getAddress();
            int idx = 0;
            while (prefixLength >= 8) {
                idx++;
                prefixLength -= 8;
            }
            if (idx < byteAddr.length) {
                byteAddr[idx++] &= arr[prefixLength];
            }
            for (; idx < byteAddr.length; idx++) {
                byteAddr[idx] = (byte) 0x00;
            }
            return InetAddress.getByAddress(byteAddr).getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean checkNetworkAvailable() {
        Context context = Hin2nApplication.getInstance();
        //获取手机所有链接管理对象（包括对Wi-Fi，net等连接的管理）
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        } else {
            //获取NetworkInfo对象
            NetworkInfo[] info = manager.getAllNetworkInfo();
            if (info != null && info.length > 0) {
                for (int i = 0; i < info.length; i++) {
                    System.out.println(i + "状态" + info[i].getState());
                    System.out.println(i + "类型" + info[i].getTypeName());

                    // 判断当前网络状态是否为连接状态
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
