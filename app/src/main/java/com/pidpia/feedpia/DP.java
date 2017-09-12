package com.pidpia.feedpia;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by jenorain on 2015-07-22.
 */
public class DP   {
    public int DP(Activity target, int size){
        DisplayMetrics metrics;
        metrics = new DisplayMetrics();
        target.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int sizes = getDPI(size, metrics);
        return sizes;
    }
    public static int getDPI(int size, DisplayMetrics metrics){
        return (size * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
    }

}
