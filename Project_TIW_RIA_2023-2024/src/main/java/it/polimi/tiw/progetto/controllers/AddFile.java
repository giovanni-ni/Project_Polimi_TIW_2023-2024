package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.progetto.beans.File;
import it.polimi.tiw.progetto.beans.User;
import it.polimi.tiw.progetto.dao.FileDao;

@WebServlet("/AddFile")
public class AddFile extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}



	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		
        File file = new File();
        file.setHolder(user.getUsername());
        file.setName(req.getParameter("name"));
        file.setType(req.getParameter("type"));
        file.setParent_id(req.getParameter("id"));
        file.setSummary(req.getParameter("summary"));
        
        try {
        	int id = Integer.parseInt(req.getParameter("id"));
        } catch(NumberFormatException e) {
        	resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "parentId is string");
        	return;
        }
        	
        
        
        
        FileDao fd = new FileDao();
        Boolean exist = (Boolean) null;
        try {
        	exist = fd.checkFileName(req.getParameter("name"), user.getUsername());
        	if(exist == true) {
        		resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    			resp.getWriter().println("nome file esistente"); 
        	} else {
        		fd.add(file);
    			resp.setStatus(HttpServletResponse.SC_OK);
    			resp.sendRedirect("HomePage");
        	}
		} catch (SQLException e) {
			if(e.getMessage() == null) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database can't be reached.");
			} else {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
			
			return;
		}
        
	}

}
