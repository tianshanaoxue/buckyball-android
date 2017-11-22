package com.buckyball.viewutils;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView showFloatView, hiddenFloatView, destroyFloatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        showFloatView = (TextView) findViewById(R.id.showFloatView);
        hiddenFloatView = (TextView) findViewById(R.id.hiddenFloatView);
        destroyFloatView = (TextView) findViewById(R.id.destroyFloatView);
        showFloatView.setOnClickListener(this);
        hiddenFloatView.setOnClickListener(this);
        destroyFloatView.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FloatViewUtils.hiddenFloatView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatViewUtils.showFloatView(this,R.layout.float_view_layout,floatViewonClick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatViewUtils.onAppExit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.showFloatView:
                FloatViewUtils.showFloatView(this,R.layout.float_view_layout,floatViewonClick);
                FloatViewUtils.setPosition(1080,800);
                break;
            case R.id.hiddenFloatView:
                FloatViewUtils.hiddenFloatView();
                break;
            case R.id.destroyFloatView:
                FloatViewUtils.onAppExit();
                break;
        }
    }

    private View.OnClickListener floatViewonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this,"浮动窗被点击",Toast.LENGTH_SHORT).show();
        }
    };
}
