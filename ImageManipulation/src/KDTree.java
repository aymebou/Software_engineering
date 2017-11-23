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
    //La variable compteur sert à retenir dans à quelle dimension doit être normal le nouvel hyperplan.
    //compteur = 0 quand on appelle la fonction, la variable sera modifiée au cours des appels récursifs.
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

    //Construit les n premieres couches d'un KDTree a partir d'une liste de noeuds
    private void buildNLayersFromNodes(List<Node> list, int n, int compteur) {

        if (list.size() == 0 || n == 0) {
            filsNonTries = new ArrayList<Node>(list);
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
        this.filsG.buildNLayersFromNodes(list.subList(0, list.size() / 2), n - 1, (compteur + 1) % dimension);
        this.filsD = new KDTree();
        this.filsD.buildNLayersFromNodes(list.subList(list.size() / 2, list.size()), n - 1, (compteur + 1) % dimension);

    }


    //Construit un KDTree a partir d'un tableau de pixels
    public void buildFromArray(int[][] array) {this.buildFromNodes(buildNodeList(array), 0);}

    //Construit les n premieres couches d'un KDTree a partir d'un tableau de pixel
    public void buildNLayersFromArray(int[][] array, int n) {this.buildNLayersFromNodes(buildNodeList(array), n, 0);}

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

    /* Construit une palette de 2^powOf2 couleurs en calculant la moyenne des sous-arbres
    de la powOf2-eme couche du KDTree */
    public int[][] buildPaletteNaive(int powOf2){
        int[][] palette = new int[(int)pow(2, powOf2)][dimension];
        ArrayList<KDTree> layer = this.getLayer(powOf2);
        for (int i = 0 ; i < layer.size() ; i++){
            //palette[i] = layer.get(i).tete.coord;
            palette[i] = layer.get(i).moyenne();
        }
        return palette;
    }

    /* Construit la palette en faisant la moyenne des pixels non-tries appartenant
    aux sous-arbres de la derniere couche du debut d'arbre construit */
    public int[][] buildPalette(int powOf2) {
        int[][] palette = new int[(int)pow(2, powOf2)][dimension];
        ArrayList<KDTree> layer = this.getLayer(powOf2);
        for (int i = 0 ; i < layer.size() ; i++) {
            palette[i] = layer.get(i).moyenneFilsNonTries();
        }
        return palette;
    }

}
