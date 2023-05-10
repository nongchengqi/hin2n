package wang.switchy.hin2n.receiver;

import android.content.Context;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import wang.switchy.hin2n.storage.model.MyObjectBox;
import wang.switchy.hin2n.storage.model.N2NSettingModel;

public class ObjectBox {
    private static BoxStore boxStore;

    public static void init(Context context) {
        boxStore = MyObjectBox.builder()
                .androidContext(context.getApplicationContext())
                .build();
    }

    public static BoxStore get() { return boxStore; }

    public static Box<N2NSettingModel> getSettingBox() { return boxStore.boxFor(N2NSettingModel.class); }
}
