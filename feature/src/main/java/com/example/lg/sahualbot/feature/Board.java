package com.example.lg.sahualbot.feature;
import android.graphics.Color;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
public class Board {

    private HashMap hashMap;
    private ArrayList tileList;
    private Drawer drawer;
    private int tmpTile, paeTile;

    public Board () {
        hashMap = new HashMap();
    }
    public Board (ArrayList tileList, Drawer drawer) {
        hashMap = new HashMap();
        this.tileList = tileList;
        this.drawer = drawer;
    }

    public HashMap getBoardMap(){
        return hashMap;
    }
    public void setDrawer(Drawer drawer) {
        this.drawer = drawer;
    }

    protected void removeStone(ArrayList list){
        for (int i = 0 ; i < list.size(); i++){
            update((int)list.get(i));
        }
    }

    protected void check(int tileID, int color){
        hashMap.put(tileID, color);
        tmpTile = tileID;
    }
    protected void rollBack(){
        hashMap.remove(tmpTile);
    }
    public void update(int tileID){
        hashMap.remove(tileID);
        FrameLayout tile =(FrameLayout) tileList.get(tileID);
        tile.setBackground(null);
    }
    public void update(int tileID, int color){
        hashMap.put(tileID, color);
        FrameLayout tile =(FrameLayout) tileList.get(tileID);
        tile.setBackground(drawer.drawStone(color));
    }
    public int getStoneColor(int tileID){//타일에 스톤이 없거나 키값이 없으면 0리턴
        if(hashMap.get(tileID) == null) return 0;
        else if ((int) hashMap.get(tileID) == Color.BLACK) return Color.BLACK;
        else if ((int) hashMap.get(tileID) == Color.WHITE) return Color.WHITE;
        else return 0;

    }
    public boolean isEmpty(int tileId) {
        if (tileId == -1) return  false;
        else if (hashMap.get(tileId) == null) return true;
        else return false;
    }
    public void setTileList(ArrayList tileList) {
        this.tileList = tileList;
    }
    public void setPae(int tileId){//패일 때
        FrameLayout tile;
        if (paeTile != -1){
            tile =(FrameLayout) tileList.get(paeTile);
            tile.setBackground(null);
        }
        if (tileId == -1) {
            paeTile = tileId;
            return;
        }
        else {
            paeTile = tileId;
            tile = (FrameLayout) tileList.get(paeTile);
            tile.setBackground(drawer.drawSquare("패"));
        }
    }
    public int getPaeTile() {
        return paeTile;
    }
    public void removeAll(){
        for (int i = 0 ; i < 361 ; i++){
            FrameLayout tile =(FrameLayout) tileList.get(i);
            if (tile != null)
                tile.setBackground(null);
        }
        hashMap.clear();
    }

    public void setHashMap(HashMap hashMap) {
        removeAll();
        this.hashMap = hashMap;
        for (int i = 0 ; i < 361 ; i++){
            if (hashMap.get(i) != null){
                FrameLayout tile =(FrameLayout) tileList.get(i);
                tile.setBackground(drawer.drawStone((int)hashMap.get(i)));
            }
        }
    }
}
