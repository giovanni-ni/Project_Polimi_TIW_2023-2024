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
import it.polimi.tiw.progetto.dao.ObjectDao;

@WebServlet("/HomePage")
public class HomePage extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        ObjectDao od = new ObjectDao();
        
        try {
            // 读取HTML模板文件
            String htmlTemplate = new String(Files.readAllBytes(Paths.get(getServletContext().getRealPath("HomeCS.html"))), "UTF-8");
            
            HttpSession s = req.getSession();
    		User user = (User) s.getAttribute("user");
            
            
            // 生成文件系统的HTML结构
            StringBuilder fileSystemHtml = new StringBuilder();
            od.generateFileSystemHtml(fileSystemHtml, null, user.getUsername());
            
            // 将文件系统的HTML插入到模板中的特定位置
            String finalHtml = htmlTemplate.replace("<!-- Servlet -->", fileSystemHtml.toString());
            
            finalHtml = finalHtml.replace("<!-- name -->", "Welcome back " + user.getUsername());
            // 输出最终的HTML内容
            out.println(finalHtml);
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
}
