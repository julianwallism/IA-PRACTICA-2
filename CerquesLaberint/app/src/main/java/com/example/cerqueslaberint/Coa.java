package com.example.cerqueslaberint;

/**
 * Created by Ramon Mas on 10/10/21.
 * GestiÃ³ d'elements amb una Coa de Nodes
 * Operacions: afegeix, treu, buida, consulta, elements
 */

public class Coa {

    class Node {
        Object element;
        Node següent;

        public Node(Object o) {
            element = o;
            següent = null;
        }
    }

    Node primer;   // el primer element de la coa
    Node darrer;   // el darrer
    int  elements; // quants en hi ha ?

    public Coa()
    {
        darrer = null;
        elements = 0;
    }

    public void afegeix(Object o)   // inserta un objecte dins la coa
    {
        Node node = new Node(o);
        if (primer == null)
        {
            primer = node;
            darrer = node;
        } else
        {
            següent = node;
            darrer = node;
        }
        elements++;
    }

    public Object treu()   // extreu el primer objecte de la coa
    {
        if (primer == null)
            return null;
        Object o = primer.element;
        primer = primer.següent;
        elements--;
        return o;
    }

    public boolean buida()
    {
        return (elements == 0);
    }  // coa buida ?

    public int elements()
    {
        return elements;
    }  // quants elements hi ha ?

    public Object consulta()   // consulta el primer element perÃ² no el treu
    {
        if (primer == null)
            return null;
        else
            return primer.element;
    }
}
