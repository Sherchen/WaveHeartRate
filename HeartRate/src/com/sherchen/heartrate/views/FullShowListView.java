package com.sherchen.heartrate.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * The description of use:
 * <br />
 * Created time:2014/6/18 11:35
 * Created by Dave
 */
public class FullShowListView extends ListView {
    public FullShowListView(Context context) {
        super(context);
    }

    public FullShowListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullShowListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
    }
}
