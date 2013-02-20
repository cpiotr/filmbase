package pl.ciruk.film.dataminer.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

public class FilmListParser {
	private static final Logger LOG = Logger.getLogger(FilmListParser.class);
	
	/**
	 * Domyslny format pojedynczej linii zwracanego tekstu. <br/>
	 * Jako parametry podawane sa: TYTUL, PELNY_OPIS, KATEGORIA, DATA. Wszystkie dane w postaci tekstu.
	 */
	public static final String DEFAULT_OUTPUT = "%s~%s~%s~%s";
	
	/** Domyslny separator danych. */
	public static final String DEFAULT_DELIMITER = "~";
	
	public static final String[] MUSIC = { "koncert", "teledysk", "film muzyczny" };
	public static final String[] CARTOON = { "animacja", "kreskówka" };
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	private String outputFormat;
	
	public FilmListParser() {
		this(null);
	}
	
	public FilmListParser(String outputFormat) {
		this.outputFormat = outputFormat == null ? DEFAULT_OUTPUT : outputFormat;
	}
	
	public String parse(File file) throws Exception {
		String content = getContent(file);
		return processContent(content, new Date(file.lastModified()));
	}
	
	private String getContent(File file) throws Exception {
		InputStream input = new FileInputStream(file);
		ContentHandler textHandler = new BodyContentHandler(-1);
		Metadata metadata = new Metadata();
		OfficeParser parser = new OfficeParser();
		ParseContext context = new ParseContext();

		try {
			parser.parse(input, textHandler, metadata, context);
		} finally {
			if (input != null) {
				input.close();
			}
		}
		
		String content = textHandler.toString();
		return content;
	}
	
	private String processContent(String content, Date fileModificationDate) {
		String[] lines = content.split("\r?\n");
		StringBuilder buffer = new StringBuilder();
		for (String line : lines) {
			String trimmed = line.trim();
			if (!trimmed.isEmpty()) {
				if (trimmed.contains("-") || trimmed.contains("–")) {
					boolean music = isMusic(trimmed);
					boolean cartoon = isCartoon(trimmed);
					
					String[] parts = trimmed.split("[-–]");
					String title = parts[0].trim();
					String category = (music) ? "music" : (cartoon ? "cartoon" : "film");
					
					buffer.append(String.format(outputFormat, title, trimmed, category, DATE_FORMAT.format(fileModificationDate)));
					buffer.append('\n');
				}
			}
		}
		
		return buffer.toString();
	}
	
	private boolean isMusic(String line) {
		return containsAny(MUSIC, line);
	}
	
	private boolean isCartoon(String line) {
		return containsAny(CARTOON, line);
	}
	
	private boolean containsAny(String[] keys, String line) {
		for (String keyword : keys) {
			if (line.contains(keyword)) {
				return true;
			}
		}
		
		return false;
	}
}
