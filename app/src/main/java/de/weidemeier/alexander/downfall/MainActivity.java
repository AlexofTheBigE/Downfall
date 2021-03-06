package de.weidemeier.alexander.downfall;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private SnowDisplay snowDisplay;

    private SnowThread snowThread; // TODO replace with following:
    /* private SnowThread[] snowThreads; */

    private TextView informationView;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // TODO get number of cores
        // TODO create multiple snowThreads
        // and place them in a list

        snowDisplay = (SnowDisplay) findViewById(R.id.display);
        SurfaceHolder holder = snowDisplay.getHolder();

        informationView = (TextView) findViewById(R.id.textView_display_information);

        snowThread = new SnowThread(snowDisplay.getHolder(), this);

        snowDisplay.setThread(snowThread);

        button = (Button) findViewById(R.id.button_start_pause);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatementConcurrentModificationException
        if (id == R.id.action_settings) {
            return true;
        }

        //currently disabled.
        if (id == R.id.whatsapp_share) {
            //makesaveScreenshot();
            return true;
        }

        if (id == R.id.fps_lock) {
            snowThread.setFpsLock((snowThread.getFpsLock())? false : true);
            item.setChecked((snowThread.getFpsLock())? true : false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onButtonClicked(View view) {

        // shows information about the display on the information textView.
        SurfaceHolder holder = snowDisplay.getHolder();
        String infTxt = holder.getSurfaceFrame().width() + ":" + holder.getSurfaceFrame().height();
        informationView.setText(infTxt);

        // TODO Modify, so all threads are started, paused or resumed
        
        if (!snowThread.isStarted()) {
            snowThread.start();
            button.setText("Pause");
        } else if (snowThread.isPaused()) {
            snowThread.onResume();
            button.setText("Pause");
        } else {
            snowThread.onPause();
            button.setText("Resume");
        }
    }


    public void makesaveScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now +
                    ".jpeg";
            snowDisplay.setDrawingCacheEnabled(true);
            Bitmap bmp = Bitmap.createBitmap(snowDisplay.getDrawingCache());
            snowDisplay.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // TODO make it parallel
    @Override
    public void onPause() {
        super.onPause();
        if (snowThread.isStarted()) {
            snowThread.onPause();
            button.setText("Resume");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO kill all snowThreads
        snowThread.kill();
        try {
            snowThread.join();
            Log.println(Log.INFO, "continuity", "Thread was successfully joined :)");
        } catch (InterruptedException e) {
            Log.w("Exception", "The InterruptedException was thrown while trying to join the snow" +
                    "thread!");
        }
    }
}
