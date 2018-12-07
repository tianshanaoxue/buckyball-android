package com.buckyball.module.numberkeyboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * @author liuwei
 * @desc
 * @date 2018/12/4 16:48
 * @email liuweies@didichuxing.com
 */
public class BbNkbMainActivity extends AppCompatActivity {

    private EditText bbnbkEditText01;
    private KeyboardUtil mKeyboardUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbnbk_main);
        bbnbkEditText01 = findViewById(R.id.bbnbkEditText01);

        mKeyboardUtil = new KeyboardUtil(this,R.xml.number_replace_key,true);
        mKeyboardUtil.attachTo(bbnbkEditText01,true);
    }

}
