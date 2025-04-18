package com.gameengine.core.utils;

import com.gameengine.core.entity.Entity;
import org.joml.Vector3f;

import java.util.Timer;
import java.util.TimerTask;

public class Animation {
    public static void explosion(Entity entity, float speed, Vector3f dir) {
        Timer timer = new Timer();
        float duration = 1000.0f;

        Vector3f start = new Vector3f(entity.getPos());
        Vector3f end = new Vector3f(start).add(new Vector3f(dir).mul(speed));
        Vector3f control = new Vector3f(start).add(new Vector3f(dir).mul(speed));
        control.y += 2.0f;

        TimerTask task = new TimerTask() {
            int bounceCount = 0;
            int maxBounces = 2;
            long startTime = System.currentTimeMillis();
            @Override
            public void run() {
                float elapsed = System.currentTimeMillis() - startTime;
                float t = elapsed / duration;

                if (t <= 2.0f) {
                    float oneMinusT = 1 - t;
                    Vector3f pos = new Vector3f(start).mul(oneMinusT * oneMinusT)
                            .add(new Vector3f(control).mul(2 * oneMinusT * t))
                            .add(new Vector3f(end).mul(t * t));
                    if (pos.y < 0 && bounceCount < maxBounces) {
                        // Clamp to ground and bounce
                        pos.y = 0;
                        start.set(pos);
                        control.y = Math.abs(control.y) * 0.6f;
                        end.y = Math.abs(end.y) * 0.6f;

                        bounceCount++;
                        startTime = System.currentTimeMillis(); // restart Bezier curve
                        return; // skip setting this frame
                    }
                    entity.setPos(pos.x, pos.y, pos.z);
                } else {
                    timer.cancel();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1); // Update every 10ms
    }

    public static void rotateOverTime(Entity entity, float rotationSpeed) {
        Timer timer = new Timer();
        long startTime = System.currentTimeMillis();
        float duration = 100000; // 2 seconds in milliseconds
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                float elapsedTime = (currentTime - startTime) / 1000.0f; // Convert to seconds
                if (elapsedTime <= duration / 1000.0f) {
                    // Apply rotation for 10ms interval
                    float rotationAmount = rotationSpeed/3 * 0.01f; // 10ms = 0.01s
                    entity.incRotation(0, rotationAmount, 0);
                } else {
                    timer.cancel(); // Stop the timer after 2 seconds
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1); // Run every 10ms
    }

}
