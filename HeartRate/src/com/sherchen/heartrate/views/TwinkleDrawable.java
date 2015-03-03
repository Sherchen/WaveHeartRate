package com.sherchen.heartrate.views;

import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.SparseArray;
import android.widget.ImageView;

/**
 * The description of use:
 * <br />
 * Created time:2014/6/17 18:37
 * Created by Dave
 */
public class TwinkleDrawable
//        extends LevelListDrawable
{

private static final String LOG_TAG = TwinkleDrawable.class.getSimpleName();
private static final boolean DEBUG = true;

    private static final int TWINKLE_INTERVAL = 200;//ms

    private static final int LEVEL_START = 0;
    private int m_LastLevel;
    private int m_TwinkleLevel;
    private int m_DefaultLevel;

    private CountDownTimer m_Timer;
    private SparseArray<Drawable> m_List;

    private ImageView m_TwinkleView;


    public TwinkleDrawable(ImageView twinkleView) {
        super();
        m_TwinkleView = twinkleView;
        m_LastLevel = LEVEL_START;
        m_List = new SparseArray<Drawable>();
    }



    public void addDrawable(Drawable drawable, boolean defaultSet){
        m_List.put(m_LastLevel, drawable);
        setImage(m_LastLevel);
        if(defaultSet){
            m_DefaultLevel = m_LastLevel;
        }
        m_LastLevel++;
    }

    public void startTwinkle(){
        m_TwinkleLevel = LEVEL_START;
        setImage(m_TwinkleLevel);
        startTimer();
    }

    private void setImage(int level){
        m_TwinkleView.setImageDrawable(m_List.get(level));
    }

    private void stopTimer(){
        if(m_Timer != null){
            m_Timer.cancel();
            m_Timer = null;
        }
    }

    private void startTimer(){
        m_Timer = new CountDownTimer(Integer.MAX_VALUE, TWINKLE_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                m_TwinkleLevel += 1;
                if(m_TwinkleLevel >= m_LastLevel){
                    m_TwinkleLevel = LEVEL_START;
                }
                log("The twinkle level is " + m_TwinkleLevel);
                setImage(m_TwinkleLevel);
            }

            @Override
            public void onFinish() {

            }
        };
        m_Timer.start();
    }

    public void stopTwinkle(){
        stopTimer();
        setImage(m_DefaultLevel);
    }

    public void log(String msg){
        if(DEBUG)android.util.Log.v(LOG_TAG, msg);
    }

    public void recycle(){
        stopTimer();
        if(m_LastLevel > 0){
            for(int i=0;i<m_LastLevel;i++){
                m_List.get(i).setCallback(null);
            }
        }
    }
}
