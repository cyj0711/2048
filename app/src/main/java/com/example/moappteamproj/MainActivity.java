package com.example.moappteamproj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Tiles tile[][]=new Tiles[4][4]; // 각 Button View와 매칭되는 연산용 배열
    Button buttonAry[][]=new Button[4][4];  // layout에 출력할 Button View
    ArrayList<Tiles> listPosition = new ArrayList<Tiles>(); // 빈 타일중에 랜덤으로 새 타일을 넣기위한 리스트
    public Direction direction;
    float downX=0.0f,downY=0.0f;  // 터치(눌렀을 때) 좌표
    float upX=0.0f,upY=0.0f;  // 터치(땠을 때) 좌표

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.activity_main);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();


                if (action == MotionEvent.ACTION_DOWN)  // 터치 눌렀을 때
                {
                    downX=event.getX();
                    downY=event.getY();
                }
                else if(action == MotionEvent.ACTION_UP)    // 터치 땠을 때
                {
                    upX=event.getX();
                    upY=event.getY();

                    direction = findDirection(downX,downY,upX,upY);

                    Log.d("MainActivity",direction.toString());
                }
                return true;
            }
        });

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
        int randomNumber=(int)(Math.random()*listPosition.size());

        listPosition.get(randomNumber).number=2;

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

    public Direction findDirection(float downX, float downY, float upX, float upY)
    {
        Direction direction=Direction.NONE;

        /*
              (0,1)                    (UP)
       (-1,0) (0,0) (1,0)  ==  (LEFT) (NONE) (RIGHT)
              (0,-1)                  (DOWN)
        왼쪽 숫자는 x, 오른쪽 숫자는 y 방향계
        */
        int x=0,y=0;    // 방향계

        float difX,difY; // down과 up의 차이

        difX=upX-downX;
        difY=upY-downY;

        if(Math.abs(difX) > Math.abs(difY))     // 옆으로 움직였을 때
        {
            if(difX < 0.0f)
                direction = Direction.LEFT;
            else
                direction = Direction.RIGHT;
        }
        else if (Math.abs(difX) < Math.abs(difY))   // 위아래로 움직였을 때
        {
            if(difY < 0.0f)
                direction = Direction.UP;
            else
                direction = Direction.DOWN;
        }
        else    // 안움직였을 때
            direction = Direction.NONE;

        return direction;
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
