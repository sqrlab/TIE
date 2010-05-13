package Backups;


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

public class PaintTopGUIBackUp8 extends JPanel{
	
	// to generate random numbers
	private Dimension scrnsize;
	private int sq_height;
	private int sq_width;
    private int max_Transitions;
	private final int y_offset = 0;
	private List<Integer> transition_Num;
	private List<List<Integer>> transition_states;
	private List<List<String>> transition_states_info;
	private int trace_Num;
	private int thread_Num;
	private Dimension topDim;
	private Graphics gCopy;
	
	// highlight vlaues
	private boolean highlight = false;
	private int highlight_x = -1;
	private int highlight_y = -1;
	
	// colors
	private Color[] genColors;
	private Color[] threadColor ;
	
    
	public PaintTopGUIBackUp8(int trace_Num, int thread_Num, List<Integer> transition_Num, List<List<Integer>> transition_states, List<List<String>> transition_states_info){

		
		this.transition_Num = transition_Num;
		this.transition_states = transition_states;
		this.transition_states_info = transition_states_info;
		this.trace_Num = trace_Num;
		this.thread_Num = thread_Num;
		
		// get max transitions
		max_Transitions = 0;
		for(int i=0; i < transition_Num.size(); i++){
            if(transition_Num.get(i) > max_Transitions){
            	max_Transitions = transition_Num.get(i);
            }
	    }
		
		// Get the current screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		scrnsize = toolkit.getScreenSize();
		scrnsize.width = scrnsize.width;
		scrnsize.height = scrnsize.height - 20;
		
        JPanel topPanel = new JPanel();
        topPanel.setLayout(null);
        
        topPanel.setBackground(Color.WHITE);
        topPanel.setVisible(true);
      
        topDim = new Dimension();
        topDim.height = scrnsize.height / 3;
        topDim.width = scrnsize.width;
        topPanel.setBackground(Color.white);
        
		// initializing the square height
		sq_width = (int)(topDim.width / trace_Num);
		sq_height = (int)(topDim.height / max_Transitions);
		
        this.add(topPanel);
        this.setSize(topDim);
        this.setVisible(true);

	}
	
	
    public void paint(Graphics g) {		  
    	  gCopy = g;
		  g.setColor(Color.WHITE);
		  g.fillRect(0, 0, scrnsize.width, scrnsize.height);
		  int y = 0;
		  int x = 0;
		  int threadNum = 0;
		  
		  
			// Defining a Color to each thread
	        genColors = new Color[10];
	        genThreadColors();
			Color[] threadColor = new Color[thread_Num];
		    for (int i=0; i < thread_Num; i++){						
				 // assign colors
				 threadColor[i] = genColors[i];
			 }	
			
		  
		  for(int j = 0; j < trace_Num; j++){
			x = j * sq_width;
			y = y_offset;
			
			// coloring the panel
			for(int i=0; i < transition_states.get(j).size(); i++){
				 y = y_offset + i*sq_height;
				
				 threadNum = transition_states.get(j).get(i);
			     g.setColor(threadColor[threadNum]);
			    
			     if(highlight){
			    	 if(j == highlight_x && i == highlight_y){
			    	      g.setColor(Color.RED);
			    	 }
			     }
			     
			     g.fillRect(x, y, sq_width, sq_height); 
			    
			}
			
			// drawing the borders
			g.setColor(Color.WHITE);
			g.drawLine(x, 0, x, transition_states.get(j).size()*sq_height);
			g.drawLine((j+1) * sq_width, 0, (j+1) * sq_width, transition_states.get(j).size()*sq_height);
			g.drawLine(j * sq_width, transition_states.get(j).size()*sq_height, (j+1) * sq_width, transition_states.get(j).size()*sq_height);
			 
		  }
    }
    
	/*
	 * used with mouse listener to get the column (x)
	 */
	public int getTraceNumber(int x){
		
		for (int i = 1; i < trace_Num ; i++){
			
			if((x > (i-1)*sq_width) && (x < i*sq_width)){
				return (i-1);
			}
		}
		
		return trace_Num - 1;
	}
	
	/*
	 * used with mouse listener to get the row (y)
	 */
	public int getTransitionNumber(int trace, int y){
		
		for (int i = 1; i < transition_states.get(trace).size() ; i++){
			
			if((y > (i-1)*sq_height) && (y < i*sq_height)){
	
				return (i-1);
			}
		}
		
		return transition_states.get(trace).size() -1;
	}
	
	/*
	 * 
	 */
	public void setHighlight(int row, int col){
		highlight = true;
		highlight_x = col;
		highlight_y = row;
		
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