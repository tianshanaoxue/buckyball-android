package com.buckyball.module.numberkeyboard;

/**
 * @author liuwei
 * @desc
 * @date 2018/12/4 20:46
 * @email liuweies@didichuxing.com
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class KeyboardUtil {
    /**
     * 显示键盘的视图
     */
    private Activity mActivity;
    /**
     * 键盘视图
     */
    private KeyboardView mKeyboardView;
    /**
     * 键盘
     */
    private Keyboard mKeyboard;
    /**
     * 输入框
     */
    private EditText mEditText;
    /**
     * 键盘布局
     */
    private View mViewContainer;

    private static final String[] needRandomOrder = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    private boolean mRandomKeyboard = false;

    /**
     * 焦点改变监听
     */
    View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                showSoftKeyboard();
            } else {
                hideSoftKeyboard();
            }
        }
    };

    /**
     * 构造方法
     *
     * @param activity 根视图
     */
    public KeyboardUtil(Activity activity, int keyboardResId, boolean mRandomKeyboard) {
        this.mActivity = activity;
        this.mRandomKeyboard = mRandomKeyboard;
        this.mKeyboard = new Keyboard(mActivity, keyboardResId);
    }

    /**
     * 构造方法
     *
     * @param activity 根视图
     */
    public KeyboardUtil(Activity activity, int keyboardResId) {
        this(activity, keyboardResId, false);
    }

    /**
     * 构造方法
     *
     * @param activity 根视图
     */
    public KeyboardUtil(Activity activity) {
        this(activity, R.xml.number_with_decimal_point, false);
    }

    public void setmRandomKeyboard(boolean mRandomKeyboard) {
        this.mRandomKeyboard = mRandomKeyboard;
    }

    /**
     * 绑定输入框
     *
     * @param editText 输入框
     * @param isAuto   是否自动显示
     */
    public void attachTo(EditText editText, boolean isAuto) {
        this.mEditText = editText;
        hideSystemSoftKeyboard(this.mEditText);
        setAutoShowOnFocus(isAuto);
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowing()) {
                    showSoftKeyboard();
                }
            }
        });
    }

    /**
     * 隐藏系统软件盘
     *
     * @param editText 输入框
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void hideSystemSoftKeyboard(EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt < 11) {
            editText.setInputType(InputType.TYPE_NULL);
        } else {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 焦点时自动显示
     *
     * @param enabled 是否显示
     */
    private void setAutoShowOnFocus(boolean enabled) {
        if (null == mEditText) {
            return;
        }
        if (enabled) {
            mEditText.setOnFocusChangeListener(mOnFocusChangeListener);
        } else {
            mEditText.setOnFocusChangeListener(null);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftKeyboard() {
        if (null == mViewContainer) {
            mViewContainer = mActivity.getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        } else {
            if (null != mViewContainer.getParent()) {
                return;
            }
        }

        FrameLayout frameLayout = (FrameLayout) mActivity.getWindow().getDecorView();
        KeyboardView keyboardView = mViewContainer.findViewById(R.id.keyboard_view);
        this.mKeyboardView = keyboardView;

        if (mRandomKeyboard) {
            randomKeyboard();
        }

        this.mKeyboardView.setKeyboard(mKeyboard);
        this.mKeyboardView.setEnabled(true);
        this.mKeyboardView.setPreviewEnabled(false);
        this.mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        frameLayout.addView(mViewContainer, layoutParams);

        mViewContainer.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.down_to_up));
        if (mViewContainer.getVisibility() == View.GONE) {
            mViewContainer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置键盘乱序
     */
    private void randomKeyboard() {
        List<Keyboard.Key> keyList = mKeyboard.getKeys();
        int count = keyList.size();

        List<String> allNeedRandomOrder = Arrays.asList(needRandomOrder);

        //用来临时存放旧的位置的key内容
        List<KeyContent> tmpList = new ArrayList<>();

        List<Integer> keyOldIndex = new ArrayList<>();

        Keyboard.Key key;
        for (int i = 0; i < count; i++) {
            key = keyList.get(i);
            if (allNeedRandomOrder.contains(key.label.toString())) {
                tmpList.add(new KeyContent(key));
                keyOldIndex.add(i);
            }
        }

        //打乱顺序
        Collections.shuffle(tmpList);

        for (int i = 0; i < keyOldIndex.size(); i++) {
            copyKey(tmpList.get(i),keyList.get(keyOldIndex.get(i)));
        }
    }

    private void copyKey(KeyContent from, Keyboard.Key to) {
        to.label = from.label;
        to.codes = from.codes;
        to.icon = from.icon;
        to.iconPreview = from.iconPreview;
        to.popupResId = from.popupResId;
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard() {
        if (null != mViewContainer && null != mViewContainer.getParent()) {
            mViewContainer.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.up_to_hide));
            ((ViewGroup) mViewContainer.getParent()).removeView(mViewContainer);
            mViewContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 判断是否显示
     *
     * @return true, 显示; false, 不显示
     */
    public boolean isShowing() {
        if (null == mViewContainer) {
            return false;
        }
        return mViewContainer.getVisibility() == View.VISIBLE;
    }

    KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int i) {
        }

        @Override
        public void onRelease(int i) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if (null != mEditText) {
                keyCode(primaryCode, mEditText);
            }
            mKeyboardView.postInvalidate();
        }

        @Override
        public void onText(CharSequence charSequence) {
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

    /**
     * 字符
     *
     * @param primaryCode 主要字符
     * @param editText    编辑框
     */
    private void keyCode(int primaryCode, EditText editText) {
        Editable editable = editText.getText();
        int start = editText.getSelectionStart();
        if (primaryCode == Keyboard.KEYCODE_DELETE) { // 回退
            if (editText.hasFocus()) {
                if (!TextUtils.isEmpty(editable)) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            }
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) { // 大小写切换
            mKeyboardView.setKeyboard(mKeyboard);
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 取消
            hideSoftKeyboard();
        } else {
            if (editText.hasFocus()) {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }
    }

    private static class KeyContent {
        private CharSequence label;
        private Drawable icon;
        private int[] codes;
        private Drawable iconPreview;
        private int popupResId;

        public KeyContent(Keyboard.Key key) {
            this.label = key.label;
            this.icon = key.icon;
            this.codes = key.codes;
            this.iconPreview = key.iconPreview;
            this.popupResId = key.popupResId;
        }
    }
}


