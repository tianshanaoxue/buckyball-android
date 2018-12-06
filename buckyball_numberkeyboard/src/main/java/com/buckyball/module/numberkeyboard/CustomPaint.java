package com.buckyball.module.numberkeyboard;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * @author liuwei
 * @desc
 * @date 2018/12/5 20:10
 * @email liuweies@didichuxing.com
 */
public class CustomPaint extends Paint {
    private Context mContext;

    public CustomPaint(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public Typeface setTypeface(Typeface typeface) {
        typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DINMedium.ttf");
        return super.setTypeface(typeface);
    }
}
