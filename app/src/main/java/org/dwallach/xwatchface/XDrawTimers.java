package org.dwallach.xwatchface;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by dwallach on 1/2/15.
 */
public class XDrawTimers {
    private static final String TAG = "XDrawTimers";

    private static long stopwatchStartTime;
    private static long stopwatchPriorTime;
    private static boolean stopwatchIsRunning;
    private static boolean stopwatchIsReset;
    private static long stopwatchUpdateTimestamp = 0;

    public static void setStopwatchState(long startTime, long priorTime, boolean isRunning, boolean isReset, long updateTimestamp) {
        // ignore old / stale updates
        if(updateTimestamp > stopwatchUpdateTimestamp) {
            stopwatchStartTime = startTime;
            stopwatchPriorTime = priorTime;
            stopwatchIsRunning = isRunning;
            stopwatchIsReset = isReset;
            stopwatchUpdateTimestamp = updateTimestamp;
        }
    }

    private static Paint paintBucket[][];

    private static final int styleNormal = 0;
    private static final int styleAmbient = 1;
    private static final int styleMax = 1;

    private static final int colorStopwatchSecondHand = 0;
    private static final int colorStopwatchMinuteHand = 1;
    private static final int colorTimerHand = 2;
    private static final int colorMax = 2;

    private static final float gamma = 2.2f;

    private static Paint getPaint(int argb, int style, float width) {
        Paint retPaint = new Paint();
        retPaint.setAntiAlias(true);
        retPaint.setStrokeCap(Paint.Cap.SQUARE);  // regular hands have rounded ends, we'll do square here to look different
        retPaint.setStrokeWidth(width);

        switch(style) {
            case styleNormal:
                retPaint.setColor(argb);
                break;

            case styleAmbient:
                // CIE standard for luminance. Because overkill.

                int a = (argb & 0xff000000) >> 24;
                int r = (argb & 0xff0000) >> 16;
                int g = (argb & 0xff00) >> 8;
                int b = argb & 0xff;

                float fr = r / 255.0f;
                float fg = g / 255.0f;
                float fb = b / 255.0f;

                float fy = (float) (.2126f * Math.pow(fr, gamma) + .7152f * Math.pow(fg, gamma) + .0722f * Math.pow(fb, gamma));

                if (fy > 1.0f) fy = 1.0f;

                int y = (int) (fy * 255);

                retPaint.setColor((a << 24) | (y << 16) | (y << 8) | y);

                break;
        }
        retPaint.setColor(argb);

        return retPaint;
    }

    private static void initPaintBucket() {
        Log.v(TAG, "initPaintBucket");

        paintBucket = new Paint[styleMax][colorMax];

        for(int style=0; style < styleMax; style++) {
            paintBucket[style][colorStopwatchSecondHand] = getPaint(0xff80A3F2, style, 2f);  // light blue
            paintBucket[style][colorStopwatchMinuteHand] = getPaint(0xff80A3F2, style, 3f);  // light blue
            paintBucket[style][colorTimerHand] = getPaint(0xFFF2CF80, style, 2f); // orange-ish
        }
    }

    public static void draw(Canvas canvas, int width, int height, boolean ambientMode, boolean lowbitAmbient) {
        // we're not even going to try drawing in low-bit ambient mode; forget about it
        if(ambientMode && lowbitAmbient) return;

        long currentTime = System.currentTimeMillis();

        if(paintBucket == null)
            initPaintBucket();

        long stopwatchRenderTime = 0;

        float centerX = width / 2f;
        float centerY = height / 2f;

        // we don't draw anything if the stopwatch is non-moving and at 00:00.00
        if(!stopwatchIsReset) {
            if (!stopwatchIsRunning) {
                stopwatchRenderTime = stopwatchPriorTime;
            } else {
                stopwatchRenderTime = currentTime - stopwatchStartTime + stopwatchPriorTime;
            }

            int drawStyle;

            if (ambientMode)
                drawStyle = styleAmbient;
            else
                drawStyle = styleNormal;

            // code borrowed/derived from SweepWatchFace

            float seconds = stopwatchRenderTime / 1000.0f;
            float secRot = seconds / 30f * (float) Math.PI;
            float minutes = stopwatchRenderTime / 60000.0f;
            float minRot = minutes / 30f * (float) Math.PI;

            float secLength = centerX - 20;
            float minLength = centerX - 40;

            float secX = (float) Math.sin(secRot) * secLength;
            float secY = (float) -Math.cos(secRot) * secLength;
            canvas.drawLine(centerX, centerY, centerX + secX, centerY + secY, paintBucket[drawStyle][colorStopwatchSecondHand]);

            float minX = (float) Math.sin(minRot) * minLength;
            float minY = (float) -Math.cos(minRot) * minLength;
            canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY, paintBucket[drawStyle][colorStopwatchMinuteHand]);
        }
    }
}