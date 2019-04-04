package tuni.tuukka.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;



public class TimerService extends Service {
    public final static String TIMER_EVENT_NAME = "time";
    boolean run;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        run = true;

        new Thread(() -> {
            Intent broadcast = new Intent(TIMER_EVENT_NAME);
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);

            while (run) {
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
