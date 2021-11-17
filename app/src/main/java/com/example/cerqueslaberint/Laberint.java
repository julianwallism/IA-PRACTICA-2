package com.example.cerqueslaberint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import java.util.Random;

/**
 * Created by Ramon Mas on 10/10/21.
 * Genera laberints de grandaria files x columnes i permet eliminar un percentatge aproximat de parets interiors
 * per augmentar el número de solucions
 */

public class Laberint {
    private final int MAX_MAZE_FILES = 35;
    private final int MAX_MAZE_COLUMNES = 35;

    private final int PARED_NORD = 1;
    private final int PARED_EST = 2;
    private final int PARED_SUR = 4;
    private final int PARED_OEST = 8;

    public static final int AMUNT =     512;
    public static final int DRETA =    1024;
    public static final int AVALL =    2048;
    public static final int ESQUERRA = 4096;

    private final int MAX_CELLS = 16;

    private int CELL_SIZE_X = 100;
    private int CELL_SIZE_Y = 100;

    private Random randomGenerator;
    private Context context;

    private int windowWidth;
    private int windowHeight;
    private int ALL_WALLS;
    public int nodes;

    private Personatge bitxo;
    private double percentatgeEliminacio;
    private String missatge = "";
    private int longitud;
    private Punt[] objectes;
    boolean mostraObjectes = false;

    public Punt porta = new Punt();

    private Bitmap[] cells = new Bitmap[MAX_CELLS];
    private Bitmap   bitxoBmp, objecteBmp;
    private Bitmap   fons;
    private Bitmap   puntet;

    private int offsetx = 0;
    private int offsety = 0;


    private int nivellActual;
    int nFiles;
    int nColumnes;
    private int casellesTotals;
    private int maze[][] = new int[MAX_MAZE_FILES][MAX_MAZE_COLUMNES];

    public Laberint(Context cont, int files, int columnes, int percent)
    {
        percentatgeEliminacio = percent/(100.0 * 2);  // valors aproximats
        context = cont;
        ALL_WALLS = (PARED_NORD + PARED_EST + PARED_SUR + PARED_OEST);
        randomGenerator = new Random();

        for (int i = 0; i < MAX_CELLS; i++) {
            cells[i] = carregaImatgeEscalada(R.drawable.cel00 + i, 100, 100);
        }

        bitxoBmp = carregaImatgeEscalada(R.drawable.riuverd, 100 * 12, 100);
        objecteBmp = carregaImatgeEscalada(R.drawable.vermella, 100, 100);
        fons = carregaImatgeEscalada(R.drawable.negre, 147, 189);
        puntet = carregaImatgeEscalada(R.drawable.puntet, 100, 100);

        DisplayMetrics metrics = cont.getResources().getDisplayMetrics();
        int w = metrics.widthPixels;
        int h = metrics.heightPixels;

        windowWidth  = w;
        windowHeight = h;

        nFiles = files;
        nColumnes = columnes;

        int CELL_SIZE;
        if (nColumnes < nFiles)
            CELL_SIZE = (windowHeight-200) / (nFiles + 1);
        else
            CELL_SIZE = windowWidth  / (nColumnes + 1);

        CELL_SIZE_X = CELL_SIZE;
        CELL_SIZE_Y = CELL_SIZE;

        offsetx = (int) ((windowWidth-CELL_SIZE*(nColumnes+1))/2.0 +(CELL_SIZE_X / 2));
        offsety = 10;

        inicialitza();
    }

    public void setBitxo(Personatge b)
    {
        bitxo = b;
    }
    public void setMissatge(String m)
    {
        missatge = m;
    }
    public void setNodes(int n)
    {
        nodes = n;
    }
    public void setLongitudCami(int l) {longitud = l;}
    public void incNodes()
    {
        nodes++;
    }
    public void inicialitza() {
        initAmbNivell(1, nFiles, nColumnes);
    }

    public Bitmap carregaImatgeEscalada(int dibuix, int ampla, int alt)
    {
        Bitmap tmpBitmap;

        tmpBitmap = BitmapFactory.decodeResource(context.getResources(), dibuix);
        return Bitmap.createScaledBitmap(tmpBitmap, ampla, alt, true);
    }

    public Punt getPorta()
    {
        return porta;
    }

    public Punt getObjecte(int i) {return objectes[i];}

    public Punt xy(int fila, int columna)  // converteix fila i columna del laberint a x,y (en píxels) de la pantalla
    {
        return new Punt(offsetx + columna * CELL_SIZE_X, offsety + fila * CELL_SIZE_Y);
    }

    public Punt filaColumna(int x, int y) // converteix x i y de la pantalla a la casella on es troa del laberint
    {
        int f, c;

        c = (x - offsetx) / CELL_SIZE_X;
        f = (y - offsety) / CELL_SIZE_Y;

        if (c >= 0 && c < nColumnes && f >= 0 && f < nFiles)
            return new Punt(f, c);
        else
            return new Punt(-1, -1);
    }


    public void initAmbNivell(int n, int f, int c)
    {
        nivell(n);
        creaNou(f, c);  // laberint de fxc
        objectes = new Punt[] {new Punt(0,0), new Punt(nColumnes-1,0), new Punt(nColumnes-1, nFiles-1), new Punt(0, nFiles-1)};  // viatjant de comenrç, els bitxitos
        bitxo = null;
    }

    public void nivell(int n) {
        nivellActual = n;
    }

    public int Random_0_X(int val)   // retorna un número aleatori de 0 a X  (enter)
    {
        int x = randomGenerator.nextInt(val);
        return (x);
    }

    public double Random_0_1()       // retorna un número entre 0 i 1 (double)
    {
        int x = randomGenerator.nextInt(100);
        return (x / 100.0);
    }

    public void creaNou(int files, int columnes)
    {
        nFiles = files;
        nColumnes = columnes;
        casellesTotals = nFiles * nColumnes;
        int casellesVisitades = 1;
        int[] veinats = new int[4];

        for (int f = 0; f < nFiles; f++)
            for (int c = 0; c < nColumnes; c++)
                maze[f][c] = ALL_WALLS;

        int casellaActualF = (int) (Random_0_1() * nFiles);
        int casellaActualC = (int) (Random_0_1() * nColumnes);

        int novaActualF;
        int novaActualC;

        int veinatsIntactes;
        int posicioAleatoria;
        int oposada;

        int pilaF[] = new int[casellesTotals];
        int pilaC[] = new int[casellesTotals];
        int pilaTop;

        pilaTop = 0;

        while (casellesVisitades < casellesTotals) {
            // veinats intactes de casellaActual
            veinatsIntactes = 0;
            if (casellaActualF > 0) {
                if (maze[casellaActualF - 1][casellaActualC] == ALL_WALLS) {
                    veinats[veinatsIntactes++] = PARED_NORD;
                }
            }
            if (casellaActualF < nFiles - 1) {
                if (maze[casellaActualF + 1][casellaActualC] == ALL_WALLS) {
                    veinats[veinatsIntactes++] = PARED_SUR;
                }
            }
            if (casellaActualC > 0) {
                if (maze[casellaActualF][casellaActualC - 1] == ALL_WALLS) {
                    veinats[veinatsIntactes++] = PARED_OEST;
                }
            }
            if (casellaActualC < nColumnes - 1) {
                if (maze[casellaActualF][casellaActualC + 1] == ALL_WALLS) {
                    veinats[veinatsIntactes++] = PARED_EST;
                }
            }

            if (veinatsIntactes > 0) {
                // tria el veinat on ens movem aleatoriament
                posicioAleatoria = (int) (Random_0_1() * veinatsIntactes);

                novaActualF = casellaActualF;
                novaActualC = casellaActualC;
                switch (veinats[posicioAleatoria]) {
                    case PARED_OEST:
                        novaActualC = casellaActualC - 1;
                        oposada = PARED_EST;
                        break;
                    case PARED_EST:
                        novaActualC = casellaActualC + 1;
                        oposada = PARED_OEST;
                        break;
                    case PARED_SUR:
                        novaActualF = casellaActualF + 1;
                        oposada = PARED_NORD;
                        break;
                    case PARED_NORD:
                        novaActualF = casellaActualF - 1;
                        oposada = PARED_SUR;
                        break;
                    default:
                        oposada = 0;
                        break;
                }

                // baixa les parets per on passam
                maze[casellaActualF][casellaActualC] -= veinats[posicioAleatoria];
                maze[novaActualF][novaActualC] -= oposada;

                // guarda la casella actual
                pilaF[pilaTop] = casellaActualF;
                pilaC[pilaTop] = casellaActualC;
                pilaTop++;

                casellaActualF = novaActualF;
                casellaActualC = novaActualC;

                // n'he visitada una altra
                casellesVisitades++;
            } else {
                pilaTop--;
                casellaActualF = pilaF[pilaTop];
                casellaActualC = pilaC[pilaTop];
            }
        }

        // Aleatoriament eliminam algunes parets per fer els recorreguts més curts
        // Quantes ? proporcionalmente al número de caselles (%)

        int casellesEliminades = 0;
        int fEliminar, cEliminar;
        int codi;  //codi corresponent a la pared

        while (casellesEliminades < casellesTotals * percentatgeEliminacio) {
            // casella aleatoria excepte els costats

            fEliminar = 1 + (int) (Random_0_1() * (nFiles - 2));
            cEliminar = 1 + (int) (Random_0_1() * (nColumnes - 2));

            // pared aleatoria
            int exp = (int) (Random_0_1() * 4);
            codi = (int) (Math.pow(2.0, exp));
            if ((maze[fEliminar][cEliminar] & codi) != 0) //existeix la pared ?
            {
                maze[fEliminar][cEliminar] -= codi;

                // Ara hem de llevar la pared de la mateixa casella veinada
                switch (codi) {
                    case PARED_OEST:
                        maze[fEliminar][cEliminar - 1] -= PARED_EST;
                        break;
                    case PARED_EST:
                        maze[fEliminar][cEliminar + 1] -= PARED_OEST;
                        break;
                    case PARED_SUR:
                        maze[fEliminar + 1][cEliminar] -= PARED_NORD;
                        break;
                    case PARED_NORD:
                        maze[fEliminar - 1][cEliminar] -= PARED_SUR;
                        break;
                }
                casellesEliminades++;
            }

        }

        posaPorta();
    }

    public void pintaCasella(Canvas canvas, Bitmap bitmap, int x, int y)  // pinta una imatge a una posició x,y
    {
        Rect src;

        src = new Rect(0, 0, 100, 100);
        Rect dst = new Rect(x, y, (x + CELL_SIZE_X), (y + CELL_SIZE_Y));
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, src, dst, paint);
    }

    public void pinta(Canvas canvas)  // pinta tota la pantalla
    {
        if (canvas == null) return;

        Rect rorigen = new Rect(0, 0, fons.getWidth(), fons.getHeight());
        Rect rdesti = new Rect(0, 0, windowWidth, windowHeight);
        canvas.drawBitmap(fons, rorigen, rdesti, null);

        // Ara tenc totes ses caselles, les he de pintar

        int fila = 0;
        int columna = 0;
        int imatge, costats;
        Boolean oNord, nOest, eNord;

        imatge = 0;

        for (int i = 0; i < casellesTotals + nFiles + nColumnes + 1; i++) {
            fila = i / (nColumnes + 1);
            columna = i % (nColumnes + 1);

            if (fila == nFiles || columna == nColumnes) {
                if (columna == nColumnes) {
                    if (fila == 0) imatge = 6;
                    else if (fila == nFiles) imatge = 7;
                    else
                        imatge = 5;
                } else if (fila == nFiles) {
                    imatge = 1;
                }
            } else {
                imatge = maze[fila][columna];

                // ara posam els costats que facin falta
                costats = imatge & (PARED_NORD | PARED_OEST);

                oNord = columna > 0 && ((maze[fila][columna - 1] & PARED_NORD) != 0);
                nOest = fila > 0 && ((maze[fila - 1][columna] & PARED_OEST) != 0);
                eNord = columna < nColumnes - 1 && ((maze[fila][columna + 1] & PARED_NORD) != 0);

                imatge = 0;

                switch (costats) {
                    case 1:
                        imatge = 1;
                        if (oNord && eNord) imatge = 1;
                        else if (!oNord && !nOest) imatge = 2;
                        else if (oNord && !eNord && !nOest) imatge = 3;
                        else if (!oNord && nOest && eNord) imatge = 4;
                        break;
                    case 8:
                        imatge = 5;
                        if (!nOest) imatge = 6;
                        break;
                    case 9:
                        if (!nOest && !oNord) imatge = 10;
                        else imatge = 11;
                        break;

                    default: // no hi ha pareds al nord ni a l'oest, mira el trocet que hi ha d'haver
                        if (oNord && nOest) imatge = 7;
                        else if (oNord) imatge = 8;
                        else if (nOest) imatge = 7;
                        break;
                }
            }

            Punt lloc = xy(fila, columna);
            if (fila != nFiles && columna != nColumnes) {
                if (bitxo != null && bitxo.dinsCami(fila, columna))
                    pintaCasella(canvas, cells[15], lloc.x, lloc.y);
                else
                    pintaCasella(canvas, cells[12], lloc.x, lloc.y);
            }
            pintaCasella(canvas, cells[imatge], lloc.x, lloc.y);
        }

        Punt pos = xy(porta.x, porta.y);


        // ara pintam la porta
        if (porta.x == 0) pintaCasella(canvas, cells[14], pos.x, pos.y);
        if (porta.x == nFiles-1) pintaCasella(canvas, cells[14], pos.x, pos.y+CELL_SIZE_Y);
        if (porta.y == 0) pintaCasella(canvas, cells[13], pos.x, pos.y);
        if (porta.y == nColumnes-1) pintaCasella(canvas, cells[13], pos.x+CELL_SIZE_X, pos.y);

        // ara pintam el bitxo

        if (bitxo != null)
        {
            bitxo.actualitzaPosicio();
            bitxo.pintaCami(canvas);
            pintaCasella(canvas, bitxoBmp, bitxo.getX() + 3, bitxo.getY());
        }

        if (mostraObjectes)
        {
            for (int i=0;i<4;i++)
            {
                if (objectes[i].visible)
                {
                    Punt pix = xy(objectes[i].x, objectes[i].y);
                    pintaCasella(canvas, objecteBmp, pix.x, pix.y);
                }
            }
        }

        int alturaText = windowHeight/22;
        int primeraLinia = windowHeight/11;
        escriuCentrat(canvas, "Nodes: "+nodes, alturaText, nFiles*CELL_SIZE_Y+primeraLinia+alturaText+10);
        if (missatge != "") {
            escriuCentrat(canvas, missatge, alturaText, nFiles * CELL_SIZE_Y + primeraLinia);
            escriuCentrat(canvas, "Long: "+longitud, alturaText, nFiles * CELL_SIZE_Y + primeraLinia+2*(alturaText+10));
        }
    }

    public void agafaObjectes(Punt p)   // per recollir els bitxitos el viatjant de comerç
    {
        // si hi ha un objecte, l'agafa
        if (mostraObjectes)
        {
            for (int i=0;i<4;i++)
            {
                if (objectes[i].visible)
                {
                    if (objectes[i].x == p.x && objectes[i].y == p.y) objectes[i].visible = false;
                }
            }
        }
    }

    public void mostraObjectes()
    {
        mostraObjectes = true;
        for (int i=0; i<4;i++)
            objectes[i].visible = true;
    }

    public void amagaObjectes()
    {
        mostraObjectes = false;
    }

    public void aturaMusica()
    {
        ((MainActivity) context).musicaOFF();
    }

    public void posaPorta()   // aleatòriament col·loca la porta a un costat del laberint
    {
        // primer triam el quadrant (de 0 a 3)

        int quadrant = Random_0_X(4);
        Punt lloc = new Punt();
        Punt centre = new Punt();

        centre.x = nColumnes / 2;
        centre.y = nFiles / 2;

        switch (quadrant) {
            case 0:// AdaltEsquerra
                if (Random_0_X(50) < 25) {
                    lloc.x = 0;
                    lloc.y = 1 + (int) (centre.y * Random_0_1());
                } else {
                    lloc.x = 1 + (int) (centre.x * Random_0_1());
                    lloc.y = 0;
                }
                break;
            case 1: // AdaltDreta
                if (Random_0_X(50) < 25) {
                    lloc.x = 0;
                    lloc.y = centre.y + (int) ((centre.y - 2) * Random_0_1());
                } else {
                    lloc.x = 1 + (int) (centre.x * Random_0_1());
                    lloc.y = nFiles - 1;
                }
                break;
            case 2: // AbaixEsquerra
                if (Random_0_X(50) < 25) {
                    lloc.x = 1 + (int) (centre.x * Random_0_1());
                    lloc.y = 0;
                } else {
                    lloc.x = nColumnes - 1;
                    lloc.y = 1 + (int) (centre.y * Random_0_1());
                }
                break;
            case 3: // AbaixDreta
                if (Random_0_X(50) < 25) {
                    lloc.x = nColumnes - 1;
                    lloc.y = centre.y + (int) ((centre.y - 2) * Random_0_1());
                } else {
                    lloc.x = centre.x + (int) ((centre.x - 2) * Random_0_1());
                    lloc.y = nFiles - 1;
                }
                break;
        }

        porta.x = lloc.y;
        porta.y = lloc.x;
    }

    // Mira si puc anar cap a una direccio tenint en compte d'on venc

    public boolean pucAnar(int f, int c, int dir)
    {
        switch (dir)
        {
            case ESQUERRA:  if ((maze[f][c] & PARED_OEST) == 0 && c>0) return true;
                            break;
            case DRETA:     if ((maze[f][c] & PARED_EST) == 0 && c<nColumnes+1) return true;
                            break;
            case AMUNT:     if ((maze[f][c] & PARED_NORD) == 0 && f>0) return true;
                            break;
            case AVALL:     if ((maze[f][c] & PARED_SUR) == 0 && f<nFiles+1) return true;
                            break;
        }
        return false;
    }

    // pinta un puntet

    public void pintaPuntet(Canvas canvas, Punt p)
    {
        Punt lloc = xy(p.x, p.y);
        pintaCasella(canvas, puntet, lloc.x, lloc.y);
    }

    public void escriuCentrat(Canvas canvas, String missatge, int size, int y)
    {
        Paint paint = new Paint();
        //paint.setTypeface(((GeneralActivity) context).fontJoc);
        paint.setTextSize(size);

        // Centrat horitzontal

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.RED);
        paint.setAlpha(255);

        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) (y - ((paint.descent() + paint.ascent()))/4) ;

        canvas.drawText(missatge, xPos, yPos, paint);
    }
}
