import java.util.ArrayList;
import java.util.Comparator;

import static java.lang.Math.pow;

public class KDTree {

    private class Node {

        private int[] coord; //coord[0] = x; coord[1] = y
        private int direction; //direction = 0 si verticale, 1 si horizontale, 2 si côte.
        
        private Node(int[] position){
            coord = position;
            direction = 0;
        }


        /* Retourne true si le noeud est a gauche de l'hyperplan de l'autreNoeud */
        public boolean isLeft(Node other){
        		return coord[direction] > other.coord[direction];
        }
        
        public void setDirection(int dir) {
        		direction = dir;
        }
        
        public int getDirection() {
        		return direction;
        }
      
    }

    public class Pixel{
    	
    		private int[] color;
    		
    		public Pixel(int[] rgb) {
    			color = rgb;
    		}
    		
    		public int getColor(int ind) {
    			return color[ind];
    		}
    		
    		public int[] getCoord() {
    			return color;
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
    
    public boolean isLeaf() {
    		return (filsG == null || filsD == null);
    }

    public void addNode(Node node){
    		
    		if (tete == null) {
    			tete = node;
    		}
    		else if (node.isLeft(tete)){

            if (filsG == null){
                filsG = new KDTree(node);
                node.setDirection((tete.getDirection() + 1) % 2);
            } else {
                filsG.addNode(node);
            }
        } else {

            if (filsD == null){
                filsD = new KDTree(node);
                node.setDirection((tete.getDirection() + 1) % 2);
            } else {
                filsD.addNode(node);
            }
        }
    }
   
    ArrayList<Pixel> toArrayPixel(int[][] pixels) {
		ArrayList<Pixel> array = new ArrayList<Pixel>();
		for(int i = 0; i < pixels.length; i++) {
			Pixel pix = new Pixel(pixels[i]);
			array.add(pix);
		}
		return array;
	}
    
    public void initFromList(int[][] list) {
    		this.initFromArray(toArrayPixel(list));
    }
    
    public void initFromArray(ArrayList<Pixel> array) {
    		System.out.println("initFromArray start");
    		ArrayList<Pixel> rArray = new ArrayList<Pixel>();
    		ArrayList<Pixel> gArray = new ArrayList<Pixel>();
    		ArrayList<Pixel> bArray = new ArrayList<Pixel>();
    		for(int i = 0; i < array.size(); i++) {
    			rArray.add(array.get(i));
    			gArray.add(array.get(i));
    			bArray.add(array.get(i));
    		}
    		rArray.sort(new Comparator<Pixel>() {
			public int compare(Pixel o1, Pixel o2) {
				if(o1.getColor(0) < o2.getColor(0)) {
					return 1;
				}
				else if(o1.getColor(0) > o2.getColor(0)) {
					return -1;
				}
				else {
					return 0;
				}	
			}
    		});	
    		gArray.sort(new Comparator<Pixel>() {
    			public int compare(Pixel o1, Pixel o2) {
    				if(o1.getColor(1) < o2.getColor(1)) {
    					return 1;
    				}
    				else if(o1.getColor(1) > o2.getColor(1)) {
    					return -1;
    				}
    				else {
    					return 0;
    				}	
    			}
        	});
        	bArray.sort(new Comparator<Pixel>() {
    			public int compare(Pixel o1, Pixel o2) {
    				if(o1.getColor(2) < o2.getColor(2)) {
    					return 1;
    				}
    				else if(o1.getColor(2) > o2.getColor(2)) {
    					return -1;
    				}
    				else {
    					return 0;
    				}	
    			}
        	});
        	int i = 0;
        	while(rArray.size() > 0) {
        		Pixel medPixel;
        		if(i == 0) {
        			medPixel = rArray.get(rArray.size()/2);
        		}
        		if(i == 1) {
        			medPixel = gArray.get(gArray.size()/2);
        		}
        		else {
        			medPixel = bArray.get(bArray.size()/2);
        		}
        		rArray.remove(medPixel);
        		gArray.remove(medPixel);
        		bArray.remove(medPixel);
        		
        		Node noeud = new Node(medPixel.getCoord());
        		noeud.setDirection(i);
        		this.addNode(noeud);
        		
        		i = (i + 1) % 3;
        	}
        	System.out.println("initFromArray end");
    }

    /*Construit une liste de noeuds à partir d'une liste de pixels (liste de triplets RGB)
    private Node[] buildNodeList(int[][] pixels) {
        Node[] nodes = new Node[pixels.length];
        for (int i = 0 ; i < nodes.length ; i++) {
            nodes[i] = new Node(pixels[i]);
        }
        return nodes;
    }*/

    /* Construit naivement un KDTree a partir d'une liste de noeuds en les
    ajoutant un par un
    private void buildFromNodesNaive(Node[] liste) {
        On ajoute les noeuds dans un ordre aleatoire pour ne pas ajouter des couleurs trop similaires à la suite,
        ce qui donnerait un arbre déséquilibré et donc une erreur lors de la construction de la palette, puisque
        une branche de l'arbre serait (quasiment) vide 
        ArrayList<Integer> indicesAleatoires = new ArrayList<>();
        for (int i = 0 ; i < liste.length ; i++){
            indicesAleatoires.add(i);
        }
        Collections.shuffle(indicesAleatoires);
        this.tete = liste[indicesAleatoires.get(0)];
    //    this.tete.setHyperplanTete();
        for (int i = 1 ; i < liste.length ; i++){
            this.addNode(liste[indicesAleatoires.get(i)]);
        }
    }

    //Construit un KDTree à partir d'une liste de pixels de façon naïve, en les ajoutant 1 à 1
    public void buildFromArrayNaive(int[][] array){
        this.buildFromNodesNaive(buildNodeList(array));
    } */

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
    		System.out.println("getLayer");
        ArrayList<KDTree> list = new ArrayList<KDTree>();
        if (n == 0 || this.isLeaf()){
            list.add(this);
            System.out.println("Coucou");
            return list;
        } else {
        		
            list = filsD.getLayer(n - 1);
            list.addAll(filsG.getLayer(n - 1));
            return list;
        }
    }

    /* Construit une palette de 2^powOf2 couleurs en choisissant les pixels de tete
    des sous-arbres de la couche n du KDTree stockant les pixels de l'image */
    public int[][] buildPalette(int powOf2){
    		System.out.println("buildPalette" + powOf2);
        int[][] palette = new int[(int)pow(2, powOf2)][dimension];
        ArrayList<KDTree> layer = this.getLayer(powOf2);
        for (int i = 0 ; i < layer.size() ; i++){
            palette[i] = layer.get(i).tete.coord;
        }
        System.out.println("buildPalette end" + palette[0] + palette[1]);
        return palette;
        
    }
}
