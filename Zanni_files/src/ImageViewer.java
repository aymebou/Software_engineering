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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ImageViewer extends JFrame /*implements ActionListener*/
{
	private DisplayedImage inputImage = new DisplayedImage(); 
	private DisplayedImage ouputImage = new DisplayedImage();
	private JButton buttonAction = new JButton("Action");
	private JButton buttonInversion = new JButton("Inversion"); 	//Crée un nouveau bouton "Inversion"
	private JButton buttonHisto = new JButton("Histogramme");		//Crée un nouveau bouton "Histogramme"

	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	
	private JMenuItem itemOpen = new JMenuItem("Open");			//Crée une nouvelle option "Open"
	private JMenuItem itemSave = new JMenuItem("Save");			//Crée une nouvelle option "Save"
	private JMenuItem itemClose = new JMenuItem("Close");

	public ImageViewer () {
		this.setTitle("Image Viewer");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 400);

		JPanel input = new JPanel();
		input.setLayout(new BoxLayout(input, BoxLayout.PAGE_AXIS));
		input.add(inputImage);

		JPanel action = new JPanel();
		action.setLayout(new BoxLayout(action, BoxLayout.PAGE_AXIS));
		action.add(buttonAction);
		// Defines action associated to buttons
		buttonAction.addActionListener(new ButtonListener());
		action.add(buttonInversion);
		buttonInversion.addActionListener(new Inversion());
		action.add(buttonHisto);
		buttonHisto.addActionListener(new Histogramme());

		JPanel output = new JPanel();
		output.setLayout(new BoxLayout(output, BoxLayout.PAGE_AXIS));
		output.add(ouputImage); 

		JPanel global = new JPanel();
		global.setLayout(new BoxLayout(global, BoxLayout.LINE_AXIS));
		global.add(input);
		global.add(action);
		global.add(output);

		this.getContentPane().add(global);

		itemOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				{
		            // création de la boîte de dialogue
		            JFileChooser dialogue = new JFileChooser();
		             
		            // affichage
		            dialogue.showOpenDialog(null);
		             
		            // récupération du fichier sélectionné
		            inputImage.setImage(dialogue.getSelectedFile());
		            ouputImage.setImage(dialogue.getSelectedFile());
		            input.add(inputImage);	//Ajoute l'image choisie
		            output.add(ouputImage);
		            input.repaint();			//Refresh l'image
		        }
			}//Aucune gestion de l'échec
		});
		this.fileMenu.add(itemOpen);
		
		this.fileMenu.addSeparator();
		itemSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				{
		            // création de la boîte de dialogue
		            JFileChooser dialogue = new JFileChooser();
		             
		            // affichage
		            dialogue.showSaveDialog(null);
		            try {
		            		ImageIO.write(ouputImage.getBuffer(), "png", dialogue.getSelectedFile());		//Save as TextEdit file, why ?
		            } catch (IOException e) {
		            		e.printStackTrace();
		            }
				}
			}
		});
		this.fileMenu.add(itemSave);
		
		this.fileMenu.addSeparator();
		itemClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}        
		});
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
			BarChartInt chart = new BarChartInt("Tré bo barChart", 
			         "Répartition des couleurs", pixels);
			chart.pack( );        
			RefineryUtilities.centerFrameOnScreen( chart );        
			chart.setVisible( true ); 
		}
	}

}