package se.hammar.rickard.banktool.parser;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.hammar.rickard.banktool.manager.AccountEntry;

public class AccountParser {

	private List<AccountEntry> accountEntries = new ArrayList<AccountEntry>();

	public void Parse(String input) {
		String[] lines = input.split("\n");

		for (int i = 0; i < lines.length; i++) {
			doRegExp(lines[i]);
		}
	}

	private void doRegExp(String input) {
		input = formatString(input);
		// Regexp for: date (yy-mm-dd), any char, a tab, date
		String primaryRegExp = "\\d\\d-\\d\\d-\\d\\d.*\\t\\d\\d-\\d\\d-\\d\\d";
		// name regexp
//		String namePattern = "\\t\\d\\d-\\d\\d-\\d\\d\\s*\\t([\\w\\såäöÅÄÖéÉ'-\\/\\.&]+?\\d*)\\s*\\t";
		String namePattern = "\\t\\d\\d-\\d\\d-\\d\\d\\s*\\t([\\w\\såäöÅÄÖ'-\\/\\.&]+?\\d*)\\s*\\t";
		// amount regexp optional "-" sign, at least one digit,  
		String amountPattern = namePattern + "(-?\\d+\\s?(\\d+)?,\\d\\d)";
		// transferdate regexp
		String datePattern = "\\t((\\d\\d)-(\\d\\d)-(\\d\\d))";
		Pattern p = Pattern.compile(primaryRegExp);
		Pattern p2 = Pattern.compile(namePattern);
		Pattern p3 = Pattern.compile(amountPattern);
		Pattern p4 = Pattern.compile(datePattern);
		Matcher m = p.matcher(input);
		while (m.find()) {
			// find the name
			Matcher m2 = p2.matcher(input);
			m2.find();
			System.out.println(input);
			String name = m2.group(1);
			System.out.println(name);
			// find the amount
			m2 = p3.matcher(input);
			m2.find();
			String amount = m2.group(2);
			amount = replaceCommaAndSpaceChars(amount);
			double amountDouble = Double.parseDouble(amount);
			System.out.println(amount);
			// find the transferdate
			m2 = p4.matcher(input);
			m2.find();
			int year = Integer.parseInt(m2.group(2));
			int month = Integer.parseInt(m2.group(3));
			int day = Integer.parseInt(m2.group(4));
			Calendar calendar = Calendar.getInstance();
			calendar.set(year + 2000, month - 1, day); //add 2000 to get current year (assuming decade is 2000)
			accountEntries.add(new AccountEntry(name, amountDouble, calendar));
			DateFormat df = DateFormat.getDateInstance();
			System.out.println("calendar month: " + calendar.get(Calendar.MONTH));
			System.out.println(df.format(calendar.getTime()));
		}
	}

	/**
	 * Get all entries found.
	 * 
	 * @return all entries found
	 */
	public List<AccountEntry> getEntries() {
		return accountEntries;
	}

	private String replaceCommaAndSpaceChars(String numberWithCommasAndSpaces) {
		numberWithCommasAndSpaces = numberWithCommasAndSpaces.replace(',', '.');
		return numberWithCommasAndSpaces.replaceAll(" ", "");

	}
	/**
	 * Removes Acute accent letters like é and à
	 * @param s string to format
	 * @return a formated string
	 */
	private String formatString(String s) {
		String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        return temp.replaceAll("[^\\p{ASCII}]","");
    }
}