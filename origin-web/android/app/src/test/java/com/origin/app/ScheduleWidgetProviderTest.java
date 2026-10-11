package com.origin.app;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAppWidgetManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class ScheduleWidgetProviderTest {
    private Context context;
    private ShadowAppWidgetManager manager;
    private int id;
    private String today;
    @Before public void setup() {
        context = RuntimeEnvironment.getApplication();
        context.getSharedPreferences("schedule_widget",0).edit().clear().commit();
        manager = Shadows.shadowOf(AppWidgetManager.getInstance(context));
        id = manager.createWidget(ScheduleWidgetProvider.class, R.layout.schedule_widget);
        today = new SimpleDateFormat("yyyy-MM-dd",Locale.ROOT).format(new Date());
    }
    private JSONObject course(String date,String title,String start,String location) throws Exception {
        return new JSONObject().put("date",date).put("title",title).put("start",start).put("end","18:00").put("location",location);
    }
    private void sync(JSONArray items) {
        context.getSharedPreferences("schedule_widget",0).edit().putString("items",items.toString())
            .putLong("updated",System.currentTimeMillis()).commit();
        ScheduleWidgetProvider.refresh(context);
    }
    private String text(int resource) { return ((TextView)manager.getViewFor(id).findViewById(resource)).getText().toString(); }
    @Test public void showsOnlyTodayInTimeOrderAndDisplaysLocation() throws Exception {
        sync(new JSONArray().put(course(today,"晚课","16:00","三教502"))
            .put(course("2000-01-01","其他日期","08:00","旧教室"))
            .put(course(today,"早课","09:00","null")));
        String courses=text(R.id.widget_courses);
        assertTrue(courses.indexOf("早课") < courses.indexOf("晚课"));
        assertTrue(courses.contains("三教502"));
        assertFalse(courses.contains("其他日期"));
        assertFalse(courses.contains("null"));
        assertTrue(text(R.id.widget_title).contains(today));
    }
    @Test public void clearingCacheRemovesPreviousAccountCourses() throws Exception {
        sync(new JSONArray().put(course(today,"私人课程","08:00","教室")));
        sync(new JSONArray());
        assertFalse(text(R.id.widget_courses).contains("私人课程"));
        assertTrue(text(R.id.widget_courses).contains("今日暂无课程"));
    }
    @Test public void tappingWidgetOpensSchedule() {
        View view=manager.getViewFor(id);
        assertTrue(view.findViewById(R.id.widget_root).performClick());
        Intent intent=Shadows.shadowOf((Application)context).getNextStartedActivity();
        assertNotNull(intent);
        assertEquals(MainActivity.class.getName(),intent.getComponent().getClassName());
        assertTrue(intent.getBooleanExtra("openSchedule",false));
    }
}
