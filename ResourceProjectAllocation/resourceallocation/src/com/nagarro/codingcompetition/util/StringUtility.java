package com.nagarro.codingcompetition.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class StringUtility {
	private static String defaultDelimeter = ",";

	public static List<String> tokeniseString(String string) {
		List<String> tokens = tokeniseString(string, defaultDelimeter);
		return tokens;
	}
	
	public static List<String> tokeniseString(final String string, final String delimeter) {
		List<String> tokens = new ArrayList<String>();
		if (null != string && !string.isEmpty()) {
			StringTokenizer st = new StringTokenizer(string, delimeter);
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (null != token && !token.isEmpty())
					tokens.add(token.trim());
			}
		}
		return tokens;
	}
	
	public static boolean checkTrueFalse(String isKey) {
		boolean key;
		if ("Y".equalsIgnoreCase(isKey.trim())) {
			key = true;
		} else {
			key = false;
		}
		return key;
	}
}
