import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImageViewer extends JFrame /*implements ActionListener*/
{
	private DisplayedImage inputImage = new DisplayedImage();
	private DisplayedImage outputImage = new DisplayedImage();
	private JButton buttonAction = new JButton("Action");

	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");

	private JMenuItem itemClose = new JMenuItem("Close");
	private JMenuItem itemOpen = new JMenuItem("Open");   /* Création du bouton Open */

	private JFileChooser chooser = new JFileChooser();

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

		JPanel output = new JPanel();
		output.setLayout(new BoxLayout(output, BoxLayout.PAGE_AXIS));
		output.add(outputImage);

		JPanel global = new JPanel();
		global.setLayout(new BoxLayout(global, BoxLayout.LINE_AXIS));
		global.add(input);
		global.add(action);
		global.add(output);

		this.getContentPane().add(global);

		this.fileMenu.addSeparator();
		itemClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}        
		});
		this.fileMenu.add(itemClose);  
		
		//implementation du bouton Open
		this.fileMenu.addSeparator();
		itemOpen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = chooser.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					inputImage.changeImage(chooser.getSelectedFile());
					outputImage.changeImage(chooser.getSelectedFile());
					input.repaint();
					output.repaint();
				}
			}
		});
		this.fileMenu.add(itemOpen);
		//fin bouton Open


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
}
