package pl.ciruk.films.ejb.api;

import java.util.List;

import javax.ejb.Local;

@Local
public interface TestLocal {
	List<String> getData();
}
