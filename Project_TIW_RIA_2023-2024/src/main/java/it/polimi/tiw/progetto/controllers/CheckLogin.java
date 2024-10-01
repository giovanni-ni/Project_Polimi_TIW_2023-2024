package it.polimi.tiw.progetto.controllers;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.progetto.beans.User;
import it.polimi.tiw.progetto.dao.UserDao;

@WebServlet("/CheckLogin")
@MultipartConfig
public class CheckLogin extends HttpServlet {
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
		
		String username = req.getParameter("username");
		String password = req.getParameter("pwd");
		
		//System.out.println(username + " " + password);
		
		if(username == null || password == null || username.isBlank() || password.isBlank()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Blank or null credentials.");
			return;
		}
		
		UserDao userDao = new UserDao();
		User user = null;
		try {
			user = userDao.checkCredential(username, password);
		} catch (SQLException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database can't be reached.");
		}
		
		if(user == null) { // Wrong credentials
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().println("Incorrect credentials");     
		} else { // Correct credentials, redirect to Home
			req.getSession().setAttribute("user", user);
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.sendRedirect("HomePage");
		}
		
	}
}
