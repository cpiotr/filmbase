package pl.ciruk.films.ejb.api;

import java.io.File;
import java.util.List;

import javax.ejb.Local;

import pl.ciruk.film.dataminer.web.FilmwebDescription;
import pl.ciruk.films.entity.Film;

/**
 * Interfejs DAO dla filmow.
 * @author piotrc
 *
 */
@Local
public interface FilmServiceLocal {
	/**
	 * Wyszukuje filmy na podstawie podanych kryteriow. <br/>
	 * W przypadku podania pustego parametru, zwraca liste wszystkich filmow.
	 * @param criteria
	 * @return
	 */
	List<Film> find(FilmSearchCriteria criteria);
	
	/**
	 * Zapisuje film do bazy danych.
	 * @param film
	 * @return 
	 * 		{@code true} - jezeli pomyslnie zapisano rekord do bazy
	 * 		{@code false} - w przypadku wystapienia bledu
	 */
	boolean save(Film film);
	
	/**
	 * Usuwa film z bazy danych.
	 * @param film
	 * @return 
	 * 		{@code true} - jezeli pomyslnie zapisano rekord do bazy
	 * 		{@code false} - w przypadku wystapienia bledu
	 */
	boolean remove(Film film);
	
	/**
	 * Aktualizuje repozytorium filmow danymi z pliku z lista nowych tytulow.
	 * @param filmListFile
	 */
	void updateWithListFile(File filmListFile);
	
	/**
	 * Pobiera dane dotyczace wybranego filmu z portalu Filmweb.
	 * @param film
	 * @return
	 */
	FilmwebDescription getDescrption(Film film);
}
