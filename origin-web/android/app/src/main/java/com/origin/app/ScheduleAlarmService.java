package com.origin.app;

import android.app.*;
import android.content.*;
import android.media.*;
import android.net.Uri;
import android.os.*;
import androidx.core.app.NotificationCompat;

public class ScheduleAlarmService extends Service {
    private MediaPlayer player;
    private Vibrator vibrator;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private static final int NOTIFICATION_ID=8127;
    private static final String CHANNEL="origin_ringing_alarms";
    @Override public IBinder onBind(Intent intent){return null;}
    @Override public int onStartCommand(Intent intent,int flags,int startId) {
        if(intent==null || "STOP".equals(intent.getAction())){stopSelf();return START_NOT_STICKY;}
        boolean en=getSharedPreferences("origin_display",0).getString("language","zh").equals("en");
        release();
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel channel=new NotificationChannel(CHANNEL,en?"Course alarms":"课前闹钟",NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(en?"Schedule alarm":"日程闹钟");channel.setSound(null,null);channel.enableVibration(false);
            manager.createNotificationChannel(channel);
        }
        PendingIntent stop=PendingIntent.getService(this,0,new Intent(this,ScheduleAlarmService.class).setAction("STOP"),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        PendingIntent open=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class).putExtra("openSchedule",true).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification notification=new NotificationCompat.Builder(this,CHANNEL).setSmallIcon(R.drawable.ic_alarm_notification)
            .setContentTitle(intent.getStringExtra("title")).setContentText((en?"Your schedule is about to start":"日程即将开始")+(intent.getStringExtra("location")==null || intent.getStringExtra("location").isEmpty()?"":" · "+intent.getStringExtra("location")))
            .setCategory(NotificationCompat.CATEGORY_ALARM).setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(open).setOngoing(true).addAction(R.drawable.ic_alarm_notification,en?"Dismiss":"关闭闹钟",stop).build();
        startForeground(NOTIFICATION_ID,notification);
        try {
            Uri sound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if(sound==null)sound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            player=new MediaPlayer();player.setWakeMode(this,PowerManager.PARTIAL_WAKE_LOCK);
            player.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
            player.setDataSource(this,sound);player.setLooping(true);player.prepare();player.start();
        }catch(Exception ignored) {if(player!=null){player.release();player=null;}}
        vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        if(vibrator!=null && vibrator.hasVibrator()) {
            long[] pattern={0,700,400,700,900};
            if(Build.VERSION.SDK_INT>=26)vibrator.vibrate(VibrationEffect.createWaveform(pattern,0));else vibrator.vibrate(pattern,0);
        }
        handler.postDelayed(this::stopSelf,60000);
        return START_NOT_STICKY;
    }
    private void release(){handler.removeCallbacksAndMessages(null);if(player!=null){player.release();player=null;}if(vibrator!=null){vibrator.cancel();vibrator=null;}}
    @Override public void onDestroy(){release();stopForeground(true);super.onDestroy();}
}
