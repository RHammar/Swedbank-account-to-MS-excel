package se.hammar.rickard.banktool.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import se.hammar.rickard.banktool.manager.ParserManager;

public class CategoryAdder extends JFrame {
	private ParserManager manager;
	private ParserMenues menues;

	CategoryAdder(ParserManager manager, ParserMenues menues) {
		super("Add Category");
		this.manager = manager;
		this.menues = menues;
	}

	public void initPanel() {
		// final JFrame frame = new JFrame("Add Category");
		JPanel panel = new JPanel(new BorderLayout());
		JPanel namePanel = new JPanel(new FlowLayout());
		panel.add(namePanel);
		final JTextField nameTextField = new JTextField(20);
		JLabel textFieldLabel = new JLabel("Category Name");
		textFieldLabel.setLabelFor(nameTextField);
		namePanel.add(nameTextField);
		namePanel.add(textFieldLabel);

		JPanel buttonsPanel = new JPanel(new FlowLayout());
		JRadioButton incomeRbutton = new JRadioButton("Income");
		incomeRbutton.setActionCommand("Income");
		JRadioButton expensebutton = new JRadioButton("Expense");
		expensebutton.setActionCommand("Expense");
		expensebutton.setSelected(true);
		final ButtonGroup group = new ButtonGroup();
		group.add(incomeRbutton);
		group.add(expensebutton);

		final JLabel reportLabelabel = new JLabel();
		JPanel reportPane = new JPanel(new FlowLayout());
		reportLabelabel.setEnabled(false);
		reportPane.add(reportLabelabel);

		JButton createButton = new JButton("Add");
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String command = group.getSelection().getActionCommand();
				CategoryListerFrame lister = menues.getCategoryLister();
				// Check the selection
				if (command.equals("Income")) {
					manager.addIncomeCategory(nameTextField.getText());
					lister.addIncomeCategory(nameTextField.getText());
				} else if (command.equals("Expense")) {
					manager.addExpenseCategory(nameTextField.getText());
					lister.addExpenseCategory(nameTextField.getText());
				}
				manager.updateEntriesWithCategories();
				reportLabelabel.setText(nameTextField.getText() + " added!");

				pack();
				nameTextField.setText("");
			}
		});

		buttonsPanel.add(incomeRbutton);
		buttonsPanel.add(expensebutton);
		buttonsPanel.add(createButton);

		add(namePanel, BorderLayout.NORTH);
		add(buttonsPanel, BorderLayout.CENTER);
		add(reportPane, BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}
}
