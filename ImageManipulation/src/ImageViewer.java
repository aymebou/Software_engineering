import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.jfree.ui.RefineryUtilities;

import javax.swing.JButton;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;


public class ImageViewer extends JFrame /*implements ActionListener*/
{
    /* Création des images */
    private DisplayedImage inputImage = new DisplayedImage();
    private DisplayedImage ouputImage = new DisplayedImage();
	private CompressedImage ouputImageComp = new CompressedImage();
	
	/*Création de l'affichage palette */
	private DisplayedImage paletteDisp = new DisplayedImage(20, 350, BufferedImage.TYPE_INT_ARGB);
	
	/* Création des boutons d'actions */
	private JButton buttonAction = new JButton("Action");
	private JButton buttonInversion = new JButton("Inversion"); 	//Crée un nouveau bouton "Inversion"
	private JButton buttonHisto = new JButton("Histogramme");		//Crée un nouveau bouton "Histogramme"
    private JButton buttonCompress = new JButton("Compresser");
 
    /* Création du menu déroulant File */
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu imageMenu = new JMenu("Image");
	private JMenu compMenu = new JMenu("Compressed image");
	private JMenuItem itemOpen = new JMenuItem("Open");			//Crée une nouvelle option "Open"
	private JMenuItem itemSave = new JMenuItem("Save");			//Crée une nouvelle option "Save"
	private JMenuItem itemOpenComp = new JMenuItem("Open");			//Crée une nouvelle option "Open"
	private JMenuItem itemSaveComp = new JMenuItem("Save");			//Crée une nouvelle option "Save"
	private JMenuItem itemClose = new JMenuItem("Close");

	public ImageViewer () {
		this.setTitle("Image Viewer");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 400);
	
		JPanel input = new JPanel();
		input.setLayout(new BoxLayout(input, BoxLayout.PAGE_AXIS));
		input.setPreferredSize(new Dimension(400, 400));
		input.add(inputImage);

		JPanel action = new JPanel();
		action.setLayout(new GridLayout(4,1));
		action.setMaximumSize(new Dimension(80, 400));
		action.add(buttonAction);
		// Defines action associated to buttons
		buttonAction.addActionListener(new ButtonListener());
		action.add(buttonInversion);
		buttonInversion.addActionListener(new Inversion());
		action.add(buttonHisto);
		buttonHisto.addActionListener(new Histogramme());
		action.add(buttonCompress);
		buttonCompress.addActionListener(new Compress());

		JPanel output = new JPanel();
		output.setLayout(new BoxLayout(output, BoxLayout.PAGE_AXIS));
		output.setPreferredSize(new Dimension(400, 400));
		output.add(ouputImage); 		
		
		JPanel paletteG = new JPanel();
		paletteG.setLayout(new BoxLayout(paletteG, BoxLayout.PAGE_AXIS)); 
		paletteG.setMinimumSize(new Dimension(40, 400));
		paletteG.add(paletteDisp);
		
		JPanel global = new JPanel();
		global.setLayout(new BoxLayout(global, BoxLayout.LINE_AXIS));
		global.add(input);
		global.add(action);
		global.add(output);
		global.add(paletteG);


		this.getContentPane().add(global);
		

		/* Définition de la fonction Open */
		itemOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				{
		            // création de la boîte de dialogue
		            JFileChooser dialogue = new JFileChooser();
		             
		            // affichage
		            int returnVal = dialogue.showOpenDialog(null);
		             
		            // récupération du fichier sélectionné
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        inputImage.setImage(dialogue.getSelectedFile());
                        ouputImage.setImage(dialogue.getSelectedFile());
                        input.add(inputImage);    //Ajoute l'image choisie
                        output.add(ouputImage);
                        input.repaint();            //Refresh l'image
                    }
		        }
			}
		});
		this.imageMenu.add(itemOpen);

		/* Définition de la fonction Save */
		itemSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				{
		            // création de la boîte de dialogue
		            JFileChooser dialogue = new JFileChooser();
		             
		            // affichage
		            int returnVal = dialogue.showSaveDialog(null);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
                        try {
                            Files.createFile(dialogue.getSelectedFile().toPath());
                            ImageIO.write(ouputImage.getBuffer(), "png", dialogue.getSelectedFile());        //Save as TextEdit file, why ?
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
				}
			}
		});
		this.imageMenu.add(itemSave);

		/* Définiton de la fonction Close */
		itemClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}        
		});
		
		itemOpenComp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				{
		            // création de la boîte de dialogue
		            JFileChooser dialogue = new JFileChooser();
		             
		            // affichage
		            int returnVal = dialogue.showOpenDialog(null);
		             
		            // récupération du fichier sélectionné
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                    		CompressedImage inputImageComp = new CompressedImage();
                    		try {
								inputImageComp.openFromFile(dialogue.getSelectedFile().toString());
							} catch (IOException e) {
								e.printStackTrace();
							}
                        inputImage.InitFromBuffer(inputImageComp.compressedImageToBufferedImage());
                        input.add(inputImage);    
                        ouputImage.InitFromBuffer(inputImageComp.compressedImageToBufferedImage());
                        output.add(ouputImage);	//Ajoute l'image choisie
                        input.repaint();            //Refresh l'image
                    }
		        }
			}
		});
		this.compMenu.add(itemOpenComp);

		
		itemSaveComp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				{
		            // création de la boîte de dialogue
		            JFileChooser dialogue = new JFileChooser();
		             
		            // affichage
		            int returnVal = dialogue.showSaveDialog(null);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
                        try {
                        		Files.createFile(dialogue.getSelectedFile().toPath());
                        		ouputImageComp.saveOnFile(dialogue.getSelectedFile().toPath());
                            //ImageIO.write(ouputImage.getBuffer(), "png", dialogue.getSelectedFile());        //Save as TextEdit file, why ?
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
				}
			}
		});
		this.compMenu.add(itemSaveComp);
		
		this.fileMenu.add(imageMenu);
		this.fileMenu.add(compMenu);
		this.fileMenu.addSeparator();
		this.fileMenu.add(itemClose);

		
		this.menuBar.add(fileMenu);
		this.setJMenuBar(menuBar);

		this.setVisible(true);
	}

	/**
	 * Class listening to a given button
	 */
	class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) 
		{
			System.out.println("Action Performed");
		}
	}
	
	class Inversion implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			ouputImage.inversion();
			ouputImage.repaint();			//Refresh l'image
		}
	}
	
	class Histogramme implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			int[] pixels = inputImage.getColor();
			BarChartInt chart = new BarChartInt("BarChart", 
			         "Répartition des couleurs", pixels);
			chart.pack( );        
			RefineryUtilities.centerFrameOnScreen( chart );        
			chart.setVisible( true ); 
		}
	}

	class Compress implements ActionListener {
	    public void actionPerformed(ActionEvent event){
	    		
	        int[][] pixels = inputImage.buildPixelArray(); //contiendra les 3 composantes de chaque pixel
	        KDTree colorsTree = new KDTree();
	        colorsTree.buildNLayersFromArray(pixels, 4);      //stocke certains les pixels dans le KDTree
            int[][] palette = colorsTree.buildPalette(4);  //construit une palette de 2^powOf2 couleurs (
            paletteDisp.createPalette(palette);
			paletteDisp.repaint();
            ouputImage.compress(palette, pixels);
            ouputImage.repaint();
            ouputImageComp = ouputImage.displayedImageToCompressedImage(palette, pixels);
        }
    }
	

}