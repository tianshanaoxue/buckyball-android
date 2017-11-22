package com.buckyball.android.animation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * @author 刘伟 liuweies@didichuxing.com
 * @Desc
 * @date 17/10/30 11:22
 */
public class AnimationActivity extends AnimBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anim_activity_animation);
        //300%
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.translate_down_to_up_with_alpha_transparent_to_normal);
        animation1.setStartOffset(100);
        //200%
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.translate_down_to_up_with_alpha_transparent_to_normal1);

        TextView textView2 = (TextView) findViewById(R.id.titleTv);

        TextView textView1 = (TextView) findViewById(R.id.contentTV);

        textView1.startAnimation(animation1);
        textView2.startAnimation(animation2);


    }
}
