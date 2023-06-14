package com.example.smt_bunny;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;

import static com.example.smt_bunny.MainActivity.File_Touch;
import static com.example.smt_bunny.MainActivity.accelChart1;
import static com.example.smt_bunny.MainActivity.accelValues1;
import static com.example.smt_bunny.MainActivity.accelValues2;
import static com.example.smt_bunny.MainActivity.accelValues3;
import static com.example.smt_bunny.MainActivity.bunny;
import static com.example.smt_bunny.MainActivity.carrot;
import static com.example.smt_bunny.MainActivity.closeAppBtn;
import static com.example.smt_bunny.MainActivity.debug_mode;
import static com.example.smt_bunny.MainActivity.quit_click_count;
import static com.example.smt_bunny.MainActivity.startTrial;
import static com.example.smt_bunny.MainActivity.touchChart;

public class CanvasView extends View  {
    public Paint mPaint;
    public static Canvas mCanvas;

    private GameState bunnyGame;

    float left=100;
    float top = 700;
    float right = left+20;
    float bottom = top+20;

    public static List yAxisValues = new ArrayList();
    public static LineChartData data = new LineChartData();
    public static List lines = new ArrayList();
    public static Line line = new Line(yAxisValues);

    public static List yAxisValues_accel = new ArrayList();
    public static List lines_accel = new ArrayList();
    public static Line line_accel1 = new Line(yAxisValues_accel);
    public static List yAxisValues_accel2 = new ArrayList();
    public static Line line_accel2 = new Line(yAxisValues_accel2);
    public static List yAxisValues_accel3 = new ArrayList();
    public static Line line_accel3 = new Line(yAxisValues_accel3);
    public static LineChartData data_accel = new LineChartData();

    private float carrotOffsetX;
    private float carrotOffsetY;

    private boolean carrotMove = false;

    //private RectF oval1 = new RectF(100,400,150,420);


    public CanvasView(Context context, GameState bunnyGame) {
        super(context);
        mPaint = new Paint();
        this.bunnyGame = bunnyGame;
    }


    public void drawTouch(float left, float top, float right, float bottom){
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        super.onDraw(mCanvas);
        canvas.drawColor(Color.TRANSPARENT);
        mPaint.setColor(Color.BLACK);
//        mPaint.setStyle(Style.STROKE);
        mPaint.setAntiAlias(true);
        if(debug_mode){canvas.drawOval(left, top, right, bottom, mPaint);}
    }

    public boolean onTouchEvent(MotionEvent event){
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                quit_click_count = 0;
                closeAppBtn.setTextColor(Color.TRANSPARENT);

                carrotOffsetX = event.getRawX() - carrot.getX();
                carrotOffsetY = event.getRawY() - carrot.getY();
//                Log.w("thing",String.valueOf(carrotOffsetX));
//                Log.w("width",String.valueOf(bunny.getWidth()));
                carrotMove = carrotOffsetX >=0 & carrotOffsetX <= carrot.getWidth() & carrotOffsetY >=0 & carrotOffsetY <= carrot.getHeight();

                right = left+event.getTouchMajor();
                bottom = top+event.getTouchMinor();
                carrot.invalidate();

                yAxisValues.add(new PointValue(1,1));
                line.setHasPoints(false);
                lines.add(line);
                data.setLines(lines);
                touchChart.setLineChartData(data);

                yAxisValues_accel.add(new PointValue(1, (float) .001));
                line_accel1.setHasPoints(false);
                line_accel1.setColor(Color.RED);
                lines_accel.add(line_accel1);
                yAxisValues_accel2.add(new PointValue(1, (float) .001));
                line_accel2.setHasPoints(false);
                line_accel2.setColor(Color.BLUE);
                lines_accel.add(line_accel2);
                yAxisValues_accel3.add(new PointValue(1, (float) .001));
                line_accel3.setHasPoints(false);
                line_accel3.setColor(Color.YELLOW);
                lines_accel.add(line_accel3);
                data_accel.setLines(lines_accel);
                accelChart1.setLineChartData(data);

                break;

            case MotionEvent.ACTION_MOVE:

                if(carrotMove){
                    carrot.setX(event.getRawX()-carrotOffsetX);
                    carrot.setY(event.getRawY()-carrotOffsetY);
                }

                right = left+event.getTouchMajor()*10;
                bottom = top+event.getTouchMinor()*10;

                float area = (float) (event.getTouchMajor()/2 * event.getTouchMinor()/2 * Math.PI);
                yAxisValues.add(new PointValue(yAxisValues.size(), area));
                touchChart.setLineChartData(data);

                yAxisValues_accel.add(new PointValue(yAxisValues_accel.size(), accelValues1*10));
                yAxisValues_accel2.add(new PointValue(yAxisValues_accel2.size(), accelValues2*10));
                yAxisValues_accel3.add(new PointValue(yAxisValues_accel3.size(), (float) ((accelValues3-9.86)*10)));
                accelChart1.setLineChartData(data_accel);
                carrot.invalidate();
                break;

            case MotionEvent.ACTION_UP:
                if(carrot.getX() < 0){
                    carrot.setX(0); //Carrot is too far left, bring back
                } else if (carrot.getX() + carrot.getWidth() > screenWidth){
                    carrot.setX(screenWidth - carrot.getWidth()); //too far right, bring back
                }
                if(carrot.getY() < 0){
                    carrot.setY(0); //Carrot is too far up, bring back
                } else if (carrot.getY() + carrot.getHeight() > screenHeight){
                    carrot.setY(screenHeight - carrot.getHeight()); //too far down, bring back
                }

                float carrot_center_x = carrot.getX()+(carrot.getWidth()/2);
                float carrot_center_y = carrot.getY()+(carrot.getHeight()/2);
                float bunny_center_x = bunny.getX()+(bunny.getWidth()/2);
                float bunny_center_y = bunny.getY()+(bunny.getHeight()/2);
                boolean x_overlap = Math.abs(carrot_center_x - bunny_center_x) <= (bunny.getWidth()/2+carrot.getWidth()/2)*.5;
                boolean y_overlap = Math.abs(carrot_center_y - bunny_center_y) <= (bunny.getHeight()/2+carrot.getHeight()/2)*.5;

                if( x_overlap & y_overlap){
                    bunnyGame.incrementTrial();
                    bunnyGame.nextTrial();
                }
                right = left+20;
                bottom = top+20;
                carrot.invalidate();
                break;
        }
        saveSamples(event);
        return true;

    }
    void saveSamples(MotionEvent ev) {
        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        String mydat = "";
        for (int h = 0; h < historySize; h++) {
            long htime = ev.getHistoricalEventTime(h);
            for (int p = 0; p < pointerCount; p++) {
                mydat = mydat+htime + ", " + ev.getPointerId(p) +", "+ ev.getHistoricalX(p,h) + ", " + ev.getHistoricalY(p,h) + ", " + ev.getHistoricalTouchMajor(p,h) + ", " + ev.getHistoricalTouchMinor(p,h) + ", " + ev.getHistoricalTouchMajor(p,h)/2 * ev.getHistoricalTouchMinor(p,h)/2 * Math.PI + ", " + ev.getHistoricalPressure(p,h) + "\n";
            }
        }
        long evtime = ev.getEventTime();
        for (int p = 0; p < pointerCount; p++) {
            mydat = mydat+evtime + ", " + ev.getPointerId(p) +", "+ ev.getX(p) + ", " + ev.getY(p) + ", " + ev.getTouchMajor(p) + ", " + ev.getTouchMinor(p) + ", " + ev.getTouchMajor(p)/2 * ev.getTouchMinor(p)/2 * Math.PI + ", " + ev.getPressure(p) + "\n";
        }
        WriteToFile(File_Touch,mydat);
    }

    private void WriteToFile(File WriteFile, String data) {
        try {
            FileOutputStream fos = new FileOutputStream(WriteFile,true);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
