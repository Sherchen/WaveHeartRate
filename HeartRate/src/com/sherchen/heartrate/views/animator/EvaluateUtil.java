package com.sherchen.heartrate.views.animator;

/**
 * The description of use:
 * <br />
 * Created time:2014/7/8 9:35
 * Created by Dave
 */
public class EvaluateUtil {
    public static float getEvaluate(float fraction, float start, float end){
        return start + (end - start) * fraction;
    }
}
