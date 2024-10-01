package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.sql.SQLException;

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


@WebServlet("/login")
public class Login extends HttpServlet {

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
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("pwd");
		
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
		
		String path;
		if(user == null) { // Wrong credentials
			ServletContext servletContext = getServletContext();
			final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
			webContext.setVariable("errorMsg", "Incorrect credential");
			path = "/login.html";
			templateEngine.process(path, webContext, resp.getWriter());
		} else { // Correct credentials, redirect to Home
			req.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/HomePage";
			resp.sendRedirect(path);
		}
		
	}
	

}
