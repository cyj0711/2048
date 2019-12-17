package com.example.moappteamproj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Tiles tile[][]=new Tiles[4][4]; // 각 Button View와 매칭되는 연산용 배열
    TextView ViewAry[][]=new TextView[4][4];  // layout에 출력할 Button View
    ArrayList<Tiles> listPosition = new ArrayList<Tiles>(); // 빈 타일중에 랜덤으로 새 타일을 넣기위한 리스트
    public Direction direction = Direction.NONE;
    float downX=0.0f,downY=0.0f;  // 터치(눌렀을 때) 좌표
    float upX=0.0f,upY=0.0f;  // 터치(땠을 때) 좌표

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.activity_main);   // 전체 화면에 대해 터치이벤트를 받음

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

                    direction = findDirection(downX,downY,upX,upY); // 두 터치 좌표를 통해 슬라이드 방향 확인

                    if(isEnd()==false) {
                        update();
                        draw();
                    }

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

        draw();
    }

    // 매 프레임마다 연산
    public void update()
    {

        if(direction != Direction.NONE)
        {
            if(moveTile(direction))
                addNewTile();
        }

    }

    // 매 프레임마다 출력
    public void draw()
    {
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                if(tile[i][j].number == 0)
                    ViewAry[i][j].setText("");
                else
                    ViewAry[i][j].setText(Integer.toString(tile[i][j].number));

            }
        }
    }

    // 새로운 타일(2 or 4)을 빈 타일에 추가
    void addNewTile()
    {
        listPosition.clear();   // 이전 턴에서의 list 값이 있으면 안되니 전부 clear

        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
                if(tile[i][j].number==0)
                    listPosition.add(tile[i][j]);   // 빈 타일이므로 list에 추가
        }

        int randomNumber=(int)(Math.random()*listPosition.size());
        listPosition.get(randomNumber).number=2;

    }

    // 입력받은 방향에 맞춰 타일 이동
    boolean moveTile(Direction direction)
    {

        if(canMove(direction)==false)
            return false;

        switch(direction)
        {
            case UP :
                moveUp();
                break;
            case DOWN:
                moveDown();
                break;
            case LEFT:
                moveLeft();
                break;
            case RIGHT:
                moveRight();
                break;
        }

        return true;
    }

    // 해당 방향으로 움직일 수 있는지 여부 확인
    boolean canMove(Direction direction)
    {
        if(direction == Direction.UP)   // 위로 움직일경우
        {
            for(int i=1;i<4;i++)
            {
                for(int j=0;j<4;j++)
                {
                    if(tile[i][j].number == 0)  // 0은 무시
                        continue;

                    if((tile[i-1][j].number == 0) || (tile[i-1][j].number == tile[i][j].number))    // 윗 방향으로 빈 공간이 있거나 합칠 수 있으면 이동 가능
                        return true;
                }
            }
        }
        else if(direction == Direction.DOWN)   // 아래로 움직일경우
        {
            for(int i=2;i>-1;i--)
            {
                for(int j=0;j<4;j++)
                {
                    if(tile[i][j].number == 0)  // 0은 무시
                        continue;

                    if((tile[i+1][j].number == 0) || (tile[i+1][j].number == tile[i][j].number))    // 아래 방향으로 빈 공간이 있거나 합칠 수 있으면 이동 가능
                        return true;
                }
            }
        }
        else if(direction == Direction.LEFT)   // 왼쪽으로 움직일경우
        {
            for(int i=0;i<4;i++)
            {
                for(int j=1;j<4;j++)
                {
                    if(tile[i][j].number == 0)  // 0은 무시
                        continue;

                    if((tile[i][j-1].number == 0) || (tile[i][j-1].number == tile[i][j].number))    // 왼쪽 방향으로 빈 공간이 있거나 합칠 수 있으면 이동 가능
                        return true;
                }
            }
        }
        else if(direction == Direction.RIGHT)   // 오른쪽로 움직일경우
        {
            for(int i=0;i<4;i++)
            {
                for(int j=2;j>-1;j--)
                {
                    if(tile[i][j].number == 0)  // 0은 무시
                        continue;

                    if((tile[i][j+1].number == 0) || (tile[i][j+1].number == tile[i][j].number))    // 오른쪽 방향으로 빈 공간이 있거나 합칠 수 있으면 이동 가능
                        return true;
                }
            }
        }

        return false;
    }

    void moveUp()
    {
        for(int i=1; i<4;i++)
        {
            for (int j=0;j<4;j++)
            {
                if(tile[i][j].number == 0)  // 빈 타일은 무시하고 바로 다음 타일 선택
                    continue;

                int k = i - 1;

                while(k != -1 && tile[k][j].number == 0) { k--; }   // 현재 타일 제일 위쪽의 빈공간을 찾음


                if(k== -1)  // 제일 위에 비어있으면 제일 위로 타일 이동
                {
                    tile[k+1][j].number = tile[i][j].number;
                    tile[i][j].number = 0;
                }
                else    // 위에 다른 타일이 있으면
                {
                    if(tile[k][j].number == tile[i][j].number)  // 위에 있는 타일이랑 숫자가 같아서 조합되는 경우 두 타일을 합친다.
                    {
                        tile[k][j].number = tile[k][j].number<<1;
                        tile[i][j].number = 0;
                    }
                    else    // 서로 다르면 그냥 그 밑으로 옮긴다.
                    {
                        int temp = tile[i][j].number;
                        tile[i][j].number = 0;
                        tile[k+1][j].number = temp;
                    }

                }

            }
        }
    }



    void moveDown()
    {
        for(int i=2; i>-1;i--)
        {
            for (int j=0;j<4;j++)
            {
                if(tile[i][j].number == 0)  // 빈 타일은 무시하고 바로 다음 타일 선택
                    continue;

                int k = i + 1;

                while(k != 4 && tile[k][j].number == 0) { k++; }   // 현재 타일 제일 위쪽의 빈공간을 찾음


                if(k == 4)  // 제일 위에 비어있으면 제일 위로 타일 이동
                {
                    tile[k-1][j].number = tile[i][j].number;
                    tile[i][j].number = 0;
                }
                else    // 위에 다른 타일이 있으면
                {
                    if(tile[k][j].number == tile[i][j].number)  // 위에 있는 타일이랑 숫자가 같아서 조합되는 경우 두 타일을 합친다.
                    {
                        tile[k][j].number = tile[k][j].number<<1;
                        tile[i][j].number = 0;
                    }
                    else    // 서로 다르면 그냥 그 밑으로 옮긴다.
                    {
                        int temp = tile[i][j].number;
                        tile[i][j].number = 0;
                        tile[k-1][j].number = temp;
                    }

                }

            }
        }
    }

    void moveLeft()
    {
        for(int i=0; i<4;i++)
        {
            for (int j=1;j<4;j++)
            {
                if(tile[i][j].number == 0)  // 빈 타일은 무시하고 바로 다음 타일 선택
                    continue;

                int k = j - 1;

                while(k != -1 && tile[i][k].number == 0) { k--; }   // 현재 타일 제일 위쪽의 빈공간을 찾음


                if(k == -1)  // 제일 위에 비어있으면 제일 위로 타일 이동
                {
                    tile[i][k+1].number = tile[i][j].number;
                    tile[i][j].number = 0;
                }
                else    // 위에 다른 타일이 있으면
                {
                    if(tile[i][k].number == tile[i][j].number)  // 위에 있는 타일이랑 숫자가 같아서 조합되는 경우 두 타일을 합친다.
                    {
                        tile[i][k].number = tile[i][k].number<<1;
                        tile[i][j].number = 0;
                    }
                    else    // 서로 다르면 그냥 그 밑으로 옮긴다.
                    {
                        int temp = tile[i][j].number;
                        tile[i][j].number = 0;
                        tile[i][k+1].number = temp;
                    }

                }

            }
        }
    }

    void moveRight()
    {
        for(int i=0; i<4;i++)
        {
            for (int j=2;j>-1;j--)
            {
                if(tile[i][j].number == 0)  // 빈 타일은 무시하고 바로 다음 타일 선택
                    continue;

                int k = j + 1;

                while(k != 4 && tile[i][k].number == 0) { k++; }   // 현재 타일 제일 위쪽의 빈공간을 찾음


                if(k == 4)  // 제일 위에 비어있으면 제일 위로 타일 이동
                {
                    tile[i][k-1].number = tile[i][j].number;
                    tile[i][j].number = 0;
                }
                else    // 위에 다른 타일이 있으면
                {
                    if(tile[i][k].number == tile[i][j].number)  // 위에 있는 타일이랑 숫자가 같아서 조합되는 경우 두 타일을 합친다.
                    {
                        tile[i][k].number = tile[i][k].number<<1;
                        tile[i][j].number = 0;
                    }
                    else    // 서로 다르면 그냥 그 밑으로 옮긴다.
                    {
                        int temp = tile[i][j].number;
                        tile[i][j].number = 0;
                        tile[i][k-1].number = temp;
                    }

                }

            }
        }
    }

    // 게임오버인지 확인
    public boolean isEnd()
    {
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                // 빈자리가 하나라도 있으면 아직 게임 안끝남(return false)
                if(tile[i][j].number==0) {
                    return false;
                }
            }
        }

        // ===============================================
        // 각 타일의 상하좌우가 전부 해당 타일이랑 전부 다른숫자일때,(움직여도 조합이 안될때) 게임오버 하는거에 대한 코드 추가하기
        // ===============================================

        return true;
    }

    public Direction findDirection(float downX, float downY, float upX, float upY)
    {
        Direction direction=Direction.NONE;

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

        ViewAry[0][0]=(TextView)findViewById(R.id.ary00);
        ViewAry[0][1]=(TextView)findViewById(R.id.ary01);
        ViewAry[0][2]=(TextView)findViewById(R.id.ary02);
        ViewAry[0][3]=(TextView)findViewById(R.id.ary03);

        ViewAry[1][0]=(TextView)findViewById(R.id.ary10);
        ViewAry[1][1]=(TextView)findViewById(R.id.ary11);
        ViewAry[1][2]=(TextView)findViewById(R.id.ary12);
        ViewAry[1][3]=(TextView)findViewById(R.id.ary13);

        ViewAry[2][0]=(TextView)findViewById(R.id.ary20);
        ViewAry[2][1]=(TextView)findViewById(R.id.ary21);
        ViewAry[2][2]=(TextView)findViewById(R.id.ary22);
        ViewAry[2][3]=(TextView)findViewById(R.id.ary23);

        ViewAry[3][0]=(TextView)findViewById(R.id.ary30);
        ViewAry[3][1]=(TextView)findViewById(R.id.ary31);
        ViewAry[3][2]=(TextView)findViewById(R.id.ary32);
        ViewAry[3][3]=(TextView)findViewById(R.id.ary33);

        // 4*4 Button에 매치되는 4*4 tile 배열 동기화

        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                tile[i][j]=new Tiles(i,j);
            }
        }

        addNewTile();
        addNewTile();
    }
}
