package com.sherchen.heartrate.views.animator;

/**
 * The description of use:
 * <br />
 * Created time:2014/7/8 9:33
 * Created by Dave
 */
public class SimpleTranslate implements Translate {
    private float m_Start;
    private float m_End;

    public SimpleTranslate(float start, float end) {
        m_Start = start;
        m_End = end;
    }

    @Override
    public float translate(float value) {
        return EvaluateUtil.getEvaluate(value, m_Start, m_End);
    }
}
