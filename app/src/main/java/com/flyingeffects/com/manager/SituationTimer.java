package com.flyingeffects.com.manager;

import com.flyingeffects.com.entity.RequestMessage;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class SituationTimer {

    private Timer timer;
    private TimerTask task;

    public void startTimer(int second) {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(new RequestMessage());
            }
        };
        timer.schedule(task, 0, second * 1000);
    }


    public void destroyTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }


}
