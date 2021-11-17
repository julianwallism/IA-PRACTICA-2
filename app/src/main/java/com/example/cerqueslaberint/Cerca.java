package com.example.cerqueslaberint;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ramon Mas on 10/10/21.
 * Classe que conté els diferents algorismes de cerca que s'han d'implementar
 */

/**
 *   AUTORS:__________________________________________
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

public class    Cerca
{
    static final public int MANHATTAN = 2;
    static final public int EUCLIDEA  = 3;

    Laberint laberint;      // laberint on es cerca
    int files, columnes;    // files i columnes del laberint

    static final int[] DIRECCIONS = {Laberint.AMUNT, Laberint.DRETA, Laberint.AVALL, Laberint.ESQUERRA};
    static final int[] OFFSET_FIL = {1, 0, -1, 0};
    static final int[] OFFSET_COL = {0, -1, 0, 1};
    boolean[][] visitats;

    public Cerca(Laberint l)
    {
        files = l.nFiles;
        columnes = l.nColumnes;
        laberint = l;
        visitats = new boolean[files][columnes];
    }

    public Cami CercaEnAmplada(Punt origen, Punt desti)
    {
        Cami camiTrobat = new Cami(files*columnes);
        laberint.setNodes(0);
        System.out.println("(" + desti.x + ", " + desti.y + ")");
        // Implementa l'algoritme aquí
        Coa oberts = new Coa();
        oberts.afegeix(origen);
        while(!oberts.buida()){
            Punt punt = (Punt) oberts.treu();
            if (punt.equals(desti)){
                break;
            } else {
                for(int i = 0; i < DIRECCIONS.length; i++){
                    Punt successor = generarSuccessor(punt, i);
                    if(successor != null){
                        successor.previ = punt;
                        if(successor.equals(desti)){
                            break;
                        }
                        oberts.afegeix(successor);
                    }
                }
                visita(punt);
            }
        }
        System.out.println("(" + desti.previ.x + ", " + desti.previ.y + ")");
        Punt pare = desti.previ;
        ArrayList<Punt> recorregut = new ArrayList<>();
        recorregut.add(desti);
        while(!pare.equals(origen)){
            recorregut.add(pare);
            pare = pare.previ;
        }
        recorregut.remove(0);
        Collections.reverse(recorregut);
        camiTrobat.cami = recorregut.toArray(camiTrobat.cami);
        for(Punt p: camiTrobat.cami){
            System.out.println(p);
            System.out.println("(" + p.x + ", " + p.y + ")");
        }
        return camiTrobat;
    }

    public Cami CercaEnProfunditat(Punt origen, Punt desti)
    {
        Cami camiTrobat = new Cami(files*columnes);
        laberint.setNodes(0);

        // Implementa l'algoritme aquí
        camiTrobat.afegeix(desti);

        return camiTrobat;
    }

    public Cami CercaAmbHeurística(Punt origen, Punt desti, int tipus)
    {   // Tipus pot ser MANHATTAN o EUCLIDIA
        int i;
        Cami camiTrobat = new Cami(files*columnes);
        laberint.setNodes(0);

        // Implementa l'algoritme aquí
        camiTrobat.afegeix(desti);

        return camiTrobat;
    }


    public Cami CercaViatjant(Punt origen, Punt desti)
    {
        Cami camiTrobat = new Cami(files*columnes);
        laberint.setNodes(0);

        // Implementa l'algoritme aquí
        camiTrobat.afegeix(desti);

        return camiTrobat;
    }

    private Punt generarSuccessor(Punt punt, int dir){
        //System.out.println("Punt: " + punt.x + ", " + punt.y + ", dir = " + dir + ", DIRECCIONS[dir]): " + DIRECCIONS[dir]);
        Punt successor = new Punt(punt.x + OFFSET_COL[dir], punt.y + OFFSET_FIL[dir]);
        if(esPosicioCorrecta(successor) && laberint.pucAnar(successor.x, successor.y, DIRECCIONS[dir])){
            if(!visitat(successor)){
                return successor;
            }
        }
        return null;
    }

    private void visita(Punt p){
        visitats[p.x][p.y] = true;
    }

    private boolean esPosicioCorrecta(Punt p){
        return p.x >= 0 && p.x < files && p.y >= 0 && p.y < columnes;
    }

    private boolean visitat(Punt p){
        return visitats[p.x][p.y];
    }
}
