package com.example.lg.sahualbot.feature;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {



    enum tool {
        EMPTY, BLACK, WHITE
    }
    enum Turn {
        BLACK, WHITE
    }
    enum  Mode {
        EDIT, PLAY, PLAY_WITH_COMPUTER
    }
    int tileID = -1;
    float x,y;
    tool selectedTool = tool.BLACK;
    Turn turn = Turn.BLACK;
    Mode mode = Mode.EDIT;
    ImageView toolButton,folderButton;//editTool
    ImageView passButton, undoButton, folderButton2;//playTool
    LinearLayout viewBoard;
    LinearLayout toolLayout;
    LinearLayout editTool;
    LinearLayout playTool;
    LinearLayout.LayoutParams tmpLayout;
    Spinner spinner;
    Button resultButton, reset;// editTool
    Button resultButton2; // playTool
    TextView resultArea;
    Board board;
    Drawer drawer;
    Rule rule;
    Quiz quiz = new Quiz();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        create();
        createListener();
    }
    @SuppressLint("ResourceType")
    private void create(){
        viewBoard = (LinearLayout) findViewById(R.id.Board);
        toolLayout = (LinearLayout) findViewById(R.id.tool);
        editTool = (LinearLayout) findViewById(R.id.edit_tool);
        playTool = (LinearLayout) findViewById(R.id.play_tool);
        spinner = (Spinner) findViewById(R.id.spinner);
        resultArea = (TextView) findViewById(R.id.resultArea);
        resultButton = (Button) findViewById(R.id.resultButton);
        resultButton2 = (Button) findViewById(R.id.resultButton_2);
        toolButton = (ImageView) findViewById(R.id.toolbutton);
        passButton = (ImageView) findViewById(R.id.pass_button);
        undoButton = (ImageView) findViewById(R.id.undo_button);
        folderButton = (ImageView) findViewById(R.id.archive);
        folderButton2 = (ImageView) findViewById(R.id.archive2);
        reset = (Button) findViewById(R.id.reset);
        board = new Board();
        rule = new Rule(board);
        x = viewBoard.getLayoutParams().width;
        y = viewBoard.getLayoutParams().height;

        drawer = new Drawer(x, y);
        viewBoard.setBackground(drawer.createBoard());
        ArrayList tileList = new ArrayList();

        for (int i = 0; i < 19; i++) {
            LinearLayout rowLayout = new LinearLayout(getApplicationContext());
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            for (int j = 0; j < 19; j++) {

                FrameLayout tile = new FrameLayout(getApplicationContext());
                tile.setId(i*19 + j);
                tile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mode == Mode.EDIT) {
                            if (selectedTool == tool.EMPTY) {
                                board.update(v.getId());
                            } else if (selectedTool == tool.BLACK) {
                                rule.killOwnAction(v.getId(),Color.BLACK);
                            } else {
                                rule.killOwnAction(v.getId(),Color.WHITE);
                            }
                        } else /*if(mode == Mode.PLAY)*/ {
                            if (turn == Turn.BLACK) {

                                if (rule.goRule(v.getId(), Color.BLACK)){//rule에서 true값이 리턴 될 경우, rule안에서 moveCount가 이미 올라있음. 최소 moveCount = 1
                                    turn = Turn.WHITE;
                                }
                            } else {
                                if (rule.goRule(v.getId(), Color.WHITE)) {
                                    turn = Turn.BLACK;
                                }
                            }
                        }
                    }
                });

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) x / 19, (int) y / 19);
                rowLayout.addView(tile, lp);
                tileList.add(tile);
            }
            viewBoard.addView(rowLayout, llp);
        }
        board.setTileList(tileList);
        board.setDrawer(new Drawer(x,y));



        @SuppressLint("ResourceType") ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.mode, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);


    }
    private void createListener() {

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rule.getMove_Count() == 0)
                    return;
                if (mode == Mode.PLAY){
                    rule.undo(1, quiz);
                    if(turn == Turn.BLACK) {
                        turn = Turn.WHITE;
                    }
                    else {
                        turn = Turn.BLACK;
                    }
                } else if (mode == Mode.PLAY_WITH_COMPUTER){
                    rule.undo(2, quiz);
                }
            }
        });
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(turn == Turn.BLACK) {
                    turn = Turn.WHITE;
                    rule.pass(Color.BLACK);
                }
                else {
                    turn = Turn.BLACK;
                    rule.pass(Color.WHITE);
                }
            }
        });
        resultButton.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.out.println(spinner.getSelectedItem().toString());
                if(spinner.getSelectedItem().toString().equalsIgnoreCase("흑선으로 검토")){
                    clickSelfTestMode();
                    turn = Turn.BLACK;
                }
                if(spinner.getSelectedItem().toString().equalsIgnoreCase("백선으로 검토")){
                    clickSelfTestMode();
                    turn = Turn.WHITE;
                }
                if (spinner.getSelectedItem().toString().equalsIgnoreCase("흑선활")){
                    resultArea.setText("삶");
                }
                if (spinner.getSelectedItem().toString().equalsIgnoreCase("흑선백사")){
                    resultArea.setText("죽음");
                }
                if (spinner.getSelectedItem().toString().equalsIgnoreCase("백선흑사")){
                    resultArea.setText("패");
                }
                if (spinner.getSelectedItem().toString().equalsIgnoreCase("백선활")){
                    resultArea.setText("빅");
                }
            }
        });
        resultButton2.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                tmpLayout = (LinearLayout.LayoutParams) editTool.getLayoutParams();
                editTool.setLayoutParams(playTool.getLayoutParams());
                editTool.setVisibility(View.VISIBLE);
                playTool.setVisibility(View.INVISIBLE);
                playTool.setLayoutParams(tmpLayout);
                mode = Mode.EDIT;
                board.setHashMap((HashMap)quiz.getHashMap().clone());
            }
        });
        toolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedTool == tool.BLACK) {
                    selectedTool = tool.WHITE;
                    toolButton.setImageResource(R.drawable.selectedwhite);
                } else if (selectedTool == tool.WHITE) {
                    selectedTool = tool.EMPTY;
                    toolButton.setImageResource(R.drawable.selectedxmark);
                } else {
                    selectedTool = tool.BLACK;
                    toolButton.setImageResource(R.drawable.selectedblack);
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board.setPae(-1);
                board.removeAll();
            }
        });
    }

    private void clickSelfTestMode(){
        quiz.setHashMap((HashMap)board.getBoardMap().clone());
        rule.reset();
        tmpLayout = (LinearLayout.LayoutParams) playTool.getLayoutParams();
        playTool.setLayoutParams(editTool.getLayoutParams());
        playTool.setVisibility(View.VISIBLE);
        editTool.setVisibility(View.INVISIBLE);
        editTool.setLayoutParams(tmpLayout);
        mode = Mode.PLAY;
    }

}
