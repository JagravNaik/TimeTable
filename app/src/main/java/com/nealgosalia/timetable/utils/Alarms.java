package com.nealgosalia.timetable.utils;

import android.app.PendingIntent;
import android.content.Context;

/**
 * Created by kira on 20/12/16.
 */

public class Alarms {
    Context context;
    PendingIntent pendingIntent;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }
}
