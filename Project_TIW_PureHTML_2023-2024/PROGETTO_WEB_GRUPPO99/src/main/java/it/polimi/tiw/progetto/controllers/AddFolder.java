package it.polimi.tiw.progetto.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.progetto.beans.Folder;
import it.polimi.tiw.progetto.beans.User;
import it.polimi.tiw.progetto.dao.FolderDao;
import it.polimi.tiw.progetto.dao.ObjectDao;

@WebServlet(urlPatterns = {"/HomePage/addFolder","/addFolder"})
public class AddFolder extends HttpServlet {

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
		if(req.getParameter("id") != null) {
			
			HttpSession session = req.getSession();
			if(req.getParameter("id").equals("")) {
				session.setAttribute("destinazione", null);
			} else {
				session.setAttribute("destinazione", req.getParameter("id"));
			}
			try {
				int id = Integer.parseInt(req.getParameter("id"));
			} catch(NumberFormatException e) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "parentId is string");
				return;
			}
			
			
			RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/AddFolder.html");
	        dispatcher.forward(req, resp);
	        return;
		} 
		resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
       
        try {
            // 读取HTML模板文件
            String htmlTemplate = new String(Files.readAllBytes(Paths.get(getServletContext().getRealPath("WEB-INF/Destination.html"))), "UTF-8");
            
            HttpSession s = req.getSession();
    		User user = (User) s.getAttribute("user");
            
            
            // 生成文件系统的HTML结构
            StringBuilder fileSystemHtml = new StringBuilder();
            
            ObjectDao od = new ObjectDao();
            od.generateFileSystemHtmlAddFolder(fileSystemHtml, null, user.getUsername());
            
            // 将文件系统的HTML插入到模板中的特定位置
            String finalHtml = htmlTemplate.replace("<!-- Servlet -->", fileSystemHtml.toString());
            
            finalHtml = finalHtml.replace("<!-- name -->", "seleziona dove vuoi aggiungere la cartella");
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

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		
		Folder folder = new Folder();
		folder.setOwner(user.getUsername());
		folder.setName(req.getParameter("name"));
		
		//System.out.println(session.getAttribute("destinazione"));
		
		if(session.getAttribute("destinazione") == null) {
			folder.setParent_id(null);
		} else {
			folder.setParent_id(session.getAttribute("destinazione").toString());
		}
		
		
		FolderDao fd = new FolderDao();
		
		Boolean exist = null;
		try {
			exist = fd.checkFolderName(req.getParameter("name"), user.getUsername());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String path;
		if(exist == true) {
			ServletContext servletContext = getServletContext();
			final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
			webContext.setVariable("errorMsg", "folder name already exists");
			
			path = "/WEB-INF/AddFolder.html";
			templateEngine.process(path, webContext, resp.getWriter());
		} else {
			try {
				
				//System.out.println(folder.getParent_id());				
				
				fd.add(folder);
			} catch (SQLException e) {
				if(e.getMessage() == null) {
					resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database can't be reached.");
				} else {
					resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				}
				return;
			}
			path = getServletContext().getContextPath() +  "/HomePage";
			resp.sendRedirect(path);
		}
	    
	}
}
