package pl.ciruk.film.utils.core;

import java.util.List;

/**
 * Klasa uzytkowa zawierajaca metody przydatne podczas przetwarzania tekstu.
 * @author piotrc
 *
 */
public final class StringHelper {
	private StringHelper() {
		
	}
	
	public static boolean isEmpty(String text) {
		return text == null || text.isEmpty();
	}
	
	public static boolean isNotEmpty(String text) {
		return !isEmpty(text);
	}
	

	public static boolean containsAny(String[] keys, String line) {
		for (String keyword : keys) {
			if (line.contains(keyword)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean containsAny(List<String> keys, String line) {
		return containsAny(keys.toArray(new String[keys.size()]), line);
	}
}
