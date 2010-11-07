package se.hammar.rickard.banktool.gui;

import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import se.hammar.rickard.banktool.manager.ParserManager;

public class CategoryListerFrame extends JFrame {
	private ParserManager manager;
	private DefaultListModel expenseListModel = new DefaultListModel();
	private DefaultListModel incomeListModel = new DefaultListModel();
	//private JFrame frame;
	private JPanel panel;

	public CategoryListerFrame(ParserManager manager) {
		super("Categories");
		this.manager = manager;
	}

	public void initPanel() {
		//frame = new JFrame("Categories");
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		JLabel incomeLabel = new JLabel("Incomes");
		panel.add(incomeLabel);
		Iterator<String> incomes = manager.getIncomeCategories().iterator();
		while (incomes.hasNext()) {
			incomeListModel.addElement(incomes.next());
		}
		JList incomesList = new JList(incomeListModel);
		incomesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		incomesList.setLayoutOrientation(JList.VERTICAL);
		incomesList.setVisibleRowCount(-1);

		JScrollPane incomeListScroller = new JScrollPane(incomesList);
		incomeListScroller.setPreferredSize(new Dimension(250, 150));
		panel.add(incomeListScroller);

		JLabel expenseLabel = new JLabel("Expenses");
		panel.add(expenseLabel);
		Iterator<String> expenses = manager.getExpenseCategories().iterator();
		while (expenses.hasNext()) {
			expenseListModel.addElement(expenses.next());
		}
		JList expensesList = new JList(expenseListModel);
		expensesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		expensesList.setLayoutOrientation(JList.VERTICAL);
		expensesList.setVisibleRowCount(-1);
		JScrollPane expenseListScroller = new JScrollPane(expensesList);
		expenseListScroller.setPreferredSize(new Dimension(250, 150));
		panel.add(expenseListScroller);
		add(panel);
		pack();
		setVisible(true);
	}

	public void addIncomeCategory(String income) {
		incomeListModel.addElement(income);

	}

	public void addExpenseCategory(String expense) {
		expenseListModel.addElement(expense);
	}

	public void clearAllCategories() {
		incomeListModel.clear();
		expenseListModel.clear();
	}
}
