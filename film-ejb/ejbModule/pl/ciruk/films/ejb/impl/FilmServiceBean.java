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
import pl.ciruk.film.dataminer.web.FilmwebDescription;
import pl.ciruk.film.dataminer.web.FilmwebParser;
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
	
	private FilmwebParser filmwebParser = new FilmwebParser();
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Film> find(FilmSearchCriteria criteria) {
		LOG.info("find");
		LOG.debug("find - Criteria: " + criteria);
		
		StringBuilder queryBuilder = new StringBuilder();
		List<Object> queryParams = Lists.newArrayList();
		queryBuilder.append("select f from ").append(Film.class.getSimpleName()).append(" f ");
		if (criteria != null && !criteria.isEmpty()) {
			queryBuilder.append("where 1=1");
			
			if (StringHelper.isNotEmpty(criteria.getTitle())) {
				queryBuilder.append("and f.title like ?" + (queryParams.size() + 1) + " ");
				queryParams.add(criteria.getTitle() + "%");
			}
			
			if (!criteria.getTypes().isEmpty()) {
				queryBuilder.append("and f.type in ?" + (queryParams.size() + 1) + " ");
				queryParams.add(criteria.getTypes());
			}
			
			if (criteria.getAdditionDate() != null) {
				queryBuilder.append("and f.insertionDate >= ?" + (queryParams.size() + 1) + " ");
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
		
		if (film.getId() != null) {
			em.merge(film);
		} else if (!exists(film)) {
			em.persist(film);
		} else {
			LOG.info("Film already exists");
		}
		
		return film.getId() != null;
	}
	
	private boolean exists(Film film) {
		Preconditions.checkArgument(film != null, PreconditionsHelper.CANT_BE_NULL, "Film");
		
		boolean result = false;
		
		Query query = em.createNamedQuery(Film.QUERY_GET_FILM).setParameter("title", film.getTitle()).setParameter("label", film.getLabel());
		List<Film> films = query.getResultList();
		result = !films.isEmpty();
		
		return result;
	}

	@Override
	public boolean remove(Film film) {
		Preconditions.checkArgument(film != null, PreconditionsHelper.CANT_BE_NULL, "Film");
		
		boolean removed = false;
		if (film.getId() != null) {
			em.remove(em.find(Film.class, film.getId()));
			removed = em.find(Film.class, film.getId()) == null;
		}
		
		return removed;
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

	@Override
	public FilmwebDescription getDescrption(Film film) {
		Preconditions.checkArgument(film != null, PreconditionsHelper.CANT_BE_NULL, "Film");

		FilmwebDescription description = null;
		
		if (StringHelper.isNotEmpty(film.getLabel())) {
			int from = film.getLabel().lastIndexOf("(") + 1;
			if (from > -1 && from < film.getLabel().length()-1) {
				int to = film.getLabel().lastIndexOf(")");
				if (to < from) {
					to = film.getLabel().length();
				}
				List<String> actors = Lists.newArrayList(film.getLabel().substring(from, to).split(","));
				description = filmwebParser.find(film.getTitle(), actors);
			}
		}
		
		return description;
	}
}
