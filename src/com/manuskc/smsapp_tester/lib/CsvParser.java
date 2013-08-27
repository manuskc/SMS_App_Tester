package com.manuskc.smsapp_tester.lib;

import java.util.ArrayList;
import java.util.List;

public class CsvParser {
	public static String[]  getLines(String fileContent) {
		if(fileContent != null) {
			return fileContent.toString().split("\r(\n)?|(\n)"); //$NON-NLS-1$
		}
		return null;
	}
	
	public static String[] getColumns(String line) {
		// v1,v2,v3,v4,"v5","v6a,v6b",v7
		if(line != null) {
			String [] tempColumns =  line.split(","); //$NON-NLS-1$
			List<String> result = new ArrayList<String>();
			for(int i = 0; i < tempColumns.length; i++) {
				String column = tempColumns[i].trim();
				if(column.startsWith("\"")) { //$NON-NLS-1$
					String value = ""; //$NON-NLS-1$
					column = column.substring(1);
					while(!column.endsWith("\"") && i < (tempColumns.length-1) ) { //$NON-NLS-1$
						value += column +", "; //$NON-NLS-1$
						i+=1;
						column = tempColumns[i].trim();
					}
					value += column.substring(0,column.length() - 1);
					result.add(value);
				} else {
					result.add(column);
				}
			}
			return result.toArray(new String[]{});
		}
		return null;
	}
}
