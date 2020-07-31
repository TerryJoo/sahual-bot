package com.example.lg.sahualbot.feature;

import java.util.ArrayList;
import java.util.HashMap;

public class AI {
    private int color;//컴퓨터가 배정받은 색깔
    private HashMap board;
    Rule rule;

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setBoard(HashMap board) {
        this.board = board;
    }

    public HashMap getBoard() {
        return board;
    }
    private ArrayList getArangeToTreeSearch(){//가장 먼저 되어야할.. 이것에 따라 귀사활인지, 변사활인지, 중앙사활인지 판단 가능
        ArrayList list = new ArrayList();
        return list;
    }

    private void MCTS(ArrayList arange){//몬테카를로 트리 서치, 어레이에 등록되어있는 tile에 한해서 무작위 검사.

    }

    private void safeStone(int firstColor){

    }

    private void killStone(int filrstColor){

    }

    private int findSafeArea(ArrayList connectedStone){
        return -1;
    }
}
