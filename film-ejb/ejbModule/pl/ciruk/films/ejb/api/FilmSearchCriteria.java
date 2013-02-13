package pl.ciruk.films.ejb.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.ciruk.film.utils.core.StringHelper;
import pl.ciruk.films.datatype.FilmSortColumn;
import pl.ciruk.films.datatype.FilmType;

public class FilmSearchCriteria implements Serializable {

	/**  */
	private static final long serialVersionUID = -3586200660901487305L;

	private String title;
	
	private List<FilmType> types;
	
	private Date additionDate;
	
	private FilmSortColumn sortColumn = FilmSortColumn.TITLE;
	
	public boolean isEmpty() {
		return StringHelper.isEmpty(title) && getTypes().isEmpty() && additionDate == null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<FilmType> getTypes() {
		if (types == null) {
			types = new ArrayList<FilmType>();
		}
		return types;
	}

	public void setTypes(List<FilmType> types) {
		this.types = types;
	}

	public Date getAdditionDate() {
		return additionDate;
	}

	public void setAdditionDate(Date additionDate) {
		this.additionDate = additionDate;
	}

	public FilmSortColumn getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(FilmSortColumn sortColumn) {
		this.sortColumn = sortColumn;
	}
}
