package com.seanjohnson.textfighter.display;

import com.seanjohnson.textfighter.HistoryLinkedList;
import com.seanjohnson.textfighter.Player;
import com.seanjohnson.textfighter.TextFighter;
import com.seanjohnson.textfighter.location.Choice;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.text.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Scanner;
import java.util.NoSuchElementException;

/*

Title: Text Fighter - [modname]

TOP: scroller to change text size

MIDDLE: text area to show game content

BOTTOM: Input field

 */

public class GraphicalInterface extends JFrame {

	public HistoryLinkedList<String> copiedInputHistory = new HistoryLinkedList<>();

	private static int defaultFontSize = 10;
	private static int maxFontSize = 20;
	private static int minFontSize = 1;

	private static File fontFile = new File("DejaVuSansMono.ttf");
	private static Font displayFont;

	private static Color backgroundColor = Color.GRAY;

	//All the components
	public JSlider textSizeSlider;
	public JScrollPane scrollPane;
	public NoWrapJTextPane gameOutputArea;
	public HintTextField inputArea;

	public boolean canEnterInput = false;

	public JTabbedPane contentPane = new JTabbedPane();

	public JPanel gameArea = new JPanel();
	public JPanel guideArea = new JPanel();

	public JTree fileTree;
	public NoWrapJTextPane fileViewer;

	private void initComponents() {

		this.setTitle("Text Fighter");
		this.setSize(500,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane.add("Game", gameArea);
		contentPane.add("Guide", guideArea);
		createGameArea(); //The guide area will be created when the mod is loaded

		this.setContentPane(contentPane);
		this.setVisible(true);

		inputArea.requestFocusInWindow();

	}

	public JPanel createGameArea() {
		//The content pane
		gameArea.setBackground(backgroundColor);
		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		layout.setVgap(5);
		gameArea.setLayout(layout);

		//The text size scroller area
		textSizeSlider = new JSlider(1, minFontSize, maxFontSize, defaultFontSize);
		textSizeSlider.setBackground(backgroundColor);
		textSizeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				displayFont = displayFont.deriveFont(((float)textSizeSlider.getValue()));
				gameOutputArea.setFont(displayFont);
			}
		});
		textSizeSlider.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(10,0,5,0), //The top is greater than the bottom to compensate for the layout.VGap
				BorderFactory.createMatteBorder(0,1,0,0,Color.DARK_GRAY)
		));

		//The game output area
		gameOutputArea = new NoWrapJTextPane();
		gameOutputArea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		gameOutputArea.setBackground(backgroundColor);
		gameOutputArea.setFont(displayFont);
		gameOutputArea.setEditable(false); //We don't want the player to be able to edit stuff
		scrollPane = new JScrollPane(gameOutputArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBackground(backgroundColor);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//The input area
		inputArea = new HintTextField("Enter Your Action");
		inputArea.setFont(displayFont);
		inputArea.addFocusListener(new FocusListener() { //Input hint
			@Override
			public void focusGained(FocusEvent e) {
				if(inputArea.getText().isEmpty()) {
					inputArea.setText("");
					inputArea.hintShown = false;
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(inputArea.getText().isEmpty()) {
					inputArea.setText(inputArea.getHint());
					inputArea.hintShown = true;
				}
			}
		});
		inputArea.setFocusTraversalKeysEnabled(false); // so we can hit tab
		inputArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
					case(KeyEvent.VK_ENTER):
						if(inputArea.getText().length() < 1) { break; } //If the input area is empty, then dont do anything because you are otherwise wasting processing power
						synchronized (TextFighter.waiter) {
							if (canEnterInput) {
								TextFighter.waiter.notify(); //so that the game logic will run
							}
						}
						break;
					case(KeyEvent.VK_UP): //Scroll up through the command history
						if(copiedInputHistory.getCurrentIndex() < TextFighter.inputHistory.size() - 1) {
							copiedInputHistory.set(copiedInputHistory.getCurrentIndex(), inputArea.getText());
							copiedInputHistory.setCurrentIndex(copiedInputHistory.getCurrentIndex() + 1);
							inputArea.setText(copiedInputHistory.getCurrentIndexValue());
						}
						break;
					case(KeyEvent.VK_DOWN): //Scroll down through command history
						if(copiedInputHistory.getCurrentIndex() > 0) {
							copiedInputHistory.set(copiedInputHistory.getCurrentIndex(), inputArea.getText());
							copiedInputHistory.setCurrentIndex(copiedInputHistory.getCurrentIndex() - 1);
							inputArea.setText(copiedInputHistory.getCurrentIndexValue());
						}
						break;
					case(KeyEvent.VK_TAB): //Autocomplete commands with tab (as is done in most terminals)
						if(inputArea.getText().length() == 0 || TextFighter.player.getLocation().getPossibleChoices().size() == 0) { break; } // Dont do anything if there is no input or there are no possible choices

						//Get the new choice text
						String newText = autoComplete(inputArea.getText());
						if(newText.equals(inputArea.getText())) { break; } //Nothing has changed, so dont worry about it

						//Update the gui
						inputArea.setText(newText); //Change the input area
						inputArea.setCaretPosition(newText.length()); //Put the inputArea cursor at the end
						break;
					default:
						super.keyReleased(e);
						break;
				}
			}
		});
		inputArea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//Add all the components
		gameArea.add(textSizeSlider, BorderLayout.EAST);
		gameArea.add(scrollPane, BorderLayout.CENTER);
		gameArea.add(inputArea, BorderLayout.SOUTH);

		return gameArea;
	}

	public void createGuideArea() {
		//Called after the mod has been loaded

		guideArea.setBackground(backgroundColor);
		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		layout.setVgap(5);
		guideArea.setLayout(layout);

		UIManager.put("Tree.rendererFillBackground", false); //Ensure that the background of each tree node is not drawn

		//The file browser area
	    fileTree = new JTree(new DefaultMutableTreeNode(TextFighter.modName));

		//The file viewer area
		fileViewer = new NoWrapJTextPane();
		fileViewer.setEditable(false);
		fileViewer.setFont(displayFont);
		fileViewer.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		fileViewer.setBackground(backgroundColor);
		JScrollPane fileViewerScrollPane = new JScrollPane(fileViewer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		fileViewerScrollPane.setBackground(backgroundColor);
		fileViewerScrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		if(TextFighter.packUsed != null) {
			File guideDirectory = new File(TextFighter.packUsed.getAbsolutePath() + File.separatorChar + "guide");
			if (guideDirectory.exists()) { //Recursively get all the files as DefaultMutableTreeNodes
				fileTree = new JTree(getGuideFiles(guideDirectory));
				fileTree.setRootVisible(false);
				fileViewer.setText("Open a file on the right to get started.");
			} else {
				fileViewer.setText("Unfortunately, this mod does not have a guide.");
			}
		} else {
			if(!TextFighter.vanillaGuideDir.exists()) {
				fileTree.setRootVisible(false);
				fileViewer.setText("The vanilla mod guide was not able to be loaded.");
			} else {
				fileTree = new JTree(getGuideFiles(TextFighter.vanillaGuideDir));
				fileTree.setRootVisible(false);
				fileViewer.setText("Open a file on the right to get started.");
			}
		}

		//Created the filetree area
		fileTree.setBackground(backgroundColor);
		JScrollPane fileTreeScrollPane = new JScrollPane(fileTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		fileTreeScrollPane.setBackground(backgroundColor);
		fileTreeScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		fileTreeScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(10, 0, 10, 0),
				BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 1, 0, 0, Color.DARK_GRAY),
						BorderFactory.createEmptyBorder(5, 10, 5, 5))
		));

		//Allow the nodes to be clicked to open
		fileTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				guideArea.revalidate(); //Have it resize to automatically fit all the filenames
				File directory;
				if(TextFighter.packUsed == null) {
					directory = TextFighter.installationRoot;
				} else {
					directory = TextFighter.packUsed;
				}
				int selRow = fileTree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = fileTree.getPathForLocation(e.getX(), e.getY());
				if(selRow != -1) {
					if(e.getClickCount() == 1) {
						String path = String.join(File.pathSeparator, Arrays.toString(selPath.getPath())).replace(", ", File.separator);
						path = directory + File.separator + path.substring(1,path.length()-1);
						File guideFile = new File(path);
						if(guideFile.isFile()) { //I dont want random errors because I am trying to open a directory
							try {
								Scanner scan = new Scanner(guideFile).useDelimiter("\\Z");
								String content = scan.next();
								if (content == null) {
									fileViewer.setText("This guide file has no content.");
								} else {
									fileViewer.setText(content);
								}
							} catch (IOException | NoSuchElementException ex) {
								fileViewer.setText("An error occured while loading that guide file");
								Display.displayError("An error occured while loading that guide file");
								ex.printStackTrace();
							}
						}
					}
				}
			}
		});

		guideArea.add(fileViewerScrollPane, BorderLayout.CENTER);
		guideArea.add(fileTreeScrollPane, BorderLayout.EAST);

	}

	/**
	 * Auto-fills in the possible choice.
	 * @return          new input area string
	 * @param inputText The current input area text
	 */
	public static String autoComplete(String inputText) {
		TextFighter.player.getLocation().filterPossibleChoices();
		String[] possibleWantedCommands = new String[TextFighter.player.getLocation().getPossibleChoices().size()];
		int i = 0;
		for(Choice c : TextFighter.player.getLocation().getPossibleChoices()) {
			if(c.getName().startsWith(inputText)) {
				possibleWantedCommands[i] = c.getName();
				i++; //increase in possibleWantedCommands
			}
		}
		int l = possibleWantedCommands.length;
		if(l > 0 && possibleWantedCommands[0] == null) { return inputText; } //If there are none, then do nothing
		if((l == 1 && possibleWantedCommands[0] != null) || (l > 1 && possibleWantedCommands[1] == null)) { //If there is only one, then we are good to just use it
			return possibleWantedCommands[0] + " ";
		}
		//What if there are multiple commands that start with the current string?
		int commandIndex = inputText.length()-1;
		boolean firstCommand = true;
		boolean done = false;
		while(true) { //This will be broken out of upon `return`
			firstCommand = true;
			char character = ' ';
			for(int p=0; p<i; p++) {
				String name = possibleWantedCommands[p];
				if(name.length()-1 < commandIndex) { //No more characters, so this is what we will use
					return name.substring(0,commandIndex);
				}

				if(firstCommand) {
					character = name.charAt(commandIndex);
					firstCommand = false;
				} else if(name.charAt(commandIndex) == character) {
					continue;
				} else if(name.charAt(commandIndex) != character) { //there are different ones
					return name.substring(0,commandIndex);
				}
			}
			commandIndex++;
		}
	}


	/**
	 * A recursive way to get all the files within a directory as TreeNodes for use by a JTree
	 * @param parentDirectory   The directory to find more guide files inside
	 * @return                  The DefaultMutableTreeNode representation of this directory and all its contents and sub-contents
	 */
	public static DefaultMutableTreeNode getGuideFiles(File parentDirectory) {
		//We be using recursion here
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(parentDirectory.getName());
		if(parentDirectory.list() == null) { return root; }
		for(String f : parentDirectory.list()) {
			File file = new File(parentDirectory.getAbsolutePath() + File.separatorChar + f);
			if(file.exists() && file.isDirectory()) {
				root.add(getGuideFiles(file));
			} else if (file.exists()) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(f);
				root.add(node);
			}
		}
		return root;
	}

	/***
	 * Sets the window title to "Text Fighter - [modname]" after
	 * the mod has been loaded.
	 */
	public void updateTitle() {
		String title = "Text Fighter (" + TextFighter.version + ")";
		if (TextFighter.testMode) {
			title = title + " (mod testing)";
		} if (TextFighter.getModName() != "-") {
			title = title + " - " + TextFighter.getModName();
		}
		this.setTitle(title);
	}

	public void addOutputText(String e, Color c) {

		AttributeSet attributes = new SimpleAttributeSet(gameOutputArea.getInputAttributes());
		StyleConstants.setForeground((MutableAttributeSet)attributes, c);
		//StyleConstants.setBackground(attributes, background); In case I ever want to do backgrounds

		try {
			gameOutputArea.getStyledDocument().insertString(gameOutputArea.getDocument().getLength(), e + "\n", attributes);
		} catch (BadLocationException ignored) { }
	}

	public GraphicalInterface() {
		//Loads the menlo font

		try {
			displayFont = Font.createFont(Font.TRUETYPE_FONT,TextFighter.class.getResourceAsStream("/" + fontFile.getName()));
			displayFont = displayFont.deriveFont((float)defaultFontSize);
		} catch (IOException | FontFormatException e) {
			Display.displayError("The display font file not found or cannot be read. Defaulting to Courier New.");
			displayFont = new Font("Courier New", Font.PLAIN, defaultFontSize);
		}
		//displayFont = new Font("Menlo", Font.PLAIN, defaultFontSize);
		initComponents();
	}

	/**
	 * A JTextField with an input hint. Note that most of the functionality is not here
	 * Credit to: Bart Kiers on StackOverflow: https://stackoverflow.com/users/50476/bart-kiers
	 * Found at: https://stackoverflow.com/questions/1738966/java-jtextfield-with-input-hint
	 */
	public class HintTextField extends JTextField  {

		private String hint;
		private boolean hintShown = true;

		public String getHint() {
			return hint;
		}
		public Boolean getHintShown() {
			return hintShown;
		}

		@Override
		public String getText() {
			return hintShown ? "" : super.getText();
		}

		public HintTextField(String hint) {
			this.hint = hint;
			this.setText(hint);
		}

	}

	public class NoWrapJTextPane extends JTextPane {
		@Override
		public boolean getScrollableTracksViewportWidth() {
			// Only track viewport width when the viewport is wider than the preferred width
			return getUI().getPreferredSize(this).width
					<= getParent().getSize().width;
		};

		@Override
		public Dimension getPreferredSize() {
			// Avoid substituting the minimum width for the preferred width when the viewport is too narrow
			return getUI().getPreferredSize(this);
		};
	}

}