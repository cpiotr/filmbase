package pl.ciruk.films.web.bean;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import pl.ciruk.film.dataminer.web.FilmwebDescription;
import pl.ciruk.films.ejb.api.FilmServiceLocal;
import pl.ciruk.films.entity.Film;

@Named(value="filmBean")
@SessionScoped
public class FilmBean implements Serializable {
	/** */
	private static final long serialVersionUID = 3343180246659294215L;

	private static Logger LOG = Logger.getLogger(FilmBean.class);
	
	@EJB
	private FilmServiceLocal service;
	
	@Inject
	private FilmListBean filmList;
	
	private Film film;
	
	private FilmwebDescription description;
	
	public void remove() {
		LOG.info("remove");
		LOG.debug("remove - Film: " + getFilm());
		
		if (getFilm() != null) {
			service.remove(getFilm());
			filmList.refresh();
		}
	}
	
	public void save() {
		LOG.info("save");
		LOG.debug("save - Film: " + getFilm());
		
		if (getFilm() != null) {
			service.save(getFilm());
			filmList.refresh();
		}
	}
	
	public void view() {
		LOG.info("view");
		LOG.info("view - Film: " + getFilm());
		
		if (getFilm() != null) {
			setDescription(service.getDescrption(getFilm()));
			LOG.info(ToStringBuilder.reflectionToString(getDescription()));
		} else {
			LOG.warn("view - No film was selected");
		}
	}

	public Film getFilm() {
		return film;
	}

	public void setFilm(Film film) {
		this.film = film;
	}

	public FilmwebDescription getDescription() {
		return description;
	}

	public void setDescription(FilmwebDescription description) {
		this.description = description;
	}

}
