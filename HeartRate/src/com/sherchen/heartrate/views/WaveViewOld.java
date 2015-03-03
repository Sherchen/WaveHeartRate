package com.sherchen.heartrate.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The description of use:
 * <br />
 * Created time:2014/6/13 16:01
 * Created by Dave
 */
public class WaveViewOld extends View {
    private static final int WAVE_PEAK_LEFT_HEIGHT = 1;

    private static final int WAVE_FIRST_TROUGH_HEIGHT_WEIGHT = 2;
    private static final int WAVE_SECOND_TROUGH_HEIGHT_WEIGHT = 1;
    private static final int WAVE_PEAK_HEIGHT_WEIGHT = 6;

    private static final int WAVE_HEADER_EXTRA_WEIGHT = 2;
    private static final int WAVE_FIRST_TROUGH_WIDTH_WEIGHT = 2;
    private static final int WAVE_SECOND_TROUGHT_WIDTH_WEIGHT = 2;
    private static final int WAVE_PEAK_WIDTH_WEIGHT = 10;

    private static final int WAVE_NORMAL_MIN_VALUE = 50;
    private static final int WAVE_NORMAL_MAX_VALUE = 80;
    private static final int MAX_VALID_ENTRIES = 6;

    private float m_WaveHeaderLineWidth;
    private float m_WaveFooterLineWidth;
//    private float m_WaveHeaderTroughHeight;
//    private float m_WaveFooterTroughHeight;
//    private float m_WavePeakHeight;
    private float m_WaveHeaderExtraWidth;
    private float m_WavePeakHaflWidth;
    private float m_CellWidth;
    private float m_CellHeight;
    private float m_NormalLineY;
    private float m_SecondTroughY;
    private float m_WeightWidth;
    private float m_WeightHeight;


    private List<Integer> m_AllEntries;
    private List<Integer> m_ValidEntries;
    private Path m_Path;

    private CountDownTimer m_Timer;

    private long m_StartTime;
    private int m_Average;
    
    private Paint m_NormalPaint;
    private Paint m_PeakPaint;

    private Random m_EmulateRan;
    private int[] m_EmulateHeartRate = new int[]{
         0, 49, 0,
         55, 0, 68,
         0, 66, 0,
         75
    };

//    private int[] m_EmulateHeartRate = new int[]{
//            0, 0, 0,
//            0, 0, 0,
//            0, 0, 0,
//            0
//    };

    public WaveViewOld(Context context, AttributeSet attrs) {
        super(context, attrs);
        setValues();
    }

    private void setValues(){
        m_AllEntries = new ArrayList<Integer>();
        m_ValidEntries = new ArrayList<Integer>();
        m_Path = new Path();
        m_EmulateRan = new Random();
        
        m_NormalPaint = new Paint();
        m_NormalPaint.setColor(Color.WHITE);
        m_NormalPaint.setAntiAlias(true);
        m_NormalPaint.setStyle(Style.STROKE);
        m_NormalPaint.setStrokeWidth(2.f);
        
        m_PeakPaint = new Paint();
        m_PeakPaint.setColor(Color.WHITE);
        m_PeakPaint.setAntiAlias(true);
        m_PeakPaint.setStyle(Style.STROKE);
        m_PeakPaint.setStrokeWidth(2.f);
    }

    public void startPaint(int duration, int interval){
        m_StartTime = System.currentTimeMillis();
        m_Timer = new CountDownTimer(duration, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
//            	disable hardware test
//                int value = 0;
//                if(m_TrickListener != null){
//                    value = m_TrickListener.getPainterValue();
//                }
            	
//            	emulate test
                int value = getEmulateHeartRate();
                if(m_TrickListener != null){
                   m_TrickListener.onPainterTrick(value);
                }
                addEntry(value);
                invalidate();
            }

            @Override
            public void onFinish() {
                m_Average = calculateAverage();
                clearOld();
                invalidate();
                if(m_TrickListener != null){
                    m_TrickListener.onPainterFinished();
                }
            }
        };
        m_Timer.start();
    }

    public interface OnPainterTrickListener{

        int getPainterValue();

        void onPainterTrick(int value);

        void onPainterFinished();
    }

    private OnPainterTrickListener m_TrickListener;

    public void setOnPainterTrickListener(OnPainterTrickListener listener){
        m_TrickListener = listener;
    }

    private int getEmulateHeartRate(){
        return m_EmulateHeartRate[m_EmulateRan.nextInt(10)];
    }

    private void stopPaint(){
        //ignore this now
    }

    private void clearOld(){
        m_AllEntries.clear();
        m_ValidEntries.clear();
    }

    private void addEntry(int entry){
        m_AllEntries.add(0, entry);
        addValidEntry(entry);
    }

    private void addValidEntry(int entry){
        m_ValidEntries.add(0, entry);
        int size = m_ValidEntries.size();
        if(size > MAX_VALID_ENTRIES){
            m_ValidEntries.remove(size - 1);
        }
    }

    private int calculateAverage(){
        int size = m_AllEntries.size();
        int validSize = 0;
        int validTotal = 0;
        for(int i=0;i<size;i++){
            if(m_AllEntries.get(i) != 0){
                validSize++;
                validTotal += m_AllEntries.get(i);
            }
        }
        if(validSize != 0){
            return validTotal / validSize;
        }else{
            return -1;
        }
    }

    public int getAverage(){
        return m_Average;
    }

    public long getMeasureStartTime(){
        return m_StartTime;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        doCalculate(getWidth(), getHeight());
    }

    private void doCalculate(int wWidth, int wHeight){
        if(m_WaveHeaderLineWidth == 0){//Just testing condition.
            int widthWeight = WAVE_FIRST_TROUGH_WIDTH_WEIGHT + WAVE_SECOND_TROUGHT_WIDTH_WEIGHT + WAVE_PEAK_WIDTH_WEIGHT + WAVE_HEADER_EXTRA_WEIGHT;
            int heightWeight = WAVE_FIRST_TROUGH_HEIGHT_WEIGHT + WAVE_PEAK_HEIGHT_WEIGHT;

            final int validWidth = wWidth;
            final int validHeight = wHeight;
            m_CellWidth = validWidth / MAX_VALID_ENTRIES;
            m_CellHeight = validHeight - WAVE_PEAK_LEFT_HEIGHT;
            m_WeightWidth =  m_CellWidth / widthWeight;
            m_WeightHeight = m_CellHeight / heightWeight;

            m_WaveHeaderLineWidth = m_WeightWidth * WAVE_FIRST_TROUGH_WIDTH_WEIGHT;
            m_WaveFooterLineWidth = m_WeightWidth * WAVE_SECOND_TROUGHT_WIDTH_WEIGHT;
            m_WavePeakHaflWidth = m_WeightHeight * WAVE_PEAK_HEIGHT_WEIGHT / 2;
            m_NormalLineY = m_CellHeight - m_WeightHeight * WAVE_FIRST_TROUGH_HEIGHT_WEIGHT;
            m_SecondTroughY = m_CellHeight - m_WeightHeight * WAVE_SECOND_TROUGH_HEIGHT_WEIGHT;
            m_WaveHeaderExtraWidth = m_WeightWidth * WAVE_HEADER_EXTRA_WEIGHT;
        }
    }

    private boolean isDelayRunning;
    private static final Object mLock = new Object();

    private void drawWaves(final Canvas canvas){
        if(isDelayRunning){
            synchronized (mLock){
                //Ignore the code
            }
        }
        int size = m_ValidEntries.size();
        float[] firstPoints = new float[]{0.0f, m_NormalLineY};
        int value;
//        Path tmpPath = null;
        List<Path> firstTmpPath = null;
        for(int i=0;i<size;i++){
            value = m_ValidEntries.get(i);
            if(value != 0 && i == 0){
                firstTmpPath = new ArrayList<Path>();
                firstPoints = drawWave(canvas, value, firstPoints, firstTmpPath);
            }else{
                firstPoints = drawWave(canvas, value, firstPoints, null);
            }
        }
        if(firstTmpPath != null){
            final List<Path> tmp = firstTmpPath;
//            isDelayRunning = true;

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mLock){
                            drawSpecialFirstPeak(canvas, tmp);
                            isDelayRunning = false;
                        }
                    }
                }, 2);
        }
    }

    private float[] drawWave(Canvas canvas, int value, float[] firstPoints, List<Path> out){
        if(value == 0){
            return drawWaveNormalLine(canvas, firstPoints);
        }else{
            if(out != null){
                return drawWavePeak(canvas, firstPoints, value, out);
            }else{
                return drawWavePeak(canvas, firstPoints, value, null);
            }
        }
    }

    private float[] drawWaveNormalLine(Canvas canvas,float[] firstPoints){
        float x = firstPoints[0];
        float y = firstPoints[1];
        float lastX = x + m_CellWidth;
        float lastY = y;
        canvas.drawLine(x, y, lastX, lastY, m_NormalPaint);
        return new float[]{lastX, lastY};
    }

    private float[] drawWavePeak(Canvas canvas, float[] firstPoints, int value, List<Path> out){
        final float peakHeight = getPeakHeight(value);
        float firstX = firstPoints[0];
        float firstY = firstPoints[1];
        m_Path.reset();
        m_Path.moveTo(firstX, firstY);
        float x = firstX + m_WaveHeaderExtraWidth;
        float y = firstY;
        m_Path.lineTo(x, firstY);//draw extra line
        if(out != null){
            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(firstX, firstY);
//            out.add(new float[]{x, y, firstX, firstY});
            out.add(path);
            firstX = x;
            firstY = y;
        }
        x = x + m_WaveHeaderLineWidth;
        y = peakHeight;// draw first trough line
        m_Path.lineTo(x, y);
        if(out != null){
            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(firstX, firstY);
//            out.add(new float[]{x, y, firstX, firstY});
            out.add(path);
            firstX = x;
            firstY = y;
        }
        x = x + m_WavePeakHaflWidth;
        y = m_CellHeight - peakHeight;
        m_Path.lineTo(x, y);//draw first peak line
        if(out != null){
            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(firstX, firstY);
//            out.add(new float[]{x, y, firstX, firstY});
            out.add(path);
            firstX = x;
            firstY = y;
        }
        x = x + m_WavePeakHaflWidth;
        y = m_SecondTroughY;
        m_Path.lineTo(x, y);// draw second peak line
        if(out != null){
            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(firstX, firstY);
//            out.add(new float[]{x, y, firstX, firstY});
            out.add(path);
            firstX = x;
            firstY = y;
        }
        float lastX = x + m_WaveFooterLineWidth;
        float lastY = m_NormalLineY;
        m_Path.lineTo(lastX, lastY);// draw second trough line
        if(out != null){
            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(firstX, firstY);
//            out.add(new float[]{x, y, firstX, firstY});
            out.add(path);
        }
        if(out == null){
            canvas.drawPath(m_Path, m_PeakPaint);
        }else{
            Collections.reverse(out);
        }
        return new float[]{lastX, lastY};
    }

    //Todo draw the first peak from right to left, and then it will make an animtion, but now it's not finished.
    private void drawSpecialFirstPeak(Canvas canvas, List<Path> path){
        int size = path.size();
        for(int i =0;i<size;i++) {
//            canvas.drawLines(path.get(i), m_PeakPaint);
            canvas.drawPath(path.get(i), m_PeakPaint);
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
////                e.printStackTrace();
//                //ignore the exception handler
//            }
        }
    }

    private float getPeakHeight(int value){
        if(value < WAVE_NORMAL_MIN_VALUE){
            return m_CellHeight - WAVE_PEAK_LEFT_HEIGHT;
        }else if(value <= WAVE_NORMAL_MAX_VALUE){
            return m_CellHeight;
        }else{
            return m_CellHeight + WAVE_PEAK_LEFT_HEIGHT;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWaves(canvas);
    }
}
