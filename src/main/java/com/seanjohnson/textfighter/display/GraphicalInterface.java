package com.seanjohnson.textfighter.display;

import com.seanjohnson.textfighter.HistoryLinkedList;
import com.seanjohnson.textfighter.TextFighter;
import org.w3c.dom.Text;

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
	public JTextPane gameOutputArea;
	public HintTextField inputArea;

	public boolean canEnterInput = false;

	public JTabbedPane contentPane = new JTabbedPane();

	public JPanel gameArea = new JPanel();
	public JPanel guideArea = new JPanel();

	public JTree fileTree;
	public JTextPane fileViewer;

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
		gameOutputArea = new JTextPane();
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
		inputArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
					case(KeyEvent.VK_ENTER):
						synchronized (TextFighter.waiter) {
							if (canEnterInput) {
								TextFighter.waiter.notify();
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
					case(KeyEvent.VK_DOWN):
						if(copiedInputHistory.getCurrentIndex() > 0) {
							copiedInputHistory.set(copiedInputHistory.getCurrentIndex(), inputArea.getText());
							copiedInputHistory.setCurrentIndex(copiedInputHistory.getCurrentIndex() - 1);
							inputArea.setText(copiedInputHistory.getCurrentIndexValue());
						}
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
		fileViewer = new JTextPane();
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
			fileTree.setRootVisible(false);
			fileViewer.setText("The mod guide is not yet supported for vanilla TextFighter.");
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
				int selRow = fileTree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = fileTree.getPathForLocation(e.getX(), e.getY());
				if(selRow != -1) {
					if(e.getClickCount() == 1) {
						String path = String.join(File.pathSeparator, Arrays.toString(selPath.getPath())).replace(", ", File.separator);
						path = TextFighter.packUsed + File.separator + path.substring(1,path.length()-1);
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
	 * A recursive way to get all the files within a directory as TreeNodes for use by a JTree
	 * @param parentDirectory   The directory to find more guide files inside
	 * @return                  The DefaultMutableTreeNode representation of this directory and all its contents and sub-contents
	 */
	public DefaultMutableTreeNode getGuideFiles(File parentDirectory) {
		//We be using recursion here
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(parentDirectory.getName());
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
		String title = "Text Fighter";
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

}