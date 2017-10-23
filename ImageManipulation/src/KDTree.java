import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.pow;

public class KDTree {

    private class Node {

        private int[] coord;
        private int[] hyperplan = new int[dimension + 1];    /* Contient les coefficients de l'equation d'hyperplan
                                            a*x + b*y + c*z + ... + cste = 0 */

        private Node(int[] coordonnees){
            coord = coordonnees;
        }


        /* Retourne true si le noeud est a gauche de l'hyperplan de l'autreNoeud */
        boolean estAGauche(Node autreNoeud){
            int somme = 0;
            for (int i = 0 ; i < dimension ; i++){
                somme += autreNoeud.hyperplan[i] * this.coord[i];
            }
            somme += autreNoeud.hyperplan[dimension];
            return somme < 0;
        }

        /* Construit un hyperplan normal à celui du noeud pere */
        void setHyperplan(Node pere){
            for (int i = 0 ; i < dimension ; i++) {
                hyperplan[i] = pere.hyperplan[(i + 1) % dimension];
            }
            hyperplan[dimension] = - (hyperplan[0] * coord[0] + hyperplan[1] * coord[1] + hyperplan[2] * coord[2]);
            /*hyperplan[dimension] = 0;
            for (int i = 0 ; i < dimension ; i++) {
                hyperplan[dimension] -= (pere.hyperplan[i] * coord[i]);
            }*/
        }

        /* Attribue un hyperplan arbitraire, utile pour attribuer un hyperplan au premier
        noeud de l'arbre */
        void setHyperplanTete(){
            hyperplan[0] = 1;
            for (int i = 1 ; i < dimension ; i++){
                hyperplan[i] = 0;
            }
            hyperplan[dimension] = - (hyperplan[0] * coord[0]);
        }

    }

    private int dimension = 3;
    private Node tete;
    private KDTree filsG;
    private KDTree filsD;


    public KDTree(Node noeudTete){

        tete = noeudTete;

        filsG = null;
        filsD = null;

    }

    public KDTree() {
        tete = null;
        filsG = null;
        filsD = null;
    }


    //Ajoute un noeud à un KDTree, inutile pour le TD, utile seulement pour les tests
    /*void addNode(Node node){

        if (node.estAGauche(this.tete)){

            if (this.filsG == null){
                this.filsG = new KDTree(node);
                node.setHyperplan(this.tete);
            } else {
                this.filsG.addNode(node);
            }

        } else {

            if (this.filsD == null){
                this.filsD = new KDTree(node);
                node.setHyperplan(this.tete);
            } else {
                this.filsD.addNode(node);
            }

        }

    }*/

    //Construit une liste de noeuds a partir d'un tableau de pixels
    private List<Node> buildNodeList(int[][] pixels) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0 ; i < pixels.length ; i++) {
            nodes.add(new Node(pixels[i]));
        }
        return nodes;
    }

    //Construit un KDTree a partir d'une liste de noeuds representant des pixels
    private void buildFromNodes(List<Node> list, int compteur) {

        if (list.size() == 0) {
            return;
        }

        list.sort(new Comparator<Node>() {
            @Override
            public int compare(Node node, Node t1) {
                if (node.coord[compteur] < t1.coord[compteur]) {
                    return 1;
                } else if (node.coord[compteur] > t1.coord[compteur]) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        Node median = list.remove(list.size() / 2);

        this.tete = median;
        this.filsG = new KDTree();
        this.filsG.buildFromNodes(list.subList(0, list.size() / 2), (compteur + 1) % 3);
        this.filsD = new KDTree();
        this.filsD.buildFromNodes(list.subList(list.size() / 2, list.size()), (compteur + 1) % 3);

    }

    //Construit un KDTree a partir d'un tableau de pixels
    public void buildFromArray(int[][] array) {this.buildFromNodes(buildNodeList(array), 0);}

    //Non utilisé finalement (trop complexe), calcule le  pixel moyen d'un arbre
    /*private int[] moyenne(){
        int[] moy = new int[dimension];
        if (filsG != null && filsD != null) {
            for (int i = 0 ; i < dimension ; i++) {
                moy[i] = (filsG.moyenne()[i] + filsD.moyenne()[i]) / 2;
            }
        } else if (filsG != null){
            moy = filsG.moyenne();
        } else if (filsD != null) {
            moy = filsD.moyenne();
        } else {
            for (int i = 0 ; i < dimension ; i++) {
                moy[i] = 122;
            }
        }
        return moy;
    }*/

    /* Retourne la liste des 2^n sous-arbres de la couche n d'un arbre */
    private ArrayList<KDTree> getLayer(int n){
        ArrayList<KDTree> list = new ArrayList<>();
        if (n == 0){
            list.add(this);
            return list;
        } else {
            list = this.filsD.getLayer(n - 1);
            list.addAll(this.filsG.getLayer(n - 1));
            return list;
        }
    }

    /* Construit une palette de 2^powOf2 couleurs en choisissant les pixels de tete
    des sous-arbres de la couche n du KDTree stockant les pixels de l'image */
    public int[][] buildPalette(int powOf2){
        int[][] palette = new int[(int)pow(2, powOf2)][dimension];
        ArrayList<KDTree> layer = this.getLayer(powOf2);
        for (int i = 0 ; i < layer.size() ; i++){
            palette[i] = layer.get(i).tete.coord;
            //palette[i] = layer.get(i).moyenne();
        }
        return palette;
    }

}
