package com.sherchen.heartrate.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.sherchen.heartrate.R;
import com.sherchen.heartrate.control.util.ViewHolder;
import com.sherchen.heartrate.greendao.HistoryEntity;

import java.util.List;

/**
 * The description of use:
 * <br />
 * Created time:2014/6/18 15:04
 * Created by Dave
 */
public class HistoryAdapter extends EntityListAdapter<HistoryEntity> {
    public HistoryAdapter(List<HistoryEntity> m_List, Context m_Context, int m_LayoutResId) {
        super(m_List, m_Context, m_LayoutResId);
    }

    @Override
    protected void bindView(View convertView, HistoryEntity entity) {
        TextView tvTime = (TextView) ViewHolder.find(convertView, R.id.tv_time_history_item);
        TextView tvRate = (TextView) ViewHolder.find(convertView, R.id.tv_rate_history_item);
        tvTime.setText(entity.getStrCalculateTime());
        tvRate.setText(String.valueOf(entity.getRate()));
    }
}
