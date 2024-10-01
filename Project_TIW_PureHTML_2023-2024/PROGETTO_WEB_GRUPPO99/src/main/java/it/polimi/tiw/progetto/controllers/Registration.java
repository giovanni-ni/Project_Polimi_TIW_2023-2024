package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.progetto.beans.User;
import it.polimi.tiw.progetto.dao.UserDao;

@WebServlet("/register")
public class Registration extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	
	@Override
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = "/WEB-INF/Register.html";
		resp.setContentType("text");
		ServletContext servletContext = getServletContext();
		final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
		templateEngine.process(path, webContext, resp.getWriter());
	}



	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserDao u = new UserDao();
		Boolean b = (Boolean) null;
		
		String username = req.getParameter("username");
		String email = req.getParameter("email");
		String password = req.getParameter("pwd");
		String path = null;
		
		if(username == null || password == null || username.isBlank() || password.isBlank() || email == null || email.isBlank()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Blank or null credentials.");
			return;
		}
		
		ServletContext servletContext = getServletContext();
		final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
		
		
		b = isValidEmail(email);
		
		if(b == false) {
			
			webContext.setVariable("errorMsg", "email not valid");
			path = "/WEB-INF/Register.html";
			templateEngine.process(path, webContext, resp.getWriter());
		}
		
		b = isValidPassword(password);
		
		if(b == false) {
			webContext.setVariable("errorMsg", "password not valid: Almeno 8 caratteri compresi almeno uno maiuscolo, uno minuscolo, un numero, un carattere speciale ");
			path = "/WEB-INF/Register.html";
			templateEngine.process(path, webContext, resp.getWriter());
			return;
		}
		
		try {
			b = u.findUser(username);
		} catch (SQLException e) {
			resp.sendError(500,"data base error");
		}
		
		if(b==false) {
			try {
				b = u.findUserEmail(email);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				resp.sendError(500,"data base error");
			}
		}
				
		if(b == false) {
			
			
			//System.out.println(username + " "+ email + " "+ password);
			User user = new User(username, email, password);
			try {
				u.addUser(user);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("errore");
				
				resp.sendError(500,"data base error");
				return;
			}
			
			req.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/HomePage";
			resp.sendRedirect(path);
			
			
		} else {
			
			webContext.setVariable("errorMsg", "username or email already exists");
			path = "/WEB-INF/Register.html";
			templateEngine.process(path, webContext, resp.getWriter());
		}
		
	}
	
	private boolean isValidEmail(String email) {
		
		String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		    
		Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
		
        if (email == null) {
            return false;
        }
        
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
	
	


    private boolean isValidPassword(String password) {
        
    	String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        
        Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    	
    	if (password == null) {
            return false;
        }
        
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }
	
	
}
