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

import it.polimi.tiw.progetto.beans.User;
import it.polimi.tiw.progetto.dao.FolderDao;
import it.polimi.tiw.progetto.dao.ObjectDao;

@WebServlet("/HomePage/Contenuto")
public class Contenuto extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
       
        HttpSession session = req.getSession();
        
        try {
            // 读取HTML模板文件
            String htmlTemplate = new String(Files.readAllBytes(Paths.get(getServletContext().getRealPath("WEB-INF/Contenuto.html"))), "UTF-8");
            
            HttpSession s = req.getSession();
    		User user = (User) s.getAttribute("user");
            
    		// 获取请求参数中的文件夹ID
            String folderIdParam = req.getParameter("id");
            
            if(folderIdParam == null || folderIdParam.equals("")) {
            	resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "error. null id");
    			return;
            }
            
            Integer folderId = (folderIdParam != null) ? Integer.parseInt(folderIdParam) : null;
            
            
    		
            // 生成文件系统的HTML结构
            StringBuilder fileSystemHtml = new StringBuilder();
            
            ObjectDao od = new ObjectDao();
            
            od.checkFolderHolder(folderIdParam, user.getUsername());
            
            od.generateFileSystemHtmlContenuto(fileSystemHtml, folderId, user.getUsername(), folderId);
            	
            // 将文件系统的HTML插入到模板中的特定位置
            String finalHtml = htmlTemplate.replace("<!-- Servlet -->", fileSystemHtml.toString());
            
            FolderDao fd = new FolderDao();
            
            
            String name = fd.getFolderName(folderIdParam);
            
            session.setAttribute("origine", name);
            finalHtml = finalHtml.replace("<!-- nomeCartella -->", name);
            //finalHtml = finalHtml.replace("<!-- previous -->", "<a href='" + req.getHeader("Referer") + "'>" + "previous page</a>");
            // 输出最终的HTML内容
            out.println(finalHtml);
        } catch (SQLException e) {
        	if(e.getMessage() == null) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database can't be reached.");
			} else {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
			
			return;
		} catch (NumberFormatException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "id cannot be string");
		}
    }

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	
		
}
