package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.progetto.beans.User;
import it.polimi.tiw.progetto.dao.UserDao;

@WebServlet("/register")
public class Registration extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserDao u = new UserDao();
		Boolean b = (Boolean) null;
		
		String username = req.getParameter("username");
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		
		if(username == null || password == null || username.isBlank() || password.isBlank() || email == null || email.isBlank()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Blank or null credentials.");
			return;
		}
	
		b = isValidEmail(email);
		
		if(b == false) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().println("email non corretto"); 
			return;
		}
		
		b = isValidPassword(password);
		
		if(b == false) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().println("Password non rispetta i requisiti:almeno 1 maiuscolo, 1 minuscolo, 1 numero, 1 carattere speciale"); 
			return;
		}
		
		try {
			b = u.findUser(req.getParameter("username"));
		} catch (SQLException e) {
			
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database can't be reached.");
			return;
		}
		
		if(b == false) {
			
			User user = new User(username, email, password);
			u.addUser(user);
			resp.setStatus(HttpServletResponse.SC_OK);
		    resp.getWriter().write("Registrazione completata");
		    resp.sendRedirect("index.html");
		} else {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().println("username esistente"); 
		}
		
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
	 
	 private boolean isValidEmail(String email) {
			
			String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
			    
			Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
			
	        if (email == null) {
	            return false;
	        }
	        
	        Matcher matcher = EMAIL_PATTERN.matcher(email);
	        return matcher.matches();
	  }
}
