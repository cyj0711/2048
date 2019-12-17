package com.example.moappteamproj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Tiles tile[][]=new Tiles[4][4]; // 각 Button View와 매칭되는 연산용 배열
    Button buttonAry[][]=new Button[4][4];  // layout에 출력할 Button View
    ArrayList<Tiles> listPosition = new ArrayList<Tiles>(); // 빈 타일중에 랜덤으로 새 타일을 넣기위한 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startGame();

    }

    // 전체 게임 진행
    public void startGame()
    {

        initTile();

        while(isEnd()==false) {
            update();
            draw();
        }
    }

    // 매 프레임마다 연산
    public void update()
    {
        addNewTile();
    }

    // 매 프레임마다 출력
    public void draw()
    {
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                buttonAry[i][j].setText(Integer.toString(tile[i][j].number));
            }
        }
    }

    // 새로운 타일(2 or 4)을 빈 타일에 추가
    void addNewTile()
    {

    }

    // 게임오버인지 확인
    public boolean isEnd()
    {
        int count=0;
        boolean foundZero = false;
        listPosition.clear();   // 이전 턴에서의 list 값이 있으면 안되니 전부 clear

        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                // 빈자리가 하나라도 있으면 아직 게임 안끝남(return false)
                if(tile[i][j].number==0) {
                    listPosition.add(tile[i][j]);   // 빈 타일이므로 list에 추가
                    foundZero=true;
                }
            }
        }
        if(foundZero)
            return false;

        // ===============================================
        // 각 타일의 상하좌우가 전부 해당 타일이랑 전부 다른숫자일때,(움직여도 조합이 안될때) 게임오버 하는거에 대한 코드 추가하기
        // ===============================================

        return true;
    }

    // 처음 초기화
    public void initTile()
    {
        // 각 버튼에 대해 layout과 동기화

        buttonAry[0][0]=(Button)findViewById(R.id.ary00);
        buttonAry[0][1]=(Button)findViewById(R.id.ary01);
        buttonAry[0][2]=(Button)findViewById(R.id.ary02);
        buttonAry[0][3]=(Button)findViewById(R.id.ary03);

        buttonAry[1][0]=(Button)findViewById(R.id.ary10);
        buttonAry[1][1]=(Button)findViewById(R.id.ary11);
        buttonAry[1][2]=(Button)findViewById(R.id.ary12);
        buttonAry[1][3]=(Button)findViewById(R.id.ary13);

        buttonAry[2][0]=(Button)findViewById(R.id.ary20);
        buttonAry[2][1]=(Button)findViewById(R.id.ary21);
        buttonAry[2][2]=(Button)findViewById(R.id.ary22);
        buttonAry[2][3]=(Button)findViewById(R.id.ary23);

        buttonAry[3][0]=(Button)findViewById(R.id.ary30);
        buttonAry[3][1]=(Button)findViewById(R.id.ary31);
        buttonAry[3][2]=(Button)findViewById(R.id.ary32);
        buttonAry[3][3]=(Button)findViewById(R.id.ary33);

        // 4*4 Button에 매치되는 4*4 tile 배열 동기화

        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                tile[i][j]=new Tiles(i,j);
            }
        }
    }
}
