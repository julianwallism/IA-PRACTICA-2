package com.example.cerqueslaberint;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.List;
import java.util.Stack;

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

    static final int[] DIRECCIONS = {Laberint.AMUNT, Laberint.DRETA, Laberint.AVALL, Laberint.ESQUERRA};
    static final int[] OFFSET_X = {-1, 0, 1, 0}; //
    static final int[] OFFSET_Y = {0, 1, 0, -1};
    boolean[][] visitats;

    public Cerca(Laberint l) {
        files = l.nFiles;
        columnes = l.nColumnes;
        laberint = l;
        visitats = new boolean[files][columnes];
    }

    public Cami CercaEnAmplada(Punt origen, Punt desti) {
        Cami camiTrobat = new Cami(files * columnes);
        laberint.setNodes(0);

        // Implementa l'algoritme aquí
        visitats = new boolean[files][columnes];
        Coa oberts = new Coa();
        oberts.afegeix(origen);
        Punt punt = null;
        while (!oberts.buida()) {
            punt = (Punt) oberts.treu();
            if (punt.equals(desti)) {
                break;
            } else {
                ArrayList<Punt> successors = generaSuccessors(punt);
                visita(punt);
                for (Punt successor : successors) {
                    oberts.afegeix(successor);
                }
            }
        }
        generaCami(punt, origen, camiTrobat);
        return camiTrobat;
    }

    public Cami CercaEnProfunditat(Punt origen, Punt desti) {
        Cami camiTrobat = new Cami(files * columnes);
        laberint.setNodes(0);

        // Implementa l'algoritme aquí
        visitats = new boolean[files][columnes];
        ArrayDeque<Punt> oberts = new ArrayDeque<>();
        oberts.push(origen);
        Punt punt = null;
        while (!oberts.isEmpty()) {
            punt = (Punt) oberts.pop();
            if (punt.equals(desti)) {
                break;
            } else {
                ArrayList<Punt> successors = generaSuccessors(punt);
                visita(punt);
                for (Punt successor : successors) {
                    oberts.push(successor);
                }
            }
        }
        generaCami(punt, origen, camiTrobat);
        return camiTrobat;
    }

    public Cami CercaAmbHeurística(Punt origen, Punt desti, int tipus) {   // Tipus pot ser MANHATTAN o EUCLIDIA
        int i;
        Cami camiTrobat = new Cami(files * columnes);
        laberint.setNodes(0);

        // Implementa l'algoritme aquí
        PriorityQueue<Punt> tancats = new PriorityQueue<>();
        PriorityQueue<Punt> oberts = new PriorityQueue<>();
        origen.distanciaDeLinici = 0;
        oberts.add(origen);
        Punt punt = null;
        while(!oberts.isEmpty()){
            punt = oberts.peek();
            if(punt.equals(desti))
                break;
            oberts.remove(punt);
            ArrayList<Punt> successors = generaSuccessors(punt);
            for (Punt successor : successors) {
                int pesTotal = punt.distanciaDeLinici + 1;
                if (pesTotal < successor.distanciaDeLinici) {
                    successor.distanciaDeLinici = pesTotal;
                    successor.distanciaAlFinal = successor.distanciaDeLinici + heuristica(successor, desti, tipus);
                    if (!oberts.contains(successor))
                        oberts.add(successor);
                }
            }
        }
        generaCami(punt, origen, camiTrobat);
        return camiTrobat;
    }

    private double heuristica(Punt punt, Punt desti, int tipus){
        if(tipus == EUCLIDEA)
            return euclidea(punt, desti);
        else
            return manhattan(punt, desti);
    }

    private double euclidea(Punt punt, Punt desti){
        float x = Math.abs(punt.x - desti.x);
        float y = Math.abs(punt.y - desti.y);
        return Math.sqrt(x*x + y*y);
    }

    private double manhattan(Punt punt, Punt desti){
        float x = Math.abs(punt.x - desti.x);
        float y = Math.abs(punt.y - desti.y);
        return x + y;
    }


    public Cami CercaViatjant(Punt origen, Punt desti) {
        Cami camiTrobat = new Cami(files * columnes);
        laberint.setNodes(0);

        // Implementa l'algoritme aquí
        camiTrobat.afegeix(desti);

        return camiTrobat;
    }

    private ArrayList<Punt> generaSuccessors(Punt punt) {
        ArrayList<Punt> successors = new ArrayList<>();
        Punt successor;
        for (int i = 0; i < DIRECCIONS.length; i++) {
            successor = new Punt(punt.x + OFFSET_X[i], punt.y + OFFSET_Y[i]);
            if (esPosicioCorrecta(successor) && laberint.pucAnar(punt.x, punt.y, DIRECCIONS[i])) {
                successor.previ = punt;
                successors.add(successor);
            }
        }
        return successors;
    }

    private void visita(Punt p) {
        visitats[p.x][p.y] = true;
    }

    private boolean esPosicioCorrecta(Punt p) {
        return p.x >= 0 && p.x < columnes && p.y >= 0 && p.y < files && !visitats[p.x][p.y];
    }

    private void generaCami(Punt inici, Punt fi, Cami cami){
        while (inici != null && !inici.equals(fi)) {
            cami.afegeix(inici);
            inici = inici.previ;
        }
        cami.afegeix(fi);
    }
}
