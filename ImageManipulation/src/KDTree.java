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
        this.filsG.buildFromNodes(list.subList(0, list.size() / 2), (compteur + 1) % dimension);
        this.filsD = new KDTree();
        this.filsD.buildFromNodes(list.subList(list.size() / 2, list.size()), (compteur + 1) % dimension);

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
