package com.origin.app;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;
import com.getcapacitor.*;
import com.getcapacitor.annotation.*;

@CapacitorPlugin(name="ScheduleAlarms",permissions={@Permission(alias="notifications",strings={Manifest.permission.POST_NOTIFICATIONS})})
public class ScheduleAlarmsPlugin extends Plugin {
    private JSObject result() {
        JSObject r=new JSObject();
        r.put("supported",true);
        r.put("exact",ScheduleAlarmStore.allowed(getContext()));
        r.put("notifications",ScheduleAlarmStore.notificationsAllowed(getContext()));
        r.put("count",ScheduleAlarmStore.restore(getContext()));
        return r;
    }
    @PluginMethod public void status(PluginCall call) { call.resolve(result()); }
    @PluginMethod public void sync(PluginCall call) {
        JSArray data=call.getArray("items");
        if(data==null){call.reject("缺少闹钟列表");return;}
        try { ScheduleAlarmStore.replace(getContext(),data);call.resolve(result()); }
        catch(Exception e){call.reject("日程已保存，但本地闹钟同步失败",e);}
    }
    @PluginMethod public void requestAccess(PluginCall call) {
        if(Build.VERSION.SDK_INT>=33 && getPermissionState("notifications")!=PermissionState.GRANTED) {
            requestPermissionForAlias("notifications",call,"notificationResult");return;
        }
        openExact(call);
    }
    @PermissionCallback private void notificationResult(PluginCall call) { openExact(call); }
    private void openExact(PluginCall call) {
        try {
            if(Build.VERSION.SDK_INT>=31 && !ScheduleAlarmStore.allowed(getContext())) {
                getActivity().startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,Uri.parse("package:"+getContext().getPackageName())));
            } else if(!ScheduleAlarmStore.notificationsAllowed(getContext())) {
                getActivity().startActivity(new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,getContext().getPackageName()));
            }
            call.resolve(result());
        }catch(Exception e){call.reject("请在系统设置中允许 Origin 的通知和闹钟权限",e);}
    }
}
