import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset; 
import org.jfree.data.category.DefaultCategoryDataset; 
import org.jfree.ui.ApplicationFrame; 

public class BarChartInt extends ApplicationFrame {
	
		int[] data;
	
		   public BarChartInt( String applicationTitle , String chartTitle , int[] data ) {
		      super( applicationTitle );    
		      this.data = data;
		      JFreeChart barChart = ChartFactory.createBarChart(
		         chartTitle,           
		         "Category",            
		         "Score",            
		         createDataset(),          
		         PlotOrientation.VERTICAL,           
		         true, true, false);
		         
		      ChartPanel chartPanel = new ChartPanel( barChart );        
		      chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );        
		      setContentPane( chartPanel ); 
		   }
		   
		   private CategoryDataset createDataset( ) {
		      final String red = "Red";        
		      final String blue = "Blue";        
		      final String green = "Green";    
		      
		      final DefaultCategoryDataset dataset = 
		      new DefaultCategoryDataset( );  

		      dataset.addValue( this.data[1] , red , "");   
		      dataset.addValue( this.data[0] , blue , ""); 
		      dataset.addValue( this.data[2] , green , ""); 
		                    

		      return dataset; 
		   }
}
