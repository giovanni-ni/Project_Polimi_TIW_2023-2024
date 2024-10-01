package it.polimi.tiw.progetto.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.progetto.utils.JdbcHelper;

public class ObjectDao {

	public void delete(String id) throws SQLException {
		String sql = "SELECT type FROM FolderSystem WHERE id=" + id;
		JdbcHelper dbHelper = new JdbcHelper();
		ResultSet rs = dbHelper.executeQuery(sql);
		if(rs.next()) {
			String type = rs.getString("type");
			if ("FOLDER".equals(type)) {
				FolderDao fd = new FolderDao();
				fd.deleteFolder(id);
			}
			
			if("FILE".equals(type)) {
				FileDao fd = new FileDao();
				fd.deleteFile(id);
			}
			
		}
		
		dbHelper.closeDB();
		
	}
	
	public void generateFileSystemHtmlHomePage(StringBuilder out, Integer parentId, String username) throws SQLException {
		JdbcHelper dbHelper = new JdbcHelper();
		String sql = "SELECT id, name, type FROM FolderSystem WHERE holder = '" + username + "' AND parent_id " + (parentId == null ? "IS NULL" : "= ?");
        ResultSet rs = null;
        try {
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
                	out.append("<li class='folder'><a href='HomePage/Contenuto?id=").append(id).append("'>").append(name).append("</a>");
                    generateFileSystemHtmlHomePage(out, id, username); 
                    out.append("</li>");
                } 
            }
            out.append("</ul>");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	dbHelper.closeDB();
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
	
	public void generateFileSystemHtmlAddFile(StringBuilder out, Integer parentId, String username) throws SQLException {
		JdbcHelper dbHelper = new JdbcHelper();
		String sql = "SELECT id, name, type FROM FolderSystem WHERE holder = '" + username + "' AND parent_id " + (parentId == null ? "IS NULL" : "= ?");
        ResultSet rs = null;
        try {
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
                	out.append("<li class='folder'><a href='HomePage/addFile?id=").append(id).append("'>").append(name).append("</a>");
                    generateFileSystemHtmlAddFile(out, id, username); 
                    out.append("</li>");
                } 
            }
            out.append("</ul>");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	dbHelper.closeDB();
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
	
	public void generateFileSystemHtmlAddFolder(StringBuilder out, Integer parentId, String username) throws SQLException {
		JdbcHelper dbHelper = new JdbcHelper();
		
		String sql = "SELECT id, name, type FROM FolderSystem WHERE holder = '" + username + "' AND parent_id " + (parentId == null ? "IS NULL" : "= ?");
        ResultSet rs = null;
        try {
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
                	out.append("<li class='folder'><a href='HomePage/addFolder?id=").append(id).append("'>").append(name).append("</a>");
                    generateFileSystemHtmlAddFolder(out, id, username); 
                    out.append("</li>");
                } 
            }
            out.append("</ul>");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	dbHelper.closeDB();
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
	
	public void generateFileSystemHtmlContenuto(StringBuilder out, Integer parentId, String username, int first) throws SQLException {
		JdbcHelper dbHelper = new JdbcHelper();
		String sql = "SELECT id, name, type, parent_id FROM FolderSystem WHERE holder = '" + username + "' AND parent_id " + (parentId == null ? "IS NULL" : "= ?");
        ResultSet rs = null;
        try {
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
                int father =  rs.getInt("parent_id");
                       
                if ("FOLDER".equals(type)) {
                	out.append("<li class='folder'><a href='Contenuto?id=").append(id).append("'>").append(name).append("</a>");
                    generateFileSystemHtmlContenuto(out, id, username,first); 
                    out.append("</li>");
                } else if("FILE".equals(type) && (father == first) ) {
                	out.append("<li class='file'>")
                    .append(name)
                    .append(" <a href='Contenuto/Documento?id=").append(id).append("'>Accedi</a>")
                    .append(" | ")
                    .append("<a href='Sposta?id=").append(id).append("'>Sposta</a>")
                    .append("</li>");
                }
            }
            
            out.append("</ul>");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	dbHelper.closeDB();
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
	
	
	public void generateFileSystemHtmlSposta(StringBuilder out, String parentId, String username, String origine) {
		JdbcHelper dbHelper = new JdbcHelper();
		String sql = "SELECT id, name, type, parent_id FROM FolderSystem WHERE holder = '" + username + "' AND parent_id " + (parentId == null ? "IS NULL" : "= ?");
        ResultSet rs = null;
        try {
            if (parentId == null) {
                rs = dbHelper.executeQuery(sql);
            } else {
                rs = dbHelper.executeQuery(sql, parentId);
            }
            out.append("<ul>");
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                
                
                if ("FOLDER".equals(type)) {
                	
                	if(!id.equals(origine)) {
                		out.append("<li class='folder'><a href='Sposta?id=").append(id).append("'>").append(name).append("</a>");
                	} else {
                		out.append("<li class='folder'>").append("'>").append(name).append("</a>");
                	}
                	
                    generateFileSystemHtmlSposta(out, id, username, origine); 
                    out.append("</li>");
                } 
            }
            out.append("</ul>");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	dbHelper.closeDB();
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
	
	public boolean checkFolderHolder(String id, String username) throws SQLException{
		JdbcHelper dbHelper = new JdbcHelper();
		ResultSet resultSet = dbHelper.executeQuery("select holder, type from foldersystem where id = ?", id);
		 if (resultSet.next()) {
			 if(!resultSet.getString("holder").equals(username)) {
				 dbHelper.closeDB();
				 throw new SQLException("this is not your folder");
			 } 	
			 if(!resultSet.getString("type").equals("FOLDER")) {
				 dbHelper.closeDB();
				 throw new SQLException("this is not a folder");
			 } 	
			 
		 }
		 return true;
	}
	
	
}
