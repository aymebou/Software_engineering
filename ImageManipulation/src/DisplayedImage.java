import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import static java.lang.Math.pow;

public class DisplayedImage extends JPanel {
	
    private BufferedImage image;
    private int[][] palette; //Si l'image est issue d'une compression, contient la palette

    public DisplayedImage(){ }

    
    public DisplayedImage(int x, int y, int type) {
    		image = new BufferedImage(x, y, type);
    }
    
    public void InitFromBuffer(BufferedImage buffer) {
    		image = buffer;
    }
    
    public void setPixelColor(int x, int y, Color color) {
    	int rgb = color.getRGB();
    	image.setRGB(x, y, rgb);
    }
    	
    public void setImage(File imageName) {			//Modifie l'image.
        try {
            image = ImageIO.read(imageName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    

    public void setImage(BufferedImage image){
        this.image = image;
    }
    
    		
    public BufferedImage getBuffer() {				//Renvoie le buffer de l'image
    		return this.image;
    	}

    public int[][] getPalette(){
        return palette;
    }
    	
    public void inversion() {
        int w = this.image.getWidth();
        int h = this.image.getHeight();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color color = new Color(image.getRGB(i, j));			//Création d'un objet color. Puis modification de ses couleurs
                int blue = 255 - color.getBlue();
                int red = 255 - color.getRed();
                int green = 255 - color.getGreen();

                image.setRGB(i,  j, new Color(red, green, blue).getRGB());	//Modification des couleurs de l'image.
            }
        }
    }

    public int[] getColor() {
        int[] res = {0, 0, 0};
        int w = this.image.getWidth();
        int h = this.image.getHeight();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color color = new Color(image.getRGB(i, j));
                if(color.getBlue() > color.getRed() && color.getBlue() > color.getGreen()) {
                    res[0]++;
                }
                else if(color.getRed() > color.getBlue() && color.getRed() > color.getGreen()) {
                    res[1]++;
                }
                else {
                    res[2]++;
                }
            }
        }
        return res;
    }
    
    public int[] getRGB(int x, int y){
        int res[] = new int[3];
        Color color = new Color(this.image.getRGB(x, y));
        res[0] = color.getRed();
        res[1] = color.getGreen();
        res[2] = color.getBlue();
        return res;
    }
    



    /* Retourne un tableau contenat les 3 composantes RGB de chacun des pixels */
    public int[][] buildPixelArray() {
        int height = this.image.getHeight();
        int width = this.image.getWidth();
        int[][] pixels = new int[height * width][3];
        for (int y = 0 ; y < height ; y++){
            for (int x = 0 ; x < width ; x++){
                Color color = new Color(this.image.getRGB(x, y));
                pixels[y*width + x][0] = color.getRed();      //Les indices sont un peu laborieux comme les pixels sont stockés
                pixels[y*width + x][1] = color.getGreen();    // en une seule dimension mais ça marche
                pixels[y*width + x][2] = color.getBlue();
            }
        }
        return pixels;
    }

    /* Remplace le pixel(x, y) de outputImage par le pixel de la palette le plus adapté */
    public void setBestColor(int x, int y, int[][] palette, int[] pixel){
    		Color color = new Color(palette[0][0], palette[0][1], palette[0][2]);
        int min = (int)pow((pixel[0] - palette[0][0]), 2) + (int)pow((pixel[1] - palette[0][1]), 2) + (int)pow((pixel[2] - palette[0][2]), 2);
        for (int k = 1 ; k < palette.length ; k++){
            int distance = (int)pow((pixel[0] - palette[k][0]), 2) + (int)pow((pixel[1] - palette[k][1]), 2) + (int)pow((pixel[2] - palette[k][2]), 2);
            if (distance < min){
            		min = distance;
                color = new Color(palette[k][0], palette[k][1], palette[k][2]);
            }
        }
        this.image.setRGB(x, y, color.getRGB());
    }
    
    /* Construit l'image compressée en appelant setBestColor pour chaque pixel */
    public void compress(int[][] palette, int[][] pixels){
        this.palette = palette;
        int height = this.image.getHeight();
        int width = this.image.getWidth();
        for (int y = 0 ; y < height ; y++){
            for (int x = 0 ; x < width ; x++){
                this.setBestColor(x, y, palette, pixels[y * width + x]);
            }
        }
    }


    public void drawPalette(int[][] palette) {
    		for(int i = 0; i < 16; i++) {
    			for(int j = 0; j < 20; j++) {
    				for(int k = 0; k < 20; k++) {
    					Color color = new Color(palette[i][0], palette[i][1], palette[i][2]);
    					this.image.setRGB(j, i*22 + k, color.getRGB());
    				}
    			}
    		}
    }

    public void paintComponent(Graphics g){
    		//g.drawImage(image, 0, 0, this); // draw as much as possible
    		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this); // draw full image
    }                   
}