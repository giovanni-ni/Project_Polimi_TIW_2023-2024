package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.progetto.beans.Folder;
import it.polimi.tiw.progetto.beans.User;
import it.polimi.tiw.progetto.dao.FolderDao;

@WebServlet("/AddFolder")
public class AddFolder extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		
		String name = req.getParameter("name");
		String parentId = req.getParameter("id");
		
		if(!parentId.equals("null")) {
			try {
				int idP = Integer.parseInt(parentId);
			} catch(NumberFormatException e) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "number format error");
			}
		}
		
		Folder folder = new Folder();
		folder.setOwner(user.getUsername());
		folder.setName(name);
		FolderDao fd = new FolderDao();
		Boolean exist = (Boolean) null;
		
		try {
			exist = fd.checkFolderName(req.getParameter("name"), user.getUsername());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(exist == true) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.getWriter().println("nome cartella esistente"); 
		} else {
			
			try {
				if(!req.getParameter("id").equals("null")) {
					 
					folder.setParent_id(req.getParameter("id"));
					fd.add(folder);
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.sendRedirect("HomePage");
				} else {
					folder.setParent_id(null);
					fd.add(folder);
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

}
