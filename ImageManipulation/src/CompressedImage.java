import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CompressedImage {
	//Définition d'une image compressée : fichier non propriétaire, écrit en binaire
	/* On code sur 2 octets la hauteur d'abord puis sur 2 octets également la largeur
	 * Vient aussi la taille de la palette, sur 2 octet.
	 * En soi, il faudrait rester cohérent et stocker la taille de la palette sur 1 octet, comme 
	 * les positions sont stockées sur 1 octet mais bon...
	 * 
	 * On code ensuite la palette en RGB : celle-ci dispose de 8 couleurs dans notre cas donc 
	 * celle ci sera codée sur 8*3*2 octet (afin que les RGB puissent aller jusqu'à 256 bit chacun) 
	 * Enfin, le liste des pixels dans depuis le coin haut gauche jusqu'au coin bas droite, en ligne
	 * 
	 *  ATTENTION : on ne note pas les couleurs des pixels mais bien les positions (indices) dans la palette
	 *  chacun codé sur 1 octet
	 * 
	 * EXTENSION DE FICHIER : .comp
	 * */
	
	
	private byte [] h;
	private byte [] w;
	private byte [] paletteSize;
	private byte [] palette;
	private byte [] pixelList;
	

	//initialisation à partir des données classiques
	public void CompressedImage (int h_int, int w_int,int paletteSize_int, int [][] palette_int, int [] pixelList_int ) {
		
		//h, w et paletteSize :
		h = intToTwoOctet(h_int);
		w = intToTwoOctet(w_int);
		paletteSize = intToTwoOctet(paletteSize_int);
		
		//Palette ::
		palette = new byte [paletteSize_int * 6];
		for (int i=0;i<palette.length;i=i+2) {
		byte [] toWrite;
		if (i%3 ==2) {
			toWrite = intToTwoOctet(palette_int[ i/ paletteSize_int][1]); 
		}
		else if (i%3 ==1) {
			toWrite = intToTwoOctet(palette_int[ i/ paletteSize_int][2]); 

		}
		else {
			toWrite = intToTwoOctet(palette_int[ i/ paletteSize_int][i%3]); 

		}
		/*
		 * ATTENTION : En implémentant comme ça, on code sur les bits en R--B--G donc pas dans le bon ordre !!
		 * On inverse donc le cas i%3 = 1 et i%3 = 2
		 */ 
		palette[i] = toWrite[0];
		palette[i+1] = toWrite[1];
		}
		
		// pixelList :: tout est codé sur 1 octet
		pixelList = new byte [pixelList_int.length];
		for (int i=0;i < pixelList.length;i++) {
			pixelList[i] = intToOctet(pixelList_int[i]);
		}
	}

	
	//Fonction auxiliaires de conversion en bits, très pratique
	private byte [] intToTwoOctet(int n) {
		byte byte2 = (byte) (n >> 7);
		byte byte1 = (byte) (n - n>>7) ;
		byte [] table = {byte1,byte2};
		return table;
	}
	private byte intToOctet(int n) {
		assert (n<128); // juste pour vérifier que tout va bien 
		byte result = (byte) n;
		return result;
	}
	
	
	public void saveOnFile() {
		
		// On crée les données (on les met à la suite)
		byte [] rawBinData = new byte [h.length + w.length + 
		                               paletteSize.length + palette.length + pixelList.length ];
		
		//Pour plus de simplicité, on les met tous à la suite et on crée une variable offset pour
		//garder la position du i et bien tout écrire.
		
		int offset =0;
		//h
		for (int i=0;i<h.length;i++) {
			rawBinData[i+offset] = h[i];
		}
		offset +=h.length;
		//w
		for (int i=0;i<w.length;i++) {
			rawBinData[i+offset] = w[i];
		}
		offset += w.length;
		//pletteSize
		for (int i=0;i<paletteSize.length;i++) {
			rawBinData[i+offset] = paletteSize[i];
		}
		offset += paletteSize.length;
		//palette
		for (int i=0;i<palette.length;i++) {
			rawBinData[i+offset] = palette[i];
		}
		offset += palette.length;
		//pixelList
		for (int i=0;i<pixelList.length;i++) {
			rawBinData[i+offset] = pixelList[i];
		}
		
		// Ensuite, on écrit le flux d'octets "raw" dans un fichier
		
	    try {
		    OutputStream output = null;
			try {
		        output = new BufferedOutputStream(new FileOutputStream("out.comp"));
		        output.write(rawBinData);
		      }
			finally {
		        output.close();
		    }
		}
	    catch(FileNotFoundException ex){
	    	
	    }
	    catch(IOException ex){
	    	
	    }	
	}
	
	private int byteTwoToInt(byte [] table) {
		//Fonction bien spécifique au cas présent, données bin codées sur 2 octets
		int result = (int) table [0] + (int) table[1] >> 7;
		return result;
	}
	
	public void openFromFile(String fileName) throws IOException {
	    FileInputStream input = null;
		byte [] h_byte = new byte [2];
		byte [] w_byte = new byte [2];
		byte [] paletteSize;
		int i;
	    try {
			input = new FileInputStream(fileName);
		    i = input.read(h_byte); 
		    i = input.read(w_byte);
		    
		    
		    

		} catch(Exception ex) {
	        
	         // if any error occurs
	         ex.printStackTrace();
	      } finally {
	         
	         // releases all system resources from the streams
	         if(input!=null)
	            input.close();
	      }
	    
	}
	
	
	
	
}
