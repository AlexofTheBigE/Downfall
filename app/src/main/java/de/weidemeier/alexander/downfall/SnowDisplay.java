package de.weidemeier.alexander.downfall;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ConcurrentModificationException;

/**
 * Created by thebige on 20.12.16.
 */

public class SnowDisplay extends SurfaceView implements SurfaceHolder.Callback {

    private SnowThread snowThread;


    public SnowDisplay(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
    }


    public void setThread(SnowThread snowThread) {
        this.snowThread = snowThread;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }


    /* Callback invoked when the surface dimensions change. */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }


    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        /*boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }*/
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            boolean paused = snowThread.isPaused();

            //pause the snow thread just for the fun of it
            if (!paused) snowThread.onPause();

            try {
                //check if click happened on a snowflake
                snowThread.hitsSnowflake(event.getX(), event.getY());
            } catch (ConcurrentModificationException e) {
                //do nothing. Always look on the bright side of life.
            }


            //resume the snow thread because the show must go on ;)
            if (!paused) snowThread.onResume();

            return true;
        }
        return false;
    }
}
