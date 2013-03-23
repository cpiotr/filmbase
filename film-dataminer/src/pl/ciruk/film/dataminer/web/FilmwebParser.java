package pl.ciruk.film.dataminer.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.ciruk.film.utils.core.StringHelper;
import pl.ciruk.utils.preconditions.PreconditionsHelper;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class FilmwebParser {
	private static final String SEARCH_BY_TITLE_URL_FORMAT = "http://www.filmweb.pl/search/film?q=%s";
	
	private static final Log LOG = LogFactory.getLog(FilmwebParser.class);
	
	public FilmwebDescription find(String title, List<String> actors) {
		LOG.info("find");
		LOG.debug(String.format("find - Title: %s; Actors: %s", title, actors));
		
		FilmwebDescription result = null;
		
		if (StringHelper.isNotEmpty(title)) {
			try {
				// Pobieranie strony z wynikami wyszukiwania
				String url = String.format(SEARCH_BY_TITLE_URL_FORMAT, URLEncoder.encode(title, "UTF-8"));
				Document doc = Jsoup.connect(url).get();
				
				// Kontener opisu filmu 
				Elements foundDescriptions = doc.select(".hitDescWrapper");
				foundDescLoop: for (Element description : foundDescriptions) {
					if (StringHelper.fuzzyEquals(getShortTitle(description), title)) {
						result = mapDescription(description);
						
						List<String> filmwebActors = getActors(description);
						for (String actor : filmwebActors) {
							if (StringHelper.fuzzyContains(actors, actor)) {
								result = mapDescription(description);
								break foundDescLoop;
							}
						}
					}
				}
			} catch (IOException e) {
				LOG.error("find - Error while retrieving Filmweb page", e);
			}
		} else {
			LOG.warn("parse - Title is empty");
		}
		
		return result;
	}
	
	private FilmwebDescription mapDescription(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		FilmwebDescription filmwebDescription = new FilmwebDescription();
		filmwebDescription.setTitle(getFullTitle(description));
		filmwebDescription.setScore(getScore(description));
		filmwebDescription.setUrl(getURL(description));
		return filmwebDescription;
	}
	
	private List<String> getActors(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		List<String> actors = Lists.newArrayListWithExpectedSize(description.childNodeSize());
		
		// Aktorzy przechowywani sa w postaci elementow listy nieuporzadkowanej
		for (Element filmwebActor : description.select(".filmInfo li")) {
			actors.add(filmwebActor.text());
		}
		return actors;
	}
	
	private String getShortTitle(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		String title = getFullTitle(description);
		if (StringHelper.isNotEmpty(title)) {
			String[] parts = title.split("[\\)\\(/\\\\]");
			title = parts[0];
		}
		return title;
	}
	
	private String getFullTitle(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		String title = null;
		for (Element filmwebTitle : description.select(".hdr-medium")) {
			title = filmwebTitle.text();
			break;
		}
		return title;
	}
	
	private Double getScore(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		Double score = null;
		for (Element filmwebRatio : description.select(".rateInfo strong")) {
			String textRatio = filmwebRatio.text();
			String[] ratioParsts = textRatio.split("/");
			if (ratioParsts.length == 2) {
				score = StringHelper.parseNumber(ratioParsts[0]) / StringHelper.parseNumber(ratioParsts[1]);
				break;
			} else {
				LOG.warn("getScore - Invalid ratio: " + textRatio);
			}
		}
		return score;
	}
	
	private String getURL(Element description) {
		Preconditions.checkArgument(description != null, PreconditionsHelper.CANT_BE_NULL, "FilmDescription element");
		
		String result = null;
		try {
			String domain = "http://www.filmweb.pl";
			for (Element filmwebTitle : description.select("a.hdr-medium")) {
				String href = "href";
				if (filmwebTitle.hasAttr(href)) {
					String location = filmwebTitle.attributes().get(href);
					URI uri = new URI(location);
					if (uri.isAbsolute()) {
						result = uri.toURL().toString();
					} else {
						result = new URL(domain + uri.toString()).toString();
					}
					break;
				}
			}
		} catch (MalformedURLException e) {
			LOG.warn("getURL - Invalid url address", e);
		} catch (URISyntaxException e) {
			LOG.warn("getURL - Invalid url address", e);
		}
		return result;
	}
	
	public static void main(String[] args) {
		FilmwebParser parser = new FilmwebParser();
		FilmwebDescription d = parser.find("Zaraz wracam", Lists.newArrayList(" Kim Basinger"));
		System.out.println(d.getTitle());
	}
}
