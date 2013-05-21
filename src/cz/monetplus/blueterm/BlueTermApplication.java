package cz.monetplus.blueterm;

import android.app.Application;
import android.content.Context;

public class BlueTermApplication extends Application {

    /**
     * Application context.
     */
    private static Context context;

    public void onCreate() {
        super.onCreate();
        BlueTermApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return BlueTermApplication.context;
    }
}
