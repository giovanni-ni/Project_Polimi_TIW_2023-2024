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

import it.polimi.tiw.progetto.beans.File;
import it.polimi.tiw.progetto.beans.User;
import it.polimi.tiw.progetto.dao.FileDao;
import it.polimi.tiw.progetto.dao.ObjectDao;

@WebServlet(urlPatterns = {"/HomePage/addFile","/addFile"})
public class AddFile extends HttpServlet {

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
			try {
				int parent_id = Integer.parseInt(req.getParameter("id"));
			} catch (NumberFormatException e) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "parentId is string");
			}
			
			
			HttpSession session = req.getSession();
			session.setAttribute("destinazione", req.getParameter("id"));
			
			
			
			RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/AddFile.html");
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
            od.generateFileSystemHtmlAddFile(fileSystemHtml, null, user.getUsername());
            
            //generateFileSystemHtml(fileSystemHtml, dbHelper, null, user.getUsername());
            
            // 将文件系统的HTML插入到模板中的特定位置
            String finalHtml = htmlTemplate.replace("<!-- Servlet -->", fileSystemHtml.toString());
            
            finalHtml = finalHtml.replace("<!-- name -->", "seleziona dove vuoi aggiungere la cartella");
            // 输出最终的HTML内容
            out.println(finalHtml);
        } catch (SQLException e) {
		
			e.printStackTrace();
		}
	}
	
@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		
		File file = new File();
		
		String name = req.getParameter("name");
		String type = req.getParameter("tipo");
		String sommario = req.getParameter("sommario");
		String parent_id = session.getAttribute("destinazione").toString();
		
		 if(name == null || name.equals("") || type == null || type.equals("") || sommario == null || sommario.equals("")) {
         	resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "error. null or blank value");
 			return;
         }
		 
		
		
        file.setHolder(user.getUsername());
        file.setName(name);
        file.setType(type);
        file.setParent_id(parent_id);
        file.setSummary(sommario);
		
        FileDao fd = new FileDao();
        
        Boolean exist = null;
        
        
        try {
        	int idparent = Integer.parseInt(parent_id);
        	exist = fd.checkFileName(name, user.getUsername());
        	String path;
        	if(exist == true) {
        		ServletContext servletContext = getServletContext();
    			final WebContext webContext = new WebContext(req, resp, servletContext, req.getLocale());
    			webContext.setVariable("errorMsg", "file name already exists");
    			
    			path = "/WEB-INF/AddFile.html";
    			templateEngine.process(path, webContext, resp.getWriter());
        	} else {
        		fd.add(file);
        		path = getServletContext().getContextPath() +  "/HomePage";
        		resp.sendRedirect(path);
        		
        	}
        	
			
		} catch (SQLException e) {
			if(e.getMessage() == null) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database can't be reached.");
			} else {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
			
			return;
		} catch (NumberFormatException e) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "parentId is string");
		}
        
        
	}
}
