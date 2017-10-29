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
import java.nio.file.Path;
import java.awt.Color;
import java.awt.image.BufferedImage;



public class CompressedImage {
	//D�finition d'une image compress�e : fichier non propri�taire, �crit en binaire
	/* On code sur 2 octets la hauteur d'abord puis sur 2 octets �galement la largeur
	 * Vient aussi la taille de la palette, sur 2 octets.
	 * En soi, il faudrait rester coh�rent et stocker la taille de la palette sur 1 octet, comme 
	 * les positions sont stock�es sur 1 octet mais bon...
	 * 
	 * On code ensuite la palette en RGB : celle-ci dispose de 8 (16?) couleurs dans notre cas donc 
	 * celle ci sera cod�e sur 8*3*2 octet (afin que les RGB puissent aller jusqu'� 256 bit chacun) 
	 * Enfin, le liste des pixels dans l'ordre depuis le coin haut gauche jusqu'au coin bas droite, en ligne
	 * 
	 *  ATTENTION : on ne note pas les couleurs des pixels mais bien les positions (indices) dans la palette
	 *  chacun cod� sur 1 octet
	 * 
	 * EXTENSION DE FICHIER : .comp
	 * */
	
	
	private byte [] h;
	private byte [] w;
	private byte [] paletteSize;
	private byte [] palette;
	private byte [] pixelList;
	

	public CompressedImage() {
	}
	
	//initialisation � partir des donn�es classiques
	public void initCompressedImage (int h_int, int w_int,int paletteSize_int, int [][] palette_int, int [] pixelList_int ) {
		
		//h, w et paletteSize :
		h = intToTwoOctet(h_int);
		w = intToTwoOctet(w_int);
		paletteSize = intToTwoOctet(paletteSize_int);
		//Palette ::
		palette = new byte [paletteSize_int * 6];
		for (int i = 0; i < paletteSize_int; i ++) {
			for(int j = 0; j < 3; j++) {
				byte[] toWrite;
				toWrite = intToTwoOctet(palette_int[i][j]);
				palette[i*6 + j*2 ] = toWrite[0];
				palette[i*6 + j*2 + 1] = toWrite[1];
			}
		}
		// pixelList :: tout est cod� sur 1 octet
		pixelList = new byte [pixelList_int.length];
		for (int i=0;i < pixelList.length;i++) {
			pixelList[i] = intToOctet(pixelList_int[i]);
		}
	}

	
	//Fonction auxiliaires de conversion en bits, tr�s pratique
	static byte [] intToTwoOctet(int n) {
		byte byte1 = (byte) (n & 0x7F);
		byte byte2 = (byte) ((n >> 7) & 0x7F);
		byte [] table = {byte1,byte2};
		return table;
	}
	private byte intToOctet(int n) {
		assert (n<128); // juste pour v�rifier que tout va bien 
		byte result = (byte) n;
		return result;
	}
	
	
	public void saveOnFile(Path path) {
		
		// On cr�e les donn�es (on les met � la suite)
		byte [] rawBinData = new byte [h.length + w.length + 
		                               paletteSize.length + palette.length + pixelList.length ];
		
		//Pour plus de simplicit�, on les met tous � la suite et on cr�e une variable offset pour
		//garder la position du i et bien tout �crire.
		
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
		
		// Ensuite, on �crit le flux d'octets "raw" dans un fichier
		
	    try {
		    OutputStream output = null;
			try {
				output = new BufferedOutputStream(new FileOutputStream(new File(path + ".comp")));
		        //output = new BufferedOutputStream(new FileOutputStream("out.comp"));
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
		//Fonction bien sp�cifique au cas pr�sent, donn�es bin cod�es sur 2 octet
		int result = (int) table [0] + (int) table[1]*128;
		return result;
	}
	
	public void openFromFile(String fileName) throws IOException {
	    FileInputStream input = null;
		h = new byte [2];
		w = new byte [2];
		paletteSize = new byte [2];
		/*byte [] palette;
		byte [] pixelList;*/
		
	    try {
			input = new FileInputStream(fileName);
			//Maintenant, stockons
			// Le read fonctionne comme en C, on a un pointeur qui se souvient de l'endroit o� on est.
		    input.read(h);
		    input.read(w);
		    int h_int = byteTwoToInt(h);
		    int w_int = byteTwoToInt(w);
		    input.read(paletteSize);
		    int paletteSize_int = byteTwoToInt(paletteSize);
		    palette = new byte [paletteSize_int * 6];
		    input.read(palette);
		    pixelList = new byte [h_int*w_int];
		    input.read(pixelList); 

		} catch(Exception ex) {
	        
	         // if any error occurs
	         ex.printStackTrace();
	      } finally {
	         
	         // releases all system resources from the streams
	         if(input!=null)
	            input.close();
	      }
	    
	}
	
	public DisplayedImage compressedImageToDisplayedImage () {
		DisplayedImage pic = new DisplayedImage(byteTwoToInt(w),byteTwoToInt(h),BufferedImage.TYPE_INT_ARGB);
		
		byte [] R_byte; 
		byte [] G_byte; 
		byte [] B_byte;
		int w_int = byteTwoToInt(w);
		for (int x = 0; x < w_int ; x++) {
			for (int y = 0; y <byteTwoToInt(h) ; y++) {
				R_byte = new byte [] { palette[(int) 6*pixelList[y*w_int + x]], 
						palette[ (int) 6*pixelList[y*w_int + x] + 1]};

				G_byte = new byte [] { palette[(int) 6*pixelList[y*w_int + x] + 2], 
						palette[ (int) 6*pixelList[y*w_int + x] + 3]};

				B_byte = new byte [] { palette[(int) 6*pixelList[y*w_int + x] + 4], 
						palette[ (int) 6*pixelList[y*w_int + x] + 5]};
				Color color = new Color(byteTwoToInt(R_byte), byteTwoToInt(G_byte), byteTwoToInt(B_byte));
				pic.setPixelColor(x, y, color);
			}
		}
		return pic;
	}
	
	public BufferedImage compressedImageToBufferedImage () {
		DisplayedImage pic = new DisplayedImage(byteTwoToInt(w),byteTwoToInt(h),BufferedImage.TYPE_INT_ARGB);
		
		byte [] R_byte; 
		byte [] G_byte; 
		byte [] B_byte;
		int w_int = byteTwoToInt(w);
		for (int x = 0; x < w_int ; x++) {
			for (int y = 0; y <byteTwoToInt(h) ; y++) {
				R_byte = new byte [] { palette[(int) 6*pixelList[y*w_int + x]], 
						palette[ (int) 6*pixelList[y*w_int + x] + 1]};

				G_byte = new byte [] { palette[(int) 6*pixelList[y*w_int + x] + 2], 
						palette[ (int) 6*pixelList[y*w_int + x] + 3]};

				B_byte = new byte [] { palette[(int) 6*pixelList[y*w_int + x] + 4], 
						palette[ (int) 6*pixelList[y*w_int + x] + 5]};
				Color color = new Color(byteTwoToInt(R_byte), byteTwoToInt(G_byte), byteTwoToInt(B_byte));
				pic.setPixelColor(x, y, color);
			}
		}
		return pic.getBuffer();
	}
		
	
	
	
	
}
