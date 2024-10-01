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
import it.polimi.tiw.progetto.dao.ObjectDao;


@WebServlet("/HomePage/Sposta")
public class Sposta extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
       
        HttpSession session = req.getSession();
        session.setAttribute("previousURL", req.getRequestURL().toString());
        
        
        
        String fileIdParam = req.getParameter("id");
        
        if(fileIdParam == null || fileIdParam.equals("")) {
        	resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "error. null id");
			return;
        }
        
        try {
        	int id = Integer.parseInt(fileIdParam);
        } catch(NumberFormatException e) {
        	resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "parentId is string");
        	return;
        }
        
        
        
        
        String fileId = (fileIdParam != null) ? fileIdParam : null;
        
        if(session.getAttribute("elemento_da_spostare") == null) {
        	session.setAttribute("elemento_da_spostare", fileId);
        	
        } else {
        	String n = session.getAttribute("elemento_da_spostare").toString();
        	//sposta(n, fileId);
        	
        	FileDao fd = new FileDao();
        	try {
        		
        		HttpSession s = req.getSession();
        		User user = (User) s.getAttribute("user");
        		
				fd.moveFile(n, fileId, user.getUsername());
			} catch (SQLException e) {
				if(e.getMessage() == null) {
					resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database can't be reached.");
				} else {
					resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
				return;
			}
        	
        	session.removeAttribute("elemento_da_spostare");
        	String path = getServletContext().getContextPath() + "/HomePage/Contenuto?id="+ fileId;
			resp.sendRedirect(path);
        	return;
        }
        
        //System.out.println(session.getAttribute("previousURL"));
        
        // 使用 JdbcHelper 连接数据库
        //JdbcHelper dbHelper = new JdbcHelper();
        try {
            // 读取HTML模板文件
            String htmlTemplate = new String(Files.readAllBytes(Paths.get(getServletContext().getRealPath("WEB-INF/Destination.html"))), "UTF-8");
            
            HttpSession s = req.getSession();
    		User user = (User) s.getAttribute("user");
            
    		FileDao fd = new FileDao();
    		File file = fd.getFile(fileId.toString(), user.getUsername());
    		
            /*String sql = "SELECT name, parent_id FROM FolderSystem WHERE id = " +fileId;
            ResultSet rs = dbHelper.executeQuery(sql);*/
            //int origine = 0;
            //String name = null;
    		String origine = null;
    		String name = null;
            if(file != null) {
            	/*origine = rs.getInt("parent_id");
            	name = rs.getString("name");*/
            	origine = file.getParent_id();
            	name = file.getName();
            }
    		
            // 生成文件系统的HTML结构
            StringBuilder fileSystemHtml = new StringBuilder();
            
            ObjectDao od = new ObjectDao();
            od.generateFileSystemHtmlSposta(fileSystemHtml, null, user.getUsername(), origine);
            
            //generateFileSystemHtml(fileSystemHtml, dbHelper, null, user.getUsername(),origine);
            
            // 将文件系统的HTML插入到模板中的特定位置
            String finalHtml = htmlTemplate.replace("<!-- Servlet -->", fileSystemHtml.toString());
            
            finalHtml = finalHtml.replace("<!-- name -->", "Stai spostando il documento "+ name +" dalla cartella "+ ((String) session.getAttribute("origine")) + "scegli la cartella di destinazione");
            // 输出最终的HTML内容
            out.println(finalHtml);
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
