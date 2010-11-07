package se.hammar.rickard.banktool.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import se.hammar.rickard.banktool.manager.AccountEntry;
import se.hammar.rickard.banktool.manager.ParserManager;

public class SwedbankParserGui extends JPanel {

	private static final long serialVersionUID = 1L;
	private ParserManager manager;
	private JTextArea textArea;
	// Create a file chooser
	private final JFileChooser fc = new JFileChooser();
	// text field for the file chooser
	private JTextField fileTextField;
	private JFrame frame;
	private JPanel inputPane;
	private JPanel buttonsPane;
	private JPanel textControlsPane;
	private JButton writeButton;

	public SwedbankParserGui(ParserManager manager) {
		this.manager = manager;
		setLayout(new BorderLayout());

		// Create a text area.
		textArea = new JTextArea("Paste your account page into this field.");
		textArea.setFont(new Font("Serif", Font.ITALIC, 16));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.addMouseListener(textAreaMouselistener);
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(250, 250));
		areaScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(BorderFactory
						.createTitledBorder("Input window"), BorderFactory
						.createEmptyBorder(5, 5, 5, 5)), areaScrollPane
						.getBorder()));

		// create a parse button
		buttonsPane = new JPanel(new FlowLayout());
		JButton parseButton = new JButton("Parse");
		parseButton.setToolTipText("Perform parsing operation");
		parseButton.addActionListener(parseButtonListener);
		buttonsPane.add(parseButton);

		// create a write button
		writeButton = new JButton("write");
		writeButton.setToolTipText("Write result to file");
		writeButton.setEnabled(false);
		writeButton.addActionListener(writeButtonListener);
		buttonsPane.add(writeButton);

		// create a save file area
		final String defaultFileName = "out.xls";
		// get current working dir
		String defaultDir = System.getProperty("user.dir");
		//String defaultDir = new File(".").getAbsolutePath();
		String defaultPath = defaultDir +"\\"+ defaultFileName;
		File defaultFile = new File(defaultPath);
		JPanel fileSelectPane = new JPanel(new FlowLayout());
		fileTextField = new JTextField();
		fileTextField.addKeyListener(keyAdapter);

		fileTextField.setPreferredSize(new Dimension(400, 30));
		JButton selectFileButton = new JButton("Save");
		selectFileButton.setToolTipText("Select output file");
		fileSelectPane.add(fileTextField);
		fileSelectPane.add(selectFileButton);
		selectFileButton.addActionListener(openFileButtonListener);
		fc.setSelectedFile(defaultFile);
		manager.getExcelPrinter().setOutputFile(defaultFile);
		fileTextField.setText(defaultPath);
		fc.addPropertyChangeListener(JFileChooser.DIRECTORY_CHANGED_PROPERTY,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						String path = fc.getCurrentDirectory()
								.getAbsolutePath()
								+ "\\" + defaultFileName;
						fc.setSelectedFile(new File(path));
						fileTextField.setText(path);
						fc.updateUI();
						fileTextField.updateUI();
					}
				});

		// Put everything together.
		inputPane = new JPanel(new BorderLayout());
		inputPane.add(areaScrollPane, BorderLayout.WEST);
		inputPane.add(buttonsPane, BorderLayout.SOUTH);
		inputPane.add(fileSelectPane, BorderLayout.NORTH);
		// inputPane.add(textField);

		// create textfield
		// Lay out the text controls and the labels.
		textControlsPane = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();

		textControlsPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Parse result"), BorderFactory
						.createEmptyBorder(5, 5, 5, 5)));
		textControlsPane.setLayout(gridbag);
		// textControlsPane.setPreferredSize(new Dimension(250, 1000));
		JScrollPane textControlsScrollPane = new JScrollPane(textControlsPane);
		textControlsScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textControlsScrollPane.setPreferredSize(new Dimension(540, 500));
		textControlsScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(BorderFactory
						.createTitledBorder("Input window"), BorderFactory
						.createEmptyBorder(5, 5, 5, 5)), textControlsScrollPane
						.getBorder()));
		inputPane.add(textControlsScrollPane);
		add(inputPane);
	}

	public void actionPerformed(ActionEvent e) {

	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	private void createAndShowGUI(SwedbankParserGui gui) {

		// Create and set up the window.
		frame = new JFrame("SwedBank account parser");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		frame.add(gui);

		// Display the window.
		frame.pack();
		frame.setVisible(true);

		// Create the menu bar.
		frame.setJMenuBar(new ParserMenues(manager));
	}

	private void addLabelTextRows(List<AccountEntry> entries,
			Container container) {
		GridLayout2 g = new GridLayout2(0, 4);
		g.setVgap(1);
		container.setLayout(g);
		Dimension fieldDim = new Dimension(150, 25);
		Dimension mediumDim = new Dimension(125, 25);
		Dimension smallDim = new Dimension(100, 25);
		for (AccountEntry account : entries) {
			// create fied for date
			DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
			JTextField dateField = new JTextField(df.format(account
					.getCalendar().getTime()));
			dateField.setPreferredSize(smallDim);
			container.add(dateField);
			// create textfield
			JTextField textField = new JTextField(account.getName());
			textField.setPreferredSize(fieldDim);
			container.add(textField);
			// create combobox
			JComboBox dropDown = account.getCategoryBox();
			dropDown.setPreferredSize(mediumDim);

			JTextField amountField = new JTextField("" + account.getAmount()
					+ " kr");
			amountField.setEditable(false);
			amountField.setPreferredSize(smallDim);
			if (account.isIncome()) {
				amountField.setBackground(Color.GREEN);
			} else {
				amountField.setBackground(new Color(255, 50, 50)); // light red
			}
			container.add(amountField);
			container.add(dropDown);
		}

		// GridLayout g = new GridLayout(0, 3);
		// int numLabels = textFields.length;
		// container.setLayout(g);
		// for (i = 0; i < numLabels; i++) {
		// container.add(textFields[i]);
		// JTextField amountField = new JTextField("" + amounts[i] + " kr");
		// amountField.setEditable(false);
		// amountField.setBackground(Color.green);
		// container.add(amountField);
		// container.add(dropDowns[i]);
		// }
	}

	public void initGui() {
		// Schedule a job for the event dispatching thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI(SwedbankParserGui.this);
			}
		});
	}

	ActionListener parseButtonListener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			manager.performParse(textArea.getText());
			List<AccountEntry> entries = manager.getEntries();
			manager.updateEntriesWithCategories();
			textControlsPane.removeAll();
			addLabelTextRows(entries, textControlsPane);
			writeButton.setEnabled(true);
			textControlsPane.updateUI();
			// GridBagConstraints c = new GridBagConstraints();
			// c.gridwidth = GridBagConstraints.REMAINDER; // last
			// c.anchor = GridBagConstraints.WEST;
			// c.gridx = 1;
			// textControlsPane.add(writeButton, BorderLayout.SOUTH);
		}
	};

	ActionListener writeButtonListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			manager.getExcelPrinter().write();

		}

	};

	ActionListener openFileButtonListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// Handle open button action.
			int returnVal = fc.showSaveDialog(inputPane);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File outputFile = fc.getSelectedFile();
				fileTextField.setText(outputFile.getAbsolutePath());
				manager.getExcelPrinter().setOutputFile(outputFile);
			} else {
				// canceled by user
			}
		}
	};

	// set the file when enter is pressed
	KeyAdapter keyAdapter = new KeyAdapter() {
		public void keyPressed(KeyEvent evt) {
			int iKey = evt.getKeyCode();
			if (iKey == KeyEvent.VK_ENTER) {
				System.out.println("enter pressed");
				manager.getExcelPrinter().setOutputFile(new File(fileTextField.getText()));
			}
		}
	};

	MouseListener textAreaMouselistener = new MouseListener() {

		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
				textArea.selectAll();
			}

		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

	};
}
