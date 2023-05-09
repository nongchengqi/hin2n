package wang.switchy.hin2n;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;


import androidx.multidex.MultiDexApplication;

import wang.switchy.hin2n.storage.db.base.DaoMaster;
import wang.switchy.hin2n.storage.db.base.DaoSession;


/**
 * Created by janiszhang on 2018/4/19.
 */

public class Hin2nApplication extends MultiDexApplication {

    public Context AppContext;

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    static {
        System.loadLibrary("slog");
        System.loadLibrary("uip");
        System.loadLibrary("n2n_v2s");
        // n2n_v2 is part of edge_v2 due to dependency on the g_status
        System.loadLibrary("n2n_v1");
        System.loadLibrary("edge_v2s");
        System.loadLibrary("edge_v2");
        System.loadLibrary("edge_v1");
        System.loadLibrary("edge_jni");
    }

    //静态单例
    public static Hin2nApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        AppContext = this;

        setDatabase();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initNotificationChannel();
        }
    }

    public static Hin2nApplication getInstance() {
        return instance;
    }

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this, "N2N-db", null);
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void initNotificationChannel() {
        String id = getString(R.string.notification_channel_id_default);
        String name = getString(R.string.notification_channel_name_default);
        createNotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String id, CharSequence name, int importance) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel(id, name, importance));
    }
}
