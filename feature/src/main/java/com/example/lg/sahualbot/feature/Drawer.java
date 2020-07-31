package com.example.lg.sahualbot.feature;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Drawer {

	int boardWidth, boardHeight, x, y;//x and y is location of Display from board(0, 0)

	public Drawer (float boardWidth, float boardHeight){
		y = (int) boardHeight/19;
        x = (int) boardWidth/19;
        this.boardWidth = x/19;
        this.boardHeight = y/19;
    }

	public Drawable createBoard(){
		return new BoardView();
	}
	public Drawable drawStone(int color) {
	    return new StoneDrawer(color);
    }
    public Drawable drawSquare(String string){
	    if (string.equalsIgnoreCase("패")){
	        return new SquareDrawer(Color.BLACK, 2);
        } else if (string.equalsIgnoreCase("타일선택")){
            return new SquareDrawer(Color.RED, 1);
        } else if (string.equalsIgnoreCase("착수")){
            return new SquareDrawer(Color.BLUE, 1);

        } else return null;
    }

	public class BoardView extends Drawable {
		@Override
        public void draw(@NonNull Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(2);
                paint.setAntiAlias(true);

                for (int i = 1; i <= 19; i++){
                    canvas.drawLine(getCenterPoint(x*i), getCenterPoint(y), getCenterPoint(x*i), getCenterPoint(y*19), paint);
                    if(i == 4 || i == 10 || i==16){
                        canvas.drawCircle(getCenterPoint(x*i), getCenterPoint(y*4),x/10,paint);
                        canvas.drawCircle(getCenterPoint(x*i), getCenterPoint(y*10),x/10,paint);
                        canvas.drawCircle(getCenterPoint(x*i), getCenterPoint(y*16),x/10,paint);
                    }
                }
                for (int i = 1; i<=19 ; i++){
                    canvas.drawLine(getCenterPoint(x),getCenterPoint(y*i), getCenterPoint(x*19), getCenterPoint(y*i), paint);
                }
            }
        @Override
        public void setAlpha(int alpha) {

        }
        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }
        @SuppressLint("WrongConstant")
        @Override
        public int getOpacity() {
            return 0;
        }
    }
    public class StoneDrawer extends Drawable {

	    int color;

        public StoneDrawer(int color) {
            this.color = color;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            Paint paint = new Paint();
            paint.setColor(color);
            paint.setStrokeWidth(2);
            paint.setAntiAlias(true);
            canvas.drawCircle(getCenterPoint(x), getCenterPoint(y),x/2-3,paint);
        }
        @Override
        public void setAlpha(int alpha) {

        }
        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @SuppressLint("WrongConstant")
        @Override
        public int getOpacity() {
            return 0;
        }
    }
    public class SquareDrawer extends Drawable {

	    int color;
	    int size;
	    float startX, endX;

        public SquareDrawer(int color, int size) {//size의 숫자크기에 반비례하여 그려짐
            this.color = color;
            this.size = x/size;
            startX = x - this.size/2;
            endX = x - startX;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            Paint paint = new Paint();
            paint.setColor(color);
            paint.setStrokeWidth(4);
            paint.setAntiAlias(true);

            canvas.drawLine(startX, startX, startX, endX, paint);
            canvas.drawLine(startX, startX, endX, startX, paint);
            canvas.drawLine(startX, endX, endX, endX, paint);
            canvas.drawLine(endX, startX, endX, endX, paint);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @SuppressLint("WrongConstant")
        @Override
        public int getOpacity() {
            return 0;
        }
    }
    public float getCenterPoint(float i){
        return i-(x/2);
    }

}

