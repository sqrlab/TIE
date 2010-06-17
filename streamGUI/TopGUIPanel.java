package gov.nasa.jpf.gVisualizer;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class TopGUIPanel extends JPanel{
	
	// to generate random numbers
	private Dimension scrnsize;
	private int sq_height;
	private int sq_width;
    private int max_Transitions;
	private final int y_offset = 0;
	private List<Integer> transitionNum;
	private List<List<Integer>> transition_states;
	private int traceNum;
	private int threadNum;
	private Dimension topDim;
	private Dimension scrollDim;
	
	// highlight vlaues
	private boolean highlight = false;
	private int highlight_x = -1;
	private int highlight_y = -1;
	
	int[][] otherHighlight_ours;
	
	// colors
	private Color[] genColors;
	private Color[] threadColor ;
	
	// When a certain column is highlighted, we need to darken the colour with this constant
	private int colorInc = 100;
	private boolean MenuHighlight = false;
	private JPanel topPanel;
	
    
	public TopGUIPanel(int trace_Num, int thread_Num, List<Integer> transition_Num, List<List<Integer>> transition_states, Dimension topScrollDim){
		this.topDim = topScrollDim;
		
        topPanel = new JPanel();
        topPanel.setLayout(null);
        
        topPanel.setBackground(Color.white);
        topPanel.setVisible(true);
		
        //System.out.println("3. TopPaint Constructor");
		  
		// Defining a Color to each thread
        genColors = new Color[10];
        genThreadColors();
		threadColor = new Color[thread_Num];
	    for (int i=0; i < thread_Num; i++){						
			 // assign colors
			 threadColor[i] = genColors[i];
		 }	
        
		// set the values
		this.setValues(trace_Num, transition_Num, transition_states);
       
		this.add(topPanel);
        this.setSize(topDim);
        this.setVisible(true);

	}
	
	/*
	 * 
	 */
	public void setValues(int trace_Num, List<Integer> transition_Num, List<List<Integer>> transition_states){
		this.transitionNum = transition_Num;
		this.transition_states = transition_states;
		this.traceNum = trace_Num;
		
		//System.out.println("4.0 - TopPaint SetValues");
		
		// get max transitions
		max_Transitions = 0;
		for(int i=0; i < transitionNum.size(); i++){
            if(transitionNum.get(i) > max_Transitions){
            	max_Transitions = transition_Num.get(i);
            }
	    }
		

		//System.out.println(transition_states.toString());
		
		// Get the current screen size
	/*	Toolkit toolkit = Toolkit.getDefaultToolkit();
		scrnsize = toolkit.getScreenSize();
		scrnsize.width = scrnsize.width;
		scrnsize.height = scrnsize.height - 20;
		*/
        
		// initializing the square height
		sq_width = (int)(topDim.width/ traceNum);
		sq_height = (int)(topDim.height / max_Transitions + 1);

	}
	
	/*
	 * This method draws the schedules
	 * 
	 */
    public void paint(Graphics g) {		
    	  //System.out.println("4.1 -  TopPaint SetValues");
    	
		  g.setColor(Color.WHITE);
		  
		  g.fillRect(0, 0, this.getSize().width, this.getSize().height);
		  int y = 0;
		  int x = 0;
		  int threadNum = 0;
			
		    		    
		  for(int j = 0; j < traceNum; j++){
			x = j * sq_width;
			y = y_offset;
			
			
			// coloring the panel
			for(int i=0; i < transition_states.get(j).size(); i++){
				//System.out.println( "Trace Number: " + j + " ------> Transition Number: " + i);
				
				 y = y_offset + i*sq_height;
				
				 threadNum = transition_states.get(j).get(i);
			     g.setColor(threadColor[threadNum]);
			     g.fillRect(x, y, sq_width, sq_height);
			    
			     // error button
			    
			     if(i == transition_states.get(j).size() - 1){
					g.setColor(Color.GRAY);     
				   	g.fillRect(x, y, sq_width, sq_height);
			     }
			     
			     
			     // specific highlight
			     if(highlight){
			    	 
			    	 if(j == highlight_x){
			    	    Color tempLight = threadColor[threadNum].brighter();
			    	 
			    	    g.setColor(tempLight);
				        g.fillRect(x, y, sq_width, sq_height);
					    // error button
				        if(i == transition_states.get(j).size() - 1){
								g.setColor(Color.GRAY.brighter());     
							   	g.fillRect(x, y, sq_width, sq_height);
						}
			    	 }
			    	 else{
				    	    Color tempDark = threadColor[threadNum].darker();
					    	 
				    	    g.setColor(tempDark);
					        g.fillRect(x, y, sq_width, sq_height);
					        // error button
					        if(i == transition_states.get(j).size() - 1){
								g.setColor(Color.GRAY.darker());     
							   	g.fillRect(x, y, sq_width, sq_height);
						}
			    	 }
			    	 
			    	 // highlight similar transitions
			    	 if(MenuHighlight){
			    		 for(int z = 0; z < otherHighlight_ours.length; z++){
			    		 	if(j == otherHighlight_ours[z][0]  && i == otherHighlight_ours[z][1]){
				    		 	 			    		  
						     	g.setColor(Color.yellow);
						     	// g.setColor(threadColor[threadNum]);
						     	g.fillOval(x, y, sq_width, sq_height);
						     	//g.drawRect(x, y, sq_width, sq_height);
				    	 	}
			    		 
			    	 	}
			    	 }
			    	 
			    	 
			    	 if(j == highlight_x  && i == highlight_y){
			    		 g.setColor(Color.RED);	 
					     g.fillRect(x, y, sq_width, sq_height);
			    		  
			    	     // g.setColor(threadColor[threadNum]);
			    	      //g.fillOval(x, y, sq_width, sq_height);
			    	      //g.drawRect(x, y, sq_width, sq_height);
			    	 }
			    	 
			     }
			        
			}
			
			// drawing the borders
		  
		   	if(j == highlight_x || (j-1) == highlight_x && highlight){
		   		g.setColor(Color.RED);
				g.drawLine(x, 0, x, max_Transitions*sq_height);
		   	}else{
			   g.setColor(Color.WHITE);
			   g.drawLine(x, 0, x, max_Transitions*sq_height);
		   	}


			 
		  }
    }
    
	/*
	 * used with mouse listener to get the column (x)
	 */
	public int getTraceNumber(int x){
		
		for (int i = 1; i < traceNum ; i++){
			
			if((x > (i-1)*sq_width) && (x < i*sq_width)){
				return (i-1);
			}
		}
		
		return traceNum - 1;
	}
	
	/*
	 * used with mouse listener to get the row (y)
	 */
	public int getTransitionNumber(int trace, int y){
		
		for (int i = 1; i < transition_states.get(trace).size(); i++){
			
			if((y > (i-1)*sq_height) && (y < i*sq_height)){
				//System.out.println("ROW: " + (i - 1));
				return (i-1);
			}
		}
		
		return transition_states.get(trace).size() - 1;
	}
	
	/*
	 * This method is used to highlight the current transition
	 * selected.
	 */
	public void setHighlight(int row, int col, int[][] otherHighlight, int size){
		highlight = true;
		highlight_x = col;
		highlight_y = row;
		
		// fill in row and col values for the other cubes that need to be selected
		if(otherHighlight != null){
		   otherHighlight_ours = new int[size][2];
		   for(int i = 0; i < size; i++){
			 // System.out.println("paintTop i: " + i);
			  otherHighlight_ours[i][0] = otherHighlight[i][0];
			  otherHighlight_ours[i][1] = otherHighlight[i][1];
		   }
		}
		
	}
	
	
	/*
	 * Setter Method for Menu Highlight Option
	 */
	int checkTimes = 0;
	public void setMenuHighlight(){
		checkTimes++;
		if(checkTimes % 2 == 0){
		   this.MenuHighlight = false;
		}
		else{
			this.MenuHighlight = true;
		}
	}
	
	/*
	 * This method generates colors for up to 10 different threads (RGB values)
	 * - We are not using red because it is the high lighter color
	 */
	public void genThreadColors(){
		genColors[0] = new Color(58,98,132); // blue
		genColors[1] = new Color(51,157,70); // green
		genColors[2] = new Color(255,127,50); // orange
		genColors[3] = new Color(145,105,171); // purple
		genColors[4] = new Color(136,89,79); // brown
		genColors[5] = new Color(226,123,182); // pink
		genColors[6] = new Color(88,88,88); // grey
		genColors[7] = new Color(84,143,174); // teal
		genColors[8] = new Color(191,186,57); // gold
		genColors[9] = new Color(1,71,189); // royalblue
	}
}
