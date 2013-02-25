package pl.ciruk.films.adapter;

import pl.ciruk.film.dataminer.domain.FilmDTO;
import pl.ciruk.films.datatype.FilmType;
import pl.ciruk.films.entity.Film;

public final class FilmAdapter {
	private FilmAdapter() {
		
	}
	
	public static Film fromDTO(FilmDTO dto) {
		Film film = new Film();
		film.setInsertionDate(dto.getInsertionDate());
		film.setLabel(dto.getLabel());
		film.setTitle(dto.getTitle());
		film.setType(FilmType.getByLabel(dto.getCategory().getLabel()));
		return film;
	}
}
