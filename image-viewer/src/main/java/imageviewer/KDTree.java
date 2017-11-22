import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.pow;

public class KDTree {

    private class Node {

        private int[] coord;

        private Node(int[] coordonnees){
            coord = coordonnees;
        }
    }

    private int dimension = 3;
    private Node tete;
    private KDTree filsG;
    private KDTree filsD;

    /* Si necessaire pour la construction d'une palette, contient les noeuds appartenant a un
    sous-arbre mais qui n'ont pas encore ete clases dans un KDTree.
    Reste vide dans les autres cas pour ne pas consommer de ressources inutilement. */
    private List<Node> filsNonTries;


    public KDTree(Node noeudTete){

        tete = noeudTete;
        filsG = null;
        filsD = null;
    }

    /**
     * Construit un KDTree vide.
     */
    KDTree() {
        tete = null;
        filsG = null;
        filsD = null;
    }
    	
    

    //Construit une liste de noeuds a partir d'un tableau de pixels
    private List<Node> buildNodeList(int[][] pixels) {
        List<Node> nodes = new ArrayList<>();
        for (int i = 0 ; i < pixels.length ; i++) {
            nodes.add(new Node(pixels[i]));
        }
        return nodes;
    }


    /**
     * Construit un KDTree à partir d'une liste de noeuds représentant des pixels.
     * @param list : liste de noeuds.
     * @param compteur : compte le nombre d'appels récursifs de la fonction, afin de déterminer la direction
     *                 de l'hyperplan. Doit être initialisée à zéro quand la fonction est appelée.
     */
    private void buildFromNodes(List<Node> list, int compteur) {

        if (list.size() == 0) {
            return;
        }

        list.sort((node, t1) -> {
            if (node.coord[compteur] < t1.coord[compteur]) {
                return 1;
            } else if (node.coord[compteur] > t1.coord[compteur]) {
                return -1;
            } else {
                return 0;
            }
        });
        Node median = list.remove(list.size() / 2);

        this.tete = median;
        this.filsG = new KDTree();
        this.filsG.buildFromNodes(list.subList(0, list.size() / 2), (compteur + 1) % dimension);
        this.filsD = new KDTree();
        this.filsD.buildFromNodes(list.subList(list.size() / 2, list.size()), (compteur + 1) % dimension);
    }


    /**
     * Construit les n premières couches d'un KDTree à partit d'une liste de noeuds représentant des pixels.
     * @param list : la liste de noeuds.
     * @param n : le nombre de couches à construire.
     * @param compteur : compte le nombre d'appels récursifs de la fonction afin de déterminer la direction des
     *                 hyperplans. Doit être initialisée à zéro quand on appelle la fonction.
     */
    private void buildNLayersFromNodes(List<Node> list, int n, int compteur) {

        if (list.size() == 0 || n == 0) {
            filsNonTries = new ArrayList<Node>(list);
            return;
        }

        list.sort((node, t1) -> {
            if (node.coord[compteur] < t1.coord[compteur]) {
                return 1;
            } else if (node.coord[compteur] > t1.coord[compteur]) {
                return -1;
            } else {
                return 0;
            }
        });
        Node median = list.remove(list.size() / 2);

        this.tete = median;
        this.filsG = new KDTree();
        this.filsG.buildNLayersFromNodes(list.subList(0, list.size() / 2), n - 1, (compteur + 1) % dimension);
        this.filsD = new KDTree();
        this.filsD.buildNLayersFromNodes(list.subList(list.size() / 2, list.size()), n - 1, (compteur + 1) % dimension);

    }


    /**
     * Construit un KDTree à partir d'un tableau de pixels.
     * @param array : tableau de pixels. Un pixel est un tableau de 3 entiers RGB.
     */
    public void buildFromArray(int[][] array) {this.buildFromNodes(buildNodeList(array), 0);}


    /**
     * Construit les n premières couches d'un KDTree à partir d'un tableau de pixels.
     * @param array : tableau de pixels. Un pixel est un tableau de 3 entiers RGB.
     * @param n : le nombre de couches à construire.
     */
    void buildNLayersFromArray(int[][] array, int n) {this.buildNLayersFromNodes(buildNodeList(array), n, 0);}

    private int[] moyenne(){
        int[] moy = new int[dimension];
        if (filsG.tete != null && filsD.tete != null) {
            int[] moyG = filsG.moyenne();
            int[] moyD = filsD.moyenne();
            for (int i = 0 ; i < dimension ; i++) {
                moy[i] = (moyG[i] + moyD[i]) / 2;
            }
        } else if (filsG.tete != null){
            moy = filsG.moyenne();
        } else if (filsD.tete != null) {
            moy = filsD.moyenne();
        } else {
            for (int i = 0 ; i < dimension ; i++) {
                moy[i] = this.tete.coord[i];
            }
        }
        return moy;
    }

    private int[] moyenneFilsNonTries() {
        int moy[] = new int[dimension];
        for (int i = 0 ; i < filsNonTries.size() ; i++) {
            for (int j = 0 ; j < dimension ; j++) {
                moy[j] += filsNonTries.get(i).coord[j];
            }
        }
        for (int j = 0 ; j < dimension ; j++) {
            moy[j] /= filsNonTries.size();
        }
        return moy;
    }


    /**
     * Retourne la liste des 2^n sous-arbres de la couche n d'un KDTree.
     * @param n : couche de l'arbre dont on veut retourner les sous-arbres.
     * @return la liste des 2^n sous-arbres.
     */
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


    /**
     * Construit une palette de 2^powOf2 couleurs représentatives des couleurs stockées dans le KDTree.
     * @param powOf2
     * @return palette : un tableau de 2^powOf2 pixels.
     */
    public int[][] buildPaletteNaive(int powOf2){
        int[][] palette = new int[(int)pow(2, powOf2)][dimension];
        ArrayList<KDTree> layer = this.getLayer(powOf2);
        for (int i = 0 ; i < layer.size() ; i++){
            //palette[i] = layer.get(i).tete.coord;
            palette[i] = layer.get(i).moyenne();
        }
        return palette;
    }


    /**
     * Construit une palette de 2^powOf2 couleurs représentatives des couleurs stockées dans le KDTree.
     * @param powOf2 :
     * @return palette : un tableau de 2^powOf2 pixels.
     */
    int[][] buildPalette(int powOf2) {
        int[][] palette = new int[(int)pow(2, powOf2)][dimension];
        ArrayList<KDTree> layer = this.getLayer(powOf2);
        for (int i = 0 ; i < layer.size() ; i++) {
            palette[i] = layer.get(i).moyenneFilsNonTries();
        }
        return palette;
    }

}
