package com.muath.myswiperefresh;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.muath.myswiperefresh.jellyrefresh.JellyRefreshLayout;


public class MainActivity extends AppCompatActivity {

    private JellyRefreshLayout mJellyLayout;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setTitle("Jelly");
        mJellyLayout = findViewById(R.id.jelly_refresh);
        mJellyLayout.setPullToRefreshListener(pullToRefreshLayout -> pullToRefreshLayout
                .postDelayed(() -> mJellyLayout.setRefreshing(false), 3000));
        View loadingView = LayoutInflater.from(this).inflate(R.layout.view_loading, null);
        mJellyLayout.setLoadingView(loadingView);
        //for mahmoud
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
        if (id == R.id.jelly_refresh) {
            mJellyLayout.post(new Runnable() {
                @Override
                public void run() {
                    mJellyLayout.setRefreshing(true);
                }
            });
            mJellyLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mJellyLayout.setRefreshing(false);
                }
            }, 3000);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
