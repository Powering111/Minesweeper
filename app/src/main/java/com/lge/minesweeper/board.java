package com.lge.minesweeper;


import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

enum direction{
    up, down, left, right
};
public class board{
    public tile[][] map;
    private int width, height, mineCount;
    int x, y, flagcount;
    boolean isfirstopen, isgameover;
    gameSurfaceView mgameSurfaceView;
    Context mContext;
    class tile{
        boolean isMine;
        boolean isOpened;
        boolean isFlagged;
        int minesAround;
        public tile(){
            isMine=false;
            isOpened=false;
            isFlagged=false;
        }
        public void setMine(){
            this.isMine=true;
        }
        public void open(){
            this.isOpened=true;
        }
        public void flag(){
            this.isFlagged=true;
        }
        public void unflag(){
            this.isFlagged=false;
        }
        public void setMineNumber(int n){
            this.minesAround=n;
        }
    }
    public board(Context context, gameSurfaceView gsv, int width, int height, int mineCount){
        this.mContext= context;
        this.mgameSurfaceView = gsv;
        this.width = width;
        this.height = height;
        this.mineCount=mineCount;
        map = new tile[width+1][height+1];
        this.x=width/2+1;
        this.y=height/2+1;
        this.flagcount=0;
        isfirstopen=true;
        isgameover=false;
        for(int i=1;i<=width;i++){
            for(int j=1;j<=height;j++){
                map[i][j]=new tile();
            }
        }
    }
    private void firstOpen(int x, int y){
        class tuple{
            int a,b;
            tuple(int a, int b){
                this.a=a;
                this.b=b;
            }
        }
        //set mines
        ArrayList<tuple> tempList = new ArrayList();
        for(int i=1;i<=width;i++){
            for(int j=1;j<=height;j++){
                if(i==x&&j==y) continue;
                else tempList.add(new tuple(i,j));
            }
        }
        Collections.shuffle(tempList);
        for(int i=0;i<mineCount;i++){
            try {
                tuple a = tempList.get(i);
                map[a.a][a.b].setMine();
            }catch(IndexOutOfBoundsException e){
                Toast.makeText(mContext, "오류!",Toast.LENGTH_SHORT).show();
                mgameSurfaceView.finishSurface();
            }
        }
        //count number
        for(int i=1;i<=width;i++){
            for(int j=1;j<=height;j++){
                map[i][j].setMineNumber(calcMinesAround(i,j));
            }
        }
        isfirstopen=false;
        mgameSurfaceView.startGame();
    }
    private int calcMinesAround(int x, int y){
        if(map[x][y].isMine){
            return -1;
        }
        else return ifMine(x-1,y)+ifMine(x-1,y-1)+ifMine(x-1,y+1)+ifMine(x,y-1)+ifMine(x,y+1)+ifMine(x+1,y-1)+ifMine(x+1,y)+ifMine(x+1,y+1);
    }
    private int calcFlagsAround(int x, int y){
        return ifFlag(x-1,y)+ifFlag(x+1,y)+ifFlag(x-1,y-1)+ifFlag(x+1,y-1)+ifFlag(x,y-1)+ifFlag(x-1,y+1)+ifFlag(x,y+1)+ifFlag(x+1,y+1);
    }
    private int ifFlag(int x, int y){
        if(valid(x,y)){
            if(map[x][y].isFlagged||(map[x][y].isOpened&&map[x][y].isMine)){
                return 1;
            }
        }
        return 0;
    }
    private int ifMine(int x, int y){
        if(valid(x,y)){
            return map[x][y].isMine ? 1:0;
        }
        else return 0;
    }
    private boolean valid(int x, int y){
        if(x>=1 && x<=width && y>=1 && y<=height) return true;
        else return false;
    }
    public void moveXY(direction dir){
        if(isgameover)return;
        switch(dir){
            case up:
                if(this.y >1) this.y--;
                break;
            case down:
                if(this.y<this.height) this.y++;
                break;
            case left:
                if(this.x>1) this.x--;
                break;
            case right:
                if(this.x<width)this.x++;
                break;
        }
    }

    public void flag(){
        if(isgameover)return;
        if(!map[x][y].isOpened){
            if(map[x][y].isFlagged) {
                map[x][y].unflag();
                this.flagcount--;
            }
            else {
                map[x][y].flag();
                this.flagcount++;
            }
        }
        checkClear();
    }
    public void open(){
        if(isgameover) {
            mgameSurfaceView.finishSurface();
        }
        if(map[x][y].isFlagged) return;
        if(isfirstopen) firstOpen(x,y);
        if(!map[x][y].isOpened) {
            if(map[x][y].minesAround==0){
                openBlank(x,y);
            }
            map[x][y].open();
            if (map[x][y].isMine) {
                gameOver();
            }

        }
        checkClear();
    }
    public void open(int x, int y){
        if(map[x][y].isFlagged) return;
        if(!map[x][y].isOpened){
            if (map[x][y].minesAround == 0) {
                openBlank(x,y);
            }map[x][y].open();
            if(map[x][y].isMine){
                gameOver();
            }
        }
        checkClear();
    }
    public void clean(){
        if(isgameover)return;
        if(map[x][y].isOpened){
            if(map[x][y].minesAround==calcFlagsAround(x,y)){
                clean(x-1,y);
                clean(x-1,y-1);
                clean(x-1,y+1);
                clean(x,y-1);
                clean(x,y+1);
                clean(x+1,y-1);
                clean(x+1,y);
                clean(x+1,y+1);
            }
        }
    }
    private void clean(int x, int y){
        if(!valid(x,y))return;
        if(!map[x][y].isFlagged && !map[x][y].isOpened){
            open(x,y);
        }
    }
    private void openBlank(int x, int y){
        if(!valid(x,y))return;
        if(map[x][y].isOpened)return;
        map[x][y].open();
        if(map[x][y].minesAround!=0) return;
        openBlank(x-1,y);
        openBlank(x+1,y);
        openBlank(x,y+1);
        openBlank(x-1,y+1);
        openBlank(x+1,y+1);
        openBlank(x,y-1);
        openBlank(x-1,y-1);
        openBlank(x+1,y-1);
    }
    private void openEverything(){
        for(int i=1;i<=width;i++){
            for(int j=1;j<=height;j++){
                map[i][j].isOpened=true;
            }
        }
    }
    private void gameOver(){
        mgameSurfaceView.gameOver();
        openEverything();
        isgameover=true;
    }
    private void checkClear(){
        if(isgameover)return;
        for(int i=1;i<=width;i++){
            for(int j=1;j<=height;j++){
                if(map[i][j].isOpened||map[i][j].isMine){
                }else{
                    return;
                }
            }
        }
        openEverything();
        isgameover=true;
        mgameSurfaceView.gameClear();
    }
}