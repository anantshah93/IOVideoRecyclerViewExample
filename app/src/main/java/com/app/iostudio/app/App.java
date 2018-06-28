package com.app.iostudio.app;

import android.app.Application;
import android.content.Context;

import com.app.iostudio.R;
import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by anantshah on 27/06/18.
 */

public class App extends Application {

    private HttpProxyCacheServer proxy;

    static String developer_key;

    @Override
    public void onCreate() {
        super.onCreate();
        developer_key = getString(R.string.developer_key);
    }

    public static String getYTApi() {
        return developer_key;
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();

        System.gc();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }


}