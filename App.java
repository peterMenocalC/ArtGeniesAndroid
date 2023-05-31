package com.art.genies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.art.genies.apis.RestApi;
import com.art.genies.common.ArtGeniesApi;
import com.facebook.FacebookSdk;

public class App extends MultiDexApplication {

    public static SharedPreferences pref;
    public static RestApi restApi;
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        sContext = getApplicationContext();
        /*if (BuildConfig.ENABLE_CRASH_LOG) {
            Fabric.with(this, new Crashlytics());
        }
        DeviceValidation.init(this.getApplicationContext());*/
        uncaughtExceptionHandler();
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized SharedPreferences getPreference() {
        return pref;
    }

    /**
     * Utility method to get the API service instance
     *
     * @return
     */
    public static synchronized RestApi getApi() {
        if (restApi == null) {
            restApi = ArtGeniesApi.create();
        }
        return restApi;
    }

    /**
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }

    private void uncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.d("Bhushan",throwable.getMessage());
            }
        });
    }
}
