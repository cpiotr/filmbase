package pl.ciruk.films.web.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import pl.ciruk.film.dataminer.file.FilmListParser;
import pl.ciruk.films.datatype.FilmType;
import pl.ciruk.films.ejb.api.FilmSearchCriteria;
import pl.ciruk.films.ejb.api.FilmServiceLocal;
import pl.ciruk.films.entity.Film;

@ManagedBean(name="filmBean")
@SessionScoped
public class FilmBean {
	private static Logger LOG = Logger.getLogger(FilmBean.class);
	
	private static final String UPLOAD_DIR = "D:\\tmp";
	@EJB
	private FilmServiceLocal service;
	
	private List<Film> films;
	
	private String title;

	private String text = "Button";
	
	public void handleFileUpload(FileUploadEvent event) {  
        LOG.info("handleFileUpload");
		
		File f = null;
		try {
			f = createFile(event.getFile());
			
			FilmListParser parser = new FilmListParser();
			String content = parser.parse(f);
			f.delete();
			
			for (String line : content.split("\n")) {
				System.out.println(line);
				String[] parts = line.split(FilmListParser.DEFAULT_DELIMITER);
				if (parts.length == 4) {
					Film film = new Film();
					film.setTitle(parts[0]);
					film.setLabel(parts[1]);
					film.setType(FilmType.getByLabel(parts[2]));
					film.setInsertionDate(FilmListParser.sdf.parse(parts[3]));
					
					boolean saved = service.save(film);
					LOG.info("handleFileUpload - saved: " + saved);
				}
			}
			
			FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");  
	        FacesContext.getCurrentInstance().addMessage(null, msg);
		} catch (Exception e) {
			LOG.error("handleFileUpload", e);
		}
    }
	
	private File createFile(UploadedFile uploadedFile) throws IOException {
		LOG.info("createFile");
		
		File file = null;
		
		if (uploadedFile != null) {
			File dir = new File(UPLOAD_DIR);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			file = new File(UPLOAD_DIR, uploadedFile.getFileName());
			
			BufferedOutputStream outputStream = null;
			BufferedInputStream inputStream = null;
			try {
				inputStream = new BufferedInputStream(uploadedFile.getInputstream());
				outputStream = new BufferedOutputStream(new FileOutputStream(file));
				
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = inputStream.read(buffer)) > -1) {
					outputStream.write(buffer, 0, length);
				}
				
				outputStream.flush();
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			}
		}
		
		return file;
	}
	
	public String getText() {
		return text;
	}
	
	public String search() {
		FilmSearchCriteria criteria = new FilmSearchCriteria();
		criteria.setTitle(title);
		films = service.find(criteria); 
		return null;
	}
	
	public List<Film> getFilms() {
		return films;
	}

	public void setFilms(List<Film> films) {
		this.films = films;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
