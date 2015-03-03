package com.sherchen.heartrate.control.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sherchen.heartrate.greendao.DaoMaster;
import com.sherchen.heartrate.greendao.DaoSession;
import com.sherchen.heartrate.greendao.HistoryEntity;
import com.sherchen.heartrate.greendao.HistoryEntityDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * The description of use:
 * <br />
 * Created time:2014/6/17 16:45
 * Created by Dave
 */
public class DbUtil {

private static final String LOG_TAG = DbUtil.class.getSimpleName();
private static final boolean DEBUG = true;

    public static void log(String msg){
        if(DEBUG) android.util.Log.v(LOG_TAG, msg);
    }

    private static final String DATABASE_NAME = "HistoryRate.db";//UserApp.db
    private static final int LIMITED_RECORD_SIZE = 10;

    public static void save(Context m_Context, HistoryEntity entity){
        SQLiteDatabase m_Database = null;
        try {
            HistoryEntityDao entryDao = getWriteDao(m_Context);
            m_Database = entryDao.getDatabase();
            long id = entryDao.insert(entity);
            log("insert id is " + id);
        } catch (Exception e) {
            // ignore the exception handler
        } finally{
            if(m_Database != null){
                m_Database.close();
                m_Database = null;
            }
        }
    }

    public static List<HistoryEntity> getTopTen(Context m_Context){
        SQLiteDatabase m_Database = null;
        List<HistoryEntity> result = null;
        try{
            HistoryEntityDao entryDao = getReadDao(m_Context);
            m_Database = entryDao.getDatabase();
            QueryBuilder qb = entryDao.queryBuilder();
//            maybe the order is not need,cause the calcuate time of later is always bigger than before.
            result = qb.orderDesc(HistoryEntityDao.Properties.CalculateTime).limit(LIMITED_RECORD_SIZE).list();
            if(result != null){
                for(HistoryEntity entity : result){
                    entity.setStrCalculateTime(CommonUtil.getReadableDateTime(entity.getCalculateTime()));
                }
            }
        }catch(Exception e){
            // ignore the exception handler
        }finally{
            if(m_Database != null){
                m_Database.close();
                m_Database = null;
            }
        }
        return result;
    }


    private static HistoryEntityDao getWriteDao(Context m_Context) throws java.lang.IllegalArgumentException, java.lang.SecurityException, java.lang.IllegalAccessException, java.lang.NoSuchFieldException, java.lang.ClassNotFoundException{
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(m_Context, DATABASE_NAME, null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        return daoSession.getHistoryEntityDao();
    }

    private static HistoryEntityDao getReadDao(Context m_Context) throws java.lang.IllegalArgumentException, java.lang.SecurityException, java.lang.IllegalAccessException, java.lang.NoSuchFieldException, java.lang.ClassNotFoundException{
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(m_Context, DATABASE_NAME, null);
        DaoMaster daoMaster = new DaoMaster(helper.getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        return daoSession.getHistoryEntityDao();
    }


    private static void clear(){
        //todo the idea is clear the record before one week, the alternative is clear the records which is beyond 1000
    }
}
