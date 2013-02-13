package pl.ciruk.films.web.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.ciruk.films.ejb.api.FilmSearchCriteria;
import pl.ciruk.films.ejb.api.FilmServiceLocal;
import pl.ciruk.films.ejb.api.TestLocal;
import pl.ciruk.films.entity.Film;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@EJB
	private FilmServiceLocal service;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		Writer writer = new OutputStreamWriter(response.getOutputStream());
		writer.write("<html><body>");
		FilmSearchCriteria criteria = new FilmSearchCriteria();
		criteria.setTitle("007%");
		for (Film f : service.find(criteria)) {
			writer.write("<p>" + f.getTitle() + "</p><br/>");
		}
 		writer.write("</body></html>");
 		writer.flush();
 		writer.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
