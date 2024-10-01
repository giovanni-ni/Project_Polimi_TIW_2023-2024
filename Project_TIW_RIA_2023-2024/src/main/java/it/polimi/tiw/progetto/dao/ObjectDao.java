package it.polimi.tiw.progetto.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.progetto.utils.JdbcHelper;

public class ObjectDao {

	public void delete(String id, String username) throws SQLException {
		String sql = "SELECT type FROM foldersystem WHERE id=" + id;
		JdbcHelper dbHelper = new JdbcHelper();
		ResultSet rs = dbHelper.executeQuery(sql);
		
		if(rs.next()) {
			String type = rs.getString("type");
			if ("FOLDER".equals(type)) {
				FolderDao fd = new FolderDao();
				
				fd.deleteFolder(id, username);
			}
			
			if("FILE".equals(type)) {
				FileDao fd = new FileDao();
				fd.deleteFile(id, username);
			}
			
		}
		
		dbHelper.closeDB();
		
	}
	
	public void generateFileSystemHtml(StringBuilder out, Integer parentId, String username) throws SQLException {
		String sql = "SELECT id, name, type FROM foldersystem WHERE holder = '" + username + "' AND parent_id " + (parentId == null ? "IS NULL" : "= ?");
        ResultSet rs = null;
        JdbcHelper dbHelper = new JdbcHelper();
        if (parentId == null) {
            rs = dbHelper.executeQuery(sql);
        } else {
            rs = dbHelper.executeQuery(sql, parentId);
        }
        out.append("<ul>");
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String type = rs.getString("type");

            if ("FOLDER".equals(type)) {
            	out.append("<div class='folder'")
            	.append("draggable=\"true\"")
                .append(" data-folder-id='").append(id).append("'>")  
                .append(name);
             generateFileSystemHtml(out, id, username);  
             out.append("</div>");
            } 
            
            if("FILE".equals(type)) {
            	out.append("<div class='file' draggable=\"true\" ")
            	   .append("data-file-id='").append(id).append("'>")  
            	   .append(name)
            	   .append("<button class='visit-button' data-file-id='").append(id).append("'>Visita</button>");
            	
            	generateFileSystemHtml(out, id, username); 
            	out.append("</div>");
            }
        }
        out.append("</ul>");
        
        dbHelper.closeDB();
	}
	
}
