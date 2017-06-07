package com.sandersoft.games.sandersoft2048.views;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sandersoft.games.sandersoft2048.R;
import com.sandersoft.games.sandersoft2048.SoundManager;
import com.sandersoft.games.sandersoft2048.utils.Globals;

public class ActivitySplash extends AppCompatActivity {

    long mSplashTime = 2000;
    SplashThread mSplashThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        boolean light = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Globals.THEME_LIGHT, false);

        RelativeLayout lay_splash = (RelativeLayout) findViewById(R.id.lay_splash);
        lay_splash.setBackgroundResource(light ? R.color.color_LBackground : R.color.color_Background);
        ImageView img_logo = (ImageView) findViewById(R.id.img_logo);
        img_logo.setImageResource(light ? R.mipmap.logo_sander_soft_blk : R.mipmap.logo_sander_soft);
        lay_splash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        mSplashThread = new SplashThread();
        mSplashThread.start();

        SoundManager.prepareMedia(this);
    }

    /**
     * Stops the thread and closes this activity, opening the main activity
     */
    public void stop(){
        runOnUiThread(new Runnable() {
            public void run() {
                if (mSplashThread != null) {
                    mSplashThread = null;

                    Intent intent = new Intent(ActivitySplash.this, ActivityMain.class);
                    startActivity(intent);

                    finish();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    /**
     * Thread thats waits to remove the splash screen
     * @author Sander
     */
    public class SplashThread extends Thread{
        public SplashThread(){

        }

        public void run(){
            try {
                Thread.sleep(mSplashTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ActivitySplash.this.stop();
        }
    }

}
