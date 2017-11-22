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

/**
 * @author
 * @Desc
 * @date 17/11/22 10:47
 */
public class FloatViewUtils {
    private static final String TAG = "FloatViewUtils";
    private static ViewGroup floatView;
    private static long lastDownTime;
    private static WindowManager mWindowManager;
    private static Activity activity;
    private static FloatViewActivityLifeCircleCallback activityLifeCircleCallback;
    /**
     * 注册时的点击监听
     */
    private static View.OnClickListener onClickListener;
    private static int layoutId = -1;
    public static void registerActivityLifeCircle(Application application, int layoutId,View.OnClickListener onClickListener) {
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
        if(resId<0 && FloatViewUtils.layoutId<0)
            return ;
        if (FloatViewUtils.activity != activity)
            destroyFloatView();
        FloatViewUtils.activity = activity;
        Log.d(TAG, "showFloatView....");
        if (floatView == null) {
            try {
//                requestAlertWindowPermission();
                //获取LayoutParams对象
                //获取的是CompatModeWrapper对象
                mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);

                // 窗体的布局样式
                final WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

                // 设置窗体显示类型——TYPE_SYSTEM_ALERT(系统提示)
                wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;

                // 设置窗体焦点及触摸：
                // FLAG_NOT_FOCUSABLE(不能获得按键输入焦点)
                wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                // 设置显示的模式
                wmParams.format = PixelFormat.RGBA_8888;
                // 设置对齐的方法
                wmParams.gravity = Gravity.LEFT | Gravity.TOP;
//                wmParams.token = activity.getWindow().getWindowManager().
                // 设置窗体宽度和高度
                wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                DisplayMetrics dm = activity.getResources().getDisplayMetrics();
                wmParams.x = dm.widthPixels;
                wmParams.y = dm.heightPixels / 2;

                LayoutInflater inflater = (LayoutInflater)
                        activity.getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);

                if (inflater != null) {
                    floatView = (ViewGroup) inflater.inflate(resId<0?FloatViewUtils.layoutId:resId, null);
                }
                wmParams.token = floatView.getWindowToken();
                if (floatView == null)
                    return;
                if (mWindowManager != null) {
                    Log.d(TAG, "mWindowManager addView " + floatView);
                    mWindowManager.addView(floatView, wmParams);
                }
                //绑定触摸移动监听
                floatView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (mWindowManager == null)
                            return false;
                        wmParams.x = (int) event.getRawX() - floatView.getWidth() / 2;
                        //25为状态栏高度
                        wmParams.y = (int) event.getRawY() - floatView.getHeight() / 2 - 40;
                        Log.d(TAG, "x1=" + event.getRawX() + ",y1=" + event.getRawY());
                        Log.d(TAG, "x2=" + wmParams.x + ",y2=" + wmParams.y);
                        mWindowManager.updateViewLayout(floatView, wmParams);
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            lastDownTime = System.currentTimeMillis();
                        } else if (System.currentTimeMillis() - lastDownTime < 250 && event.getAction() == MotionEvent.ACTION_UP) {
                            if (onClickListener != null)
                                onClickListener.onClick(floatView);
                            if(FloatViewUtils.onClickListener!=null){
                                FloatViewUtils.onClickListener.onClick(floatView);
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
        floatView.setVisibility(View.VISIBLE);
    }

    public static void setPosition(int x, int y) {
        if (floatView != null && mWindowManager != null) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) floatView.getLayoutParams();
            params.x = x;
            params.y = y;
            mWindowManager.updateViewLayout(floatView, params);
        }
    }

    /**
     * 隐藏浮动窗口
     */

    public static void hiddenFloatView() {
        Log.d(TAG, "hiddenFloatView....");
        if (floatView != null && mWindowManager != null) {
            floatView.setVisibility(View.GONE);
        }
    }

    /**
     * 销毁浮动窗口
     */
    private static void destroyFloatView(Activity activity) {
        Log.d(TAG, "destroyFloatView....");
        if (floatView != null && mWindowManager != null) {
            try {
                mWindowManager.removeView(floatView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mWindowManager = null;
            floatView = null;
            activity = null;
        }
    }

    /**
     * 在应用退出时使用
     */
    public static void onAppExit() {
        destroyFloatView();
    }


}
