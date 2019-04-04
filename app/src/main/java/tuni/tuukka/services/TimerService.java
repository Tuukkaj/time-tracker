package tuni.tuukka.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;



public class TimerService extends Service {
    public final static String TIMER_EVENT_NAME = "time";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(() -> {
            Intent broadcast = new Intent(TIMER_EVENT_NAME);
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);

            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                manager.sendBroadcast(broadcast);
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }
}
