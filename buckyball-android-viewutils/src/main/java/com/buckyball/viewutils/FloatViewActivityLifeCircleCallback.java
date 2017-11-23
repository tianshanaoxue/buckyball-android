package com.buckyball.viewutils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.List;

/**
 * @author
 * @Desc
 * @date 17/11/22 16:02
 */
public class FloatViewActivityLifeCircleCallback implements Application.ActivityLifecycleCallbacks {
    private List<String> exludeActivityName;

    public FloatViewActivityLifeCircleCallback(List<String> exludeActivityName) {
        this.exludeActivityName = exludeActivityName;
    }

    public FloatViewActivityLifeCircleCallback() {
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity != null) {
            if (exludeActivityName != null && !exludeActivityName.isEmpty() && exludeActivityName.contains(activity.getClass().getName())) {
                return;
            }
            FloatViewUtils.showFloatView(activity, -1, null);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (exludeActivityName != null && !exludeActivityName.isEmpty() && exludeActivityName.contains(activity.getClass().getName())) {
            return;
        }
        FloatViewUtils.hiddenFloatView(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        FloatViewUtils.destroyFloatView(activity);
    }
}
