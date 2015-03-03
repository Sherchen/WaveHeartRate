package com.sherchen.heartrate.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;

import com.sherchen.heartrate.R;

/**
 * The description of use:
 * <br />
 * Created time:2014/6/17 10:23
 * Created by Dave
 */
public class ProgressWheelFitButton extends Button {

private static final String LOG_TAG = ProgressWheelFitButton.class.getSimpleName();
private static final boolean DEBUG = true;

    private void log(String msg){
        if(DEBUG) android.util.Log.v(LOG_TAG, msg);
    }

    private Bitmap m_Background;

    public ProgressWheelFitButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_Background = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_start);
    }

    private int m_ParentWidth, m_ParentHeight, m_ParentRimWidth;

    public void clip(int parentWidth, int parentHeight, int parentRimWidth){
        m_ParentWidth = parentWidth;
        m_ParentHeight = parentHeight;
        m_ParentRimWidth = parentRimWidth;
        clipToAutoFit();
    }

    private int m_ParentRadius;
    private int m_CenterX;
    private int m_CenterY;
    private int m_ExtraY;

    private void clipToAutoFit(){
        int width = getWidth();
        int height = getHeight();
        m_ParentRadius = (m_ParentHeight - 2 * m_ParentRimWidth) / 2;
        int fitHeight = height - m_ParentRimWidth;
        m_ExtraY = m_ParentRadius - fitHeight;
        int fitWidth = (int)(Math.sqrt(Math.pow(m_ParentRadius, 2) - Math.pow(m_ExtraY, 2)) * 2);
        m_CenterX = fitWidth / 2;
        m_CenterY = 0;
        log(
            " width is " + width +
            " height is " + height +
            " fitWidth is " + fitWidth +
            " fitHeight is " + fitHeight
        );
        int cropLeft = (width - fitWidth) / 2 ;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
        params.leftMargin = cropLeft;
        params.topMargin = 0;
        params.rightMargin = cropLeft;
        params.bottomMargin = m_ParentRimWidth;
        setLayoutParams(params);
    }

    private boolean isValidRegion(float x, float y){
        int disToCenter = (int) Math.sqrt( Math.pow(x - m_CenterX, 2) + Math.pow(y + m_ExtraY, 2) );
        log(
          "isValidRegion -- disToCenter is " + disToCenter
        );
        return disToCenter <= m_ParentRadius;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isValid = isValidRegion(event.getX(), event.getY());
        log(
            "onTouchEvent -- isValid is " + isValid
        );
        if(isValid){
            return super.onTouchEvent(event);
        }else{
            return true;
        }
    }
}
