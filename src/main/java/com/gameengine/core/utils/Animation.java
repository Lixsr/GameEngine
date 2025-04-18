package com.gameengine.core.utils;

import com.gameengine.core.entity.Entity;

import java.util.Timer;
import java.util.TimerTask;

public class Animation {
    public static void rotateOverTime(Entity entity, float rotationSpeed) {
        Timer timer = new Timer();
        long startTime = System.currentTimeMillis();
        float duration = 2000; // 2 seconds in milliseconds
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                float elapsedTime = (currentTime - startTime) / 1000.0f; // Convert to seconds
                if (elapsedTime <= duration / 1000.0f) {
                    // Apply rotation for 10ms interval
                    float rotationAmount = rotationSpeed * 0.01f; // 10ms = 0.01s
                    entity.incRotation(rotationAmount, rotationAmount, rotationAmount);
                } else {
                    timer.cancel(); // Stop the timer after 2 seconds
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 3); // Run every 10ms
    }
}
