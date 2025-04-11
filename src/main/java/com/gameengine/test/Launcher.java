package com.gameengine.test;

import com.gameengine.core.EngineManager;
import com.gameengine.core.WindowManager;
import com.gameengine.core.utils.Consts;
import org.lwjgl.Version;

public class Launcher {
    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) {
        window = new WindowManager(Consts.TITLE,1600, 900, false);
        EngineManager engine = new EngineManager();
        game = new TestGame();

        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }
    public static TestGame getGame() {
        return game;
    }
}
