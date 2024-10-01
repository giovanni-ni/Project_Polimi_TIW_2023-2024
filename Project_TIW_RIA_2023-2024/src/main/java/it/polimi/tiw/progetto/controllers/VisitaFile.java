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

import org.json.JSONException;
import org.json.JSONObject;


@WebServlet("/VisitaFile")
public class VisitaFile extends HttpServlet{
	 private static final long serialVersionUID = 1L;
	    
	    @Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	        String fileId = req.getParameter("id");
	        
	        try {
				int idP = Integer.parseInt(fileId);
			} catch(NumberFormatException e) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "number format error");
			}
	        
	        FileDao fd = new FileDao();
	        
	        try {
	        	
	        	HttpSession session = req.getSession();
	    		User user = (User) session.getAttribute("user");
	        	
				File file = fd.getFile(fileId, user.getUsername());
				
				
				 JSONObject json = new JSONObject();
			        json.put("proprietario", file.getHolder());
			        json.put("nome", file.getName());
			        json.put("dataCreazione", file.getDate());
			        json.put("tipo", file.getType());
			        json.put("sommario", file.getSummary());
			
			        resp.setContentType("application/json");
			        resp.setCharacterEncoding("UTF-8");
			        
			        // Invia il JSON al client
			        resp.getWriter().write(json.toString());
			        
			} catch (NumberFormatException | SQLException e) {
				e.printStackTrace();
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            resp.getWriter().write("Errore nel recupero dei dettagli del file.");
	        } catch (JSONException e) {
				// TODO Auto-generated catch block
	        	resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        	resp.getWriter().write("Errore nel recupero dei dettagli del file.");
			}
	    }
}
