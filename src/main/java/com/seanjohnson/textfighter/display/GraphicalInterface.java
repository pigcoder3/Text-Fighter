package com.seanjohnson.textfighter.display;

import com.seanjohnson.textfighter.TextFighter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.text.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/*

Title: Text Fighter - [modname]

TOP: scroller to change text size

MIDDLE: text area to show game content

BOTTOM: Input field

 */

public class GraphicalInterface extends JFrame {

	private static int defaultFontSize = 10;
	private static int maxFontSize = 20;
	private static int minFontSize = 1;

	private static String fontString = "Menlo";

	private static Font displayFont = new Font(fontString, Font.PLAIN, defaultFontSize);

	private static Color backgroundColor = Color.GRAY;

	//All the components
	public JSlider textSizeSlider;
	public JScrollPane scrollPane;
	public JTextPane gameOutputArea;
	public HintTextField inputArea;
	public JButton inputConfirmButton;

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

		Action enterInput = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (TextFighter.waiter) {
					if (canEnterInput) {
						TextFighter.waiter.notify();
					}
				}
			}
		};

		//The input area
		inputArea = new HintTextField("Enter Your Action");
		inputArea.setFont(displayFont);
		inputArea.addActionListener(enterInput);
		inputArea.addFocusListener(new FocusListener() {
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
		inputArea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		//Add all the components
		contentPane.add(textSizeSlider, BorderLayout.EAST);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(inputArea, BorderLayout.SOUTH);
		//contentPane.add(inputConfirmButton, BorderLayout.SOUTH);

		this.setContentPane(contentPane);
		this.setVisible(true);

	}

	/***
	 * Sets the window title to "Text Fighter - [modname]" after
	 * the mod has been loaded.
	 */
	public void updateTitle() {
		if (TextFighter.getModName() != null) {
			this.setTitle("Text Fighter - " + TextFighter.getModName());
		}
	}

	public void addOutputText(String e, Color c) {

		AttributeSet attributes = new SimpleAttributeSet(gameOutputArea.getInputAttributes());
		StyleConstants.setForeground((MutableAttributeSet)attributes, c);
		//StyleConstants.setBackground(attributes, background); In case I ever want to do backgrounds

		try {
			gameOutputArea.getStyledDocument().insertString(gameOutputArea.getDocument().getLength(), "\n" + e, attributes);
		} catch (BadLocationException ignored) { }
	}

	public GraphicalInterface() {
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