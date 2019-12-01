package com.jops1.hyeyum_1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class BroadcastD extends BroadcastReceiver {


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    // 지정된 알람 시간이 되었을 때 onReceive를 호출
    public void onReceive(Context context, Intent intent) {
        Intent re_intent = new Intent(context, MainActivity.class);
        re_intent.putExtra("alarm", 2);

        // 상태바에 나타나는 서비스 불러움
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, re_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.logo)
                .setNumber(1).setContentTitle("혜윰")
                .setContentText("오늘의 질문에 답하셨나요?")
                .setTicker("혜윰")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).
                setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true);

        notificationmanager.notify(0, builder.build());
    }
}