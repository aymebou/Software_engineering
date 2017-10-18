import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
 
public class DisplayedImage extends JPanel {
	
    private BufferedImage image;

    /*Par defaut, on affiche img.png */
    public DisplayedImage() {
    		try {
    			image = ImageIO.read(new File("/home/martin/Documents/Cours/Depinfo/Software_Engineering/TD2/Software_engineering/Zanni_files/img.png"));
    		} catch (IOException e) {
        		e.printStackTrace();
        	}                
    }

    /* Si on fournit un fichier en argument, on affiche l'image correspondante*/
	public DisplayedImage(File file) {
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public void paintComponent(Graphics g){
    		//g.drawImage(image, 0, 0, this); // draw as much as possible
    		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this); // draw full image
    }                   
}
