package com.buckyball.viewutils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * @author
 * @Desc
 * @date 17/11/22 16:02
 */
public class FloatViewActivityLifeCircleCallback implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(activity!=null)
            FloatViewUtils.showFloatView(activity,-1,null);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        FloatViewUtils.hiddenFloatView();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
