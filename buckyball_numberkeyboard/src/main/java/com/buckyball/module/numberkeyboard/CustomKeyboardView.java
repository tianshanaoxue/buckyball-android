package com.buckyball.module.numberkeyboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author liuwei
 * @desc
 * @date 2018/12/5 15:29
 * @email liuweies@didichuxing.com
 */
public class CustomKeyboardView extends KeyboardView {
    private FrameLayout rootLayout;
    private boolean isAutoShowOnFocus = false;
    private EditText mEditText;
    private Keyboard mKeyboard;
    private Paint mPaint;

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        resetPaint();
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resetPaint();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        resetPaint();
    }

    //修改paint，目的是为了自定义字体
    private void resetPaint() {
        try {
            Field mPaintField = KeyboardView.class.getDeclaredField("mPaint");
            mPaintField.setAccessible(true);
            mPaint = new CustomPaint(getContext());
            mPaint.setAntiAlias(true);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setAlpha(255);
            mPaintField.set(this, mPaint);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        List<Keyboard.Key> keys = getKeyboard().getKeys();
        if (keys == null || keys.isEmpty()) {
            return;
        }
        for (Keyboard.Key key : keys) {
            if (key.codes[0] == Keyboard.KEYCODE_DONE) {
                StateListDrawable keyBgDrawable = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    keyBgDrawable = (StateListDrawable) getContext().getResources().getDrawable(R.drawable.mand_mobile_rn_keyboard_confirm_btn_bg_selector, null);
                } else {
                    keyBgDrawable = (StateListDrawable) getContext().getResources().getDrawable(R.drawable.mand_mobile_rn_keyboard_confirm_btn_bg_selector);
                }
                int[] drawableState = key.getCurrentDrawableState();
                keyBgDrawable.setState(drawableState);

                final Rect bounds = keyBgDrawable.getBounds();
                if (key.width != bounds.right ||
                        key.height != bounds.bottom) {
                    keyBgDrawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                }
                keyBgDrawable.draw(canvas);

                final Paint paint = (Paint) getField("mPaint");
                if (paint != null) {
                    paint.setTextSize((Integer) getField("mLabelTextSize"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        paint.setColor(getContext().getResources().getColor(android.R.color.white, null));
                    } else {
                        paint.setColor(getContext().getResources().getColor(android.R.color.white));
                    }
                    Rect rect = new Rect(key.x, key.y, key.x + key.width, key.y + key.height);
                    Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
                    int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;

                    canvas.drawText(key.label.toString(), rect.centerX(), baseline, paint);
                    canvas.restore();
                }
            }
        }
    }

    private Object getField(String fieldName) {
        try {
            Field field = KeyboardView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}

