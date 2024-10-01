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
import it.polimi.tiw.progetto.dao.ObjectDao;

@WebServlet("/Delete")
public class Delete extends HttpServlet{

	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req,resp);
    }
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String Id = req.getParameter("id");
		//System.outprintln(id);
		ObjectDao od = new ObjectDao();
		
		/*try {
			int idP = Integer.parseInt(Id);
		} catch(NumberFormatException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "number format error");
		}*/
		
		try {
			HttpSession session = req.getSession();
			User user = (User) session.getAttribute("user");
		
			
			od.delete(Id, user.getUsername());
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
