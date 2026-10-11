package com.origin.app;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(sdk=35)
public class ScheduleAlarmPermissionTest {
    @Test public void deniedExactPermissionKeepsPlanForLaterGrant()throws Exception{
        Context c=RuntimeEnvironment.getApplication();AlarmManager manager=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Shadows.shadowOf(manager).setCanScheduleExactAlarms(false);
        ScheduleAlarmStore.replace(c,new JSONArray().put(new JSONObject().put("id","1").put("at",System.currentTimeMillis()+60000)));
        assertEquals(1,ScheduleAlarmStore.items(c).length());assertEquals(0,Shadows.shadowOf(manager).getScheduledAlarms().size());
        Shadows.shadowOf(manager).setCanScheduleExactAlarms(true);assertEquals(1,ScheduleAlarmStore.restore(c));
    }
    @Test public void deniedNotificationsDoesNotScheduleUndismissableAlarm()throws Exception{
        Context c=RuntimeEnvironment.getApplication();AlarmManager manager=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Shadows.shadowOf(manager).setCanScheduleExactAlarms(true);
        Shadows.shadowOf((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).setNotificationsEnabled(false);
        ScheduleAlarmStore.replace(c,new JSONArray().put(new JSONObject().put("id","1").put("at",System.currentTimeMillis()+60000)));
        assertEquals(0,Shadows.shadowOf(manager).getScheduledAlarms().size());
    }
}
