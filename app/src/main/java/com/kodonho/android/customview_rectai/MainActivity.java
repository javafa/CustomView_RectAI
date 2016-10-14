package com.kodonho.android.customview_rectai;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final static int GROUND_LIMIT = 10;
    FrameLayout ground;
    CustomView cv;

    private int groundUnit = 0;
    private int unit = 0;
    private int player_x = 0;
    private int player_y = 0;

    Button btnUp,btnDown,btnLeft,btnRight,btnStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics dp = getResources().getDisplayMetrics();
        groundUnit = dp.widthPixels;
        unit = groundUnit / GROUND_LIMIT;

        player_x = GROUND_LIMIT / 2;
        player_y = GROUND_LIMIT / 2;

        btnUp = (Button) findViewById(R.id.btnUp);
        btnDown = (Button) findViewById(R.id.btnDown);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        btnStart = (Button) findViewById(R.id.btnStart);

        btnUp.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnStart.setOnClickListener(this);

        ground = (FrameLayout) findViewById(R.id.ground);
        cv = new CustomView(this);
        ground.addView(cv);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnUp    : player_y = player_y - 1; break;    // 위로가면 unit 만큼 y좌표 감소
            case R.id.btnDown  : player_y = player_y + 1; break;
            case R.id.btnLeft  : player_x = player_x - 1; break;  // 왼쪽으로 unit만큼 x좌표 감소
            case R.id.btnRight : player_x = player_x + 1; break;
            case R.id.btnStart:
                // start 버튼을 클릭하면 익명 클래스로 thread를 무제한으로 생성한다
                new Enemy(cv).start();
                break;
        }
        cv.invalidate();
    }

    // 적군 thread
    class Enemy extends Thread{
        int x = 0;
        int y = 0;
        int size = unit/2;
        CustomView cv;

        public Enemy(CustomView cv){
            this.cv = cv;
            cv.add(this);
        }
        @Override
        public void run(){
            int distance_x;
            int distance_y;
            while(true){
                // TODO 플레이어가 있는곳으로 나의 좌표를 변경한다
                distance_x = player_x*unit - x;
                distance_y = player_y*unit - y;
                // 플레이어와 나(적군)의 거리차가 + 이면 나의오른쪽에 플레이어가 있으므로 1씩증가
                if(distance_x > 0)
                    x = x + 1;
                else if(distance_x < 0)
                    x = x - 1;
                // 플레이어와 나(적군)의 거리차가 - 이면 나의 아래쪽에 플레이어가 있으므로 1씩증가
                if(distance_y > 0)
                    y = y + 1;
                else if(distance_y < 0)
                    y = y - 1;
                // 나(적군)의 좌표값을 변경한 후에 화면을 다시 그려준다
                cv.postInvalidate();
                try {
                    Thread.sleep(10); // 화면을 그리는 속도를 제한한다
                }catch(Exception e){e.printStackTrace();}
            }
        }
    }

    class CustomView extends View {
        Paint paint = new Paint();
        // 생성될 적군을 담아줄 변수
        ArrayList<Enemy> enemies = new ArrayList<>();
        public void add(Enemy enemy){
            enemies.add(enemy);
        }

        public CustomView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // 운동장 배경 그리기
            paint.setColor(Color.CYAN);
            canvas.drawRect(
                    0, 0, groundUnit, groundUnit, paint);
            // 플레이어 그리기
            paint.setColor(Color.MAGENTA);
            canvas.drawRect(
                    player_x*unit
                    , player_y*unit
                    , player_x*unit + unit
                    , player_y*unit + unit
                    , paint);
            //적군 그리기
            for(Enemy enemy :enemies){
                paint.setColor(Color.BLUE);
                canvas.drawCircle(
                        enemy.x+enemy.size, enemy.y+enemy.size, enemy.size, paint);
            }
        }
    }
}