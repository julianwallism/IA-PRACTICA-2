package com.example.cerqueslaberint;

/**
 * Created by Ramon Mas on 10/10/21.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class MainGame extends SurfaceView {
    private SurfaceHolder holder;
    private Refresc jocLoopThread;

    static final int AMPLADA     = 0;
    static final int PROFUNDITAT = 1;
    static final int MANHATTAN   = 2;
    static final int EUCLIDEA    = 3;
    static final int VIATJANT    = 4;
    static final String nomsCerca[] = {"Amplada","Profunditat","Manhattan","Euclidea","Viatjant de comerç"};

    private int tipusCerca = AMPLADA;  // cerca per defecte
    private Cerca cerca;

    private Context context;
    private boolean jocAturat;
    private double  velocitat;

    private Laberint laberint;
    private Personatge homonet;

    public MainGame(Context cont)
    {
        super(cont);
        context = cont;

        jocLoopThread = new Refresc(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceCreated(SurfaceHolder arg0)
            {
                initGame();
                jocLoopThread.setRunning(true);
                jocLoopThread.start();
            }

            public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
            }

            public void surfaceDestroyed(SurfaceHolder arg0) {
                jocLoopThread.setRunning(false);
            }
        });
    }

    public void setCerca(int tipus)
    {
        tipusCerca = tipus;
        laberint.setMissatge(nomsCerca[tipus]);
        if (tipusCerca == VIATJANT)  // posa els punts
            laberint.mostraObjectes();
        else
            laberint.amagaObjectes();
    }

    public void inicialitzaLaberint()
    {
        laberint.inicialitza();
    }

    public void initGame()
    {
        jocAturat = false;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int files     = sharedPref.getInt("files",    5);             // 5x5 per defecte
        int columnes  = sharedPref.getInt("columnes", 5);             // 5x5 per defecte
        int parets    = sharedPref.getInt("parets",   4);             // 40% per defecte
        velocitat     = (1+sharedPref.getInt("velocitat", 3)) /10.0 ; // 0.3 per defecte

        laberint = new Laberint(context, files+2, columnes+2, parets*10);  // sum 2 perquè l'index que retorna la selecció comença per zero i el mínim de files i columnes és 2
        cerca = new Cerca(laberint);
        laberint.setMissatge(nomsCerca[tipusCerca]);
        partir();
    }

    protected void paintScreen(Canvas canvas)

    {
        if (canvas == null) return; // si no tenim pantalla de dibuix, no pintis res
        laberint.pinta(canvas);
    }


    public boolean onTouchEvent(MotionEvent event)
    {
        int lastX, lastY;

        if (jocAturat) return false;

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                return true;
            }
            case MotionEvent.ACTION_UP:
            {
                Cami cami;

                lastX = (int) event.getX();
                lastY = (int) event.getY();
                aturar();

                Punt posicio = laberint.filaColumna(lastX,lastY);
                if (posicio.x != -1) {
                    homonet = new Personatge(laberint, laberint.filaColumna(lastX, lastY));
                    laberint.setBitxo(homonet);

                    switch (tipusCerca)
                    {
                        case PROFUNDITAT:
                            cami = cerca.CercaEnProfunditat(homonet.getCasella(), laberint.getPorta());
                            break;
                        case AMPLADA:
                            cami = cerca.CercaEnAmplada(homonet.getCasella(), laberint.getPorta());
                            break;
                        case MANHATTAN:
                            cami = cerca.CercaAmbHeurística(homonet.getCasella(), laberint.getPorta(), Cerca.MANHATTAN);
                            break;
                        case EUCLIDEA:
                            cami = cerca.CercaAmbHeurística(homonet.getCasella(), laberint.getPorta(), Cerca.EUCLIDEA);
                            break;
                        case VIATJANT:
                            cami = cerca.CercaViatjant(homonet.getCasella(), laberint.getPorta());
                            break;
                        default:
                            cami = cerca.CercaEnAmplada(homonet.getCasella(), laberint.getPorta());
                    }
                    if (cami != null) {
                        laberint.setLongitudCami(cami.longitud);
                        ((MainActivity) context).musicaON();
                        homonet.inicia(cami, velocitat);
                    }

                    partir();
                }
            }
        }

        return super.onTouchEvent(event);
    }

    public void musicaOFF() {((MainActivity) context).musicaOFF();}
    public void aturar()
    {
        jocLoopThread.setRunning(false);
    }

    public void partir()
    {
        jocLoopThread.setRunning(true);
    }
}

