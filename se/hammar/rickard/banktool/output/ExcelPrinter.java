package se.hammar.rickard.banktool.output;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import se.hammar.rickard.banktool.manager.AccountEntry;
import se.hammar.rickard.banktool.manager.ParserManager;
import se.hammar.rickard.banktool.manager.AccountEntry.CompareByDate;

public class ExcelPrinter {
	private File xlsFile;
	private ParserManager manager;
	private List<List<AccountEntry>> monthEntryHolder = new ArrayList<List<AccountEntry>>();
	/**
	 * Maps a income category to a column.
	 */
	private Map<String, Integer> incomeCategoryColumn = new HashMap<String, Integer>();
	/**
	 * Maps an expense category to a column.
	 */
	private Map<String, Integer> expenseCategoryColumn = new HashMap<String, Integer>();

	public ExcelPrinter(ParserManager manager) {
		this.manager = manager;
	}

	public void setOutputFile(File output) {
		xlsFile = output;
	}

	public void write() {
		try {
			xlsFile.createNewFile();
			monthEntryHolder.clear();
			incomeCategoryColumn.clear();
			expenseCategoryColumn.clear();
			System.out.println(xlsFile);
			List<AccountEntry> entryList = manager.getEntries();
			WritableWorkbook workbook = Workbook.createWorkbook(xlsFile);
			WritableSheet sheet = workbook.createSheet("page 1", 0);

			performSetup(sheet);
			writeCategories(sheet, 1, 1); // 1 to add space
			// for the dates column
			fillInMonths(sheet, entryList, 2, 0);
			fillInEntriesForMonths(sheet, 2, 1);
			// Comparator<AccountEntry> c = new CompareByCategory();
			// Collections.sort(entryList, c);
			// All sheets and cells added. Now write out the workbook
			workbook.write();
			workbook.close();
			// Do stuff with the strings etc
		} catch (Exception e) {
			System.out.println("caught exception " + e);
		}
	}

	private void performSetup(WritableSheet sheet) throws Exception {
		WritableFont boldFont = new WritableFont(WritableFont.ARIAL, 10,
				WritableFont.BOLD);
		WritableCellFormat bottomRightBorderCellFormat = new WritableCellFormat(
				boldFont);
		WritableCellFormat leftBorderCellFormat = new WritableCellFormat(
				boldFont);
		leftBorderCellFormat.setBorder(Border.LEFT, BorderLineStyle.MEDIUM);
		bottomRightBorderCellFormat.setBorder(Border.RIGHT,
				BorderLineStyle.MEDIUM);
		bottomRightBorderCellFormat.setBorder(Border.BOTTOM,
				BorderLineStyle.MEDIUM);
		Label label = new Label(0, 1, "Date", bottomRightBorderCellFormat);
		sheet.addCell(label);
		label = new Label(1, 0, "Incomes", leftBorderCellFormat);
		sheet.addCell(label);
		label = new Label(manager.getIncomeCategories().size() + 1, 0,
				"Expenses", leftBorderCellFormat);
		sheet.addCell(label);
		label = new Label(manager.getExpenseCategories().size()
				+ manager.getIncomeCategories().size() + 1, 0, "Summary",
				leftBorderCellFormat);
		sheet.addCell(label);
	}

	private void writeCategories(WritableSheet sheet, int categoriesRow,
			int startingColumn) throws Exception {
		List<String> incomeCategories = manager.getIncomeCategories();
		List<String> expenseCategories = manager.getExpenseCategories();
		int totalIncomeCategories = incomeCategories.size();
		int totalExpenseCategories = expenseCategories.size();
		WritableCellFormat cellFormat = new WritableCellFormat();
		cellFormat.setBorder(Border.BOTTOM, BorderLineStyle.MEDIUM);
		cellFormat.setWrap(true);
		WritableCellFormat rightBorderFormat = new WritableCellFormat();
		rightBorderFormat.setBorder(Border.RIGHT, BorderLineStyle.MEDIUM);
		rightBorderFormat.setBorder(Border.BOTTOM, BorderLineStyle.MEDIUM);
		rightBorderFormat.setWrap(true);
		WritableCellFormat leftBorderFormat = new WritableCellFormat();
		leftBorderFormat.setBorder(Border.LEFT, BorderLineStyle.MEDIUM);
		leftBorderFormat.setBorder(Border.BOTTOM, BorderLineStyle.MEDIUM);
		leftBorderFormat.setWrap(true);
		for (int i = 0; i < totalIncomeCategories; i++) {
			CellFormat c;
			if (i == totalIncomeCategories - 1) {
				// last income category
				c = rightBorderFormat;
			} else {
				c = cellFormat;
			}
			Label incomeLabel = new Label(i + startingColumn, categoriesRow,
					incomeCategories.get(i), c);
			sheet.addCell(incomeLabel);
			incomeCategoryColumn.put(incomeCategories.get(i), i
					+ startingColumn);
		}
		for (int i = 0; i < expenseCategories.size(); i++) {
			Label expenseLabel = new Label(i + totalIncomeCategories + 1,
					categoriesRow, expenseCategories.get(i));
			expenseLabel.setCellFormat(cellFormat);
			sheet.addCell(expenseLabel);
			expenseCategoryColumn.put(expenseCategories.get(i), i
					+ totalIncomeCategories + startingColumn);
		}
		// add summary headlines
		Label summaryLabel = new Label(totalIncomeCategories
				+ totalExpenseCategories + 1, categoriesRow, "Income Summary");
		summaryLabel.setCellFormat(leftBorderFormat);
		sheet.addCell(summaryLabel);
		summaryLabel = new Label(totalIncomeCategories + totalExpenseCategories
				+ 2, categoriesRow, "Expense Summary");
		summaryLabel.setCellFormat(cellFormat);
		sheet.addCell(summaryLabel);
		summaryLabel = new Label(totalIncomeCategories + totalExpenseCategories
				+ 3, categoriesRow, "Total remaining");
		summaryLabel.setCellFormat(cellFormat);
		sheet.addCell(summaryLabel);
	}

	/**
	 * Fills in the date column
	 * 
	 * @param sheet
	 *            the worksheet to use
	 * @param entries
	 *            List holding all entries
	 * @param startingRow
	 *            row to start writing in
	 * @param column
	 *            the column to use
	 * @throws Exception
	 *             if cells could not be written
	 */
	private void fillInMonths(WritableSheet sheet, List<AccountEntry> entries,
			int startingRow, int column) throws Exception {
		WritableCellFormat cellFormat = new WritableCellFormat();
		cellFormat.setBorder(Border.RIGHT, BorderLineStyle.MEDIUM);
		DateFormat df = new SimpleDateFormat("MMM-yy");
		List<Calendar> months = new ArrayList<Calendar>();
		List<Double> monthSum = new ArrayList<Double>();
		Collections.sort(entries, new CompareByDate());
		// get first month
		AccountEntry firstEntry = entries.get(0);
		Calendar cal = firstEntry.getCalendar();
		monthEntryHolder.add(new ArrayList<AccountEntry>());
		List<AccountEntry> innerList = monthEntryHolder.get(0);
		innerList.add(firstEntry);
		// write first date
		months.add(cal);
		monthSum.add(firstEntry.getAmount());
		int currentMonth = cal.get(Calendar.MONTH);
		int monthCounter = 0;
		Label dateLabel = new Label(column, startingRow + monthCounter, df
				.format(firstEntry.getCalendar().getTime()), cellFormat);
		sheet.addCell(dateLabel);
		for (int i = 1; i < entries.size(); i++) {
			AccountEntry entry = entries.get(i);
			if (entry.getCalendar().get(Calendar.MONTH) == currentMonth) {
				// this entry has same month as previous
				monthSum.set(monthCounter, entry.getAmount()
						+ monthSum.get(monthCounter));
				innerList.add(entry);

			} else { // it was another month
				monthCounter++;
				innerList = new ArrayList<AccountEntry>();
				monthEntryHolder.add(innerList);
				currentMonth = entry.getCalendar().get(Calendar.MONTH);
				innerList.add(entry);
				months.add(entry.getCalendar());
				monthSum.add(entries.get(i).getAmount());
				dateLabel = new Label(column, startingRow + monthCounter, df
						.format(entry.getCalendar().getTime()), cellFormat);
				sheet.addCell(dateLabel);
			}
		}
	}

	/**
	 * fills in entries for each category and month.
	 * 
	 * @param sheet
	 *            the sheet to write to
	 * @param firstRow
	 *            first row to write to
	 * @param startingColumn
	 *            the first column to write to
	 */
	private void fillInEntriesForMonths(WritableSheet sheet, int firstRow,
			int startingColumn) throws Exception {
		// NumberFormat currencyFormat = new NumberFormat("#.##");
		// WritableCellFormat cellFormat = new
		// WritableCellFormat(currencyFormat);
		// WritableCellFormat cellFormat = new WritableCellFormat();
		int curRow = firstRow;
		int nbrOfIncomeCategories = incomeCategoryColumn.size();
		int nbrOfExpenseCategories = expenseCategoryColumn.size();
		for (List<AccountEntry> month : monthEntryHolder) {
			double totalIncome = 0;
			double totalExpenses = 0;
			List<NumberLabel> numberLabelList = setRowToZero(curRow,
					startingColumn, nbrOfIncomeCategories
							+ nbrOfExpenseCategories + startingColumn);
			for (AccountEntry entry : month) {
				int currentColumn;
				NumberLabel nbrLabel;
				if (entry.isIncome()) {
					currentColumn = incomeCategoryColumn.get(entry
							.getCategory());
					nbrLabel = numberLabelList.get(currentColumn
							- startingColumn);
					totalIncome += entry.getAmount();
				} else {
					currentColumn = expenseCategoryColumn.get(entry
							.getCategory());
					nbrLabel = numberLabelList.get(currentColumn
							- startingColumn);
					totalExpenses += entry.getAmount();
				}
				nbrLabel.addToLabel(entry.getAmount());
				// Number n = new Number(currentColumn, curRow,
				// entry.getAmount(),
				// cellFormat);
			}
			for (NumberLabel label : numberLabelList) {
				label.addCellToSheet(sheet);
			}
			// add summary
			NumberLabel summaryLabel = new NumberLabel(nbrOfIncomeCategories
					+ nbrOfExpenseCategories + startingColumn, curRow,
					totalIncome);
			summaryLabel.addCellToSheet(sheet);
			summaryLabel = new NumberLabel(nbrOfIncomeCategories
					+ nbrOfExpenseCategories + startingColumn + 1, curRow,
					totalExpenses);
			summaryLabel.addCellToSheet(sheet);

			summaryLabel = new NumberLabel(nbrOfIncomeCategories
					+ nbrOfExpenseCategories + startingColumn + 2, curRow,
					totalIncome - Math.abs(totalExpenses));
			summaryLabel.addCellToSheet(sheet);
			curRow++;
		}
	}

	/**
	 * Sets all cells in the specified row to zero. Returns a List holding all
	 * cells in that row.
	 * 
	 * @param row
	 *            the row to set to zero
	 * @param startColumn
	 *            the first column
	 * @param endColumn
	 *            the last column
	 * @return a List holding all cells in that row
	 */
	private List<NumberLabel> setRowToZero(int row, int startColumn,
			int endColumn) throws Exception {
		int totalColumns = endColumn - startColumn;
		List<NumberLabel> labelsList = new ArrayList<NumberLabel>();
		WritableCellFormat rightBorderFormat = new WritableCellFormat(
				NumberFormats.ACCOUNTING_RED_INTEGER);
		rightBorderFormat.setBorder(Border.RIGHT, BorderLineStyle.MEDIUM);
		rightBorderFormat.setWrap(true);
		int nbrOfIncomeCats = manager.getIncomeCategories().size();
		int nbrOfExpenseCats = manager.getExpenseCategories().size();
		for (int i = 0; i < totalColumns; i++) {
			if ((i == (nbrOfIncomeCats - startColumn)) // put a line after
														// incomes
					|| (i == ((nbrOfExpenseCats + nbrOfIncomeCats) - startColumn))) {
				// put a line after expenses
				labelsList.add(new NumberLabel(i + startColumn, row, 0,
						rightBorderFormat));
			} else {
				labelsList.add(new NumberLabel(i + startColumn, row, 0));
			}
		}
		return labelsList;
	}

	private class NumberLabel {
		// private Label label;
		private Number number;
		private double sum;
		// NumberFormat n = new DecimalFormat("#.##");
		WritableCellFormat currencyFormat = new WritableCellFormat(
				NumberFormats.ACCOUNTING_RED_INTEGER);
		{
			try {
				currencyFormat.setWrap(true);
			} catch (Exception e) {
			}

		}

		public NumberLabel(int c, int r, double amount) {
			this.number = new Number(c, r, amount, currencyFormat);
			this.sum = amount;
		}

		public NumberLabel(int c, int r, double amount, CellFormat format) {
			this.number = new Number(c, r, amount, format);
			this.sum = amount;
		}

		public void addToLabel(double amount) {
			sum += amount;

			number.setValue(sum);
		}

		public void addCellToSheet(WritableSheet sheet) throws Exception {
			sheet.addCell(number);
		}

		public String toString() {
			return "sum: " + sum;
		}
	}
}