package com.example.cerqueslaberint;

import android.graphics.Canvas;
import android.media.MediaPlayer;

/**
 * Created by Ramon Mas on 10/10/21.
 * Gestió del bitxo
 */

public class Personatge
{
    private Punt posicio;      // coordenades pixels
    private Punt porta;        // casella destí final
    private Punt desplaçament; // deltas x i y
    private Punt casella;

    private Punt[] cami;       // Camí per recorrer

    private int  posicioCami;  // per on va del camí
    private boolean moviment;  // es mou ?

    private Laberint laberint;
    private double t;
    private double v;

    public Personatge(Laberint l)
    {
        posicioCami = 0;
        moviment = false;
        laberint = l;
    }

    public Personatge(Laberint l, Punt p)
    {
        this(l);
        casella = p;
        posicio = l.xy(p.x, p.y);
    }

    public Punt getCasella()
    {
        return casella;
    }

    public void setCasella(Punt c)
    {
        casella = c;
    }

    public int getX()
    {
        return posicio.x;
    }

    public int getY()
    {
        return posicio.y;
    }

    public void borraCami() {cami = null;}

    public void inicia(Cami c, double velocitat)
    {
        if (c == null) return;

        moviment = true;
        v = velocitat;
        t = 0;
        posicioCami = 0;
        cami = new Punt[c.maxim];
        for (int i = 0; i < c.longitud; i++) cami[i] = new Punt(c.cami[c.longitud-1-i].x, c.cami[c.longitud-1-i].y);
        cami[c.longitud] = new Punt(-1,-1);  // per indicar el final del recorregut
        System.out.println("Cami des de " + c.origen().x + ":" + c.origen().y + "  fins a " + c.desti().x + ":" + c.desti().y);

        if (cami == null || c.longitud == 1)
        {
            desplaçament = new Punt(0,0);
        }
        else
        {
            desplaçament = direccio(cami[posicioCami], cami[posicioCami + 1]);
        }
        posicio = new Punt(laberint.filaColumna(c.origen().x, c.origen().y));;
        porta = c.desti();
    }

    public void posaDesti(Punt p)
    {
        porta = p;
    }

    public void posaPosicio(Punt p)
    {
        moviment = false;
        posicio = p;
    }

    public boolean estaDins(Punt c)
    {
        Punt casella = laberint.filaColumna(posicio.x, posicio.y);

        if (c.x == casella.x && c.y == casella.y) return true;
        else return false;
    }

    public void mou()
    {
        if (cami == null) return;

        casella = cami[posicioCami];
        Punt origen = laberint.xy(cami[posicioCami].x, cami[posicioCami].y);
        posicio.x = (int)(origen.x+desplaçament.x*t);
        posicio.y = (int)(origen.y+desplaçament.y*t);
        t+=v;  // actualitza velocitat
    }

    public boolean dinsCami(int f, int c)
    {
        int posicio = 0;

        if (cami == null) return false; // no he cercat cap camí encara

        while (cami[posicio].x != -1)
        {
            if (cami[posicio].x == f && cami[posicio].y == c)
                return true;
            posicio++;
        }
        return false;
    }

    public void pintaCami(Canvas canvas)
    {
        int posicio = posicioCami;

        if (cami == null) return;

        while (cami[posicio].x != -1)
        {
            posicio++;
            if (cami[posicio].x != -1) laberint.pintaPuntet(canvas, cami[posicio]);
        }
    }

    public void actualitzaPosicio()
    {
        if (moviment)  // si no es mou, es queda on està
        {
            if (t < 1)
            {
                // estic anant d'una casella a l'altra
                mou();
            }
            else
            {
                // he arribat a una casella
                posicioCami++;

                if (cami != null)
                {
                     if (cami[posicioCami].x == -1 || cami[posicioCami + 1].x == -1) {
                        moviment = false;
                        casella = laberint.porta;
                        posicio = laberint.xy(casella.x, casella.y);
                        laberint.aturaMusica();
                    } else {
                        laberint.agafaObjectes(cami[posicioCami]);
                        t = 0;
                        desplaçament = direccio(cami[posicioCami], cami[posicioCami + 1]);
                        mou();
                    }
                }
            }
        }
    }

    public Punt direccio(Punt o, Punt d)
    {
        Punt origen = laberint.xy(o.x, o.y);
        Punt desti  = laberint.xy(d.x, d.y);

        // Recta amb equació paramètrica per determinar velocitat
        int dx = desti.x-origen.x;
        int dy = desti.y-origen.y;

        return new Punt(dx,dy);
    }
}
