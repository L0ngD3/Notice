package com.example.a13522.notice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button send= (Button) findViewById(R.id.send);
        send.setOnClickListener( this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                Intent intent = new Intent(MainActivity.this,CameraAlbum.class);
                PendingIntent pi  =  PendingIntent.getActivity(MainActivity.this,0, intent,0);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle("通知")
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(),R.drawable.a)))
                    // .setStyle(new NotificationCompat.BigTextStyle().bigText("asdfghjkl;'sdfghjklsdfghjklzxcvbnm,qwertyuiopszdfxghjkxcvbnm"))

                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher_round).
                        setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round))
                        .setContentIntent(pi)
                        .setVibrate(new long[] {0,1000,1000,1000})
                        .setLights(Color.RED,1000,5000)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)

                        .build();
                manager.notify(1,notification);
                break;
            default:
                break;
        }
    }
}
