package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
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

@WebServlet("/HomePage/Contenuto/Documento")
public class Document extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            
        	 PrintWriter out = resp.getWriter();
        	// 读取HTML模板文件
            String htmlTemplate = new String(Files.readAllBytes(Paths.get(getServletContext().getRealPath("WEB-INF/Documento.html"))), "UTF-8");
           
    		// 获取请求参数中的文件夹ID
            String fileIdParam = req.getParameter("id");
            
            if(fileIdParam == null || fileIdParam.equals("")) {
            	resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "error. null id");
    			return;
            }
            
            
            
            
    		
            FileDao fd = new FileDao();
            File file = null;
			try {
				
				Integer fileId = (fileIdParam != null) ? Integer.parseInt(fileIdParam) : null;
				HttpSession s = req.getSession();
		    	User user = (User) s.getAttribute("user");
				file = fd.getFile(fileIdParam, user.getUsername());
			} catch ( SQLException e) {
				if(e.getMessage() == null) {
					resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database can't be reached.");
				} else {
					resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
				
				return;
			} catch (NumberFormatException e) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "id cannot be string");
			}
            
    		
            // 将文件系统的HTML插入到模板中的特定位置
            String finalHtml = null;
            if(file!=null) {
				finalHtml = htmlTemplate.replace("<!-- titolo -->",file.getName());
				finalHtml = finalHtml.replace("<!-- proprietario -->", file.getHolder());
				finalHtml = finalHtml.replace("<!-- data -->", file.getDate());
				finalHtml = finalHtml.replace("<!-- sommario -->", file.getSummary());
				finalHtml = finalHtml.replace("<!-- tipo -->", file.getType());
				//finalHtml = finalHtml.replace("<!-- previous -->", "<a href='" + req.getHeader("Referer") + "'>" + "previous page</a>");
			}
            
            // 输出最终的HTML内容
            out.println(finalHtml);
        
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
	}
	
	
	
}
