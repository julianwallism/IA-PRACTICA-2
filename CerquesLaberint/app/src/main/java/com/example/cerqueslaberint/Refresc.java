package com.example.cerqueslaberint;

/**
 * Created by Ramon Mas on 10/10/21.
 */

import android.graphics.Canvas;

public class Refresc extends Thread
{
    private MainGame view;
    private boolean running = false;
    static final long FPS = 40;  // dibuixa FPS vegades per segon

    public Refresc(MainGame view)
    {
        this.view = view;
    }

    public void setRunning(boolean run)
    {
        running = run;
    }

    @Override
    public void run()
    {
        long i = 0;
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        long elapsedTime;
        while (true)
        {
            Canvas c = null;
            if (running)
            {
                startTime = System.currentTimeMillis();
                try {
                    c = view.getHolder().lockCanvas();
                    synchronized (view.getHolder())
                    {
                        view.paintScreen(c);
                    }
                } finally {
                    if (c != null) {
                        view.getHolder().unlockCanvasAndPost(c);
                    }
                }
                elapsedTime = System.currentTimeMillis() - startTime;
                sleepTime = ticksPS - elapsedTime;
                try {
                    if (sleepTime > 0) {
                        sleep(sleepTime);
                    }
                } catch (Exception e) {
                }
            }
        }
    }
}