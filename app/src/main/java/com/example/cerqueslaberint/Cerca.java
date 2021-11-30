package com.example.cerqueslaberint;

import java.sql.SQLOutput;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    static final int[][] PERMUTACIONES = {{1, 2, 3, 4}, {1, 2, 4, 3}, {1, 3, 2, 4}, {1, 3, 4, 2}, {1, 4, 2, 3},
            {1, 4, 3, 2}, {2, 1, 3, 4}, {2, 1, 4, 3}, {2, 3, 1, 4}, {2, 3, 4, 1},
            {2, 4, 1, 3}, {2, 4, 3, 1}, {3, 1, 2, 4}, {3, 1, 4, 2}, {3, 2, 1, 4},
            {3, 2, 4, 1}, {3, 4, 1, 2}, {3, 4, 2, 1}, {4, 1, 2, 3}, {4, 1, 3, 2},
            {4, 2, 1, 3}, {4, 2, 3, 1}, {4, 3, 1, 2}, {4, 3, 2, 1}};


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
        while (!oberts.isEmpty()) {
            punt = oberts.peek();
            if (punt.equals(desti))
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

    private double heuristica(Punt punt, Punt desti, int tipus) {
        if (tipus == EUCLIDEA)
            return euclidea(punt, desti);
        else
            return manhattan(punt, desti);
    }

    private double euclidea(Punt punt, Punt desti) {
        float x = Math.abs(punt.x - desti.x);
        float y = Math.abs(punt.y - desti.y);
        return Math.sqrt(x * x + y * y);
    }

    private double manhattan(Punt punt, Punt desti) {
        float x = Math.abs(punt.x - desti.x);
        float y = Math.abs(punt.y - desti.y);
        return x + y;
    }

    public Cami CercaViatjant(Punt origen, Punt desti) {
/*        laberint.setNodes(0);
        // Implementa l'algoritme aquí
        //Calculamos distancias entre punto inicial y los puntos a visitar
        int[][] dist = new int[6][6];
        for (int i = 0; i < 4; i++) {
            dist[0][i + 1] = CercaAmbHeurística(origen, laberint.getObjecte(i), EUCLIDEA).longitud;
        }
        //Calculamos distancias entre los 4 puntos a visitar
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 4; j++) {
                dist[i + 1][j + 1] = CercaAmbHeurística(laberint.getObjecte(i), laberint.getObjecte(j), EUCLIDEA).longitud;
                dist[j + 1][i + 1] = CercaAmbHeurística(laberint.getObjecte(i), laberint.getObjecte(j), EUCLIDEA).longitud;
            }
        }
        //Calculamos distancias de los 4 puntos a visitar al destino
        for (int i = 0; i < 4; i++) {
            dist[i + 1][5] = CercaAmbHeurística(laberint.getObjecte(i), desti, EUCLIDEA).longitud;
        }
        //ATENTOS AL CODIGO ESPAGUETI:
        int[] permutacion = new int[4];
        int coste = 9999;
        for (int[] perm : PERMUTACIONES) {
            //coste principio -> primer nodo
            int costeAux = dist[0][perm[0]];
            //coste entre nodos
            for (int i = 0; i < 3; i++) {
                costeAux += dist[perm[i]][perm[i + 1]];
            }
            //Coste ultimo nodo -> destino
            costeAux += dist[perm[3]][5];
            if (costeAux < coste) {
                coste = costeAux;
                permutacion = perm;
            }
        }
        Cami camiTrobat = new Cami(coste);
        System.out.println("permutacion: " + java.util.Arrays.toString(permutacion) + " Coste: " + coste);
        for (int[] row : dist) {
            System.out.println(java.util.Arrays.toString(row));
        }
        //camiAux= CercaAmbHeurística(laberint.getObjecte(permutacion[3]),desti,EUCLIDEA);
        Cami camiAux = CercaEnAmplada(laberint.getObjecte(permutacion[3] - 1), desti);
        for (int i = 0; i < camiAux.longitud - 1; i++) {
            camiTrobat.afegeix(camiAux.cami[i]);
        }
        for (int i = 3; i > 0; i--) {
            // camiAux=CercaAmbHeurística(laberint.getObjecte(permutacion[i]-1),laberint.getObjecte(permutacion[i+1]-1),EUCLIDEA);
            camiAux = CercaEnAmplada(laberint.getObjecte(permutacion[i] - 1), laberint.getObjecte(permutacion[i - 1] - 1));
            for (int j = camiAux.longitud - 1; j > 0; j--) {
                camiTrobat.afegeix(camiAux.cami[j]);
            }
        }

        //Cami camiAux= CercaAmbHeurística(origen,laberint.getObjecte(permutacion[0]-1),EUCLIDEA);
        camiAux = CercaEnAmplada(origen, laberint.getObjecte(permutacion[0] - 1));
        for (int i = 0; i < camiAux.longitud - 1; i++) {
            camiTrobat.afegeix(camiAux.cami[i]);
        }
        return camiTrobat;*/
        return viatjant(origen, desti);
    }

    private Cami viatjant(Punt origen, Punt desti){
        Integer[] nodes = {0, 1, 2, 3};
        List<Integer[]> camins = permutacions(nodes);
        Cami camiFinal = null;
        camiFinal.longitud = Integer.MAX_VALUE;
        for(Integer[] cami: camins){
            Cami candidat = generaCami(cami, origen, desti);
            if(candidat.longitud < camiFinal.longitud){
                camiFinal = candidat;
            }
        }
        return camiFinal;
    }

    private Cami generaCami(Integer[] cami, Punt origen, Punt desti){
        Cami candidat = new Cami(files * columnes * cami.length);
        Punt darrerNode = laberint.getObjecte(cami[cami.length - 1]);
        Cami darrerNodeDesti = CercaEnAmplada(darrerNode, desti);
        candidat = concatenaCami(darrerNodeDesti, candidat);
        for (int i = cami.length - 1; i > 0; i--) {
            Punt node = laberint.getObjecte(cami[i]);
            Punt seguentNode = laberint.getObjecte(cami[i - 1]);
            Cami parcial = CercaEnAmplada(seguentNode, node);
            candidat = concatenaCami(parcial, candidat);
        }
        Punt primerNode = laberint.getObjecte(cami[0]);
        Cami iniciPrimerNode = CercaEnAmplada(origen, primerNode);
        candidat = concatenaCami(iniciPrimerNode, candidat);
        return candidat;
    }

    private Cami concatenaCami(Cami origen, Cami destinatari){
        for(Punt p: origen.cami) {
            if (p != null) {
                destinatari.afegeix(p);
            }
        }
        return destinatari;
    }

    private static <T> List<T[]> permutacions(T[] original){
        List<T[]> permutacions = new ArrayList<>();
        permutacionsHelper(original, permutacions, original.length);
        return permutacions;
    }

    private static <T> void permutacionsHelper(T[] permutacio, List<T[]> permutacions, int n) {
        if (n <= 1) {
            permutacions.add(permutacio);
        }
        T[] permutacioTemp = Arrays.copyOf(permutacio, permutacio.length);
        for (int i = 0; i < n; i++) {
            intercanvia(permutacioTemp, i, n - 1);
            permutacionsHelper(permutacioTemp, permutacions, n - 1);
            intercanvia(permutacioTemp, i, n - 1); // backtracking
        }
    }

    private static <T> void intercanvia(T[] array, int primer, int segon) {
        T temp = array[primer];
        array[primer] = array[segon];
        array[segon] = temp;
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

    private void generaCami(Punt inici, Punt fi, Cami cami) {
        while (inici != null && !inici.equals(fi)) {
            cami.afegeix(inici);
            inici = inici.previ;
        }
        cami.afegeix(fi);
    }
}
