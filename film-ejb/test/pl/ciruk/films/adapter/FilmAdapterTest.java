package pl.ciruk.films.adapter;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import pl.ciruk.film.dataminer.domain.FilmCategory;
import pl.ciruk.film.dataminer.domain.FilmDTO;
import pl.ciruk.films.entity.Film;

public class FilmAdapterTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFromDTO() {
		FilmDTO dto = new FilmDTO("Tytuł", "Tekst opisowy", FilmCategory.FILM, new Date());
		Film film = FilmAdapter.fromDTO(dto);
		
		assertEquals(dto.getTitle(), film.getTitle());
		assertEquals(dto.getLabel(), film.getLabel());
		assertEquals(dto.getCategory().getLabel(), film.getType().getLabel());
		assertEquals(dto.getInsertionDate(), film.getInsertionDate());
	}

}
