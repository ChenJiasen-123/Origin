package com.origin.app;
import android.content.*;
public class WidgetPinReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context context, Intent intent){context.getSharedPreferences("schedule_widget",0).edit().putLong("confirmed",System.currentTimeMillis()).apply();ScheduleWidgetProvider.refresh(context);}
}
