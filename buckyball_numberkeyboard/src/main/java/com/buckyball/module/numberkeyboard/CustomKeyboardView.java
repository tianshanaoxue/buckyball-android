package com.buckyball.module.numberkeyboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
    }

    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomKeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(EditText editText, boolean isAutoShowOnFocus) {
        this.mEditText = editText;
        this.isAutoShowOnFocus = isAutoShowOnFocus;
        hideSystemSoftKeyboard(mEditText);
        setAutoShowOnFocus(isAutoShowOnFocus);
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

    /**
     * 新加的通过xmlResId设置keyboard
     *
     * @param xmlResId xml资源id
     */
    public void setKeyboard(int xmlResId) {
        this.mKeyboard = new Keyboard(this.getContext(), xmlResId);
        setKeyboard(mKeyboard);
    }

    public void setAutoShowOnFocus(boolean isAutoShowOnFocus) {
        this.isAutoShowOnFocus = isAutoShowOnFocus;
        if (mEditText == null) {
            return;
        }
        if (isAutoShowOnFocus) {
            mEditText.setOnFocusChangeListener(onFocusChangeListener);
        } else {
            mEditText.setOnFocusChangeListener(null);
        }
    }

    public void showSoftKeyboard(FrameLayout rootLayout) {
        if (rootLayout == null) {
            return;
        }
        this.rootLayout = rootLayout;
        this.setEnabled(true);
        setKeyboard(R.xml.number_with_decimal_point);
//        this.setPreviewEnabled(true);
        this.setOnKeyboardActionListener(keyboardActionListener);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        this.setVisibility(View.VISIBLE);
        this.rootLayout.addView(this.getParent() == null ? this : (View) this.getParent(), lp);
    }

    public void hideSoftKeyboard() {
        if (rootLayout != null) {
            this.setVisibility(View.GONE);
            rootLayout.removeView(this);
            rootLayout = null;
        }
    }

    /**
     * 是否显示中
     *
     * @return
     */
    public boolean isShowing() {
        return rootLayout != null && this.getVisibility() == View.VISIBLE;
    }

//    /**
//     * 隐藏系统键盘
//     *
//     * @param editText
//     */
//    public void hideSystemSoftKeyboard(EditText editText) {
//        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//            imm = null;
//        }
//    }

    /**
     * 隐藏系统键盘
     *
     * @param editText
     */
    public void hideSystemSoftKeyboard(EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
    }


    private OnKeyboardActionListener keyboardActionListener = new OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if (mEditText != null) {
                keyCodeDelect(primaryCode, mEditText);
            }
            postInvalidate();
        }

        @Override
        public void onText(CharSequence text) {

        }

        /**
         * 判断回退键 和大小写切换
         *
         * @param primaryCode
         * @param edText
         */
        private void keyCodeDelect(int primaryCode, EditText edText) {
            Editable editable = edText.getText();
            int start = edText.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (edText.hasFocus()) {
                    if (!TextUtils.isEmpty(editable)) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                }

            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
                setKeyboard(mKeyboard);
            } else {
                if (edText.hasFocus()) {
                    editable.insert(start, Character.toString((char) primaryCode));
                }
            }
        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    View.OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && isAutoShowOnFocus) {
                showSoftKeyboard((FrameLayout) ((Activity) mEditText.getContext()).getWindow().getDecorView());
            } else {
                hideSoftKeyboard();
            }
        }
    };

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

