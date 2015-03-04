package com.sherchen.heartrate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sherchen.heartrate.R;
import com.sherchen.heartrate.control.MeasureControl;
import com.sherchen.heartrate.control.util.CommonUtil;
import com.sherchen.heartrate.views.ProgressWheelFitButton;
import com.sherchen.heartrate.views.TwinkleDrawable;
import com.sherchen.heartrate.views.WaveView;
import com.todddavies.components.progressbar.ProgressWheel;


//The WaveView is bad ,and not smooth, so the mark here is stimulate me to make a good performance
public class Measure extends Activity implements View.OnClickListener {
//	disable hardware test
//    private static final int MEASURE_DURATION = 120;//s
    private static final float MEASURE_DURATION = 120f;//s
    private static final float MEASURE_INTERVAL = 1;//s
    private static final int ONE_SECOND = 1000;//ms

    private static final int MEASURE_STATE_START = 0;
    private static final int MEASURE_STATE_SAVE = 1;
    private static final int MEASURE_STATE_FAIL = 2;
    private static final int MEASURE_STATE_ING = 3;
    private int m_MeasureState = MEASURE_STATE_START;

    private ProgressWheel m_ProgressWheel;
    private ImageView m_IvSettings;
    private ProgressWheelFitButton m_BtnToggle;
    private WaveView m_WaveView;
    private ImageView m_IvLine;
    private ImageView m_IvHeart;
    private TextView m_TvLabel;
    private TextView m_TvDoneTime;
    private TwinkleDrawable m_HeartDrawable;

    private MeasureControl m_Control;

    private boolean isMeasureSuccess = true;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(m_HeartDrawable != null){
           m_HeartDrawable.recycle();
        }
        if(m_Control != null){
            m_Control.onDestroy();
        }
        
        if(m_WaveView != null){
        	m_WaveView.stopPaint();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeUiToInitial();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measure);
        setViews();
        setValues();
        setListeners();
    }

    private void setViews(){
        m_IvSettings    = (ImageView) findViewById(R.id.iv_history);
        m_ProgressWheel = (ProgressWheel) findViewById(R.id.pw_heartrate);
        m_BtnToggle     = (ProgressWheelFitButton) findViewById(R.id.btn_toggle);
        m_WaveView      = (WaveView) findViewById(R.id.wv_start);
        m_IvLine        = (ImageView) findViewById(R.id.iv_line_start);
        m_IvHeart       = (ImageView)  findViewById(R.id.iv_heart_measure);
        m_TvLabel       = (TextView)  findViewById(R.id.tv_data_measure);
        m_TvDoneTime    = (TextView)  findViewById(R.id.tv_done_time_measure);
    }

    private void setValues(){
        m_Control = new MeasureControl(this);
        m_ProgressWheel.setMax((int)(MEASURE_DURATION / MEASURE_INTERVAL));
        m_HeartDrawable = new TwinkleDrawable(m_IvHeart);
        m_HeartDrawable.addDrawable(getResources().getDrawable(R.drawable.ic_heart_big), true);
        m_HeartDrawable.addDrawable(getResources().getDrawable(R.drawable.ic_heart_small), false);
    }

    private void setListeners(){
        m_IvSettings.setOnClickListener(this);
        m_BtnToggle.setOnClickListener(this);
        m_ProgressWheel.setOnSizeChangedListener(new ProgressWheel.SizeChangedListener() {
            @Override
            public void onSizeChanged(ProgressWheel wheel) {
                m_BtnToggle.clip(wheel.getWidth(), wheel.getHeight(), wheel.getRimWidth());
            }
        });
        m_WaveView.setOnPainterTrickListener(new WaveView.OnPainterTrickListener(){
            @Override
            public int getPainterValue() {
                return -1;
            }

            @Override
            public void onPainterTrick(int value) {
                m_ProgressWheel.incrementProgress();
                if(value != 0){// if no value stay same.
                	changeUiOnLabel(true, String.format("%03d", value));
                }
            }

            @Override
            public void onPainterFinished() {
                int average = m_WaveView.getAverage();
                onMeasureFinished(average);
            }
        });
    }

    public void onMeasureFinished(int average){
        m_HeartDrawable.stopTwinkle();
        if(average != -1){
            changeUiOnLabel(true, String.format("%03d", average));
            changeUiOnSuccessFinishMeasure();
        }else{
            changeUiOnLabel(false, getString(R.string.measure_rate_result_error));
            changeUiOnFailFinishMeasure();
        }
    }

    public void onMeasureStart(){
        m_HeartDrawable.startTwinkle();
        changeUiOnStartMeasure();
        m_WaveView.startPaint((long) (MEASURE_DURATION * ONE_SECOND), (long) (MEASURE_INTERVAL * ONE_SECOND));
    }

    private void changeUiOnLabel(boolean success, String text){
            m_TvLabel.setText(text);
    }

    private void changeUiToInitial(){
        changeUiOnLabel(true, getString(R.string.measure_rate_default));
        m_ProgressWheel.resetCount(false);
        m_BtnToggle.setVisibility(View.VISIBLE);
        m_IvLine.setVisibility(View.VISIBLE);
        m_WaveView.setVisibility(View.INVISIBLE);
        m_BtnToggle.setText(R.string.measure_btn_start);
        m_TvDoneTime.setVisibility(View.GONE);

        m_MeasureState = MEASURE_STATE_START;
    }

    private void changeUiOnStartMeasure(){
        changeUiOnLabel(true, getString(R.string.measure_rate_default));
        m_ProgressWheel.resetCount(false);
        m_BtnToggle.setVisibility(View.INVISIBLE);
        m_IvLine.setVisibility(View.INVISIBLE);
        m_WaveView.setVisibility(View.VISIBLE);
        m_TvDoneTime.setVisibility(View.GONE);

        m_MeasureState = MEASURE_STATE_ING;
    }

    private void changeUiOnSuccessFinishMeasure(){
        m_ProgressWheel.resetCount(false);
        m_BtnToggle.setVisibility(View.VISIBLE);
        m_IvLine.setVisibility(View.VISIBLE);
        m_WaveView.setVisibility(View.INVISIBLE);
        m_BtnToggle.setText(R.string.measure_btn_save);
        m_TvDoneTime.setVisibility(View.VISIBLE);
        m_TvDoneTime.setText(CommonUtil.getReadableDateTime(System.currentTimeMillis()));

        m_MeasureState = MEASURE_STATE_SAVE;
    }

    private void changeUiOnFailFinishMeasure(){
        m_ProgressWheel.resetCount(false);
        m_BtnToggle.setVisibility(View.VISIBLE);
        m_IvLine.setVisibility(View.VISIBLE);
        m_WaveView.setVisibility(View.INVISIBLE);
        m_BtnToggle.setText(R.string.measure_btn_fail);
        m_TvDoneTime.setVisibility(View.GONE);

        m_MeasureState = MEASURE_STATE_FAIL;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_history:
                viewHistory();
                break;
            case R.id.btn_toggle:
                switch (m_MeasureState){
                    case MEASURE_STATE_START:
                        onBtnClickStart();
                        break;
                    case MEASURE_STATE_SAVE:
                        onBtnClickSave();
                        break;
                    case MEASURE_STATE_FAIL:
                        onBtnClickFail();
                        break;
                }
                break;
        }
    }

    private void viewHistory(){
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }

    private void onBtnClickStart(){
        onMeasureStart();
    }

    private void onBtnClickSave(){
        m_Control.saveMeasure(m_WaveView.getMeasureStartTime(), m_WaveView.getAverage());
        viewHistory();
    }

    private void onBtnClickFail(){
        onBtnClickStart();
    }


    @Override
    public void onBackPressed() {
        if(m_MeasureState == MEASURE_STATE_SAVE || m_MeasureState == MEASURE_STATE_FAIL){
            changeUiToInitial();
        }else{
            super.onBackPressed();
        }
    }


    private final BroadcastReceiver mScreenOnOffReceiver = new  BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {

                //Log.e(TAG, "screen on...");
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                //Log.e(TAG, "screen off...");
            }
        }
    };

    private static final String TAG = "Start";
    private static final boolean DEBUG = true;

    public void log(String msg){
        if(DEBUG) android.util.Log.v(TAG, msg);
    }
}
