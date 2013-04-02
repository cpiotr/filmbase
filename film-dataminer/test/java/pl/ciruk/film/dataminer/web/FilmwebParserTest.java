package pl.ciruk.film.dataminer.web;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import pl.ciruk.film.utils.core.StringHelper;

import com.google.common.collect.Lists;

public class FilmwebParserTest {

	private FilmwebParser parser;
	
	@Before
	public void setUp() throws Exception {
		parser = new FilmwebParser();
	}

	@Test
	public void shouldFindFilmsByTitle() {
		String title = "Lśnienie";
		List<FilmwebDescription> filmDescriptions = parser.find(title);
		
		assertNotNull(filmDescriptions);
		assertFalse(filmDescriptions.isEmpty());
		
		for (FilmwebDescription description : filmDescriptions) {
			assertNotNull(description.getTitle());
			assertTrue(description.getTitle().toLowerCase().contains(title.toLowerCase()));
		}
	}

	@Test
	public void shouldFindFilmsByTitleAndActors() {
		String title = "Omen";
		List<String> actors = Lists.newArrayList("Julia Stiles", "Liev Schreiber");
		
		List<FilmwebDescription> filmDescriptions = parser.find(title, actors);
		
		assertNotNull(filmDescriptions);
		assertFalse(filmDescriptions.isEmpty());
		assertEquals(1, filmDescriptions.size());
		
		for (FilmwebDescription description : filmDescriptions) {
			assertNotNull(description.getTitle());
			assertTrue(description.getTitle().toLowerCase().contains(title.toLowerCase()));
		}
	}
}
