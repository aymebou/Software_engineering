import java.util.ArrayList;
import java.util.Collections;
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
            hyperplan[0] = - pere.hyperplan[1];
            hyperplan[1] = pere.hyperplan[0];
            for (int i = 2 ; i < dimension ; i++){
                hyperplan[i] = 0;
            }
            hyperplan[dimension] = - (hyperplan[0] * coord[0] + hyperplan[1] * coord[1]);
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



    void addNode(Node node){

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

    }

    //Construit une liste de noeuds à partir d'une liste de pixels (liste de triplets RGB)
    private Node[] buildNodeList(int[][] pixels) {
        Node[] nodes = new Node[pixels.length];
        for (int i = 0 ; i < nodes.length ; i++) {
            nodes[i] = new Node(pixels[i]);
        }
        return nodes;
    }

    /* Construit naivement un KDTree a partir d'une liste de noeuds en les
    ajoutant un par un */
    private void buildFromNodesNaive(Node[] liste) {
        /* On ajoute les noeuds dans un ordre aleatoire pour ne pas ajouter des couleurs trop similaires à la suite,
        ce qui donnerait un arbre déséquilibré et donc une erreur lors de la construction de la palette, puisque
        une branche de l'arbre serait (quasiment) vide */
        ArrayList<Integer> indicesAleatoires = new ArrayList<>();
        for (int i = 0 ; i < liste.length ; i++){
            indicesAleatoires.add(i);
        }
        Collections.shuffle(indicesAleatoires);
        this.tete = liste[indicesAleatoires.get(0)];
        this.tete.setHyperplanTete();
        for (int i = 1 ; i < liste.length ; i++){
            this.addNode(liste[indicesAleatoires.get(i)]);
        }
    }

    //Construit un KDTree à partir d'une liste de pixels de façon naïve, en les ajoutant 1 à 1
    public void buildFromArrayNaive(int[][] array){
        this.buildFromNodesNaive(buildNodeList(array));
    }

    //Non utilisé finalement, calcule le  pixel moyen d'un arbre
    /*private int[] moyenne(){
        int[] moy = new int[dimension];
        if (filsD == null || filsG == null){
            for (int i = 0 ; i < dimension ; i++){
                moy[i] = (filsD.tete.coord[i] + filsG.tete.coord[i]) / 2;
            }
        } else {
            for (int i = 0 ; i < dimension ; i++){
                moy[i] = (filsD.moyenne()[i] + filsG.moyenne()[i]) / 2;
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
        }
        return palette;
    }

}
