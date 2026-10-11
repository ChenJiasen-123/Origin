package com.origin.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import org.json.JSONObject;

public class ScheduleAlarmReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c,Intent intent) {
        String action=intent.getAction();
        if("com.origin.app.RING".equals(action)) {
            String id=intent.getStringExtra("id");long at=intent.getLongExtra("at",0);
            JSONObject item=ScheduleAlarmStore.matching(c,id==null?"":id,at);
            if(item==null || !ScheduleAlarmStore.allowed(c) || !ScheduleAlarmStore.notificationsAllowed(c) || Math.abs(System.currentTimeMillis()-at)>300000)return;
            try {
                ContextCompat.startForegroundService(c,new Intent(c,ScheduleAlarmService.class)
                    .putExtra("title",item.optString("title","日程提醒")).putExtra("location",item.optString("location")));
            }catch(RuntimeException ignored) {}
        } else if(Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)
            || Intent.ACTION_TIME_CHANGED.equals(action) || Intent.ACTION_TIMEZONE_CHANGED.equals(action)
            || android.app.AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.equals(action)) {
            ScheduleAlarmStore.restore(c);
        }
    }
}
