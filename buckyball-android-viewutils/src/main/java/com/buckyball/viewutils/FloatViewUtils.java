package com.buckyball.viewutils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author
 * @Desc
 * @date 17/11/22 10:47
 */
public class FloatViewUtils {
    private static final String TAG = "FloatViewUtils";
    private static long lastDownTime;
    private static Map<String, ViewGroup> floatViewMap = new HashMap<>();
    private static FloatViewActivityLifeCircleCallback activityLifeCircleCallback;
    /**
     * 注册时的点击监听
     */
    private static View.OnClickListener onClickListener;
    private static int layoutId = -1;

    public static void registerActivityLifeCircle(Application application, int layoutId, View.OnClickListener onClickListener) {
        if (application == null)
            return;
        if (activityLifeCircleCallback == null)
            activityLifeCircleCallback = new FloatViewActivityLifeCircleCallback();
        application.registerActivityLifecycleCallbacks(activityLifeCircleCallback);
        FloatViewUtils.onClickListener = onClickListener;
        FloatViewUtils.layoutId = layoutId;
    }

    public static void unRegisterActivityLifeCircle(Application application) {
        if (application != null && activityLifeCircleCallback != null)
            application.unregisterActivityLifecycleCallbacks(activityLifeCircleCallback);
    }

    /**
     * 显示浮动窗口
     *
     * @param onClickListener
     */
    public static void showFloatView(Activity activity, int resId, final View.OnClickListener onClickListener) {
        if(activity==null)
            return ;
        if (resId < 0 && FloatViewUtils.layoutId < 0)
            return;
        if (onClickListener != null)
            FloatViewUtils.onClickListener = onClickListener;
        ViewGroup floatView = floatViewMap.get(activity.toString());
        Log.d(TAG, "showFloatView. ...activity=" + activity + "，floatView=" + floatView);
        final WindowManager mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        if (floatView == null) {
            try {

                WindowManager.LayoutParams floatViewParams = new WindowManager.LayoutParams();

                // 设置窗体显示类型——TYPE_SYSTEM_ALERT(系统提示)
                floatViewParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;

                // 设置窗体焦点及触摸：
                // FLAG_NOT_FOCUSABLE(不能获得按键输入焦点)
                floatViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                // 设置显示的模式
                floatViewParams.format = PixelFormat.RGBA_8888;
                // 设置对齐的方法
                floatViewParams.gravity = Gravity.LEFT | Gravity.TOP;
//                wmParams.token = activity.getWindow().getWindowManager().
                // 设置窗体宽度和高度
                floatViewParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                floatViewParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                floatViewParams.x = dm.widthPixels;
                floatViewParams.y = dm.heightPixels / 2;
                LayoutInflater inflater = (LayoutInflater)
                        activity.getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);

                if (inflater != null) {
                    floatView = (ViewGroup) inflater.inflate(resId < 0 ? FloatViewUtils.layoutId : resId, null);
                }
                if (floatView == null)
                    return;
                floatViewParams.token = floatView.getWindowToken();
                floatView.setLayoutParams(floatViewParams);

                floatViewMap.put(activity.toString(), floatView);
                //绑定触摸移动监听
                floatView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (mWindowManager == null)
                            return false;
                        WindowManager.LayoutParams params = (WindowManager.LayoutParams) v.getLayoutParams();
                        params.x = (int) event.getRawX() - v.getWidth() / 2;
                        //25为状态栏高度
                        params.y = (int) event.getRawY() - v.getHeight() / 2 - 40;
                        mWindowManager.updateViewLayout(v, params);
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            lastDownTime = System.currentTimeMillis();
                        } else if (System.currentTimeMillis() - lastDownTime < 250 && event.getAction() == MotionEvent.ACTION_UP) {
                            if (onClickListener != null)
                                onClickListener.onClick(v);
                            if (FloatViewUtils.onClickListener != null) {
                                FloatViewUtils.onClickListener.onClick(v);
                            }
                            return false;
                        }
                        return false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            mWindowManager.addView(floatView, floatView.getLayoutParams());
        if (floatView != null)
            floatView.setVisibility(View.VISIBLE);
    }

    public static void setPosition(Activity activity, int x, int y) {
        WindowManager mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Log.d(TAG, "setPosition. ...activity=" + activity);
        ViewGroup floatView = floatViewMap.get(activity.toString());
        if (floatView == null)
            return;
        if (mWindowManager != null) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) floatView.getLayoutParams();
            params.x = x;
            params.y = y;
            mWindowManager.updateViewLayout(floatView, params);
        }
    }

    /**
     * 隐藏浮动窗口
     */

    public static void hiddenFloatView(Activity activity) {
        Log.d(TAG, "hiddenFloatView. ...activity=" + activity);
        WindowManager mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        ViewGroup floatView = floatViewMap.get(activity.toString());
        if (floatView == null)
            return;
        if (mWindowManager != null) {
            floatView.setVisibility(View.GONE);
            mWindowManager.removeView(floatView);
        }
    }

    /**
     * 销毁浮动窗口
     */
    public static void destroyFloatView(Activity activity) {
        Log.d(TAG, "destroyFloatView. ...activity=" + activity);
        ViewGroup floatView = floatViewMap.remove(activity.toString());
        if (floatView == null)
            return;
        WindowManager mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        if (mWindowManager != null) {
            try {
                mWindowManager.removeView(floatView);
                floatView = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 在应用退出时使用
     */
    public static void onAppExit() {
        floatViewMap.clear();
    }


}
