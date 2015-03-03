package com.sherchen.heartrate.views;

import android.R.anim;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import com.sherchen.heartrate.views.animator.LinearEvaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The description of use:
 * <br />
 * Created time:2014/6/13 16:01
 * Created by Dave
 */
public class WaveView extends View implements LinearEvaluator.EvaluatorListener, Animator.AnimatorListener{

    private static final String LOG_TAG = WaveView.class.getSimpleName();
    private static final boolean DEBUG = true;
    public void debug(String msg){
        if(DEBUG) android.util.Log.v(LOG_TAG, msg);
    }

    private static final long WAIT_TIME_TRANS_PATH = 400;//ms

    private static final int WAVE_PEAK_LEFT_HEIGHT = 1;

    //The max peek that will be shown
    private static final int MAX_VALID_ENTRIES = 10;

    private static final int WIDTH_TOTAL_WEIGHTS = 8;
    private static final int HEIGHT_TOTAL_WEIGHTS = 9;

    private float m_StartX, m_StartY;
    
    private static final String END_ANIMATION_NAME = "END_ANIM";
    private static final String OTHER_ANIMATION_NAME = "OTHER_ANIM";
    
    //The peak point, all of numbers are weight, such as (4/total_width_weight)*width, (0/total_height_weight)*height
    //(8,7) to (6,0)
    
    //need to draw line by other software
    
    //draw the wave peak when the valid heartrate is gotten.
    //LLL 0  1 2 3 4 5 6 7 8 9
    //0                    (7,0)[2]
    //1				  				
    //2							
    //3								
    //4							
    //5							
    //6			   				
    //7	  (0, 6)[5]	 (4,6)[4]	
    //8	
    //9				          (8,6)[1]  	 
    //10				(5,9)[3]
				
    
    private static final int[][] m_WavePeak_Weight = new int[][]{
            new int[]{4, 0, 6, 6},
            new int[]{5, 4, 9, 6},
            new int[]{7, 5, 0, 9},
            new int[]{8, 7, 6, 0}
    };

    //draw the line when the heartrate is not gotten.
    
    //LLL 0  1 2 3 4 5 6 7 8 9
    //0                       
    //1				  				
    //2			
    //3			
    //4			
    //5			
    //6			   
    //7	 (0, 6)[2]<<<<<<<<<<<<(8, 6)[1]	
    //8	
    //9				          	 
    //10				
    private static final int[] m_WaveLine_Weight = new int[]{WIDTH_TOTAL_WEIGHTS, 0, 6, 6};


    private List<float[]> m_PeakObjects;
    private float[] m_LineObjects;

    private List<Integer> m_AllEntries;

    private CountDownTimer m_Timer;

    private long m_StartTime;
    private int m_Average;

    private Paint m_PeakPaint;

    private Random m_EmulateRan;
    private int[] m_EmulateHeartRate = new int[]{
            0, 49, 0,
            55, 0, 68,
            0, 66, 0,
            75
    };
//    private int[] m_EmulateHeartRate = new int[]{
//            35, 49, 46,
//            55, 88, 68,
//            35, 66, 67,
//            75
//    };

//    private int[] m_EmulateHeartRate = new int[]{
//            0, 0, 0,
//            0, 0, 0,
//            0, 0, 0,
//            0
//    };

    private Path m_WavePath = new Path();
    private Matrix m_Matrix;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setValues();
    }

    private void setValues(){
        m_AllEntries = new ArrayList<Integer>();
        m_EmulateRan = new Random();

        m_PeakPaint = new Paint();
        m_PeakPaint.setColor(Color.WHITE);
        m_PeakPaint.setAntiAlias(true);
        m_PeakPaint.setStyle(Style.STROKE);
        m_PeakPaint.setStrokeWidth(2.f);
    }

    public void startPaint(final long duration, final long interval){
        initWave();
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
                m_AllEntries.add(0, value);
                if(interval > WAIT_TIME_TRANS_PATH){
                    startWave(value, interval - WAIT_TIME_TRANS_PATH);
                }else{
                    startWave(value, 0);
                }
            }

            @Override
            public void onFinish() {
                m_Average = calculateAverage();
                if(m_TrickListener != null){
                    m_TrickListener.onPainterFinished();
                }
            }
        };
        m_Timer.start();
    }

    private void initWave(){
        m_WavePath.reset();
        m_WavePath.moveTo(m_StartX, m_StartY);
        m_AllEntries.clear();
        invalidate();
    }

    private void startWave(int value, long duration){
        doAnimation(value, duration);
    }

    private void doAnimation(int value, long duration){
        if(value == 0){
            Animator animator = getLineAnimator(m_LineObjects, duration);
            animator.addListener(this);
            animator.start();
        }else{
            AnimatorSet set = new AnimatorSet();
            List<Animator> peakAnim = getPeakAnimator(duration);
            set.playSequentially(peakAnim);
            set.start();
        }
    }

    private Animator getLineAnimator(float[] evaluatorObjects, long duration){
        float startX = evaluatorObjects[0];
        float startY = evaluatorObjects[1];
        float endX = evaluatorObjects[2];
        float endY = evaluatorObjects[3];
        LinearEvaluator evaluator = new LinearEvaluator(startY, endY);
        evaluator.addListener(this);
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, startX, endX);
        animator.setDuration((long) (duration * evaluatorObjects[4]));
        return animator;
    }

    private List<Animator> getPeakAnimator(long duration){
        int size = m_PeakObjects.size();
        List<Animator> peakAnimators = new ArrayList<Animator>();
        for(int i=0;i<size;i++){
        	//debug("The index is " + i + " & the fraction is " + peakObject[4]);
            Animator animator = getLineAnimator(m_PeakObjects.get(i), duration);
            peakAnimators.add(animator);
            if(i == size - 1){
            	animator.addListener(this);
            }
        }
        return peakAnimators;
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

    public void stopPaint(){
        //ignore this now
    	if(m_Timer != null){
    		m_Timer.cancel();
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
    
    private float m_CellWidth;

    private void doCalculate(int wWidth, int wHeight){
        if(m_PeakObjects == null){//Just testing condition.
            final int validWidth = wWidth;
            final int validHeight = wHeight;
            float cellWidth = validWidth / MAX_VALID_ENTRIES;
            float cellHeight = validHeight;
            m_CellWidth = cellWidth;
            m_Matrix = new Matrix();
            m_Matrix.setTranslate(cellWidth, 0);
            caculateByWeights(cellWidth, cellHeight - WAVE_PEAK_LEFT_HEIGHT);
        }
    }

    private float getFractionOfWeight(int weight, int totalWeights){
        return (float) weight / totalWeights;
    }

    private void caculateByWeights(float cellWidth, float cellHeight){
        m_PeakObjects = new ArrayList<float[]>();
        int size = m_WavePeak_Weight.length;
        float startX, startY, endX = 0f, endY, fractionFromX, fractionToX;
        int weightFromX, weightToX, weightFromY, weightToY;
        for(int i= size - 1;i>=0;i--){
            weightFromX = m_WavePeak_Weight[i][0];
            weightToX =   m_WavePeak_Weight[i][1];
            weightFromY = m_WavePeak_Weight[i][2];
            weightToY   = m_WavePeak_Weight[i][3];
            fractionFromX = getFractionOfWeight(weightFromX, WIDTH_TOTAL_WEIGHTS);
            fractionToX   = getFractionOfWeight(weightToX, WIDTH_TOTAL_WEIGHTS);
            startX = cellWidth * fractionFromX;
            endX   = cellWidth * fractionToX;
            startY = cellHeight * getFractionOfWeight(weightFromY, HEIGHT_TOTAL_WEIGHTS);
            endY   = cellHeight * getFractionOfWeight(weightToY, HEIGHT_TOTAL_WEIGHTS);

            if(i == size - 1){
                m_StartX = startX;
                m_StartY = startY;
            }

            m_PeakObjects.add(new float[]{startX, startY, endX, endY, fractionFromX - fractionToX});
            debug(
                    "the index is " + i + "\n" + 
                            "the weightFromX is " + weightFromX + "\n" + 
                            "the weightToY is " + weightToX + "\n" + 
                            "the weightFromY is " + weightFromY + "\n" + 
                            "the weightToY is " + weightToY + "\n" + 
                            "the startX is " + startX + "\n" + 
                            "the startY is " + startY + "\n" + 
                            "the endX is " + endX + "\n" + 
                            "the fractionFromX is " + fractionFromX + "\n" + 
                            "the fractionToX is " + fractionToX 
                            
            );
        }
        weightFromX = m_WaveLine_Weight[0];
        weightToX = m_WaveLine_Weight[1];
        weightFromY = m_WaveLine_Weight[2];
        weightToY = m_WaveLine_Weight[3];
        fractionFromX = getFractionOfWeight(weightFromX, WIDTH_TOTAL_WEIGHTS);
        fractionToX = getFractionOfWeight(weightToX, WIDTH_TOTAL_WEIGHTS);
        startX = cellWidth * weightFromX;
        endX = cellWidth * weightToX;
        startY = cellHeight * getFractionOfWeight(weightFromY, HEIGHT_TOTAL_WEIGHTS);
        endY = cellHeight * getFractionOfWeight(weightToY, HEIGHT_TOTAL_WEIGHTS);
        m_LineObjects = new float[]{startX, startY, endX, endY, fractionFromX - fractionToX};
        debug(
                "the line Object " + "\n" + 
                        "the weightFromX is " + weightFromX + "\n" + 
                        "the weightToX is " + weightToX + "\n" + 
                        "the weightFromY is " + weightFromY + "\n" + 
                        "the weightToY is " + weightToY + "\n" + 
                        "the startX is " + startX + "\n" + 
                        "the startY is " + startY + "\n" + 
                        "the endX is " + endX + "\n" + 
                        "the endY is " + endY + "\n" + 
                        "the fractionFromX is " + fractionFromX + "\n" + 
                        "the fractionToX is " + fractionToX + "\n"
        );
    }

    private boolean isAnimationFinishing = false;
    
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(m_WavePath, m_PeakPaint);
    }

    @Override
    public void onEvalutor(final float x, final float y) {
		m_WavePath.lineTo(x, y);
		if(isAnimationFinishing){
//    		m_WavePath.transform(m_Matrix);
			m_WavePath.offset(m_CellWidth, 0);
    		isAnimationFinishing = false;
    	}
		invalidate();
    }

    
    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
		isAnimationFinishing = true;
		debug("The duration of animation is " + animation.getDuration());
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
