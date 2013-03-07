package pl.ciruk.films.datatype;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FilmTypeTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetByLabel() {
		assertNull(FilmType.getByLabel(null));
		assertEquals(FilmType.C, FilmType.getByLabel("cartoon"));
		assertEquals(FilmType.F, FilmType.getByLabel("film"));
		assertEquals(FilmType.M, FilmType.getByLabel("music"));
	}

	@Test
	public void testGetLabel() {
		assertEquals("music", FilmType.M.getLabel());
	}

}
