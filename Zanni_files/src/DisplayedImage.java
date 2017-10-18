import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
 
public class DisplayedImage extends JPanel {
	
    private BufferedImage image;
    
    public DisplayedImage() {
    }
    	
    	public void setImage(File imageName) {			//Modifie l'image.
    		try {
    			image = ImageIO.read(imageName);
        	} catch (IOException e) {
        		e.printStackTrace();
        	}                
    }
    		
    	public BufferedImage getBuffer() {				//Renvoie le buffer de l'image
    		return this.image;
    	}
    	
    	public void inversion() {
    		int w = this.image.getWidth();
    		int h = this.image.getHeight();
    	
    		for (int i = 0; i < w; i++) {
    			for (int j = 0; j < h; j++) {
    				Color color = new Color(image.getRGB(i, j));			//Création d'un objet color. Puis modification de ses couleurs
    				int blue = Math.abs(color.getBlue() - 255);
    				int red = Math.abs(color.getRed() - 255);
    				int green = Math.abs(color.getGreen() - 255);
    
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
    	
    public void paintComponent(Graphics g){
    		//g.drawImage(image, 0, 0, this); // draw as much as possible
    		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this); // draw full image
    }                   
}