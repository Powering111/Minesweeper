package com.lge.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class gameActivity extends Activity implements KeyEvent.Callback{
    gameSurfaceView surfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int width = intent.getIntExtra("width",10);
        int height = intent.getIntExtra("height",10);
        int mineCount = intent.getIntExtra("mineCount",10);
        Log.d("troll","I am in onCreate");
        surfaceView = new gameSurfaceView(this, width, height,mineCount);
        setContentView(surfaceView);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        return surfaceView.onKeyDown(keyCode,event);
    }

    @Override
    public void onPause(){
        super.onPause();
        surfaceView.pause();
    }
    @Override
    protected void onResume(){
        super.onResume();
        surfaceView.resume();
    }
}