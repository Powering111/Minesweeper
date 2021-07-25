package com.lge.minesweeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class MainActivity extends Activity implements View.OnClickListener{
    Button startCustomBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.startEasy).setOnClickListener(this);
        findViewById(R.id.startMedium).setOnClickListener(this);
        findViewById(R.id.startHard).setOnClickListener(this);
        startCustomBtn = findViewById(R.id.startcustom);
        startCustomBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.startEasy:
                startGame(9,9,10);
                break;
            case R.id.startMedium:
                startGame(16,16,40);
                break;
            case R.id.startHard:
                startGame(30,16,99);
                break;
            case R.id.startcustom:
                showCustomMenu();
                break;
        }
    }
    private void showCustomMenu(){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(startCustomBtn, Gravity.CENTER, 0, 0);

        EditText widthText = popupView.findViewById(R.id.widthInput);
        EditText heightText = popupView.findViewById(R.id.heightInput);
        EditText minesText = popupView.findViewById(R.id.minesInput);

        popupView.findViewById(R.id.startCustomBtn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int width= Integer.parseInt(widthText.getText().toString());
                int height= Integer.parseInt(heightText.getText().toString());
                int mines=Integer.parseInt(minesText.getText().toString());
                if(width>=1&&height>=1&&width<=100&&height<=100&&mines>=0&&mines<=(width*height)) {
                    popupWindow.dismiss();
                    startGame(width, height, mines);
                }
            }
        });
    }
    private void startGame(int width, int height, int mineCount){
        Intent intent = new Intent(this,gameActivity.class);
        intent.putExtra("width",width);
        intent.putExtra("height",height);
        intent.putExtra("mineCount",mineCount);
        startActivity(intent);
    }
}