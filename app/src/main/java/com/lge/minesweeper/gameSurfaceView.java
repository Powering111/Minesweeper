package com.lge.minesweeper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.Locale;

public class gameSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    Context mContext;
    SurfaceHolder mHolder;
    RenderingThread mRenderingThread;
    board gameBoard;
    boolean render;
    final int SCR_WIDTH=240;
    final int SCR_HEIGHT=320;
    Bitmap img_base,img_selection,img_flag,img_mine,img_flaggedmine,img_time;
    Bitmap [] img_opened = new Bitmap[9];
    long startTimemillis, timeElapsed;
    int width, height, mineCount, offsetx, offsety,scale;
    public gameSurfaceView(Context context, int width, int height, int mineCount) {
        super(context);
        mContext = context;
        mHolder = getHolder();
        mHolder.addCallback(this);
        this.width=width;
        this.height=height;
        this.mineCount=mineCount;
        gameBoard = new board(mContext, this, width,height, mineCount);
        scale=25;
        offsetx=0;
        offsety=0;
        loadResources();
        resetView();

    }
    private void resetView(){
        scale=25;
        offsetx = SCR_WIDTH/2 - width*scale/2;
        offsety = SCR_HEIGHT/2 - height*scale/2;

    }
    public void startGame(){
        startTimemillis = System.currentTimeMillis();
    }
    private void loadResources(){
        img_base = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.base);
        for(int i=0;i<=8;i++){
            img_opened[i] = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.opened0 + i);
        }
        img_selection = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.selection);
        img_mine = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.bomb);
        img_flag = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.flag);
        img_flaggedmine=BitmapFactory.decodeResource(mContext.getResources(),R.drawable.flaggedbomb);
        img_time=BitmapFactory.decodeResource(mContext.getResources(),R.drawable.time);
    }
    private void fixCoordinate(){
        if(scale*(gameBoard.y-1)+offsety<28){
            offsety = 28-scale*(gameBoard.y-1);
        }
        if(scale*(gameBoard.y-1)+offsety+48>SCR_HEIGHT){
            offsety = SCR_HEIGHT-48-scale*(gameBoard.y-1);
        }
        if(scale*(gameBoard.x-1)+offsetx<0){
            offsetx=-scale*(gameBoard.x-1);
        }
        if(scale*(gameBoard.x-1)+offsetx+scale>SCR_WIDTH){
            offsetx=SCR_WIDTH-scale-scale*(gameBoard.x-1);
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch(keyCode){
            //scale*(gameBoard.x-1)+offsetx,scale*(gameBoard.y-1)+offsety,scale*(gameBoard.x-1)+offsetx+scale,scale*(gameBoard.y-1)+offsety+scale)
            case KeyEvent.KEYCODE_DPAD_UP:
                gameBoard.moveXY(direction.up);
                fixCoordinate();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                gameBoard.moveXY(direction.down);
                fixCoordinate();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                gameBoard.moveXY(direction.left);
                fixCoordinate();
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                gameBoard.moveXY(direction.right);
                fixCoordinate();
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                gameBoard.open();
                fixCoordinate();
                break;
            case KeyEvent.KEYCODE_SOFT_LEFT:
                gameBoard.flag();
                fixCoordinate();
                break;
            case KeyEvent.KEYCODE_SOFT_RIGHT:
                gameBoard.clean();
                break;
            case KeyEvent.KEYCODE_2:
                offsety+=10;
                break;
            case KeyEvent.KEYCODE_4:
                offsetx+=10;
                break;
            case KeyEvent.KEYCODE_6:
                offsetx-=10;
                break;
            case KeyEvent.KEYCODE_8:
                offsety-=10;
                break;
            case KeyEvent.KEYCODE_0:
                resetView();
                break;
            case KeyEvent.KEYCODE_STAR:
                if(scale<=50) {
                    scale+=4;
                    if(offsetx>=(SCR_WIDTH/2)){
                        offsetx+=2*width;
                    }else{
                        offsetx-=2*width;
                    }
                    if(offsety>=(SCR_HEIGHT/2)){
                        offsety+=2*height;
                    }else{
                        offsety-=2*height;
                    }
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                finishSurface();
                break;
            case KeyEvent.KEYCODE_POUND:
                if(scale>10) {
                    scale-=4;
                    if(offsetx>=(SCR_WIDTH/2)){
                        offsetx-=2*width;
                    }else{
                        offsetx+=2*width;
                    }
                    if(offsety>=(SCR_HEIGHT/2)){
                        offsety-=2*height;
                    }else{
                        offsety+=2*height;
                    }
                }
                break;
            default:
                return false;
        }
        return true;
    }



    class RenderingThread extends Thread{
        public RenderingThread(){}
        @Override
        public void run(){
            Canvas canvas;
            Paint whitePaint = new Paint();
            whitePaint.setColor(Color.WHITE);
            whitePaint.setTextSize(20);
            Paint rectPaint = new Paint();
            rectPaint.setColor(Color.BLUE);

            while(render){
                canvas = mHolder.lockCanvas();
                try{
                    synchronized (mHolder){
                        canvas.drawColor(Color.BLACK);
                        for(int i=1;i<=width;i++){
                            for(int j=1;j<=height;j++){
                                RectF rect = new RectF(scale*(i-1)+offsetx,scale*(j-1)+offsety,scale*(i-1)+offsetx+scale,scale*(j-1)+offsety+scale);
                                if(gameBoard.map[i][j].isOpened){
                                    if(gameBoard.map[i][j].isMine){
                                        if(gameBoard.map[i][j].isFlagged){
                                            canvas.drawBitmap(img_flaggedmine,null,rect,null);
                                        }else {
                                            canvas.drawBitmap(img_mine, null, rect, null);
                                        }
                                    }
                                    else canvas.drawBitmap(img_opened[gameBoard.map[i][j].minesAround],null, rect,null);
                                }
                                else {
                                    canvas.drawBitmap(img_base,null, rect,null);
                                    if(gameBoard.map[i][j].isFlagged){
                                        canvas.drawBitmap(img_flag,null, rect,null);
                                    }
                                }
                            }
                        }


                        canvas.drawBitmap(img_selection,null,
                                new RectF(scale*(gameBoard.x-1)+offsetx,scale*(gameBoard.y-1)+offsety,scale*(gameBoard.x-1)+offsetx+scale,scale*(gameBoard.y-1)+offsety+scale),null);
                        canvas.drawRect(new RectF(0,0,240,28),rectPaint);
                        canvas.drawBitmap(img_mine,null,new RectF(20, 0, 45, 25), null);
                        canvas.drawText(String.valueOf(mineCount - gameBoard.flagcount),50,20,whitePaint);
                        canvas.drawBitmap(img_time, null, new RectF(120,0,140,20),null);
                        if(startTimemillis==0){
                            canvas.drawText("00:00", 150, 20, whitePaint);
                        }else {
                            if(!gameBoard.isgameover)timeElapsed = System.currentTimeMillis() - startTimemillis;
                            canvas.drawText(String.format(Locale.US,"%02d",timeElapsed / 60000) + ":" + String.format(Locale.US,"%02d",timeElapsed / 1000 % 60), 150, 20, whitePaint);
                        }
                    }
                    //TODO game clear, offests.
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    if(canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
                try{
                    Thread.sleep(100);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void gameOver(){
        Toast.makeText(mContext,"지뢰가 터졌다.",Toast.LENGTH_SHORT).show();
    }
    public void gameClear() {
        Toast.makeText(mContext,"게임 클리어",Toast.LENGTH_SHORT).show();
    }
    public void finishSurface(){
        synchronized (mHolder){
            ((Activity)mContext).finish();
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        render=true;
        mRenderingThread = new gameSurfaceView.RenderingThread();
        mRenderingThread.start();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try{
            if(mRenderingThread != null)
                mRenderingThread.join();
        }catch(InterruptedException e){}
        mRenderingThread = null;
    }
    public void pause(){
        render=false;
        try{
            mRenderingThread.join();
        }catch(InterruptedException e){}
    }
    public void resume(){
        render=true;
        if(mRenderingThread==null && mHolder.getSurface().isValid()){
            mRenderingThread = new gameSurfaceView.RenderingThread();
            mRenderingThread.start();
        }
    }
}
