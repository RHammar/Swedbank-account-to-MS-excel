package se.hammar.rickard.banktool.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import se.hammar.rickard.banktool.gui.SwedbankParserGui;
import se.hammar.rickard.banktool.output.ExcelPrinter;
import se.hammar.rickard.banktool.parser.AccountParser;

public class ParserManager implements Serializable {
	private static final long serialVersionUID = 1L;
	private ExcelPrinter excelPrinter;
	private List<AccountEntry> entryList;
	private List<String> expenseCategories = new ArrayList<String>();
	private List<String> incomeCategories = new ArrayList<String>();

	public static final String[] DEFAULT_CATEGORIES = { "Common account", "Housing",
			"Hobbies", "Loans", "Insurances", "Subscription", "Car",
			"Traveling", "Clothes", "Other" };

	public static String[] DEFAULT_INCOME_CATEGORIES = { "Pay", "Other", };

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ParserManager manager = new ParserManager();
		manager.setDefaultCategories();
		new SwedbankParserGui(manager).initGui();
	}

	/**
	 * Sets a list as the entries to be used.
	 * 
	 * @param entryList
	 */
	public void setEntries(List<AccountEntry> entryList) {
		this.entryList = entryList;
	}

	public List<AccountEntry> getEntries() {
		return entryList;
	}

	public void addExpenseCategory(String category) {
		expenseCategories.add(category);
	}

	public void addIncomeCategory(String category) {
		incomeCategories.add(category);
		
	}
	
	public void setExpenseCategories(List<String> categoriesList) {
		this.expenseCategories = categoriesList;
		if (entryList != null) {
			String[] categoriesArray = categoriesList.toArray(new String[0]);
			for (AccountEntry entry : entryList) {
				JComboBox box = new JComboBox(categoriesArray);
				box.setMaximumRowCount(20);
				entry.setCategoryBox(box);
			}
		}
	}

	public List<String> getExpenseCategories() {
		return expenseCategories;
	}

	public List<String> getIncomeCategories() {
		return incomeCategories;
	}

	public void setDefaultCategories() {
		expenseCategories.clear();
		incomeCategories.clear();
		for (int i = 0; i < DEFAULT_CATEGORIES.length; i++) {
			expenseCategories.add(DEFAULT_CATEGORIES[i]);
		}
		for (int i = 0; i < DEFAULT_INCOME_CATEGORIES.length; i++) {
			incomeCategories.add(DEFAULT_INCOME_CATEGORIES[i]);
		}
		updateEntriesWithCategories();
	}

	public void performParse(String text) { // do the parsing...
		AccountParser ap = new AccountParser();
		ap.Parse(text);
		if (excelPrinter == null) {
			excelPrinter = new ExcelPrinter(this);
		}
		setEntries(ap.getEntries());
	}

	public ExcelPrinter getExcelPrinter() {
		if (excelPrinter == null) {
			excelPrinter = new ExcelPrinter(this);
		}
		return excelPrinter;
	}

	public void updateEntriesWithCategories() {
		if (entryList != null) {
			String[] incomes = incomeCategories.toArray(new String[0]);
			String[] expenses = expenseCategories.toArray(new String[0]);
			for(String ex: expenses){
				System.out.println("expense:" + ex);
			}
			
			for (AccountEntry entry : entryList) {
				JComboBox box = null;
				if (entry.isIncome()) {
					box = new JComboBox(incomes);
				} else {
					box = new JComboBox(expenses);
				}
				box.setMaximumRowCount(20);
				entry.setCategoryBox(box);
			}
		}
	}
}
