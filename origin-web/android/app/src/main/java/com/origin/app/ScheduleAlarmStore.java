package com.origin.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

final class ScheduleAlarmStore {
    static final String PREFS = "origin_schedule_alarms";
    static JSONArray items(Context c) {
        try { return new JSONArray(c.getSharedPreferences(PREFS,0).getString("items","[]")); }
        catch (Exception ignored) { return new JSONArray(); }
    }
    static boolean allowed(Context c) {
        return Build.VERSION.SDK_INT < 31 || ((AlarmManager)c.getSystemService(Context.ALARM_SERVICE)).canScheduleExactAlarms();
    }
    static Intent intent(Context c, JSONObject item) {
        return new Intent(c,ScheduleAlarmReceiver.class).setAction("com.origin.app.RING")
            .setData(Uri.parse("origin-alarm://schedule/"+Uri.encode(item.optString("id"))))
            .putExtra("id",item.optString("id")).putExtra("at",item.optLong("at"));
    }
    static PendingIntent pending(Context c, JSONObject item) {
        return PendingIntent.getBroadcast(c,0,intent(c,item),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }
    static void replace(Context c, JSONArray incoming) {
        AlarmManager manager=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        JSONArray previous=items(c);
        for(int i=0;i<previous.length();i++) { JSONObject item=previous.optJSONObject(i); if(item!=null) manager.cancel(pending(c,item)); }
        c.stopService(new Intent(c,ScheduleAlarmService.class));
        JSONArray clean=new JSONArray();
        java.util.HashSet<String> ids=new java.util.HashSet<>();
        for(int i=0;i<incoming.length();i++) {
            JSONObject item=incoming.optJSONObject(i);
            if(item!=null && !item.optString("id").isEmpty() && item.optLong("at")>System.currentTimeMillis() && ids.add(item.optString("id"))) clean.put(item);
        }
        c.getSharedPreferences(PREFS,0).edit().putString("items",clean.toString()).commit();
        restore(c);
    }
    static boolean notificationsAllowed(Context c) {
        if(!androidx.core.app.NotificationManagerCompat.from(c).areNotificationsEnabled())return false;
        if(Build.VERSION.SDK_INT>=26){android.app.NotificationChannel channel=((android.app.NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).getNotificationChannel("origin_ringing_alarms");if(channel!=null && channel.getImportance()==0)return false;}
        return true;
    }
    static int restore(Context c) {
        if(!allowed(c) || !notificationsAllowed(c))return 0;
        AlarmManager manager=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        JSONArray data=items(c);
        int count=0;
        for(int i=0;i<data.length();i++) {
            JSONObject item=data.optJSONObject(i);
            if(item==null || item.optLong("at")<=System.currentTimeMillis())continue;
            Intent show=new Intent(c,MainActivity.class).putExtra("openSchedule",true)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent display=PendingIntent.getActivity(c,0,show,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            try { manager.setAlarmClock(new AlarmManager.AlarmClockInfo(item.optLong("at"),display),pending(c,item)); count++; }
            catch(SecurityException ignored) { return 0; }
        }
        return count;
    }
    static JSONObject matching(Context c,String id,long at) {
        JSONArray data=items(c);
        for(int i=0;i<data.length();i++) {
            JSONObject item=data.optJSONObject(i);
            if(item!=null && id.equals(item.optString("id")) && at==item.optLong("at"))return item;
        }
        return null;
    }
}
