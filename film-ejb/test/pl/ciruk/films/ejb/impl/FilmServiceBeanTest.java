package pl.ciruk.films.ejb.impl;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import pl.ciruk.films.datatype.FilmType;
import pl.ciruk.films.ejb.api.FilmSearchCriteria;
import pl.ciruk.films.ejb.api.FilmServiceLocal;
import pl.ciruk.films.entity.BaseEntity;
import pl.ciruk.films.entity.Film;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/TEST-INF/FilmServiceBeanTest-context.xml"})
public class FilmServiceBeanTest {

	@EJB
	FilmServiceLocal service;
	
	@PersistenceContext
	EntityManager em;
	
	private FilmSearchCriteria criteria;
	
	@Before
	public void setUp() throws Exception {
		criteria = new FilmSearchCriteria();
	}

	@Test
	public void shouldReturnFilmsStartingWithLetterA() {
		Comparator<BaseEntity> cmp = new EntityComparator();
		List<Film> expected = em.createQuery("select f from Film f where f.title like 'A%'").getResultList();
		Collections.sort(expected, cmp);
		
		criteria.setTitle("a");
		List<Film> films = service.find(criteria);
		assertNotNull(films);
		
		Collections.sort(films, cmp);
		assertEquals(expected, films);
		
		for (Film film : films) {
			assertNotNull(film);
			assertNotNull(film.getTitle());
			assertTrue(film.getTitle().toUpperCase().startsWith("A"));
		}
	}
	
	@Transactional
	@Test
	public void shouldSaveSampleFilm() {
		Film f = new Film();
		f.setInsertionDate(new Date());
		f.setLabel("Testowy film");
		f.setTitle("Testowy tytuł");
		f.setType(FilmType.F);
		
		boolean saved = service.save(f);
		assertTrue(saved);
		
		Query query = em.createQuery("select f from Film f where f.title = :title and f.label = :label").setParameter("title", f.getTitle()).setParameter("label", f.getLabel());
		List<Film> films = query.getResultList();
		assertNotNull(films);
		assertFalse(films.isEmpty());
		assertEquals(f, films.get(0));
	}
	
	@Transactional
	@Test
	public void shouldNotSaveSampleFilm() {
		Film f = new Film();
		f.setInsertionDate(new Date());
		f.setLabel("Testowy film");
		f.setTitle("Testowy tytuł");
		f.setType(FilmType.F);
		
		boolean saved = service.save(f);
		assertTrue(saved);
		
		Film anotherFilm = new Film();
		try {
			BeanUtilsBean.getInstance().copyProperties(anotherFilm, f);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			fail();
		}
		anotherFilm.setId(null);
		saved = service.save(anotherFilm);
		assertFalse(saved);
	}
	
	class EntityComparator implements Comparator<BaseEntity> {

		@Override
		public int compare(BaseEntity first, BaseEntity second) {
			Long firstId = (first == null) ? -1l : first.getId();
			Long secondId = (second == null) ? -1l : second.getId();
			
			return firstId.compareTo(secondId);
		}
		
	}
}
