package com.origin.app;
import android.content.Context;
import android.content.Intent;
import android.app.Notification;
import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(sdk=30)
public class ScheduleAlarmServiceTest {
    @Test public void dueAlarmStartsRingingService()throws Exception{
        Context c=RuntimeEnvironment.getApplication();long at=System.currentTimeMillis();
        JSONObject item=new JSONObject().put("id","due").put("at",at).put("title","高数").put("location","A301");
        c.getSharedPreferences(ScheduleAlarmStore.PREFS,0).edit().putString("items",new JSONArray().put(item).toString()).commit();
        new ScheduleAlarmReceiver().onReceive(c,ScheduleAlarmStore.intent(c,item));
        Intent started=Shadows.shadowOf((android.app.Application)c).getNextStartedService();assertNotNull(started);assertEquals(ScheduleAlarmService.class.getName(),started.getComponent().getClassName());assertEquals("高数",started.getStringExtra("title"));
    }
    @Test public void ringingHasAlarmNotificationWithStopActionAndTimesOut(){
        org.robolectric.android.controller.ServiceController<ScheduleAlarmService> controller=Robolectric.buildService(ScheduleAlarmService.class).create();
        ScheduleAlarmService service=controller.get();
        service.onStartCommand(new Intent().putExtra("title","高数").putExtra("location","A301"),0,1);
        Notification notification=Shadows.shadowOf(service).getLastForegroundNotification();assertNotNull(notification);assertEquals(Notification.CATEGORY_ALARM,notification.category);assertEquals("关闭闹钟",notification.actions[0].title);
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idleFor(java.time.Duration.ofSeconds(61));assertTrue(Shadows.shadowOf(service).isStoppedBySelf());controller.destroy();
    }
}
