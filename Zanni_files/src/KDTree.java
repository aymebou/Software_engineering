public class KDTree {

    private class Node {

        private Integer[] coord;
        private Integer[] hyperplan = new Integer[dimension];    /* Contient les coefficients de l'equation d'hyperplan
                                            a*x + b*y + c*z + ... = 0 */
        private Node pere;

        private Node(Integer[] coordonnees){
            coord = coordonnees;
        }

        /* Retourne true si le noeud est a gauche de l'hyperplan de l'autreNoeud */
        boolean estAGauche(Node autreNoeud){
            int somme = 0;
            for (int i = 0 ; i < autreNoeud.hyperplan.length ; i++){
                somme += autreNoeud.hyperplan[i] * this.coord[i];
            }
            return somme < 0;
        }

        /* Construit un hyperplan normal à celui du noeud pere */
        void setHyperplan(Node pere){
            hyperplan[0] = - pere.hyperplan[1];
            hyperplan[1] = pere.hyperplan[0];
            for (int i = 0 ; i < dimension ; i++){
                hyperplan[i] = 0;
            }
        }

        /* Attribue un hyperplan arbitraire, utile pour attribuer un hyperplan au premier
        noeud de l'arbre */
        void setHyperplanTete(){
            hyperplan[0] = 1;
            for (int i = 1 ; i < hyperplan.length ; i++){
                hyperplan[i] = 0;
            }
        }

    }

    private int dimension;
    private Node tete;
    private KDTree filsG;
    private KDTree filsD;


    public KDTree(Node noeudTete){

        tete = noeudTete;

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

    /* Construit naivement un KDTree a partir d'une liste de noeuds en les
    ajoutant un par un */
    public static KDTree buildFromArrayNaive(Node[] liste) {
        KDTree newTree = new KDTree(liste[0]);
        newTree.tete.setHyperplanTete();
        for (int i = 0 ; i < liste.length ; i++){
            newTree.addNode(liste[i]);
        }
        return newTree;
    }





}
