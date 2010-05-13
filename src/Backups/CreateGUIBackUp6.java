package Backups;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

public class CreateGUIBackUp6 extends JFrame{
	
	// to generate random numbers
	private final int m = 256;
	private final int a = 67;
	private final int c = 27;
	private int x_new = 33;
	private int x_prev = 33;
	private Dimension scrnsize;

	public CreateGUIBackUp6(int trace_Num, int thread_Num, List<Integer> transition_Num, List<List<Integer>> transition_states, List<List<String>> transition_states_info){
		int state = 0;
		int randomR = 0;
		int randomG = 0;
		int randomB = 0;
		
        // Get the default toolkit
		Toolkit toolkit = Toolkit.getDefaultToolkit();

		// Get the current screen size
		scrnsize = toolkit.getScreenSize();
		scrnsize.width = scrnsize.width;
		scrnsize.height = scrnsize.height - 20;
		
		//this.setExtendedState(MAXIMIZED_BOTH);
		this.setPreferredSize(scrnsize);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(5,5));
		mainPanel.setBackground(Color.WHITE);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1, trace_Num));
		
		final JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(150, 1));
		leftPanel.setBackground(Color.white);
		
		JButton threadLegend;
		JLabel legendText;
		
		// Borders
		Border blackline = BorderFactory.createLineBorder(Color.black);
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border compoundBorderRaised = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(
		                10, 5, 10, 5, Color.black), raisedbevel);

		Border compoundBorderLowered = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(
		                2, 2, 2, 2, Color.black), loweredbevel);
		
        leftPanel.setBorder(compoundBorderRaised);
        topPanel.setBorder(blackline);
        
		
		// Defining a Color to each thread
		Color[] threadColor = new Color[thread_Num];
		   for (int i=0; i < thread_Num; i++){						
			
			 randomR = this.genRandomNumbers();
			 randomG = this.genRandomNumbers();	 
		     randomB = this.genRandomNumbers();
					   
			 // fill color
			 threadColor[i] = new Color(randomR, randomG, randomB);
			 threadLegend = new JButton();
			 threadLegend.setBackground(threadColor[i]);
			 threadLegend.setEnabled(false);
			
			 legendText = new JLabel("- Thread " + i);
		  }		
		   
	   // Text Area for transitions
	   final JTextArea transitions = new JTextArea("");
	   transitions.setColumns(100);
	   transitions.setRows(20);
	   transitions.setBackground(Color.white);
	   
	   final JTextArea transitions2 = new JTextArea("");
	   transitions.setColumns(100);
	   transitions.setRows(20);
	   transitions.setBackground(Color.white);
	   
	   final JTextArea transitions3 = new JTextArea("");
	   transitions.setColumns(100);
	   transitions.setRows(20);
	   transitions.setBackground(Color.white);
			
	   final JScrollPane scrollingArea = new JScrollPane(transitions);	
	   scrollingArea.setBorder(blackline);
	   
	   final JScrollPane scrollingArea2 = new JScrollPane(transitions2);	
	   scrollingArea.setBorder(blackline);
	   
	   final JScrollPane scrollingArea3 = new JScrollPane(transitions3);	
	   scrollingArea.setBorder(blackline);
	   
	   // COME BACK
	   // mainPanel.add(scrollingArea, BorderLayout.CENTER);
		
		Vector<JPanel> schedPaneArray = new Vector();
		Vector<JPanel> schedPaneArrayWrap = new Vector();
		
		// Top Panel Buttons
		List<List<JButton>> topPanelBtns = new ArrayList<List<JButton>>();
		
		// Left Panel Buttons
		final List<List<JButton>> leftPanelBtns = new ArrayList<List<JButton>>();
		
		for(int j = 0; j < trace_Num; j++){
			schedPaneArray.add(new JPanel());
			schedPaneArrayWrap.add(new JPanel());
			schedPaneArrayWrap.get(j).setBorder(blackline);
			
			topPanelBtns.add(new ArrayList<JButton>());
			leftPanelBtns.add(new ArrayList<JButton>());
			
			
			schedPaneArray.get(j).setLayout(new GridBagLayout());
			schedPaneArrayWrap.get(j).setLayout(new BorderLayout(10,10));
			schedPaneArrayWrap.get(j).setBackground(Color.WHITE);
			
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			
		
		  // Adding buttons for each transition
		  JButton[] threadButton = new JButton[transition_Num.get(j)];
		 // System.out.println(j + " --> Size: " + transition_Num.get(j));
		  for(int i = 0; i < transition_Num.get(j); i++){
			threadButton[i] = new JButton("Transition: " + i);
			topPanelBtns.get(j).add(new JButton("Transition: " + j + "." + i));
			leftPanelBtns.get(j).add(new JButton("Transition: " + j + "." + i));
		  }
		
		  for(int i=0; i < transition_states.get(j).size(); i++){
		     final String info = transition_states_info.get(j).get(i);
		   
		     c.gridx = 0;
		     c.gridy = i;
		   
		     state = transition_states.get(j).get(i);
		     //threadButton[i].setText("");
		     //threadButton[i].setBackground(threadColor[state]);
		     topPanelBtns.get(j).get(i).setText("");
		     topPanelBtns.get(j).get(i).setBackground(threadColor[state]);		     
		     leftPanelBtns.get(j).get(i).setText("Transition: " + i);
		     leftPanelBtns.get(j).get(i).setBackground(threadColor[state]);
		   
		     // when you press these buttons on the top, the schedule is displayed
		     topPanelBtns.get(j).get(i).setActionCommand("" + j);
		     topPanelBtns.get(j).get(i).addActionListener(
				    new ActionListener() {
				        public void actionPerformed(ActionEvent e) {
				          int buttonVal = Integer.parseInt(e.getActionCommand());
				          remove(leftPanel, buttonVal, leftPanelBtns, transitions);
				          
				        }
				    }
				);
		     
		     // when you press these buttons on the left, transition info will be displayed at the bottom
		     leftPanelBtns.get(j).get(i).addActionListener(
					    new ActionListener() {
					        public void actionPerformed(ActionEvent e) {
					        	transitions.setText(info);
					        }
					    }
					);

		   
		     //schedPaneArray.get(j).add(threadButton[i], c);
		     
		     schedPaneArray.get(j).add(topPanelBtns.get(j).get(i), c);
		     schedPaneArrayWrap.get(j).add(schedPaneArray.get(j), BorderLayout.NORTH);
		}
		
        // defaults
		  topPanel.add(schedPaneArrayWrap.get(j));
		
	    }
        
		Dimension topScrollDim = new Dimension();
		topScrollDim.height = scrnsize.height / 3;
		topScrollDim.width = scrnsize.width;
        topPanel.setBackground(Color.white);
        JScrollPane topScroll = new JScrollPane(topPanel);	
        topScroll.setPreferredSize(topScrollDim);
		mainPanel.add(topScroll, BorderLayout.NORTH);
		
		
        JScrollPane leftScroll = new JScrollPane(leftPanel);	
        Dimension leftScrollDim = new Dimension();
        leftScrollDim.height = scrnsize.height;
        leftScrollDim.width = scrnsize.width / 10;
        leftScroll.setPreferredSize(leftScrollDim);
        
		mainPanel.add(leftScroll, BorderLayout.WEST);
        this.add(mainPanel);
        
        
        // Tabbed Stuff
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("ExampleProgramOne.java", scrollingArea);
        tabbedPane.addTab("ExampleProgramTwo.java", scrollingArea2);
        tabbedPane.addTab("ExampleProgramThree.java", scrollingArea3);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
		
        this.pack();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
	}
	
	/*
	 * - clears the left panel 
	 * - adds current set of buttons to left panel
	 */	
	public void remove(JPanel leftPanel, int j, List<List<JButton>> leftPanelBtns,  JTextArea scrollText){
		int size = leftPanelBtns.get(j).size();
		
		leftPanel.removeAll();
		scrollText.setText("");
      
		leftPanel.setLayout(new GridLayout(size, 1));
		leftPanel.setBackground(Color.white);
		
		for (int l = 0; l < size; l++){
			leftPanel.add(leftPanelBtns.get(j).get(l));
		}
		
		//this.setPreferredSize(new Dimension(this.MAXIMIZED_HORIZ, this.MAXIMIZED_VERT));
		//this.setExtendedState(MAXIMIZED_BOTH);
		this.setPreferredSize(scrnsize);
		
		this.pack();
	}
	
	
	/*
	 * This method generates pseudo random numbers using the linear congruential method
	 * m = 256
	 * a = 67
	 * c = 27
	 * x0 = 33
	 * Since there are 3 values for RBG, this method can support up to 84 threads with different colours
	 */
	public int genRandomNumbers(){
		x_new = ((a*x_prev) + c ) % m;		
		x_prev = x_new;
		return x_new;
	}

}
