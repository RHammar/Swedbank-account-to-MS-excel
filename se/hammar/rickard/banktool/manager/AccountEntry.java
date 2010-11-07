package se.hammar.rickard.banktool.manager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Comparator;

import javax.swing.JComboBox;

public class AccountEntry implements Comparable<AccountEntry> {
	private String name;
	private double amount;
	private Calendar calendar;
	private String category;
	private JComboBox categoryBox;

	public AccountEntry(String name, double amount, Calendar calendar) {
		this.name = name;
		this.amount = amount;
		this.calendar = calendar;
		if (amount > 0) {
			this.category = ParserManager.DEFAULT_INCOME_CATEGORIES[0];
		} else {
			this.category = ParserManager.DEFAULT_CATEGORIES[0];
		}
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar cal) {
		this.calendar = cal;
	}

	public JComboBox getCategoryBox() {
		return categoryBox;
	}

	public void setCategoryBox(JComboBox categoryBox) {
		this.categoryBox = categoryBox;
		categoryBox.addActionListener(categoryComboBoxListener);
	}

	public String toString() {
		return this.name + " " + this.amount + " " + this.category;
	}

	public boolean isIncome() {
		return (amount > 0);
	}

	private ActionListener categoryComboBoxListener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			// get the selected category
			setCategory((String) ((JComboBox) e.getSource()).getSelectedItem());
		}

	};

	public int compareTo(AccountEntry o) {
		return getCalendar().compareTo(o.getCalendar());
	}

	static public class CompareByCategory implements Comparator<AccountEntry> {
		public int compare(AccountEntry ae1, AccountEntry ae2) {
			return ae1.getCategory().compareTo(ae2.getCategory());
		}
	}

	static public class CompareByDate implements Comparator<AccountEntry> {
		public int compare(AccountEntry ae1, AccountEntry ae2) {
			return ae1.getCalendar().compareTo(ae2.getCalendar());
		}
	}
}