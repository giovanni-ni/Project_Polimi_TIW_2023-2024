package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.progetto.beans.User;
import it.polimi.tiw.progetto.dao.FileDao;

@WebServlet("/MoveFile")
public class MoveFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	
		doPost(req,resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
			
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		FileDao fd = new FileDao();
		
		String idFile = req.getParameter("id");
		String idParent = req.getParameter("parentId");
		
		try {
			int idF = Integer.parseInt(idFile);
			int idP = Integer.parseInt(idParent);
		} catch(NumberFormatException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "number format error");
		}
		
		
		try {
			fd.moveFile(req.getParameter("id"), req.getParameter("parentId"),user.getUsername());
			resp.sendRedirect("HomePage");
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
