package pl.ciruk.film.utils.core;

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
}
