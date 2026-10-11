package com.origin.app;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class)
@Config(sdk=35)
public class ScheduleWidgetPinTest {
    static class Call extends PluginCall {
        JSObject result;
        Call() { super(null,"ScheduleWidget","test","pin",new JSObject()); }
        @Override public void resolve(JSObject data) { result=data; }
    }
    @Test public void requestsSystemPinWhenLauncherSupportsIt() {
        Context context=RuntimeEnvironment.getApplication();
        AppCompatActivity activity=Robolectric.buildActivity(AppCompatActivity.class).setup().get();
        ScheduleWidgetPlugin plugin=new ScheduleWidgetPlugin() {
            @Override public Context getContext() { return context; }
            @Override public AppCompatActivity getActivity() { return activity; }
        };
        Shadows.shadowOf(AppWidgetManager.getInstance(context)).setRequestPinAppWidgetSupported(true);
        Call call=new Call();
        plugin.pin(call);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertNotNull(call.result);
        assertTrue(call.result.optBoolean("supported"));
        assertTrue(call.result.optBoolean("requested"));
        assertEquals(AppWidgetManager.getInstance(context).getAppWidgetIds(new android.content.ComponentName(context,ScheduleWidgetProvider.class)).length,call.result.optInt("count"));
    }
    @Test public void reportsUnsupportedLauncherWithoutClaimingWidgetWasAdded() {
        Context context=RuntimeEnvironment.getApplication();
        ScheduleWidgetPlugin plugin=new ScheduleWidgetPlugin() {
            @Override public Context getContext() { return context; }
        };
        Shadows.shadowOf(AppWidgetManager.getInstance(context)).setRequestPinAppWidgetSupported(false);
        Call call=new Call();
        plugin.pin(call);
        assertNotNull(call.result);
        assertFalse(call.result.optBoolean("supported"));
        assertFalse(call.result.optBoolean("requested"));
    }
 @Test public void statusCountsInstalledWidgetsAndPinCallbackConfirms() {
 Context context=RuntimeEnvironment.getApplication();
 ScheduleWidgetPlugin plugin=new ScheduleWidgetPlugin(){@Override public Context getContext(){return context;}};
 Call before=new Call();plugin.status(before);int count=before.result.optInt("count");
 Shadows.shadowOf(AppWidgetManager.getInstance(context)).createWidget(ScheduleWidgetProvider.class,R.layout.schedule_widget);
 Call after=new Call();plugin.status(after);assertEquals(count+1,after.result.optInt("count"));
 new WidgetPinReceiver().onReceive(context,new android.content.Intent());
 Call confirmed=new Call();plugin.status(confirmed);assertTrue(confirmed.result.optLong("confirmedAt")>0);
 }
}
