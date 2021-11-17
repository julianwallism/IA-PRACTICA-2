package com.example.cerqueslaberint;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ramon Mas on 10/10/21.
 * Classe que conté els diferents algorismes de cerca que s'han d'implementar
 */

/**
 * AUTORS:__________________________________________
 */
/* S'ha d'omplenar la següent taula amb els diferents valors del nodes visitats i la llargada del camí
 * per les diferents grandàries de laberints proposades i comentar breument els resultats obtinguts.
 ****************************************************************************************************************
 *                  Profunditat           Amplada          Manhattan         Euclidiana         Viatjant        *
 *  Laberint     Nodes   Llargada    Nodes   Llargada   Nodes   Llargada   Nodes   Llargada  Nodes   Llargada   *
 * **************************************************************************************************************
 *    Petit
 *    Mitjà
 *    Gran
 *
 * Comentari sobre els resultats obtinguts:
 *
 *
 *
 *
 *
 *
 */

public class Cerca {
    static final public int MANHATTAN = 2;
    static final public int EUCLIDEA = 3;

    Laberint laberint;      // laberint on es cerca
    int files, columnes;    // files i columnes del laberint

    static final int[] DIRECCIONS = {Laberint.DRETA, Laberint.AVALL, Laberint.ESQUERRA, Laberint.AMUNT};
    static final int[] OFFSET_FIL = {-1, 0, 1, 0}; //
    static final int[] OFFSET_COL = {0, 1, 0, -1};
    boolean[][] visitats;

    public Cerca(Laberint l) {
        files = l.nFiles;
        columnes = l.nColumnes;
        laberint = l;
        visitats = new boolean[files][columnes];
    }

    public Cami CercaEnAmplada(Punt origen, Punt desti) {
        resetVisitats();
        Cami camiTrobat = new Cami(files * columnes);
        laberint.setNodes(0);
        // Implementa l'algoritme aquí
        Coa oberts = new Coa();
        oberts.afegeix(origen);
        Punt p = null;
        while (!oberts.buida()) {
            Punt punt = (Punt) oberts.treu();
            //System.out.println(punt + ", Pare: " + punt.previ);
            if (punt.equals(desti)) {
                p = punt;
                break;
            } else {
                for (int i = 0; i < DIRECCIONS.length; i++) {
                    Punt successor = generarSuccessor(punt, i);
                    if (successor != null) {
                        oberts.afegeix(successor);
                    }
                }
                visita(punt);
            }
        }
        while (!p.equals(origen)) {
            camiTrobat.afegeix(p);
            p = p.previ;
        }
        return camiTrobat;
    }

    public Cami CercaEnProfunditat(Punt origen, Punt desti) {
        Cami camiTrobat = new Cami(files * columnes);
        laberint.setNodes(0);

        // Implementa l'algoritme aquí
        camiTrobat.afegeix(desti);

        return camiTrobat;
    }

    public Cami CercaAmbHeurística(Punt origen, Punt desti, int tipus) {   // Tipus pot ser MANHATTAN o EUCLIDIA
        int i;
        Cami camiTrobat = new Cami(files * columnes);
        laberint.setNodes(0);

        // Implementa l'algoritme aquí
        camiTrobat.afegeix(desti);

        return camiTrobat;
    }


    public Cami CercaViatjant(Punt origen, Punt desti) {
        Cami camiTrobat = new Cami(files * columnes);
        laberint.setNodes(0);

        // Implementa l'algoritme aquí
        camiTrobat.afegeix(desti);

        return camiTrobat;
    }

    private Punt generarSuccessor(Punt punt, int dir) {
        Punt successor = new Punt(punt.x + OFFSET_FIL[dir], punt.y + OFFSET_COL[dir]);
        if (esPosicioCorrecta(successor) && laberint.pucAnar(punt.x, punt.y, DIRECCIONS[dir])) {
            if (!visitat(successor)) {
                successor.previ = punt;
                return successor;
            }
        }
        return null;
    }

    private void visita(Punt p) {
        visitats[p.x][p.y] = true;
    }

    private boolean esPosicioCorrecta(Punt p) {
        return p.x >= 0 && p.x < files && p.y >= 0 && p.y < columnes;
    }

    private boolean visitat(Punt p) {
        return visitats[p.x][p.y];
    }

    private void resetVisitats(){
        visitats = new boolean[files][columnes];
    }
}
