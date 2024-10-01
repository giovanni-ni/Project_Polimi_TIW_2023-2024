package it.polimi.tiw.progetto.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import it.polimi.tiw.progetto.beans.Folder;
import it.polimi.tiw.progetto.utils.JdbcHelper;

public class FolderDao {
	
	public boolean checkFolderName(String name, String username) throws SQLException {
		JdbcHelper helper = new JdbcHelper();
        
        
        ResultSet resultSet = helper.executeQuery("select * from foldersystem where name = ?  and holder = ?",name, username);
        
       
            if(resultSet.next()){
               helper.closeDB();
               return true;
            }
         
        helper.closeDB();
        return false;
	}

	public void add(Folder folder) throws SQLException {
		JdbcHelper dbhelper = new JdbcHelper();
		
		ResultSet resultSet = dbhelper.executeQuery("select holder from foldersystem where id = ?", folder.getParent_id());
		 if (resultSet.next()) {
			 if(!resultSet.getString("holder").equals(folder.getOwner())) {
				 dbhelper.closeDB();
				 throw new SQLException("this is not your folder");
			 }
				 
		}
		
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		dbhelper.executeUpdate("insert into foldersystem (holder, name, creation_date,type,parent_id) values(?,?,?,?,?)",
							folder.getOwner(), folder.getName(), timestamp, "FOLDER", folder.getParent_id());
		
		dbhelper.closeDB();
	}
	
	public void deleteFolder(String id) throws SQLException {
		JdbcHelper dbHelper = new JdbcHelper();
		String sql = "DELETE FROM FolderSystem WHERE parent_id =" + id;
		dbHelper.executeUpdate(sql);
		sql = "DELETE FROM FolderSystem WHERE id =" + id;
		dbHelper.executeUpdate(sql);
		
		dbHelper.closeDB();
	}
	
	public String getFolderName(String folderId) throws SQLException {
		JdbcHelper dbHelper = new JdbcHelper();
		String sql = "SELECT name,parent_id FROM FolderSystem WHERE id = " + folderId;
        ResultSet rs = dbHelper.executeQuery(sql);
        String name = "";
        if(rs.next()) {
        	name = rs.getString("name");
        }
        dbHelper.closeDB();
        return name;
	}
}
