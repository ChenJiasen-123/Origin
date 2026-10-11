package com.origin.app;
import com.getcapacitor.*;
import com.getcapacitor.annotation.CapacitorPlugin;
import android.appwidget.AppWidgetManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.Bundle;
import android.widget.RemoteViews;
@CapacitorPlugin(name="ScheduleWidget")
public class ScheduleWidgetPlugin extends Plugin {
 private JSObject state() {
  AppWidgetManager manager=AppWidgetManager.getInstance(getContext());
  ComponentName provider=new ComponentName(getContext(),ScheduleWidgetProvider.class);
  JSObject r=new JSObject();r.put("supported",android.os.Build.VERSION.SDK_INT>=26&&manager.isRequestPinAppWidgetSupported());
  r.put("count",manager.getAppWidgetIds(provider).length);r.put("requested",false);
  r.put("confirmedAt",getContext().getSharedPreferences("schedule_widget",0).getLong("confirmed",0));
  try{android.content.pm.ResolveInfo home=getContext().getPackageManager().resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),0);r.put("launcher",home==null?"":home.loadLabel(getContext().getPackageManager()).toString());}catch(Exception e){r.put("launcher","");}
  return r;
 }
 @PluginMethod public void status(PluginCall call){call.resolve(state());}
 @PluginMethod public void launchTarget(PluginCall call){boolean s=getActivity().getIntent().getBooleanExtra("openSchedule",false);getActivity().getIntent().removeExtra("openSchedule");JSObject r=new JSObject();r.put("schedule",s);call.resolve(r);}
 @PluginMethod public void openHome(PluginCall call){try{getActivity().startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));call.resolve();}catch(Exception e){call.reject("Unable to open launcher");}}
 @PluginMethod public void pin(PluginCall call){
  JSObject initial=state();android.util.Log.i("OriginWidget","Pin supported="+initial.optBoolean("supported")+" installed="+initial.optInt("count"));if(!initial.optBoolean("supported")){call.resolve(initial);return;}
  getActivity().runOnUiThread(()->{
   try{
    Context c=getContext();AppWidgetManager manager=AppWidgetManager.getInstance(c);
    RemoteViews preview=new RemoteViews(c.getPackageName(),R.layout.schedule_widget);
    boolean en=c.getSharedPreferences("origin_display",0).getString("language","zh").equals("en");
    preview.setTextViewText(R.id.widget_title,en?"Today's schedule":"今日课表");
    preview.setTextViewText(R.id.widget_courses,en?"09:00–10:30  Class\n\n14:00–15:30  Study":"09:00–10:30  课程\n\n14:00–15:30  自习");
    Bundle options=new Bundle();options.putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW,preview);
    PendingIntent confirmed=PendingIntent.getBroadcast(c,105,new Intent(c,WidgetPinReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    boolean requested=manager.requestPinAppWidget(new ComponentName(c,ScheduleWidgetProvider.class),options,confirmed);
    android.util.Log.i("OriginWidget","Pin request accepted="+requested);JSObject r=state();r.put("requested",requested);call.resolve(r);
   }catch(Exception e){android.util.Log.e("OriginWidget","Pin request failed",e);call.reject("无法打开系统小组件添加窗口");}
  });
 }
 @PluginMethod public void language(PluginCall call){getContext().getSharedPreferences("origin_display",0).edit().putString("language",call.getString("value","zh")).apply();ScheduleWidgetProvider.refresh(getContext());call.resolve();}
 @PluginMethod public void update(PluginCall call){JSArray items=call.getArray("items");if(items==null){call.reject("items required");return;}getContext().getSharedPreferences("schedule_widget",0).edit().putString("items",items.toString()).putLong("updated",System.currentTimeMillis()).apply();ScheduleWidgetProvider.refresh(getContext());call.resolve();}
}
