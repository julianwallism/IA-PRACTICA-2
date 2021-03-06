package com.example.cerqueslaberint;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ramon Mas on 10/10/21.
 * Classe que conté els diferents algorismes de cerca que s'han d'implementar
 */

/**
 * AUTORS: Jonathan Salisbury, Julián Wallis, Juan Ignacio Velàzquez
 */
/* S'ha d'omplenar la següent taula amb els diferents valors del nodes visitats i la llargada del camí
 * per les diferents grandàries de laberints proposades i comentar breument els resultats obtinguts.
 ****************************************************************************************************************
 *                  Profunditat           Amplada          Manhattan         Euclidiana         Viatjant        *
 *  Laberint     Nodes   Llargada    Nodes   Llargada   Nodes   Llargada   Nodes   Llargada  Nodes   Llargada   *
 * **************************************************************************************************************
 *    Petit        62       38        89        20       57        19       60       19      6042        62
 *    Mitjà        76       66        394       40       225       49       279      47      19926       142
 *    Gran         637      209       884       55       392       46       480      46      43908       188
 *
 *
 * Comentari sobre els resultats obtinguts:
 * Per a les diferents llargaries hem utilitzat les seguents mesures respectivament 10, 20, 30.
 * Per a cada algorisme s'ha utilitzat el mateix laberint començant desde la el costat contrari de
 * la sortida.
 * S'ha emprat un valor de 30%
 *
 * Podem veure com amb la cerca en profunditat obtenim un pitjor resultat que amb les altres cerques
 * però visitant menys nodes. Això concorda amb la teoria vista a classe, ja que la cerca en profunditat
 * comprova menys casos que la resta de cerques. Això fa que retorni un pitjor resultat més ràpidament.
 *
 * Sobre la cerca en amplada podem veure que el número de nodes visitats és proper al número màxim
 * possible (n*n) ja que la sortida està a l'altre punta del laberint.
 *
 * Les cerques amb heurístiques han presentat els millors resultats entre tots. La de manhattan ha
 * visitat 100 nodes menys que la Euclidiana, suposam que és per la disposició del laberint i la
 * posició d'inici.
 *
 * La cerca amb viatjant té un gran nombre de nodes visitats degut a que per resoldre s'han de
 * de calcular (n+1)! cerques, sent n el nombre de punts a visitar.
 *
 *
 *
 */

public class Cerca {

    static final public int MANHATTAN = 2;
    static final public int EUCLIDEA = 3;

    Laberint laberint;      // laberint on es cerca
    int files, columnes;    // files i columnes del laberint
    int nodesViatjant=0;
    /* Constants de moviment */
    static final int[] DIRECCIONS = {Laberint.AMUNT, Laberint.DRETA, Laberint.AVALL, Laberint.ESQUERRA};
    static final int[] OFFSET_X = {-1, 0, 1, 0};
    static final int[] OFFSET_Y = {0, 1, 0, -1};

    /* Variables */
    boolean[][] visitats;

    public Cerca(Laberint l) {
        files = l.nFiles;
        columnes = l.nColumnes;
        laberint = l;
        visitats = new boolean[files][columnes];
    }

    /**
     * Mètode que resol la cerca del laberint mitjançant recorrgut en amplada.
     *
     * @param origen Punt inicial del qual es parteix.
     * @param desti  Punt final al que s'ha de arribar.
     * @return el cami solució generat.
     */
    public Cami CercaEnAmplada(Punt origen, Punt desti) {
        Cami camiTrobat = new Cami(files * columnes);
        laberint.setNodes(0);

        // Implementa l'algoritme aquí
        visitats = new boolean[files][columnes];
        Coa oberts = new Coa();
        oberts.afegeix(origen);
        Punt punt = null;

        while (!oberts.buida()) {
            /*
            Extreïm un node de la coa d'oberts i hi afegim els seus successors
            que no hagin estat visitats
             */
            punt = (Punt) oberts.treu();
            laberint.nodes++;
            if (punt.equals(desti)) { // Si el node és el destí sortim del bucle
                break;
            } else {
                ArrayList<Punt> successors = generaSuccessors(punt);
                visitats[punt.x][punt.y] = true;
                for (Punt successor : successors) {
                    if(!visitats[successor.x][successor.y]) {
                        successor.previ = punt;
                        oberts.afegeix(successor);
                        visitats[successor.x][successor.y]=true;
                    }
                }
            }
        }
        generaCami(punt, origen, camiTrobat);
        return camiTrobat;
    }

    /**
     * Mètode que resól la cerca del laberint mitjançant recorrgut en profunditat.
     *
     * @param origen Punt inicial del qual es parteix.
     * @param desti  Punt final al que s'ha de arribar.
     * @return el cami solució generat.
     */
    public Cami CercaEnProfunditat(Punt origen, Punt desti) {
        Cami camiTrobat = new Cami(files * columnes);
        laberint.setNodes(0);

        // Implementa l'algoritme aquí
        visitats = new boolean[files][columnes];
        ArrayDeque<Punt> oberts = new ArrayDeque<>();
        oberts.push(origen);
        Punt punt = null;
        while (!oberts.isEmpty()) {
            /*
            Extreïm un node de la pila d'oberts i hi afegim els seus successors
            que no hagin estat visitats
             */
            punt = (Punt) oberts.pop();
            laberint.nodes++;
            if (punt.equals(desti)) { // Si el node és el destí sortim del bucle
                break;
            } else {
                ArrayList<Punt> successors = generaSuccessors(punt);
                visitats[punt.x][punt.y] = true;
                for (Punt successor : successors) {
                    if(!visitats[successor.x][successor.y]){
                        successor.previ=punt;
                        oberts.push(successor);
                        visitats[successor.x][successor.y] = true;
                    }
                }
            }
        }
        generaCami(punt, origen, camiTrobat);
        return camiTrobat;
    }

    /**
     * Mètode que resól la cerca del laberint mitjançant recorrgut en amplada.
     *
     * @param origen Punt inicial del qual es parteix.
     * @param desti  Punt final al que s'ha de arribar.
     * @param tipus  Enter que representa el tipus d'heuristica a utilitzar.
     * @return el cami solució generat.
     */
    public Cami CercaAmbHeurística(Punt origen, Punt desti, int tipus) {   // Tipus pot ser MANHATTAN o EUCLIDIA
        Punt[][] matriuPunts = inicialitzaMatriu(files, columnes);
        Cami camiTrobat = new Cami(files * columnes);
        laberint.setNodes(0);
        // Implementa l'algoritme aquí

        ArrayList<Punt> oberts = new ArrayList<>();
        ArrayList<Punt> tancats = new ArrayList<>();
        Punt pare = new Punt();
        origen.distanciaDeLinici = 0;
        origen.distanciaAlFinal = heuristica(origen, desti, tipus);
        oberts.add(origen);
        while(!oberts.isEmpty()){
            // Extreïm de la llista de oberts el node amb menor cost
            Collections.sort(oberts);
            pare = oberts.get(0); //M
            if(desti.equals(pare)){
                break;
            }
            oberts.remove(pare);
            tancats.add(pare);
            // Generam els successors del node
            ArrayList<Punt> successors = generaSuccessorsMatriu(matriuPunts, pare);
            int cost = pare.distanciaDeLinici + 1; //g(n') = g(n) + 1
            for(Punt successor: successors){ //N
                //Si el node successor ja està dins oberts i no hem trobat un millor camí passam al
                //següent successor
                if(oberts.contains(successor) && cost>=successor.distanciaDeLinici){
                    continue;
                }//Si el node successor ja està dins tancats i no hem trobat un millor camí passam al
                //següent successor
                if(tancats.contains(successor) && cost>=successor.distanciaDeLinici){
                    continue;
                }
                //Eliminam les occurrències del successor a la llista de tancats
                if(tancats.contains(successor)){
                    tancats.remove(successor);
                }
                //Eliminam les ocurrències del successor a la llista d'oberts
                if(oberts.contains(successor)) {
                    oberts.remove(successors);
                }
                //Inicialitzam les dades del successor i l'afegim a la coa d'oberts
                successor.previ=pare;
                successor.distanciaAlFinal= heuristica(successor, desti, tipus);
                successor.distanciaDeLinici = cost;
                laberint.nodes++;
                oberts.add(successor);
            }
        }
        nodesViatjant += laberint.nodes;
        generaCami(pare, origen, camiTrobat);
        return camiTrobat;
    }

    /**
     * Mètode que obté el camí més curt entre l'origen i el destí passant per els cuatre nodes.
     *
     * @param origen Punt inicial del qual es parteix.
     * @param desti  Punt final al que s'ha de arribar.
     * @return el cami solució generat.
     */
    public Cami CercaViatjant(Punt origen, Punt desti) {
        laberint.setNodes(0);
        nodesViatjant=0;
        Integer[] nodes = {0, 1, 2, 3};
        List<Integer[]> camins = permutacions(nodes);
        Cami camiFinal = new Cami(files*columnes);
        camiFinal.longitud = Integer.MAX_VALUE;
        for (Integer[] cami : camins) {
            Cami candidat = generaCami(cami, origen, desti);
            if (candidat.longitud < camiFinal.longitud) {
                camiFinal = candidat;
            }
        }
        laberint.setNodes(nodesViatjant);
        return camiFinal;
    }

    /**
     * Mètode que calcula la distancia entre dos punts fent servir la heuristica indicada.
     *
     * @param punt  Punt inicial per calcular la distancia
     * @param desti Punt final per calcular la distancia
     * @param tipus Enter que representa el tipus d'heuristica a utilitzar.
     * @return el modul de la distancia amb la heuristica aplicada.
     */
    private double heuristica(Punt punt, Punt desti, int tipus) {
        float x, y;
        if (tipus == EUCLIDEA) {
            x = Math.abs(punt.x - desti.x);
            y = Math.abs(punt.y - desti.y);
            return Math.sqrt(x * x + y * y);
        } else {
            x = Math.abs(punt.x - desti.x);
            y = Math.abs(punt.y - desti.y);
            return x + y;
        }
    }
    /**
     * Mètode que inicialitza una matriu de punts.
     *
     * @param files  Punt inicial per calcular la distancia
     * @param columnes Punt final per calcular la distancia
     * @return la matriu de punts.
     */
    public Punt[][] inicialitzaMatriu(int files, int columnes){
        Punt[][] matriu = new Punt[files][columnes];
        for (int i = 0; i < files; i++) {
            for (int j = 0; j < columnes; j++) {
                matriu[i][j] = new Punt(i,j);
            }
        }
        return matriu;
    }

    /**
     * Mètode que genera el camí entre l'origen i el desti passant per tots els nodes en l'ordre
     * indicat dins l'array cami.
     *
     * @param cami   Ordre pel que hem de passar pels nodes.
     * @param origen Punt d'origen del cami a cercar
     * @param desti  Punt desti del cami a cercar
     * @return el cami solució generat.
     */
    private Cami generaCami(Integer[] cami, Punt origen, Punt desti) {
        Cami candidat = new Cami(files * columnes * cami.length);
        Punt darrerNode = laberint.getObjecte(cami[cami.length - 1]);
        Cami darrerNodeDesti = CercaAmbHeurística(darrerNode, desti, EUCLIDEA);
        candidat = concatenaCami(darrerNodeDesti, candidat);
        for (int i = cami.length - 1; i > 0; i--) {
            Punt node = laberint.getObjecte(cami[i]);
            Punt seguentNode = laberint.getObjecte(cami[i - 1]);
            Cami parcial = CercaAmbHeurística(seguentNode, node, EUCLIDEA);
            candidat = concatenaCami(parcial, candidat);
        }
        Punt primerNode = laberint.getObjecte(cami[0]);
        Cami iniciPrimerNode = CercaAmbHeurística(origen, primerNode, EUCLIDEA);
        candidat = concatenaCami(iniciPrimerNode, candidat);
        return candidat;
    }

    /**
     * Mètode que concatena el cami origen al final del cami destinatari
     *
     * @param origen      cami d'on s'obtenen les dades
     * @param destinatari cami on es concatenen les dades
     * @return nou cami concatenat
     */
    private Cami concatenaCami(Cami origen, Cami destinatari) {
        for (Punt p : origen.cami) {
            if (p != null) {
                destinatari.afegeix(p);
            }
        }
        return destinatari;
    }

    /**
     * Mètode 'wrapper' per a la funció recursiva de permutacions, aquest mètode crea la llista
     * resultant, crida al mètode recursiu i retorna la llista generada.
     *
     * @param original Array amb els elements originals a permutar
     * @param <T>      Generic per a que el metode funcioni per a tots els tipus de dades.
     * @return Llista amb totes les permutacions de l'array original
     */
    private static <T> List<T[]> permutacions(T[] original) {
        List<T[]> permutacions = new ArrayList<>();
        permutacionsRecursiu(original, permutacions, original.length);
        return permutacions;
    }

    /**
     * Mètode recursiu que genera totes les permutaciones dels continguts d'un array.
     *
     * @param permutacio   permutació actual
     * @param permutacions Llista on es guarden totes les permutacions generades
     * @param n            longitud de la permutació actual
     * @param <T>          Generic per a que el metode funcioni per a tots els tipus de dades.
     */
    private static <T> void permutacionsRecursiu(T[] permutacio, List<T[]> permutacions, int n) {
        if (n == 0)
            permutacions.add(permutacio);
        T[] permutacioTemp = Arrays.copyOf(permutacio, permutacio.length);
        for (int i = 0; i < n; i++) {
            intercanvia(permutacioTemp, i, n - 1);
            permutacionsRecursiu(permutacioTemp, permutacions, n - 1);
            intercanvia(permutacioTemp, i, n - 1); // backtracking
        }
    }

    /**
     * Mètode que intercanvia dos elements d'un array
     *
     * @param array  array que conté els valors a intercanviar.
     * @param primer primer element a intercanviar
     * @param segon  segon element a intercanviar
     * @param <T>    Generic per a que el metode funcioni per a tots els tipus de dades.
     */
    private static <T> void intercanvia(T[] array, int primer, int segon) {
        T temp = array[primer];
        array[primer] = array[segon];
        array[segon] = temp;
    }

    /**
     * Mètode que genera tots els punts que es poden visitar desde el punt dondat.
     *
     * @param punt punt original
     * @return Llista dels punts als que es poden anar.
     */
    private ArrayList<Punt> generaSuccessors(Punt punt) {
        ArrayList<Punt> successors = new ArrayList<>();
        Punt successor;
        for (int i = 0; i < DIRECCIONS.length; i++) {
            successor = new Punt(punt.x + OFFSET_X[i], punt.y + OFFSET_Y[i]);
            if (esPosicioCorrecta(successor) && laberint.pucAnar(punt.x, punt.y, DIRECCIONS[i])) {
                //successor.previ = punt;
                successors.add(successor);
            }
        }
        return successors;
    }

    /**
     * Mètode que genera tots els punts que es poden visitar desde el punt dondat.
     *
     * @param matriu
     * @param punt
     * @return Llista dels punts als que es poden anar.
     */
    private ArrayList<Punt> generaSuccessorsMatriu(Punt[][] matriu, Punt punt) {
        ArrayList<Punt> successors = new ArrayList<>();
        Punt successor;
        for (int i = 0; i < DIRECCIONS.length; i++) {
            successor = new Punt(punt.x + OFFSET_X[i], punt.y + OFFSET_Y[i]);
            if (esPosicioCorrecta(successor) && laberint.pucAnar(punt.x, punt.y, DIRECCIONS[i])) {
                //successor.previ = punt;
                successors.add(matriu[punt.x+OFFSET_X[i]][punt.y+OFFSET_Y[i]]);
            }
        }
        return successors;
    }

    /**
     * Mètode que comprova si el punt donat esta dins el laberint.
     *
     * @param p punt a comprovar
     * @return boolean que representa si el punt es dintre o no.
     */
    private boolean esPosicioCorrecta(Punt p) {
        return p.x >= 0 && p.x < columnes && p.y >= 0 && p.y < files;
    }

    /**
     * Mètode que genera el cami entre dos punts mitjançant els punts previs.
     *
     * @param inici Punt inicial del camí
     * @param fi    Punt final del camí
     * @param cami  Cami generat entre eles dos punts
     */
    private void generaCami(Punt inici, Punt fi, Cami cami) {
        while (inici != null && !inici.equals(fi)) {
            cami.afegeix(inici);
            inici = inici.previ;
        }
        cami.afegeix(fi);
    }
}
