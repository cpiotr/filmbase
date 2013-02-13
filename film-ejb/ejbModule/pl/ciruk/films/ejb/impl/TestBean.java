package pl.ciruk.films.ejb.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pl.ciruk.films.ejb.api.TestLocal;
import pl.ciruk.films.entity.Film;
import pl.ciruk.films.entity.User;

/**
 * Session Bean implementation class TestBean
 */
@Stateless
@LocalBean
public class TestBean implements TestLocal {

	@PersistenceContext
	EntityManager em;
	
    /**
     * Default constructor. 
     */
    public TestBean() {
        // TODO Auto-generated constructor stub
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void test() {
    	User user = (User) em.createQuery("select u from User u where u.id=1").getSingleResult();
    	System.out.println(user.getLogin() + " " + user.getFilms());
    }
    
    public List<String> getData() {
    	List<String> data = new ArrayList<String>();
    	List<Film> films = em.createQuery("select f from Film f").getResultList();
    	for (Film f : films) {
    		data.add(f.getTitle());
    	}
    	return data;
    }
}
