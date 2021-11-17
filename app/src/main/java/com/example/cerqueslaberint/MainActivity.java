package com.example.cerqueslaberint;

/**
 * Created by Ramon Mas on 10/10/21.
 * Gestiona el cicle de vida de l'aplicació i el menú principal d'opcions
 */

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;


public class MainActivity extends GeneralActivity {
    private MainGame joc;
    private AlertDialog.Builder alert;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.init();
        setContentView(R.layout.joc);
    }

    @Override
    public void onStart() {
        super.onStart();
        FrameLayout pantalla = (FrameLayout) findViewById(R.id.layoutJoc);

        // ara posam per programa la zona de dibuix
        joc = new MainGame(this);
        pantalla.addView(joc);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    protected void onResume()
    {
        super.onResume();
    }

    protected void onRestart()
    {
        super.onRestart();
    }

    public void musicaON()
    {
        // per la música
        mediaPlayer = MediaPlayer.create(this, R.raw.menja);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void musicaOFF() {
        mediaPlayer.release();
    }

    protected void onPause()
    {
        super.onPause();
        joc.aturar();
    }

    protected void onStop()
    {
        super.onStop();
        if (mediaPlayer != null) mediaPlayer.release();
        joc.aturar();
        unbindDrawables(findViewById(R.id.layoutJoc));
    }

    private void unbindDrawables(View view)
    {
        if (view.getBackground() != null)
        {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView))
        {
            for (int i=0; i<((ViewGroup) view).getChildCount();i++)
            {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    protected void onDestroy()
    {
        super.onDestroy();
        joc.aturar();
        unbindDrawables(findViewById(R.id.layoutJoc));
        System.gc();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_newgame:  // genera un nou laberint amb les opcions triades
                startActivity(new Intent(MainActivity.this, OpcionsActivity.class));
                break;
            case R.id.action_amplada:     joc.setCerca(MainGame.AMPLADA); break;
            case R.id.action_profunditat: joc.setCerca(MainGame.PROFUNDITAT); break;
            case R.id.action_manhattan:   joc.setCerca(MainGame.MANHATTAN); break;
            case R.id.action_euclidea:    joc.setCerca(MainGame.EUCLIDEA); break;
            case R.id.action_viatjant:    joc.setCerca(MainGame.VIATJANT); break;
            default: return super.onOptionsItemSelected(item);
        }
        return true;
    }
}

