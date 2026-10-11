package com.origin.app;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(sdk=30)
public class ScheduleAlarmStoreTest {
    Context c;AlarmManager manager;
    @Before public void setup(){c=RuntimeEnvironment.getApplication();manager=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);ScheduleAlarmStore.replace(c,new JSONArray());}
    JSONObject alarm(String id,long at)throws Exception{return new JSONObject().put("id",id).put("at",at).put("title","高数");}
    @Test public void usesExactDateAlarmClockAndReplacesOldTime()throws Exception{
        long at=System.currentTimeMillis()+3600000;ScheduleAlarmStore.replace(c,new JSONArray().put(alarm("42",at)));
        assertEquals(at,manager.getNextAlarmClock().getTriggerTime());
        ScheduleAlarmStore.replace(c,new JSONArray().put(alarm("42",at+600000)));
        assertEquals(1,Shadows.shadowOf(manager).getScheduledAlarms().size());assertEquals(at+600000,manager.getNextAlarmClock().getTriggerTime());
        assertNull(ScheduleAlarmStore.matching(c,"42",at));
    }
    @Test public void cancelAndLogoutRemoveAlarmsAndCache()throws Exception{
        ScheduleAlarmStore.replace(c,new JSONArray().put(alarm("1",System.currentTimeMillis()+60000)));
        ScheduleAlarmStore.replace(c,new JSONArray());assertEquals(0,Shadows.shadowOf(manager).getScheduledAlarms().size());assertEquals(0,ScheduleAlarmStore.items(c).length());
    }
    @Test public void rebootRestoresOnlyFutureAlarms()throws Exception{
        long at=System.currentTimeMillis()+60000;JSONObject future=alarm("future",at);
        c.getSharedPreferences(ScheduleAlarmStore.PREFS,0).edit().putString("items",new JSONArray().put(future).put(alarm("past",1)).toString()).commit();
        new ScheduleAlarmReceiver().onReceive(c,new Intent(Intent.ACTION_BOOT_COMPLETED));assertEquals(1,Shadows.shadowOf(manager).getScheduledAlarms().size());assertEquals(at,manager.getNextAlarmClock().getTriggerTime());
    }
    @Test public void deletedAlarmBroadcastCannotRing()throws Exception{
        JSONObject a=alarm("gone",System.currentTimeMillis());ScheduleAlarmStore.replace(c,new JSONArray());
        new ScheduleAlarmReceiver().onReceive(c,ScheduleAlarmStore.intent(c,a));assertNull(Shadows.shadowOf((android.app.Application)c).getNextStartedService());
    }
}
