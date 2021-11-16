package com.example.cerqueslaberint;

import android.graphics.Canvas;

/**
 * Created by Ramon Mas on 10/10/21.
 * Gestió d'un camí de punts, per guardar la solució trobada
 * Un camí és una taula ordenada de punts consecutius
 */

public class Cami
{
    public Punt[] cami;   // conté els punts del camí
    public int longitud;  // número real de punts que conté: llargada del camí
    public int maxim;     // total d'espai reservat, màxim possible de punts

    public Cami(int l)
    {
        cami = new Punt[l];
        longitud = 0;
        maxim = l;
    }

    public void afegeix(Punt p)   // posa un nou punt dins el cami
    {
        cami[longitud++] = new Punt(p);
    }

    public Punt origen()  // inici del camí
    {
        if (longitud == 0) return new Punt(-1,-1);
        else return cami[0];
    }

    public Punt desti()  // acabament del camí
    {
        if (longitud == 0) return new Punt(-1,-1);
        else return cami[longitud-1];
    }

    public String toString() // escriu els punts del camí: System.out.println("Cami:"+alguncami);
    {
        String res = "";

        for (int i=longitud-1; i>=0; i--)
            res += " ("+cami[i].x+":"+cami[i].y+")";
        return res;
    }
}

