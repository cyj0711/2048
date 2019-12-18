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

    Tiles[][] tile=new Tiles[4][4]; // 각 Button View와 매칭되는 연산용 배열
    TextView[][] ViewAry=new TextView[4][4];  // layout에 출력할 Button View
    ArrayList<Tiles> listPosition = new ArrayList<Tiles>(); // 빈 타일중에 랜덤으로 새 타일을 넣기위한 리스트
    public Direction direction = Direction.NONE;    // 터치 방향
    float downX=0.0f,downY=0.0f;  // 터치(눌렀을 때) 좌표
    float upX=0.0f,upY=0.0f;  // 터치(땠을 때) 좌표
    final static int[] tileColor={0xFFD6CDC4, 0xFFEEE4DA, 0xFFECE0DC, 0xFFF2B179, 0xFFF59563, 0xFFF57C5F, 0xFFF65D3B, 0xFFEDCE71, 0xFFEDCE61, 0xFFECC850, 0xFFEDC53F, 0xFF3D3A33};  // 타일 색상
    Score score = new Score();    // 점수 클래스
    Button ViewCurScore,ViewBestScore;  // 현재 점수, 최고 점수 뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
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

                    if(!isEnd()) {
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

    // 처음 게임 시작
    public void startGame()
    {
        initTile();
        saveTile(); // 시작할 때 tile은 undo 해도 그대로임
        draw();
    }

    // 게임 연산
    public void update()
    {
        if(direction != Direction.NONE)
        {
            if(moveTile(direction)) {
                clearDirtyTile();
                updateBestScore();
                addNewTile();
            }
        }

    }

    // 게임 출력
    public void draw()
    {
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                // 타일 숫자 출력
                if(tile[i][j].getNumber() == 0)
                    ViewAry[i][j].setText("");
                else
                    ViewAry[i][j].setText(Integer.toString(tile[i][j].getNumber()));

                // 타일 색깔 지정
                int colorIndex;
                if(tile[i][j].getNumber()>0) {
                    colorIndex = (int) baseLog((double) tile[i][j].getNumber(), 2.0);
                    if(colorIndex>11)   // 2048 보다 더 큰 숫자들은 2048과 같은 색상 적용
                        colorIndex=11;
                }
                else
                    colorIndex=0;
                ViewAry[i][j].setBackgroundColor(tileColor[colorIndex]);

                // 글자 색깔 지정
                if(tile[i][j].getNumber()>0)
                {
                    if(tile[i][j].getNumber() <= 4)
                        ViewAry[i][j].setTextColor(0xFF776E65);
                    else
                        ViewAry[i][j].setTextColor(0xFFFDF4EF);
                }
            }
        }

        // 점수출력
        ViewCurScore.setText(Integer.toString(score.getCurrent()));
        ViewBestScore.setText(Integer.toString(score.getBest()));
    }

    // 새로운 타일(2 or 4)을 빈 타일에 추가
    void addNewTile()
    {
        int tileNumber=2;
        listPosition.clear();   // 이전 턴에서의 list 값이 있으면 안되니 전부 clear

        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
                if(tile[i][j].getNumber()==0)
                    listPosition.add(tile[i][j]);   // 빈 타일이므로 list에 추가
        }

        if((int)(Math.random()*5) == 0)    // 20% 확률로 2대신 4 타일 추가
            tileNumber=4;

        int randomNumber=(int)(Math.random()*listPosition.size());
        listPosition.get(randomNumber).setNumber(tileNumber);    // 빈 타일 리스트중 랜덤으로 타일 추가

        // 새 타일을 추가했는데 타일이 꽉차면 게임오버
        if(isEnd())
        {
            showGameOver();
        }

    }

    // 입력받은 방향에 맞춰 타일 이동
    boolean moveTile(Direction direction)
    {

        if(!canMove(direction)) // 터치해도 이동이 안되면 해당 터치 무시
            return false;

        // 움직이기 전에 undo 로 사용할 타일 저장
        saveTile();

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
                    if(tile[i][j].getNumber() == 0)  // 0은 무시
                        continue;

                    if((tile[i-1][j].getNumber() == 0) || (tile[i-1][j].getNumber() == tile[i][j].getNumber()))    // 윗 방향으로 빈 공간이 있거나 합칠 수 있으면 이동 가능
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
                    if(tile[i][j].getNumber() == 0)  // 0은 무시
                        continue;

                    if((tile[i+1][j].getNumber() == 0) || (tile[i+1][j].getNumber() == tile[i][j].getNumber()))    // 아래 방향으로 빈 공간이 있거나 합칠 수 있으면 이동 가능
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
                    if(tile[i][j].getNumber() == 0)  // 0은 무시
                        continue;

                    if((tile[i][j-1].getNumber() == 0) || (tile[i][j-1].getNumber() == tile[i][j].getNumber()))    // 왼쪽 방향으로 빈 공간이 있거나 합칠 수 있으면 이동 가능
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
                    if(tile[i][j].getNumber() == 0)  // 0은 무시
                        continue;

                    if((tile[i][j+1].getNumber() == 0) || (tile[i][j+1].getNumber() == tile[i][j].getNumber()))    // 오른쪽 방향으로 빈 공간이 있거나 합칠 수 있으면 이동 가능
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
                if(tile[i][j].getNumber() == 0)  // 빈 타일은 무시하고 바로 다음 타일 선택
                    continue;

                int k = i - 1;

                while(k != -1 && tile[k][j].getNumber() == 0) { k--; }   // 현재 타일 제일 위쪽의 빈공간을 찾음


                if(k== -1)  // 제일 위에 비어있으면 제일 위로 타일 이동
                {
                    tile[k+1][j].setNumber(tile[i][j].getNumber());
                    tile[i][j].setNumber(0);
                }
                else    // 위에 다른 타일이 있으면
                {
                    if(tile[k][j].getNumber() == tile[i][j].getNumber())  // 위에 있는 타일이랑 숫자가 같아서 조합되는 경우 두 타일을 합친다.
                    {
                        tile[k][j].setNumber(tile[k][j].getNumber()<<1);
                        score.setCurrent(score.getCurrent()+tile[k][j].getNumber());    // 합친거 점수에 반영
                        tile[k][j].setNumber(tile[k][j].getNumber()+1); // 세 개 이상의 타일이 한번에 안합쳐지도록 + 1
                        tile[i][j].setNumber(0);
                    }
                    else    // 서로 다르면 그냥 그 밑으로 옮긴다.
                    {
                        int temp = tile[i][j].getNumber();
                        tile[i][j].setNumber(0);
                        tile[k+1][j].setNumber(temp);
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
                if(tile[i][j].getNumber() == 0)  // 빈 타일은 무시하고 바로 다음 타일 선택
                    continue;

                int k = i + 1;

                while(k != 4 && tile[k][j].getNumber() == 0) { k++; }   // 현재 타일 제일 아래쪽의 빈공간을 찾음


                if(k == 4)  // 제일 아래에 비어있으면 제일 아래로 타일 이동
                {
                    tile[k-1][j].setNumber(tile[i][j].getNumber());
                    tile[i][j].setNumber(0);
                }
                else    // 아래에 다른 타일이 있으면
                {
                    if(tile[k][j].getNumber() == tile[i][j].getNumber())  // 아래에 있는 타일이랑 숫자가 같아서 조합되는 경우 두 타일을 합친다.
                    {
                        tile[k][j].setNumber(tile[k][j].getNumber()<<1);
                        score.setCurrent(score.getCurrent()+tile[k][j].getNumber());    // 합친거 점수에 반영
                        tile[k][j].setNumber(tile[k][j].getNumber()+1); // 세 개 이상의 타일이 한번에 안합쳐지도록 + 1
                        tile[i][j].setNumber(0);
                    }
                    else    // 서로 다르면 그냥 그 위으로 옮긴다.
                    {
                        int temp = tile[i][j].getNumber();
                        tile[i][j].setNumber(0);
                        tile[k-1][j].setNumber(temp);
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
                if(tile[i][j].getNumber() == 0)  // 빈 타일은 무시하고 바로 다음 타일 선택
                    continue;

                int k = j - 1;

                while(k != -1 && tile[i][k].getNumber() == 0) { k--; }   // 현재 타일 제일 왼쪽의 빈공간을 찾음


                if(k == -1)  // 제일 왼쪽에 비어있으면 제일 왼쪽으로 타일 이동
                {
                    tile[i][k+1].setNumber(tile[i][j].getNumber());
                    tile[i][j].setNumber(0);
                }
                else    // 왼쪽에 다른 타일이 있으면
                {
                    if(tile[i][k].getNumber() == tile[i][j].getNumber())  // 왼쪽에 있는 타일이랑 숫자가 같아서 조합되는 경우 두 타일을 합친다.
                    {
                        tile[i][k].setNumber(tile[i][k].getNumber()<<1);
                        score.setCurrent(score.getCurrent()+tile[i][k].getNumber());    // 합친거 점수에 반영
                        tile[i][k].setNumber(tile[i][k].getNumber()+1); // 세 개 이상의 타일이 한번에 안합쳐지도록 + 1
                        tile[i][j].setNumber(0);
                    }
                    else    // 서로 다르면 그냥 그 옆으로 옮긴다.
                    {
                        int temp = tile[i][j].getNumber();
                        tile[i][j].setNumber(0);
                        tile[i][k+1].setNumber(temp);
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
                if(tile[i][j].getNumber() == 0)  // 빈 타일은 무시하고 바로 다음 타일 선택
                    continue;

                int k = j + 1;

                while(k != 4 && tile[i][k].getNumber() == 0) { k++; }   // 현재 타일 제일 오른쪽의 빈공간을 찾음


                if(k == 4)  // 제일 오른쪽에 비어있으면 제일 오른쪽으로 타일 이동
                {
                    tile[i][k-1].setNumber(tile[i][j].getNumber());
                    tile[i][j].setNumber(0);
                }
                else    // 오른쪽에 다른 타일이 있으면
                {
                    if(tile[i][k].getNumber() == tile[i][j].getNumber())  // 오른쪽에 있는 타일이랑 숫자가 같아서 조합되는 경우 두 타일을 합친다.
                    {
                        tile[i][k].setNumber(tile[i][k].getNumber()<<1);
                        score.setCurrent(score.getCurrent()+tile[i][k].getNumber());    // 합친거 점수에 반영
                        tile[i][k].setNumber(tile[i][k].getNumber()+1); // 세 개 이상의 타일이 한번에 안합쳐지도록 + 1
                        tile[i][j].setNumber(0);
                    }
                    else    // 서로 다르면 그냥 그 옆으로 옮긴다.
                    {
                        int temp = tile[i][j].getNumber();
                        tile[i][j].setNumber(0);
                        tile[i][k-1].setNumber(temp);
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
                if(tile[i][j].getNumber()==0) {
                    return false;
                }
            }
        }

        // 각 타일의 상하좌우가 전부 해당 타일이랑 전부 다른숫자일때,(움직여도 조합이 안될때) 게임오버 확인
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            {
                if(i>0) // 타일의 위쪽과 비교
                {
                    if(tile[i][j].getNumber() == tile[i-1][j].getNumber())
                        return false;
                }
                if(i<3)    // 타일의 아래와 비교
                {
                    if(tile[i][j].getNumber() == tile[i+1][j].getNumber())
                        return false;
                }
                if (j>0)   // 타일의 왼쪽과 비교
                {
                    if (tile[i][j].getNumber() == tile[i][j - 1].getNumber())
                        return false;
                }
                if (j<3)   // 타일의 오른쪽과 비교
                {
                    if(tile[i][j].getNumber()==tile[i][j+1].getNumber())
                        return false;
                }

            }

        return true;
    }

    // 터치 슬라이딩 방향 확인
    public Direction findDirection(float downX, float downY, float upX, float upY)
    {
        Direction direction;

        float difX,difY; // down과 up의 차이

        // 눌렀을 때와 떘을 때 좌표 차이 확인
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

    // restart 버튼 누르면 게임 새로 세팅
    public void restart(View v)
    {
        startGame();
    }

    // undo 버튼 누르면 바로 이전 턴으로 되돌아감
    public void undo(View v)
    {
        //if(!canUndo)    // undo를 할 수 없는 상태면 무시
            //return;

        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                tile[i][j].setNumber(tile[i][j].getPreviousNumber());
            }
        }
        score.setCurrent(score.getPreviousSocre());

        draw();
    }

    // 2의 제곱수의 지수를 구하기위한 log 함수
    static double baseLog(double x, double base) {

        return Math.log10(x) / Math.log10(base);

    }

    // 합쳐진 타일에 1을 더한걸 다시 1을 빼준다.
    void clearDirtyTile()
    {
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
                if(tile[i][j].getNumber() % 2 == 1)
                    tile[i][j].setNumber(tile[i][j].getNumber()-1);
        }
    }

    void saveTile()
    {
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                tile[i][j].setPreviousNumber(tile[i][j].getNumber());
            }
        }
        score.setPreviousSocre(score.getCurrent());
    }

    // 최고기록 갱신
    void updateBestScore()
    {
        if(score.getCurrent()>score.getBest())
            score.setBest(score.getCurrent());
    }

    // 게임오버 창 표시
    void showGameOver()
    {
        DialogGameOver e = DialogGameOver.getInstance();
        e.show(getSupportFragmentManager(),DialogGameOver.TAG_EVENT_DIALOG);
    }

    // 처음 초기화
    public void initTile()
    {
        // 4*4 Button에 매치되는 4*4 tile 배열 동기화

        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                tile[i][j]=new Tiles();
            }
        }

        score.setCurrent(0);

        // 처음 시작 시 타일 두개 추가
        addNewTile();
        addNewTile();
    }

    // 뷰 초기화
    void initView()
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

        ViewCurScore=(Button)findViewById(R.id.currentScore);
        ViewBestScore=(Button)findViewById(R.id.bestScore);

    }
}
