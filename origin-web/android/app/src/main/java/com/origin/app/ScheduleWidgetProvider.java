package com.origin.app;
import android.app.PendingIntent;
import android.appwidget.*;
import android.content.*;
import android.widget.RemoteViews;
import org.json.*;
import java.text.SimpleDateFormat;
import java.util.*;
public class ScheduleWidgetProvider extends AppWidgetProvider {
    public static void refresh(Context c) {
        AppWidgetManager m = AppWidgetManager.getInstance(c);
        render(c, m, m.getAppWidgetIds(new ComponentName(c, ScheduleWidgetProvider.class)));
    }
    @Override public void onUpdate(Context c, AppWidgetManager m, int[] ids) { render(c,m,ids); }
    @Override public void onReceive(Context c, Intent i) {
        super.onReceive(c,i);
        if (Intent.ACTION_DATE_CHANGED.equals(i.getAction()) || Intent.ACTION_TIME_CHANGED.equals(i.getAction()) || Intent.ACTION_TIMEZONE_CHANGED.equals(i.getAction())) refresh(c);
    }
    private static void render(Context c, AppWidgetManager m, int[] ids) {
        boolean en=c.getSharedPreferences("origin_display",0).getString("language","zh").equals("en");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(new Date());
        android.content.SharedPreferences prefs = c.getSharedPreferences("schedule_widget",0);
        List<JSONObject> courses = new ArrayList<>();
        try {
            JSONArray all = new JSONArray(prefs.getString("items","[]"));
            for(int n=0;n<all.length();n++) {
                JSONObject item=all.getJSONObject(n);
                if(today.equals(item.optString("date"))) courses.add(item);
            }
        } catch(JSONException ignored) { }
        courses.sort(Comparator.comparing(item -> item.optString("start")));
        StringBuilder text = new StringBuilder();
        for(JSONObject item : courses) {
            text.append(item.optString("start")).append("–").append(item.optString("end"))
                .append("  ").append(item.optString("title"));
            String location = item.optString("location", "");
            if(!location.isEmpty() && !location.equals("null")) text.append(" · ").append(location);
            text.append("\n\n");
        }
        long updated=prefs.getLong("updated",0);
        Intent open = new Intent(c, MainActivity.class);
        open.putExtra("openSchedule", true);
        open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent action=PendingIntent.getActivity(c,0,open,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        for(int id : ids) {
            RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.schedule_widget);
            v.setTextViewText(R.id.widget_title,(en?"Today · ":"今日课表 · ")+today);
            v.setTextViewText(R.id.widget_courses,text.length()>0?text.toString():(en?"No classes today\nOpen Origin to sync":"今日暂无课程\n打开 Origin 同步课表"));
            v.setTextViewText(R.id.widget_status,updated==0?(en?"Open Origin to sync":"打开 Origin 同步课表"):(en?"Updated ":"更新于 ")+new SimpleDateFormat("MM-dd HH:mm",Locale.getDefault()).format(new Date(updated)));
            v.setOnClickPendingIntent(R.id.widget_root,action);
            m.updateAppWidget(id,v);
        }
    }
}
