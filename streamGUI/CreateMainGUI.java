package gov.nasa.jpf.gVisualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class CreateMainGUI extends JFrame{
	
	// variables that store information on current JPF Run
	private static int trace_Num =0;
	private static int thread_Num=0;
	private static List<Integer> transition_Num = new ArrayList<Integer>();
	private List<List<Integer>> transition_states_color = new ArrayList<List<Integer>>();
	private List<List<Integer>> transition_states_ours = new ArrayList<List<Integer>>();
	private List<String> transition_errors = new ArrayList<String>();
	private List<List<String>> transition_states_info_ours = new ArrayList<List<String>>();
	private List<String> programFileNames_ours = new ArrayList<String>();
	
	// variables hold colors and highlighting colors for the different threads
	private Color[] threadColor;
	private Color[] genColors;
	private Highlighter.HighlightPainter[] threadHighlighter;
	
	// Panels
	private TopGUIPanel topPaint;
    private JPanel mainPanel;
    private JPanel leftPanel;
	
	/*
	 * these variables are associated with the left panel buttons and their functions
	 */
	private int tranistionInfoPos = -1; // we are going to process transition info line by line, this keep tracks of which line we are on
	private int buttonID = 0;
	private String buttonCMD = "";
	private String currentLine = "";
	private String currentLineProgram = "";
	private int currentLineProgramIndex =0;
	private int startIndex = 0;
	private int endIndex = 0;
	private int curTrace = 0;
	private boolean programLineFound = false;
    private JLabel leftPanelLabel; // Label for the left buttons	
	private List<List<JButton>> leftPanelBtns;
	private List<JLabel> leftPanelLbls;
	private int curLeft_rowPos = 0;
	private int curLeft_colPos = 0;
	private int[][] transitionInfoPos;
	private int transitionInfoPos_size;
    private boolean nextTransition = false;
    private int oldTransInfoPos = -1;
	
	// File Chooser
	private static JFileChooser fc;
	
	// Misc.
    private boolean menuHighlight = false;
    private boolean stepBackEnabled = false;
    private JMenuBar menuBar;
    private GridBagConstraints d;
    private boolean newSchedPressed = true;
      
    // Step Through Panel
    private JButton stepForward;
    private JButton stepBack;

    // dimentions  variables
    private Dimension topScrollDim;
    private Dimension leftScrollDim;
    private Dimension centerScrollDim;
	private Dimension scrnsize;
    
    //private final JTextArea transitions;
    
	// Borders
	private Border blackline;
	private Border raisedbevel;
	private Border loweredbevel;
	private Border compoundBorderRaised;
	private Border compoundBorderLowered;
	
	// Split Panes
	private JSplitPane centerEastPane;
	private JSplitPane mainCenterSplit;
       
    // tabbed panes
	private JScrollPane[] scrollingArea;
	private JTabbedPane tabbedPaneCenter;
	private JTabbedPane tabbedPaneEast;
	private JTabbedPane tabbedTopCenter;
	
	// step through pane setup
    private JPanel stepThroughPane;
    private JPanel stepThroughPaneBtns;
    private JPanel stepThroughPaneInfo;
    private JLabel stepThroughCurrentTransition;
    private JLabel stepThroughCurrentSched;
    
	private JScrollPane leftScroll;
	private int state = 0;
	
	private Vector<JPanel> schedPaneArray;
	private Vector<JPanel> schedPaneArrayWrap;
	
	// Syntax Highlighting 
	private SyntaxHighlighter transitions;
	private SyntaxHighlighter[] programInfo;
	
	// Progress Bars
	private JProgressBar progressBar;
	
	// font
	private Font font;
    
	// timer
	private long millElapsed;
	private long secElapsed;
	private Timer timer;
	private JLabel timerLbl;
	
    public CreateMainGUI(int trace_Num, int thread_Num, List<Integer> transition_Num, List<List<Integer>> transition_states, List<List<String>> transition_states_info, List<String> transition_states_error, List<String> programFileNames){	
		/*
		 * Initialize the passed in variables
		 */
		this.trace_Num = trace_Num;
		this.thread_Num = thread_Num;
		this.transition_Num = transition_Num;
		
		this.transition_states_ours = transition_states;
		this.transition_errors = transition_states_error;		
		this.programFileNames_ours.clear();
		this.programFileNames_ours = programFileNames;
		
		this.transition_states_color.clear();
		this.transition_states_color = transition_states;
		
		this.transition_states_info_ours.clear();
		this.transition_states_info_ours = transition_states_info;
		transitionInfoPos = new int[1000][2];
		
        // Get the default toolkit
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		d = new GridBagConstraints();
		
		// Borders
		blackline = BorderFactory.createLineBorder(Color.black);
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		compoundBorderRaised = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(
		                10, 5, 10, 5, Color.black), raisedbevel);

		compoundBorderLowered = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(
		                2, 2, 2, 2, Color.black), loweredbevel);
		
		// Get the Screen Size
		scrnsize = toolkit.getScreenSize();
		scrnsize.width = scrnsize.width;
		scrnsize.height = scrnsize.height - 20;
		
		
		// Defining a Color to each thread and creating a seperate highlighter for each color
        genColors = new Color[10];
        threadHighlighter = new Highlighter.HighlightPainter[10];
        this.genThreadColors();
		threadColor = new Color[thread_Num];
	    for (int i=0; i < thread_Num; i++){						
			 // assign colors
			 threadColor[i] = genColors[i];
		 }
	    
	    // font for this program
	    font = new Font("Monospaced", Font.PLAIN, 14);
			
		/*
		 * Initialize all the panels
		 */
	    
	    // Menu Bar
	    this.initMenuTab();
	    this.setJMenuBar(menuBar);
		
		// main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(5,5));
		mainPanel.setBackground(Color.WHITE);
		
		// top panel
		this.initTopFrame();
		mainPanel.add(topPaint, BorderLayout.NORTH);
		
		// left panel
		this.initLeftFrame();
		mainPanel.add(leftScroll, BorderLayout.WEST);
		
		// bottom and center panel
		//this.initCenterBottomFrame();
		//mainPanel.add(centerPaint, BorderLayout.CENTER);
		this.initBottomFrame();
		this.initCenterFrame();
		mainPanel.add(mainCenterSplit, BorderLayout.CENTER);
		
		// current window initialization
		this.add(mainPanel);
	    this.pack();
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    this.setVisible(true);
    }

    /* ---------------------------------------- Frame Initialization Methods ----------------------------------------*/   
    
    /*
     * This Method initializes the Menu for this program.
     * This includes options on setting highlights, opening other
     * output files, etc...
     */
    public void initMenuTab(){
    	
		// Create MenuBar
		menuBar = new JMenuBar();

        // Define and add two drop down menu to the menubar
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
     
        // File Menu Options
        JMenuItem exitAction = new JMenuItem("Exit");
        JMenuItem openAction = new JMenuItem("Open");
        JMenuItem highlightOffAction = new JCheckBoxMenuItem("Highlight Other Code");
          
        // open Menu Option
        openAction.addActionListener(
        		 new ActionListener() {
        			 public void actionPerformed(ActionEvent e) {	
        			//	 int returnVal = fc.showOpenDialog(temp);
        				 
        				// if (returnVal == JFileChooser.APPROVE_OPTION) {
        			//		 temp.dispose();
        			//		 File inputFile = fc.getSelectedFile();
               			//	 parseFile(inputFile.toString());
            			//	 temp = new CreateGUI();             
        				// }       				 

        			 }
        		 }
        );
        
        // exit Menu Option
        exitAction.addActionListener(
       		 new ActionListener() {
       			 public void actionPerformed(ActionEvent e) {	 				 
       				 dispose();
       			 }
       		 }
       );
        
        
        // highlight Menu Option
        highlightOffAction.addActionListener(
       		 new ActionListener() {
       			 public void actionPerformed(ActionEvent e) {	
       				topPaint.setMenuHighlight();
       			 }
       		 }
       );
        
        fileMenu.add(openAction);
        fileMenu.add(exitAction);
        editMenu.add(highlightOffAction);
    }
    
    /*
     * This Method initializes the Top Panel GUI
     */
    public void initTopFrame(){
		topScrollDim = new Dimension();
		topScrollDim.height = scrnsize.height / 3;
		topScrollDim.width = scrnsize.width;
		
		topPaint = new TopGUIPanel(trace_Num, thread_Num, transition_Num, transition_states_ours, topScrollDim);
		topPaint.setPreferredSize(topScrollDim);
		
		topPaint.addMouseListener( new MouseAdapter() {
		    public void mousePressed( MouseEvent e ) {
		    	// reset the old button highlighted color
		    	leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).setForeground(Color.BLACK);
		    	
		    	curLeft_rowPos = topPaint.getTraceNumber(e.getX());
		    	curLeft_colPos = topPaint.getTransitionNumber(curLeft_rowPos, e.getY());
		    	
		    	//find all the other occurrences of this transition
		    	findAllTransitionInfo(curLeft_rowPos, curLeft_colPos);
		    	
		    	// highlight the button pressed / all the occurrences of simalar transitions
		        topPaint.setHighlight(curLeft_colPos, curLeft_rowPos, transitionInfoPos, transitionInfoPos_size);
		        
		        
		        if(curLeft_colPos >= leftPanelBtns.get(curLeft_rowPos).size()){
		        	leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).setForeground(Color.RED);
		        }else{
		        	leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).setForeground(Color.RED);
		        }
		        
		        newSchedPressed = true;
		        remove(leftPanel, curLeft_rowPos, curLeft_colPos, leftPanelBtns);
		        
		        
		        // make sure the transition raw data is in teh transition tab
		        tranistionInfoPos = -1;
		        leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).doClick();
		        topPaint.repaint();
		        
		    }
		} );
		
    }
    
    /*
     * This Method initializes the Top Panel GUI
     */
    public void initLeftFrame(){
		leftScrollDim = new Dimension();
		leftScrollDim.height = scrnsize.height;
		leftScrollDim.width = scrnsize.width / 9;
		
		// create the panel
		leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(150, 1));
		leftPanel.setBackground(Color.white);
		                
		leftPanel.setBorder(compoundBorderRaised);
		
		// Left Panel Label
		leftPanelLabel = new JLabel("", JLabel.CENTER);
		
		leftScroll = new JScrollPane(leftPanel);
		leftScroll.setPreferredSize(leftScrollDim);
		
		
		// Left Panel Buttons and Labels
		leftPanelBtns = new ArrayList<List<JButton>>();
		leftPanelLbls = new ArrayList<JLabel>();
		schedPaneArray = new Vector();
	    schedPaneArrayWrap = new Vector();
		
		this.initLeftPanelBtns();		
    }
    
	/*
	 * This method initializes the Left Panel Buttons
	 */
	public void initLeftPanelBtns(){
		leftPanelBtns.clear();
		leftPanelLbls.clear();
		schedPaneArray.clear();
		schedPaneArrayWrap.clear();
		
		for(int j = 0; j < trace_Num; j++){
			//schedPaneArray.add(new JPanel());
			schedPaneArrayWrap.add(new JPanel());
			schedPaneArrayWrap.get(j).setBorder(blackline);

			leftPanelBtns.add(new ArrayList<JButton>());
			JLabel tempLbl = new JLabel("Schedule: " + j);
			leftPanelLbls.add(tempLbl);
			
			//schedPaneArray.get(j).setLayout(new GridBagLayout());
			schedPaneArrayWrap.get(j).setLayout(new BorderLayout(10,10));
			schedPaneArrayWrap.get(j).setBackground(Color.WHITE);
			
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			
		
		  // Adding buttons for each transition (add '+ 1' for the error button)
		  JButton[] threadButton = new JButton[transition_Num.get(j)];
		 // System.out.println(j + " --> Size: " + transition_Num.get(j));
		  for(int i = 0; i < transition_Num.get(j); i++){
			threadButton[i] = new JButton("Col: " + j + "x Row: " + i );
			leftPanelBtns.get(j).add(new JButton("Col: " + j + "x Row: " + i ));
		  }
		
		  for(int i=0; i < transition_states_ours.get(j).size(); i++){
		     final String info = transition_states_info_ours.get(j).get(i);

		     c.gridx = 0;
		     c.gridy = i;
		   
		     state = transition_states_ours.get(j).get(i);	     
		     leftPanelBtns.get(j).get(i).setText("" + i );
		     leftPanelBtns.get(j).get(i).setName("" + i);
		     leftPanelBtns.get(j).get(i).setBackground(threadColor[state]);
		     leftPanelBtns.get(j).get(i).setForeground(Color.BLACK);
		     leftPanelBtns.get(j).get(i).setFont(font);
		     curTrace = j;
		     
		     // when you press these buttons on the left, transition info will be displayed at the bottom
		     leftPanelBtns.get(j).get(i).addActionListener(
					    new ActionListener(){
					        public void actionPerformed(ActionEvent e){	

					    		// disable them
					    		 stepForward.setEnabled(true);
					    		 stepBack.setEnabled(true);
					        	
					        	 //System.out.println("\nSOURCE: " + e.getSource().toString());	
					        	 Pattern number = Pattern.compile("(.*)JButton\\[([0-9]+)(,)(.*)");
					        	 Matcher partOne = number.matcher(e.getSource().toString());
					        	 if(partOne.matches()){
					        		 final int rowValue = Integer.parseInt(leftPanelLabel.getName());
					        		 final int colValue = Integer.parseInt(partOne.group(2));

					        	    
					        	  // reset the old button highlighted color
							     // check if its last button
					             int lastBtn = leftPanelBtns.get(curLeft_rowPos).size()-1;	 
					             if(lastBtn == curLeft_colPos){
					            	 //leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).setBackground(Color.GRAY);
					            	 leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).setBorder(null);
					             }else{ 
					            	 //leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).setBackground(threadColor[transition_states_ours.get(curLeft_rowPos).get(curLeft_colPos)]);
					            	 leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).setBorder(null);
					             }
							     
							     leftPanelBtns.get(curLeft_rowPos).get(curLeft_colPos).setForeground(Color.BLACK);
							    	
							     // set colors for both top panel and left panel
					        	 topPaint.setHighlight(colValue, rowValue, null, 0);
							     leftPanelBtns.get(rowValue).get(colValue).setBorder(BorderFactory.createMatteBorder(
		                                    2, 5, 2, 5, Color.RED));
							     
							     newSchedPressed = false;
					        	 remove(leftPanel, rowValue, colValue, leftPanelBtns);
	 
					        	 // assign new current button highlighted
					        	 curLeft_rowPos = rowValue;
					        	 curLeft_colPos = colValue;
					        	 }

					        	 
					        	// indicates that a same button has been pressed
					        	if(buttonID == e.getID() && buttonCMD.equals(e.getActionCommand())){
					        		//tranistionInfoPos++; 
					        		buttonID = e.getID();
					        		buttonCMD = e.getActionCommand();
					        		//System.out.println("Same Button");
					        		
					        	}
					        	else{ // new button is pressed
					        		if(!stepBackEnabled){
					        			tranistionInfoPos = -1;
					        		}
					        		buttonID = e.getID();
					        		buttonCMD = e.getActionCommand();
					        		
					        		transitions.setText(info);
					        	}
					        					        	
					        	// try highlighting text
					        	try{
						        	  
					        		JTextArea tempJTextArea = new JTextArea();
					        	    String transTemp = transitions.getStyledDocument().getText(0, transitions.getStyledDocument().getLength());
					        	    tempJTextArea.setText(transTemp);
					        		
					        	  // check if the line is in a program
					        	  while(!programLineFound){					        		  
					        		   
					        		   tranistionInfoPos++;
					        	    	     
					        	       startIndex = tempJTextArea.getLineStartOffset(tranistionInfoPos);
							           endIndex = tempJTextArea.getLineEndOffset(tranistionInfoPos);
					        	               	       
					        	       currentLine = transitions.getText().substring(startIndex, endIndex);
  
					        	       // get the program name
					        	       for(int i = 0; i < programFileNames_ours.size(); i++){
					        		       if(currentLine.contains(programFileNames_ours.get(i)) && (!programLineFound) ){
					        		    	   programLineFound = true;
					        		    	   currentLineProgram = programFileNames_ours.get(i);
					        		    	   
					        		    	   currentLineProgramIndex = i;
					        		    	   //System.out.println("Program Name: " + currentLineProgram + " -- At index: " + currentLineProgramIndex);
					        		    	  
					        		       }
					        	       }					        	               	       

					        	  }
					        	  programLineFound = false;
					        	  transitions.getHighlighter().removeAllHighlights();
					        	  int stateColor = transition_states_color.get(curLeft_rowPos).get(curLeft_colPos);
					        	  transitions.getHighlighter().addHighlight( tempJTextArea.getLineStartOffset(tranistionInfoPos), tempJTextArea.getLineEndOffset(tranistionInfoPos), threadHighlighter[stateColor] );
					        	 
					        	  /*
					        	   * This part of the code takes care of highlighting the code in java programs
					        	   */
					        	  tabbedPaneCenter.setSelectedIndex(currentLineProgramIndex); // highlights tab
					        	  currentLine = currentLine.substring(currentLine.indexOf(": ")+2); // finds the actual line of code
					        	  currentLine = currentLine.trim();
					        	  
					        	  // need to go through the code and determine what the line number is
					        	  String temp =  programInfo[currentLineProgramIndex].getStyledDocument().getText(0, programInfo[currentLineProgramIndex].getStyledDocument().getLength());    	  
					        	  startIndex = temp.indexOf(currentLine);
		    					        	  
					        	  programInfo[currentLineProgramIndex].getHighlighter().removeAllHighlights();   	  
					        	  programInfo[currentLineProgramIndex].getHighlighter().addHighlight( startIndex, startIndex+currentLine.length(), threadHighlighter[stateColor]);
	  
					        	}
					        	catch (BadLocationException bl){
					        		System.out.println("Bad Location: " + bl.toString());
					        		tranistionInfoPos = -1;
					        		nextTransition = true;
					        	}
					        }
					    }
					);

		}
		  
		  // this code takes care of last error button
		  int lastBtn = leftPanelBtns.get(j).size()-1;
		  leftPanelBtns.get(j).get(lastBtn).setText("Error Info");
		  leftPanelBtns.get(j).get(lastBtn).setBackground(Color.GRAY);
		  leftPanelBtns.get(j).get(lastBtn).setForeground(Color.BLACK);
		  
		  leftPanelBtns.get(j).get(lastBtn).addActionListener(
				  new ActionListener() {
					  public void actionPerformed(ActionEvent e) {
						  transitions.setText(transition_errors.get(curTrace));
					  }
				  }
			);
		
	    }
		
	}
 
    
    /*
     * This Method initializes the Bottom Panel GUI
     */
    public void initBottomFrame(){
	
		// Step Through Panel Set Up
		stepThroughPaneBtns = new JPanel();
		stepThroughPaneBtns.setLayout(new GridBagLayout());
		stepThroughPaneBtns.setBackground(Color.WHITE);
		
		stepThroughPaneInfo = new JPanel();
		stepThroughPaneInfo.setLayout(new GridBagLayout());
		stepThroughPaneInfo.setBackground(Color.WHITE);
		
		stepThroughPane = new JPanel();	
		stepThroughPane.setLayout(new BorderLayout());
		stepThroughPane.setBackground(Color.WHITE);
		
		// add Step Back Button
		d.fill = GridBagConstraints.WEST;
		stepBack = new JButton(" Back   ");
		
		d.gridx = 0;
		d.gridy = 3;
		stepThroughPaneBtns.add(stepBack, d);
		
		// space filler
		JLabel fillerOne = new JLabel("             ");
		d.gridx = 6;
		d.gridy = 3;
		stepThroughPaneBtns.add(fillerOne, d);
		
		// add Step Back Button
		d.fill = GridBagConstraints.EAST;
		stepForward = new JButton("Forward");

		d.gridx = 12;
		d.gridy = 3;
		stepThroughPaneBtns.add(stepForward, d);	
		
		// intialize the action listeners for these two buttons
		this.initStepThroughBtns();
		
		// disable them to start off
		stepForward.setEnabled(false);
		stepBack.setEnabled(false);
		
		TitledBorder stepThroughBtnLbl = new TitledBorder(
                new LineBorder(Color.black),
                "Step Through Code",
                TitledBorder.CENTER,
                TitledBorder.CENTER);
		stepThroughBtnLbl.setTitleColor(Color.black);
        
		Font curFontA = stepThroughBtnLbl.getTitleFont();
		stepThroughBtnLbl.setTitleFont(new Font(curFontA.getFontName(), curFontA.getStyle(), 14));
        
		stepThroughPaneBtns.setBorder(stepThroughBtnLbl);
		
		// Creating the Information tab
		d.gridx = 0;
		d.gridy = 0;
		stepThroughCurrentSched = new JLabel("Schedule: N/A");
		stepThroughCurrentSched.setFont(font);
		stepThroughPaneInfo.add(stepThroughCurrentSched, d);
		
		// add filler
		JLabel fillerTwo = new JLabel("             ");
		d.gridx = 6;
		d.gridy = 0;
		stepThroughPaneInfo.add(fillerTwo, d);
		
		d.gridx = 12;
		d.gridy = 0;
		stepThroughCurrentTransition = new JLabel("Transition: N/A");
		stepThroughCurrentTransition.setFont(font);
		stepThroughPaneInfo.add(stepThroughCurrentTransition, d);
		
        // Progress Bar    
        JLabel fillerThree = new JLabel("              ");
		d.gridx = 18;
		d.gridy = 0;
		stepThroughPaneInfo.add(fillerThree, d);
		
        // Progress Bar    
        JLabel progressLabel = new JLabel("Progress: ");
		d.gridx = 24;
		d.gridy = 0;
		progressLabel.setFont(font);
		stepThroughPaneInfo.add(progressLabel, d);
		
        progressBar = new JProgressBar(0, 1000);
        progressBar.setStringPainted(false);
        progressBar.setForeground(Color.red);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);
        progressBar.setFont(font);

		d.gridx = 30;
		d.gridy = 0;
		stepThroughPaneInfo.add(progressBar, d);
        
		
		// timer
		timerLbl = new JLabel("Elapsed Time: " + secElapsed + "." + millElapsed + " seconds");
		timerLbl.setFont(font);
		millElapsed = 0;
		secElapsed = 0;
		
	    timer = new Timer(100, new ActionListener() {
	          public void actionPerformed(ActionEvent e) {
	        	  
	        	// long now = System.currentTimeMillis(); // current time in ms
	        	// timeElapsed = now - lastUpdate; // ms elapsed since last update
	        	 millElapsed++;
	        	 if(millElapsed == 10){
	        		 secElapsed++;
	        		 millElapsed = 0;
	        	 }
	             timerLbl.setText("Elapsed Time: " + secElapsed + "." + millElapsed + " seconds");
	             //lastUpdate = now;
	          }
	       });
		
		timer.start();
		
        JLabel fillerFour = new JLabel("              ");
		d.gridx = 36;
		d.gridy = 0;
		stepThroughPaneInfo.add(fillerFour, d);
		
		d.gridx = 42;
		d.gridy = 0;
		stepThroughPaneInfo.add(timerLbl, d);
		
		TitledBorder stepThroughInfoLbl = new TitledBorder(
                new LineBorder(Color.black),
                "Schedule Information",
                TitledBorder.CENTER,
                TitledBorder.CENTER);
		stepThroughInfoLbl.setTitleColor(Color.black);
        
		Font curFontB = stepThroughInfoLbl.getTitleFont();
		stepThroughInfoLbl.setTitleFont(new Font(curFontB.getFontName(), curFontB.getStyle(), 14));
        
		stepThroughPaneInfo.setBorder(stepThroughInfoLbl);	
		
		// add it to the main step through panel
		stepThroughPane.add(stepThroughPaneBtns, BorderLayout.WEST);
		stepThroughPane.add(stepThroughPaneInfo, BorderLayout.CENTER);
			
		// set the title
		TitledBorder stepThroughMainLbl = new TitledBorder(
                new LineBorder(Color.black),
                "",
                TitledBorder.CENTER,
                TitledBorder.BELOW_TOP);
		stepThroughMainLbl.setTitleColor(Color.black);
        
		Font curFont = stepThroughMainLbl.getTitleFont();
		stepThroughMainLbl.setTitleFont(new Font(curFont.getFontName(), curFont.getStyle(), 18));
        
        stepThroughPane.setBorder(stepThroughMainLbl);
        
    }
    
    
    /*
     * This Method initializes Center Panel GUI
     */
    public void initCenterFrame(){ 	   
  	   
  	  Scanner scannerTrans = new JavaScanner();
      transitions = new SyntaxHighlighter(20, 100, scannerTrans);
      transitions.setFont(font);
  	   
  	   // text area for each program file (we also upload all information to these areas)
       programInfo = new SyntaxHighlighter[programFileNames_ours.size()];
       String programName = "";
       String line = ""; 
       int lineCount = 0;
       String tempFile = "";
       String spaceFill = "";
       
       for (int i = 0; i < programInfo.length; i++){
      	lineCount = 0;
      	tempFile = "";
      	Scanner scannerProg = new JavaScanner();
       	programInfo[i] = new SyntaxHighlighter(24, 80, scannerProg);
       	programInfo[i].setFont(font);
       	programName = programFileNames_ours.get(i);

       	try{
       	   BufferedReader reader = new BufferedReader(new FileReader("src/examples/" + programName));
       	    line = reader.readLine();
       	    while(line != null){
       	    	lineCount++;
       	    	
       	    	// Just to keep the numbering spacing consistent
       	    	if(lineCount < 10){
       	    		spaceFill = "00";
       	    	}
       	    	else if(lineCount < 100){
       	    		spaceFill = "0";
       	    	}
       	    	else{
       	    		spaceFill = "";
       	    	}
       	    	
       	    	tempFile += spaceFill + lineCount + ":\t" + line + "\n";
       	    	line = reader.readLine();
       	    }  
       	    
       	    programInfo[i].setText(tempFile);
       	}
       	catch(IOException e){
       		System.out.println("IOException: " + e.toString());
       	}
       	catch(NullPointerException n){
       		System.out.println("Null Exception: " + n.toString());
       		
       	}
       	
       	//programInfo[i].setFocusable(false);
       	programInfo[i].setCaretPosition(1);
       	programInfo[i].setCaretPosition(0);
       	
       }
	 
    	scrollingArea = new JScrollPane[programFileNames_ours.size() + 1];
        for (int i = 0; i < programInfo.length; i++){
        	scrollingArea[i] = new JScrollPane(programInfo[i]);
        	scrollingArea[i].setBorder(blackline);
        }
        
        // transitions info part
        scrollingArea[scrollingArea.length - 1] = new JScrollPane(transitions);
    	scrollingArea[scrollingArea.length - 1].setBorder(blackline);       
   
        // Tabbed Stuff ( adding program file names to tabs)
    	tabbedPaneCenter = new JTabbedPane();
    	tabbedPaneEast = new JTabbedPane();
        
        for (int i = 0; i < programFileNames_ours.size(); i++){
        	tabbedPaneCenter.addTab(programFileNames_ours.get(i), scrollingArea[i]);
        }
        
        tabbedPaneEast.addTab("Transition Info", scrollingArea[scrollingArea.length - 1]);
        Dimension eastTabDim = new Dimension();
        eastTabDim.height = scrnsize.height;
        eastTabDim.width = scrnsize.width / 10;    
        tabbedPaneEast.setPreferredSize(eastTabDim);
        
        centerEastPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPaneCenter, tabbedPaneEast);
        centerEastPane.setOneTouchExpandable(true);
        centerEastPane.setDividerLocation(scrnsize.width / 2);

        mainCenterSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centerEastPane, stepThroughPane);
        mainCenterSplit.setFocusable(false);
        mainCenterSplit.setOneTouchExpandable(true);
        mainCenterSplit.setDividerLocation(scrnsize.height / 2);
        mainCenterSplit.setEnabled(false);
        
    }
    
    /*
     * This method adss Action Listeners to Step Back 
     * and Step Forward Buttons 
     */
    public void initStepThroughBtns(){
         
        // step forward algorithm
		stepForward.addActionListener(
			    new ActionListener() {
			        public void actionPerformed(ActionEvent e) {
			        	int sched = Integer.parseInt(stepThroughCurrentSched.getName());
			        	int trans =Integer.parseInt(stepThroughCurrentTransition.getName());
			        	stepBackEnabled = false;
			        	
			        	// we subtract 2 so error info button will not be selected
			        	if(trans < (leftPanelBtns.get(sched).size() - 2)){
			        		leftPanelBtns.get(sched).get(trans).doClick();
			        		if(nextTransition){
			        			trans++;
			        			leftPanelBtns.get(sched).get(trans).doClick();
			        			nextTransition = false;
			        		}
			        	}
			        	
			        }
			    }
		);
		
		
		// step back algorithm
		stepBack.addActionListener(
			    new ActionListener() {
			        public void actionPerformed(ActionEvent e) {
			        	int sched = Integer.parseInt(stepThroughCurrentSched.getName());
			        	int trans =Integer.parseInt(stepThroughCurrentTransition.getName());
			        	
			        	stepBackEnabled = false;
			        	
			        	// we subtract 2 so error info button will not be selected
			        	if(trans < (leftPanelBtns.get(sched).size() - 2) && trans >= 0){
			        		
			        		// if we are on the first line of the transition we need to step back
			        		// to the previous transition's last line
			        		if(tranistionInfoPos == 0){
			        			
			        			if(trans != 0){
			        				trans--;
			        				String prevTransInfo = transition_states_info_ours.get(sched).get(trans);
			        			    String[] lines = prevTransInfo.split("[\n\r]");
			        			    if(lines.length <= 1){
			        			    	tranistionInfoPos = -1;
			        			    }else{
			        			    	tranistionInfoPos = lines.length - 2;
			        			    }
			        			    stepBackEnabled = true;
			        			    leftPanelBtns.get(sched).get(trans).doClick();
			        			}
			        			
			        		}else{
				        		tranistionInfoPos -= 2;
				        		leftPanelBtns.get(sched).get(trans).doClick();	
			        		}
	
			        	}
			        }
			    }
			);
    }
    
    
    
    /* ---------------------------------------- Frame Redraw Methods ----------------------------------------*/
    
    /*
     * This Method calls other Methods to redraw all of the Frames
     * in this GUI
     */
    public void reDrawAllFrames(int trace_Num, List<Integer> transition_Num, List<List<Integer>> transition_states, List<List<String>> transition_states_info, List<String> transition_states_error){
		/*
		 * Initialize the passed in variables
		 */
		this.trace_Num = trace_Num;
		this.thread_Num = thread_Num;
		this.transition_Num = transition_Num;
		this.transition_states_ours = transition_states;
		this.transition_errors = transition_states_error;		
		this.transition_states_color = transition_states;
		this.transition_states_info_ours = transition_states_info;
		//transitionInfoPos = new int[this.trace_Num][2];
    	
		// redraw top frame
    	this.reDrawTopFrame();
    	
    	// initialize the left Panel Buttons again
    	this.initLeftPanelBtns();
    }
    
    
    /*
     * This method redraws the Top Frame whenever
     * a new schedule is done
     */
    public void reDrawTopFrame(){
    	topPaint.setValues(trace_Num, transition_Num, transition_states_ours);
    	topPaint.repaint();
    }
    
    
    /* ---------------------------------------- Main GUI Functionality ----------------------------------------*/
    
    /*
	 * - clears the left panel 
	 * - adds current set of buttons to left panel
	 */	
	public void remove(JPanel leftPanel, int j, int k, List<List<JButton>> leftPanelBtns){
		int size = leftPanelBtns.get(j).size();
		
		topPaint.repaint();		

	    stepThroughCurrentSched.setText("Schedule: " + (j + 1));
	    stepThroughCurrentSched.setName("" + j);
		
	    //System.out.println("Size: " + size + " --> J: " + k);
	    if((size - 1) == k){
	    	stepThroughCurrentTransition.setText("Transition: Error Info");
	    	stepThroughCurrentTransition.setName("-1");
	    }else{
	    	stepThroughCurrentTransition.setText("Transition: " + k);
	    	stepThroughCurrentTransition.setName("" + k);
	    }
		
	    if(newSchedPressed){
	    	
			leftPanel.removeAll();
	    	leftPanel.setLayout(new GridLayout(size + 1, 1));
			leftPanel.setBackground(Color.white);
			//leftPanel.add(leftPanelLbls.get(j));
		
			leftPanelLabel.setText("Schedule: " + (j+1));
			leftPanelLabel.setName(""+ j);

			leftPanel.add(leftPanelLabel);

			for (int l = 0; l < size; l++){
				leftPanel.add(leftPanelBtns.get(j).get(l));
			}
		
			//this.setPreferredSize(new Dimension(this.MAXIMIZED_HORIZ, this.MAXIMIZED_VERT));
			//this.setExtendedState(MAXIMIZED_BOTH);
			this.setPreferredSize(scrnsize);
			leftPanel.repaint();
		
			this.pack();
	    }
		
	}
	
	
	/*
	 * this Method, given the position the transition info, finds out the positions
	 * of all other similar transition info's
	 */
	public void findAllTransitionInfo(int row, int col){
		transitionInfoPos_size = -1;
		String matchInfo = transition_states_info_ours.get(row).get(col);
		
		for(int i = 0; i < transition_states_info_ours.size(); i++){
			for(int j = 0; j < transition_states_info_ours.get(i).size(); j++){				
				if( matchInfo.equals(transition_states_info_ours.get(i).get(j))){
					transitionInfoPos_size++;	
					transitionInfoPos[transitionInfoPos_size][0] = i;
					transitionInfoPos[transitionInfoPos_size][1] = j;	
				}
			}
			
		}
		
		transitionInfoPos_size++;
		
	}
    
	
	/*
	 * This method generates colors for up to 10 different threads (RGB values)
	 * - We are not using red because it is the high lighter color
	 */
	public void genThreadColors(){
		
		genColors[0] = new Color(58,98,132); // blue
		threadHighlighter[0] = new DefaultHighlighter.DefaultHighlightPainter( genColors[0].brighter());
		
		genColors[1] = new Color(51,157,70); // green
		threadHighlighter[1] = new DefaultHighlighter.DefaultHighlightPainter( genColors[1].brighter());
		
		genColors[2] = new Color(255,127,50); // orange
		threadHighlighter[2] = new DefaultHighlighter.DefaultHighlightPainter( genColors[2].brighter());
		
		genColors[3] = new Color(145,105,171); // purple
		threadHighlighter[3] = new DefaultHighlighter.DefaultHighlightPainter( genColors[3].brighter());
		
		genColors[4] = new Color(136,89,79); // brown
		threadHighlighter[4] = new DefaultHighlighter.DefaultHighlightPainter( genColors[4].brighter());
		
		genColors[5] = new Color(226,123,182); // pink
		threadHighlighter[5] = new DefaultHighlighter.DefaultHighlightPainter( genColors[5].brighter() );
		
		genColors[6] = new Color(88,88,88); // grey
		threadHighlighter[6] = new DefaultHighlighter.DefaultHighlightPainter( genColors[6].brighter() );
		
		genColors[7] = new Color(84,143,174); // teal
		threadHighlighter[7] = new DefaultHighlighter.DefaultHighlightPainter( genColors[7].brighter() );
		
		genColors[8] = new Color(191,186,57); // gold
		threadHighlighter[8] = new DefaultHighlighter.DefaultHighlightPainter( genColors[8].brighter() );
		
		genColors[9] = new Color(1,71,189); // royal blue
		threadHighlighter[9] = new DefaultHighlighter.DefaultHighlightPainter( genColors[9] );
	}
	
	/*
	 * This method just turns off the progress bar when JPF is finished
	 * - also turns off the timer
	 */
	public void setProgressOff(){
		progressBar.setIndeterminate(false);
		progressBar.setStringPainted(true);
		progressBar.setValue(1000);
		timer.stop();
	}

}

