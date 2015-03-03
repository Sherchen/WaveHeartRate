package com.sherchen.heartrate.control;


import com.sherchen.heartrate.Measure;
import com.sherchen.heartrate.control.util.DbUtil;
import com.sherchen.heartrate.greendao.HistoryEntity;



/**
 * The description of use:
 * <br />
 * Created time:2014/6/17 14:41
 * Created by Dave
 */
public class MeasureControl  {
    private int m_HeartRate;
    private Measure m_Context;

    private static final int MSG_HRT_FAILED = 1;
    private static final int MSG_HRT_SENSOR_PAUSED = 2;
    private static final int MSG_HRT_SENSOR_START = 3;
    private boolean isHrPaused = true;
    private int mHeartRate;
    private int mTempData;





    public MeasureControl(Measure context){
        m_Context = context;
    }

    private void log(String msg){
        m_Context.log(msg);
    }

    public void saveMeasure(long time, int rate){
        HistoryEntity historyEntity = new HistoryEntity();
        historyEntity.setRate(rate);
        historyEntity.setCalculateTime(time);
        DbUtil.save(m_Context, historyEntity);
    }

    public int getHeartRate(){
        int tmp = m_HeartRate;
        m_HeartRate = 0;
        return tmp;
    }

    public void clearHeartRate(){
        m_HeartRate = 0;
    }


    public void onDestroy(){
    }
}
