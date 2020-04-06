package com.seanjohnson.textfighter.display;

import com.seanjohnson.textfighter.HistoryLinkedList;
import com.seanjohnson.textfighter.TextFighter;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.IOException;

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

	private void initComponents() {

		this.setTitle("Text Fighter");
		this.setSize(500,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//The content pane
		JPanel contentPane = new JPanel();
		contentPane.setBackground(backgroundColor);
		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		layout.setVgap(5);
		contentPane.setLayout(layout);

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
		contentPane.add(textSizeSlider, BorderLayout.EAST);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(inputArea, BorderLayout.SOUTH);

		this.setContentPane(contentPane);
		this.setVisible(true);

		inputArea.requestFocusInWindow();

	}

	/***
	 * Sets the window title to "Text Fighter - [modname]" after
	 * the mod has been loaded.
	 */
	public void updateTitle() {
		String title = "Text Fighter";
		if (TextFighter.testMode) {
			title = title + " (mod testing)";
		} if (TextFighter.getModName() != null) {
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