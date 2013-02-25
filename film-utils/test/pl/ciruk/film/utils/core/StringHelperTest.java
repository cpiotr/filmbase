package pl.ciruk.film.utils.core;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class StringHelperTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIsEmptyWithEmptyStrings() {
		String[] emptyValues = {
			null, "", new String()	
		};
		
		for (String s : emptyValues) {
			assertTrue(StringHelper.isEmpty(s));
		}
	}
	
	@Test
	public void testIsEmptyWithNotEmptyStrings() {
		String[] notEmptyValues = {
			"ala", "ma", " ", "kota"
		};
		
		for (String s : notEmptyValues) {
			assertFalse(StringHelper.isEmpty(s));
		}
	}

	@Test
	public void testIsNotEmptyWithEmptyStrings() {
		String[] emptyValues = {
			null, "", new String()	
		};
		
		for (String s : emptyValues) {
			assertFalse(StringHelper.isNotEmpty(s));
		}
	}
	
	@Test
	public void testIsNotEmptyWithNotEmptyStrings() {
		String[] notEmptyValues = {
			"ala", "ma", " ", "kota"
		};
		
		for (String s : notEmptyValues) {
			assertTrue(StringHelper.isNotEmpty(s));
		}
	}
}
