package com.mobile.flyingeffects.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.manager.MediaLoader;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixing.sxvideoengine.License;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.util.Locale;

import rx.subjects.PublishSubject;

/**
 * Created by 张同举
 * on 2017/8/14.
 */

public class BaseApplication extends MultiDexApplication {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private static BaseApplication myzxApp;
    private boolean isActive = true;

    @Override
    public void onCreate() {
        super.onCreate();
        myzxApp = this;
        MultiDex.install(this); //分包支持
        Hawk.init(this).build();
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        initAlbum();
        initLicense();
    }


    /**
     * description ：註冊VE
     * date: ：2019/5/8 16:51
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void initLicense() {
        String license = "UJ03ctDfZ1ZTWzTF2uC2dmWnOeyD0dk/UhyEu+npLrWeDxFhV7aZ96cFq/vMa2mB6O48q0I/8qI4epo2acEbZyiXD1Im4oUNERrPhVtu2nPtxeIz1yRO1BKPQGh5Jult1SlspS6g9pD/6zyP3KsdfoW5wUkc19dzSZrq9kmuYULB2j/o7g6Rh71HYMIYoq4avDT8DDeO1P+GmeUz793hELjUMUBbZwUiYC+xDMNM2LOnWK1DEAzAWWwyh3/mJdcwCyc4MY2LttOa0ksn6iWPtFxrBxw97cIFLdhLkEoMibeCPCLtmfjofB3VnEvo9AWC/eV1dqiUr19dfxhboYIV4sJQTFLQ+HyzWRrbP4F3FLVqeSGVfkGxScRCEL43wNTBUN4f8LDFqi/yyhoRj4Nc/aJdqdte38RjJPLX1tt6J78=";
        License l = License.init(license);
        l.isValid();

    }


    private long onStopTime;
    private int activityAount = 0;
    ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityAount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {


        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityAount--;
            if (activityAount == 0) {
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };


    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
//            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
            return new MaterialHeader(context).setColorSchemeResources(R.color.theme_toast, R.color.blue_0b84d3);
        });
    }


    public static BaseApplication getInstance() {
        return myzxApp;
    }

    private void initAlbum() {
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .setLocale(Locale.getDefault())
                .build()
        );
    }


}


