package it.polimi.tiw.progetto.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import it.polimi.tiw.progetto.beans.Folder;
import it.polimi.tiw.progetto.utils.JdbcHelper;

public class FolderDao {
	
	public boolean checkFolderName(String name, String username) throws SQLException {
		JdbcHelper helper = new JdbcHelper();
        
        
        ResultSet resultSet = helper.executeQuery("select * from foldersystem where name = ? and holder = ?", name, username );
        
       
            if(resultSet.next()){
               helper.closeDB();
               return true;
            }
         
        helper.closeDB();
        return false;
	}
	
	public void add(Folder folder) throws SQLException {
		JdbcHelper dbhelper = new JdbcHelper();
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		ResultSet resultSet = dbhelper.executeQuery("select holder from foldersystem where id = ?", folder.getParent_id());
		 
		if(!resultSet.next()) {
			throw new SQLException("cartella non trovata");
		}
		
		if (resultSet.next()) {
			 if(!resultSet.getString("holder").equals(folder.getOwner())) {
				 dbhelper.closeDB();
				 throw new SQLException("this is not your folder");
			 }
				 
		}
		
		
		dbhelper.executeUpdate("insert into foldersystem (holder, name, creation_date,type,parent_id) values(?,?,?,?,?)",
							folder.getOwner(), folder.getName(), timestamp, "FOLDER", folder.getParent_id());
		
		dbhelper.closeDB();
	}
	
	public void deleteFolder(String id, String username) throws SQLException {
		JdbcHelper dbHelper = new JdbcHelper();
		
		ResultSet resultSet = dbHelper.executeQuery("select holder from foldersystem where id = ?", id);
		if(!resultSet.next()) {
			throw new SQLException("cartella non trovata");
		}
		
		if (resultSet.next()) {
			 if(!resultSet.getString("holder").equals(username)) {
				 dbHelper.closeDB();
				 throw new SQLException("this is not your folder");
			 }	 
		}
		
		
		
		String sql = "DELETE FROM foldersystem WHERE parent_id =" + id;
		dbHelper.executeUpdate(sql);
		sql = "DELETE FROM foldersystem WHERE id =" + id;
		dbHelper.executeUpdate(sql);
		
		dbHelper.closeDB();
	}
	
}
