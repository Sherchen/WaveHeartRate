package com.sherchen.heartrate.control;

import android.content.Context;

import com.sherchen.heartrate.control.util.CommonUtil;
import com.sherchen.heartrate.control.util.DbUtil;
import com.sherchen.heartrate.greendao.HistoryEntity;

import java.util.List;

/**
 * The description of use:
 * <br />
 * Created time:2014/6/18 13:51
 * Created by Dave
 */
public class HistoryControl {

private static final String LOG_TAG = HistoryControl.class.getSimpleName();
private static final boolean DEBUG = true;

    public void log(String msg){
        if(DEBUG) android.util.Log.v(LOG_TAG, msg);
    }

    private Context m_Context;

    public HistoryControl(Context context){
        m_Context = context;
    }

    public List<HistoryEntity> getRecords(){
        List<HistoryEntity> list = DbUtil.getTopTen(m_Context);
        test(list);
        return list;
    }

    public void test(List<HistoryEntity> list){
        int size = list == null? 0 : list.size();
        for(int i=0;i<size;i++){
            log("The index is " + i + ",calculate time is " + CommonUtil.getReadableDateTime(list.get(i).getCalculateTime()) + ", rate is " + list.get(i).getRate() +"bpm");
        }
    }
}
