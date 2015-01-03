package org.dwallach.xwatchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class XWatchfaceReceiver extends BroadcastReceiver {
    private final static String TAG = "XWatchfaceReceiver";

    private static final String prefStopwatchRunning = "running";
    private static final String prefStopwatchReset = "reset";
    private static final String prefStopwatchStartTime = "startTime";
    private static final String prefStopwatchPriorTime = "priorTime";
    private static final String prefStopwatchUpdateTimestamp = "updateTimestamp";

    private static final String stopwatchUpdateIntent = "org.dwallach.x.stopwatch.update";

    public static final String stopwatchQueryIntent = "org.dwallach.x.stopwatch.query";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "got intent: " + intent.toString());

        if(intent.getAction().equals(stopwatchUpdateIntent)) {
            Bundle extras = intent.getExtras();
            long startTime = extras.getLong(prefStopwatchStartTime);
            long priorTime = extras.getLong(prefStopwatchPriorTime);
            boolean isRunning = extras.getBoolean(prefStopwatchRunning);
            boolean isReset = extras.getBoolean(prefStopwatchReset);
            long updateTimestamp = extras.getLong(prefStopwatchUpdateTimestamp);

            Log.v(TAG, "stopwatch update: startTime(" + startTime +
                    "), priorTime(" + priorTime +
                    "), isRunning(" + isRunning +
                    "), isReset(" + isReset +
                    "), updateTimestamp(" + updateTimestamp +
                    ")");

            XDrawTimers.setStopwatchState(startTime, priorTime, isRunning, isReset, updateTimestamp);
        }
    }

    /**
     * Call this and it will ask external stopwatches and timers to report back with their
     * state. If we already *have* state locally, then this will be a no-op, so don't worry
     * about calling it too often. Best called in places like onCreate(). Best not called in onDraw().
     *
     * @param context
     */
    public static void pingExternalStopwatches(Context context) {
        if(XDrawTimers.getStopwatchUpdateTimestamp() == 0) {
            Log.v(TAG, "sending broadcast query for external stopwatches");
            context.sendBroadcast(new Intent(stopwatchQueryIntent));
        }
    }
}
