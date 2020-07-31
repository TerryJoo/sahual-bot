package com.example.lg.sahualbot.feature;

import android.graphics.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class Rule{//이 클래스는 착수 동작 시, 올바른 착수인지 검토하고, 착수 후 발생하는 이벤트를 처리합니다. 룰에는 바둑룰과 에디터 사용룰(killOwnAction)이 있습니다.

    private enum Turn{
        BLACK, WHITE
    }
    public Board board;
    public int black_Score = 0;//사실 안쓰임
    public int white_Score = 0;//사실 안쓰임22
    public int move_Count = 0;
    public ArrayList record = new ArrayList();
    private Turn turn = Turn.BLACK;
    private ArrayList connectedStoneList = new ArrayList();
    private ArrayList wayOutList = new ArrayList();

        //Overload createMethod
    public Rule (Board board){//일반적인 경우
        turn = Turn.BLACK;
        this.board = board;
    }
    public Rule (Board board, int color_FirstMove){//editMode 보통 에디터에서 편집 후 받아오는 경우
        this.board = board;
        if (color_FirstMove == Color.WHITE)
            turn = Turn.WHITE;
        else turn = Turn.BLACK;
    }
    public Rule (Board board, int color_FirstMove, int black_Score, int white_Score){
        this.board = board;
        if (color_FirstMove == Color.WHITE)
            turn = Turn.WHITE;
        else turn = Turn.BLACK;
        this.black_Score = black_Score;
        this.white_Score = white_Score;
    }

    public int killOwnAction(int tileId, int color){//EditerMode 패,
        board.check(tileId, color);
        if(isKillingMoveAndAction(tileId, color)){
            board.update(tileId, color);
            int count = getFriendCount(tileId, color);
            int count2 = getEmptyCount(tileId);
            if(count == 0 && count2 == 1){//착수지점 사방에 아군이 없고(count = 0), 공배가 하나(count = 1)인 경우 공배TileID를 리턴
                int tmpID;
                int tile[] = getTileID(tileId);
                for (int i = 0 ; i < 4; i++){
                    if (board.isEmpty(tile[i])){
                        tmpID = tile[i];
                        return tmpID;
                    }
                }
                return  -1;
            }
            else return -1;
        } else
        if(ownCheck(tileId, color)){
            board.rollBack();
            return -2;
        }
        else {
            board.update(tileId, color);
            return -1;
        }

    }
    public boolean goRule(int tileId, int color){
        if (paeCheck(tileId)){
            return false;
        }
        if (existenceStoneCheck(tileId))
            return false;
        int i;
        i = killOwnAction(tileId, color);

        if(i == -2){//자살에 한해서..
            return false;
        }
        if(i != -1){
            if(getEmptyCount(i) == 0){
                board.setPae(i);
                record.add(move_Count, new VO(tileId%19, tileId/19, color, move_Count));
                move_Count++;
                return true;
            }
        } else {
            board.setPae(-1);
            record.add(move_Count, new VO(tileId%19, tileId/19, color, move_Count));
            move_Count++;
            return true;
        }
        return false;
    }

    public void undo(int i, Quiz quiz) {
        int tmp = move_Count - i;
        while (i > 0){
            record.remove(record.size()-1);
            i--;
        }
        move_Count = 0;

        board.removeAll();
        board.setHashMap((HashMap)quiz.getHashMap().clone());
        ArrayList tmpList = record;
        record = new ArrayList();
        for (int j = 0 ; j < tmpList.size(); j++){
            VO vo = (VO)tmpList.get(j);
            goRule(vo.getX()+vo.getY()*19, vo.getColor());
        }

    }
    public void pass(int color){
        record.add(move_Count, new VO(-1, -1, color, move_Count));
        move_Count++;
        board.setPae(-1);
    }

    private boolean isKillingMoveAndAction(int tileId, int color){//착수로 인해 상대 돌이 죽을 시 true, 아닐 시 false
        boolean b = false;
        boolean tile[] = getEnemy(tileId, color);//변두리라 타일 자체가 없는 경우false
        int c;
        if(color == Color.WHITE) c=Color.BLACK;
        else c = Color.WHITE;
        // 그 다음 해당 돌이 죽는 돌인가를 검사.
        if (tile[0]){
            connectedStoneList.clear();
            findConnectedStoneByTileID(tileId + 19, c);
            if (isDead(connectedStoneList)){
                setScore(connectedStoneList.size(), color);
                board.removeStone(connectedStoneList);
                tile=getEnemy(tileId, color);
                b= true;
            }
        }
        if (tile[1]){
            connectedStoneList.clear();
            findConnectedStoneByTileID(tileId - 19, c);
            if (isDead(connectedStoneList)){
                setScore(connectedStoneList.size(), color);
                board.removeStone(connectedStoneList);
                tile=getEnemy(tileId, color);
                b=true;
            }
        }
        if (tile[2]){
            connectedStoneList.clear();
            findConnectedStoneByTileID(tileId + 1, c);
            if (isDead(connectedStoneList)){
                setScore(connectedStoneList.size(), color);
                board.removeStone(connectedStoneList);
                tile=getEnemy(tileId, color);
                b=true;
            }
        }
        if (tile[3]){
            connectedStoneList.clear();
            findConnectedStoneByTileID(tileId - 1, c);
            if (isDead(connectedStoneList)){
                setScore(connectedStoneList.size(), color);
                board.removeStone(connectedStoneList);
                b=true;
            }
        }
        return b;
    }
    private boolean ownCheck (int tileId, int color){//착수 금지 체크: 자살 금지 해당 자리가 자살로인한 착수 금지면 true, 아니면 false
        connectedStoneList.clear();
        boolean friend[] = getFriend(tileId, color);
        if (friend[0])
            findConnectedStoneByTileID(tileId + 19, color);//재귀함수형 함수..
        if (friend[1])
            findConnectedStoneByTileID(tileId - 19, color);
        if (friend[2])
            findConnectedStoneByTileID(tileId + 1, color);
        if (friend[3])
            findConnectedStoneByTileID(tileId - 1, color);
        if(overlapCheck(tileId, connectedStoneList) == false) connectedStoneList.add(tileId);
        return isDead(connectedStoneList);
    }
    private boolean isDead(ArrayList connectedStoneList) {//공배 0개면 true
        wayOutList.clear();
        for (int i = 0; i < connectedStoneList.size(); i++){
            checkAndAddSafeWayOut((int)connectedStoneList.get(i));
        }
        return wayOutList.isEmpty();
    }
    private void checkAndAddSafeWayOut(int tileId) {
        boolean id[] = getWayOut(tileId);
        if (board.isEmpty(tileId + 19) && id[0] && overlapCheck(tileId + 19, wayOutList) == false){
            wayOutList.add(tileId + 19);
        }
        if (board.isEmpty(tileId - 19) && id[1] && overlapCheck(tileId - 19, wayOutList) == false){
            wayOutList.add(tileId - 19);
        }
        if (board.isEmpty(tileId + 1) && id[2] && overlapCheck(tileId + 1, wayOutList) == false){
            wayOutList.add(tileId + 1);
        }
        if (board.isEmpty(tileId - 1) && id[3] && overlapCheck(tileId - 1, wayOutList) == false){
            wayOutList.add(tileId - 1);
        }
    }
    private void findConnectedStoneByTileID(int tileId, int color) {//함수 호출 전, 반드시 list초기화 요망
        if (overlapCheck(tileId, connectedStoneList)) return;//재귀함수 탈출, 이미 등록되어있는 돌.
        connectedStoneList.add(tileId);
        boolean friend[] = getFriend(tileId, color);
        if (friend[0])
        findConnectedStoneByTileID(tileId+19, color);
        if (friend[1])
        findConnectedStoneByTileID(tileId-19, color);
        if (friend[2])
        findConnectedStoneByTileID(tileId+1, color);
        if (friend[3])
        findConnectedStoneByTileID(tileId-1, color);
    }
    private boolean overlapCheck(int tileID, ArrayList list){//이미 있으면 true
        for (int i = 0 ; i <list.size();i++){
            if ((int)list.get(i) == tileID){
                return true;
            }
        }
        return false;
    }
    private boolean isEnemy(int tileId, int color){
        if(board.isEmpty(tileId) || board.getStoneColor(tileId) == color)
            return false;
        else
            return true;
    }
    public boolean[] getWayOut(int tileId){
        boolean tile[] = new boolean[4];
        if (tileId < 342) tile[0] = true;
        else tile[0] = false;
        if (tileId >= 19) tile[1] = true;
        else tile[1] = false;
        if (tileId%19 != 18) tile[2] = true;
        else tile[2] = false;
        if (tileId%19 != 0) tile[3] = true;
        else tile[3] = false;
        return tile;
    }
    private boolean[] getFriend(int tileID, int color){
        boolean[] tile = getWayOut(tileID);
        if (tile[0])
            tile[0] = (board.getStoneColor(tileID +19)==color);
        if (tile[1])
            tile[1] = (board.getStoneColor(tileID -19)==color);
        if (tile[2])
            tile[2] = (board.getStoneColor(tileID +1)==color);
        if (tile[3])
            tile[3] = (board.getStoneColor(tileID -1)==color);

        return tile;
    }
    private boolean[] getEnemy(int tileId, int color){//사방에 적이 있으면 각 방향에 true값 배열 리턴
        boolean id[] = getWayOut(tileId);
        if (id[0])
            id[0] = isEnemy(tileId+19, color);
        if (id[1])
            id[1] = isEnemy(tileId-19, color);
        if (id[2])
            id[2] = isEnemy(tileId+1, color);
        if (id[3])
            id[3] = isEnemy(tileId-1, color);

        return id;
    }
    private int[] getTileID(int tileId){
        boolean b[] = getWayOut(tileId);
        int tile[] = new int[4];
        if (b[0]) tile[0] = tileId +19;
        else tile[0] = -1;
        if (b[1]) tile[1] = tileId -19;
        else tile[1] = -1;
        if (b[2]) tile[2] = tileId +1;
        else tile[2] = -1;
        if (b[3]) tile[3] = tileId -1;
        else tile[3] = -1;
        return  tile;
    }
    private boolean existenceStoneCheck(int tileId){//착수금지 체크: 돌 색과는 관계없이 타일에 돌이 이미 존재하여 착수 금지면 true, 아니면 false
        if (board.isEmpty(tileId))
            return false;
        else
            return true;
    }
    private boolean paeCheck(int tileId){//패정보는 board에, 해당 자리가 패로인한 착수 금지면 true, 아니면 false

        if (board.getPaeTile() == tileId) {
            return true;
        }
        else {
            return false;
        }
    }
    private int getEmptyCount(int tileID){
        int count = 0;
        int tile[] = getTileID(tileID);
        for (int i = 0 ; i < 4 ; i++){
            if(board.isEmpty(tile[i])) count ++;
        }
        return count;
    }
    private int getFriendCount(int tileID, int color){
        int count = 0 ;
        int tile[] = getTileID(tileID);
        for (int i = 0 ; i<4; i++){
            if (board.getStoneColor(tile[i]) == color)
                count ++;
        }
        return count;
    }
    private void setScore(int score, int color){
        if(color == Color.WHITE) white_Score += score;
        else black_Score += score;
    }

    public void setTurn(int color) {
        if (color == Color.WHITE)
        this.turn = Turn.WHITE;
        else turn = Turn.BLACK;
    }

    public int getMove_Count() {
        return move_Count;
    }
    public void reset(){
        move_Count = 0;
        record.clear();
        black_Score = 0;
        white_Score = 0;
    }
}
