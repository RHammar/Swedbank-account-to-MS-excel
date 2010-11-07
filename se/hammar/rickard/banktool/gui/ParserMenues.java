package se.hammar.rickard.banktool.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import se.hammar.rickard.banktool.manager.ParserManager;

public class ParserMenues extends JMenuBar {

	private static final long serialVersionUID = 1L;
	private ParserManager manager;
	private JMenuItem addCategoryItem;
	private JMenuItem ListCategories;
	private JMenuItem clearCategoryItem;
	private CategoryAdder categoryAdder;
	private CategoryListerFrame categoryLister;

	public ParserMenues(ParserManager manager) {
		this.manager = manager;
		categoryAdder = new CategoryAdder(manager, ParserMenues.this);
		categoryLister = new CategoryListerFrame(manager);
		// menu items
		JMenu menu;

		// Build the first menu.
		menu = new JMenu("Menu");
		menu.setMnemonic(KeyEvent.VK_A);
		this.add(menu);

		// a group of JMenuItems
		addCategoryItem = new JMenuItem("Add new Category", KeyEvent.VK_T);
		addCategoryItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		addCategoryItem.addActionListener(menueListener);
		menu.add(addCategoryItem);

		ListCategories = new JMenuItem("List Categories");
		ListCategories.setMnemonic(KeyEvent.VK_B);
		ListCategories.addActionListener(menueListener);
		menu.add(ListCategories);

		clearCategoryItem = new JMenuItem("Clear Categories");
		clearCategoryItem.setMnemonic(KeyEvent.VK_B);
		clearCategoryItem.addActionListener(menueListener);
		menu.add(clearCategoryItem);
	}

	public CategoryListerFrame getCategoryLister() {
		return categoryLister;
	}

	private ActionListener menueListener = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == addCategoryItem) {
				if (!categoryAdder.isVisible()) {
					categoryAdder.initPanel();
					categoryAdder.setAlwaysOnTop(true);
				} else {
					categoryAdder.requestFocus();
				}
			} else if (e.getSource() == ListCategories) {
				if (!categoryLister.isVisible()) {
					categoryLister.initPanel();
					categoryLister.setAlwaysOnTop(true);
				} else {
					categoryLister.requestFocus();
				}
			} else if (e.getSource() == clearCategoryItem) {
				manager.getExpenseCategories().clear();
				manager.getIncomeCategories().clear();
				manager.updateEntriesWithCategories();
				categoryLister.clearAllCategories();
			}
		}
	};
}
