package com.sherchen.heartrate.views.animator;

import android.animation.FloatEvaluator;
import android.animation.TypeEvaluator;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * The description of use:
 * <br />
 * Created time:2014/7/8 9:25
 * Created by Dave
 */
public class LinearEvaluator implements TypeEvaluator<Number> {
    private Translate m_Translate;
    public LinearEvaluator (Translate translate){
        m_Translate = translate;
    }

    private float m_YMax;
    private float m_YMin;

    public LinearEvaluator(float yMin, float yMax){
        m_YMax = yMax;
        m_YMin = yMin;
    }

    private ArrayList<EvaluatorListener> m_Listeners = new ArrayList<EvaluatorListener>();

    public interface EvaluatorListener{
        void onEvalutor(float x, float y);
    }

    public void addListener(EvaluatorListener listener){
        m_Listeners.add(listener);
    }

    public void addListener(EvaluatorListener... listeners){
        for(EvaluatorListener listener : listeners){
            m_Listeners.add(listener);
        }
    }

    public void removeListener(EvaluatorListener listener){
        m_Listeners.remove(listener);
    }

    public void clear(){
        m_Listeners.clear();
    }


    @Override
    public Float evaluate(float fraction, Number startValue, Number endValue) {
        float start = startValue.floatValue();
        float end = endValue.floatValue();
        float xCurrent = EvaluateUtil.getEvaluate(fraction, start, end);
        float yCurrent = getY(xCurrent, start, end);
        for(EvaluatorListener listener : m_Listeners){
            listener.onEvalutor(xCurrent, yCurrent);
        }
        return xCurrent;
    }

    private float getY(float current, float start, float end){
        return (current - start) * (m_YMax - m_YMin) / (end - start) + m_YMin;
    }
}
