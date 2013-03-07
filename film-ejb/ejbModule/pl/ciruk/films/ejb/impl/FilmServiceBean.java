package pl.ciruk.films.ejb.impl;

import java.io.File;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import pl.ciruk.film.dataminer.domain.FilmDTO;
import pl.ciruk.film.dataminer.file.FilmListParser;
import pl.ciruk.film.utils.core.StringHelper;
import pl.ciruk.films.adapter.FilmAdapter;
import pl.ciruk.films.ejb.api.FilmSearchCriteria;
import pl.ciruk.films.ejb.api.FilmServiceLocal;
import pl.ciruk.films.entity.Film;
import pl.ciruk.utils.preconditions.PreconditionsHelper;

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
			
			if (criteria.getAdditionDate() != null) {
				queryBuilder.append("f.insertionDate >= ?" + (queryParams.size() + 1) + " ");
				queryParams.add(criteria.getAdditionDate());
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
		Preconditions.checkArgument(film != null, "Film cannot be null");
		
		LOG.info("save");
		LOG.info("save - Film: " + film);
		
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
		Preconditions.checkArgument(film != null, PreconditionsHelper.CANT_BE_NULL, "Film");
		
		LOG.info("exists");
		
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
		Preconditions.checkArgument(film != null, PreconditionsHelper.CANT_BE_NULL, "Film");
		
		if (film.getId() != null) {
			em.remove(film);
		}
		
		return em.find(Film.class, film.getId()) == null;
	}

	@Override
	public void updateWithListFile(File filmListFile) {
		if (filmListFile != null && filmListFile.isFile()) {
			FilmListParser parser = new FilmListParser();
			List<FilmDTO> filmList = parser.parseToList(filmListFile);
				
			for (FilmDTO dto : filmList) {
				try {
					save(FilmAdapter.fromDTO(dto));
				} catch (Exception e) {
					LOG.error("updateWithListFile - An error occurred while saving film: " + dto.getTitle());
				}
			}
		} else {
			LOG.error("updateWithListFile - FilmListFile does not exist");
		}
	}
}
