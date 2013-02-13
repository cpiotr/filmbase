package pl.ciruk.films.ejb.impl;

import java.io.File;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import pl.ciruk.film.dataminer.file.FilmListParser;
import pl.ciruk.film.utils.core.StringHelper;
import pl.ciruk.films.datatype.FilmType;
import pl.ciruk.films.ejb.api.FilmSearchCriteria;
import pl.ciruk.films.ejb.api.FilmServiceLocal;
import pl.ciruk.films.entity.Film;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@Stateless
public class FilmServiceBean implements FilmServiceLocal {
	private static final Logger LOG = Logger.getLogger(FilmServiceBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Film> find(FilmSearchCriteria criteria) {
		StringBuilder queryBuilder = new StringBuilder();
		List<Object> queryParams = Lists.newArrayList();
		queryBuilder.append("select f from ").append(Film.class.getSimpleName()).append(" f ");
		if (criteria != null && !criteria.isEmpty()) {
			queryBuilder.append("where ");
			if (StringHelper.isNotEmpty(criteria.getTitle())) {
				queryBuilder.append("f.title like ?" + (queryParams.size() + 1) + " ");
				queryParams.add(criteria.getTitle() + "%");
			}
			if (!criteria.getTypes().isEmpty()) {
				queryBuilder.append("f.type in ?" + (queryParams.size() + 1) + " ");
				queryParams.add(criteria.getTypes());
			}
		}
		queryBuilder.append("order by f.title ");
		Query query = em.createQuery(queryBuilder.toString());
		for (int i = 0; i < queryParams.size(); i++) {
			query.setParameter(i+1, queryParams.get(i));
		}
		
		System.out.println(queryBuilder.toString());
		
		return query.getResultList();
	}

	@Override
	public boolean save(Film film) {
		LOG.info("save");
		LOG.info("save - Film: " + film);
		
		Preconditions.checkArgument(film != null, "Film cannot be null");
		
		if (!exists(film)) {
			if (film.getId() != null) {
				em.merge(film);
			} else {
				em.persist(film);
			}
		}
		
		return film.getId() != null;
	}
	
	private boolean exists(Film film) {
		LOG.info("exists");
		
		if (film == null) {
			throw new IllegalArgumentException("Film cannot be null");
		}
		
		boolean result = false;
		
		if (film.getId() != null) {
			result = true;
		} else {
			Query query = em.createNamedQuery(Film.QUERY_GET_FILM).setParameter("title", film.getTitle()).setParameter("label", film.getLabel());
			List<Film> films = query.getResultList();
			result = !films.isEmpty();
		}
		
		return result;
	}

	@Override
	public boolean remove(Film film) {
		if (film == null) {
			throw new IllegalArgumentException("Film cannot be null");
		}
		
		if (film.getId() != null) {
			em.remove(film);
		}
		
		return em.find(Film.class, film.getId()) == null;
	}

	@Override
	public void updateWithListFile(File filmListFile) {
		if (filmListFile == null || !filmListFile.isFile()) {
			//TODO: Dodac logowanie
		}
		
		FilmListParser parser = new FilmListParser();
		try {
			String content = parser.parse(filmListFile);
			
			for (Film film : mapToFilms(content)) {
				save(film);
			}
		} catch (Exception e) {
			
		}
	}

	private List<Film> mapToFilms(String csvContent) throws Exception {
		List<Film> films = Lists.newArrayList();
    	
		String[] lines = csvContent.split("\r?\n");
		for (String line : lines) {
			
			
			String[] parts = line.split("~");
			FilmType type = FilmType.getByLabel(parts[2]);
			
			Film film = new Film();
			film.setTitle(parts[0]);
			film.setLabel(parts[1]);
			film.setType(type);
			film.setInsertionDate(FilmListParser.sdf.parse(parts[3]));
			
			if (!films.contains(film)) {
				films.add(film);
			}
		}
		
		return films;
	}
}
