package com.buckyball.viewutils;

import android.app.Application;
import android.view.View;
import android.widget.Toast;

/**
 * @author 把代码敲晕
 * @Desc
 * @date 17/11/23 15:15
 * @Email 574613441@qq.com
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FloatViewUtils.registerActivityLifeCircle(this, R.layout.float_view_layout,new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(BaseApplication.this, "点击点击", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
