package it.polimi.tiw.progetto.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import it.polimi.tiw.progetto.beans.File;
import it.polimi.tiw.progetto.utils.JdbcHelper;

public class FileDao {

	public boolean checkFileName(String name, String username) throws SQLException {
		JdbcHelper helper = new JdbcHelper();
        
        
        ResultSet resultSet = helper.executeQuery("select * from foldersystem where name = ? and holder = ?", name, username);
        
       
            if(resultSet.next()){
               helper.closeDB();
               return true;
            }
         
        helper.closeDB();
        return false;
	}
	
	public void add(File file) throws SQLException {
			
			JdbcHelper helper = new JdbcHelper();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			
			ResultSet resultSet = helper.executeQuery("select holder from foldersystem where id = ?", file.getParent_id());
			 if (resultSet.next()) {
				 if(!resultSet.getString("holder").equals(file.getHolder())) {
					 helper.closeDB();
					 throw new SQLException("this is not your folder");
				 }
					 
			}
			
			
			helper.executeUpdate("insert into foldersystem (holder, name, creation_date,type,parent_id) values(?,?,?,?,?)",file.getHolder(),file.getName(),timestamp,"FILE",file.getParent_id());
	        ResultSet rs = helper.executeQuery("SELECT id FROM foldersystem ORDER BY id DESC LIMIT 1");
			
	        while(rs.next()) {
	        	helper.executeUpdate("insert into tb_document (id,sommario,tipo) values(?,?,?)", rs.getString("id"), file.getSummary(), file.getType());
	        }
	        
	        helper.closeDB();
	}
	
	public void deleteFile(String id) throws SQLException {
		
		JdbcHelper dbHelper = new JdbcHelper();
		
		String sql = "DELETE FROM tb_document WHERE id =" + id;
		dbHelper.executeUpdate(sql);
		sql = "DELETE FROM FolderSystem WHERE id =" + id;
		dbHelper.executeUpdate(sql);
		
		dbHelper.closeDB();
	}
	
	public void moveFile(String id, String destination, String username) throws SQLException {
		JdbcHelper helper = new JdbcHelper();
		
		ResultSet resultSet = helper.executeQuery("select holder from foldersystem where id = ?", id);
		 if (resultSet.next()) {
			 if(!resultSet.getString("holder").equals(username)) {
				 helper.closeDB();
				 throw new SQLException("this is not your file");
			 }				 
		}
		 
		 resultSet = helper.executeQuery("select holder from foldersystem where id = ?", destination);
		 if (resultSet.next()) {
			 if(!resultSet.getString("holder").equals(username)) {
				 helper.closeDB();
				 throw new SQLException("the destination is not your folder");
			 }				 
		}
		
		
		String sql = "UPDATE foldersystem SET parent_id = " + destination + " WHERE id = " +  id;
		helper.executeUpdate(sql);
		
		helper.closeDB();
		
	}
	
	public File getFile(String id, String username) throws NumberFormatException, SQLException {
		JdbcHelper dbHelper = new JdbcHelper();
		
		ResultSet resultSet = dbHelper.executeQuery("select holder from foldersystem where id = ?", id);
		 if (resultSet.next()) {
			 if(!resultSet.getString("holder").equals(username)) {
				 dbHelper.closeDB();
				 throw new SQLException("this is not your file");
			 } 	
		 }
		
		String sql = "SELECT tipo, sommario FROM tb_document WHERE id = ?";
        ResultSet rs = dbHelper.executeQuery(sql, Integer.parseInt(id));
		
        if (rs.next()) {
            String type = rs.getString("tipo");
            String summary = rs.getString("sommario");
            
            sql = "SELECT holder, name, creation_date, parent_id FROM foldersystem WHERE id = ?";
            rs = dbHelper.executeQuery(sql, Integer.parseInt(id));
            String name = "";
            String proprietario = "";
            String date = "";
            String parent_id = "";
            if(rs.next()) {
            	name = rs.getString("name");
            	proprietario = rs.getString("holder");
            	date = rs.getString("creation_date");
            	parent_id = rs.getString("parent_id");
            }
            
            File file = new File();
            file.setDate(date);
            file.setHolder(proprietario);
            file.setName(name);
            file.setSummary(summary);
            file.setType(type);
            file.setParent_id(parent_id);
            
            dbHelper.closeDB();
            
            return file;
        }
        
        dbHelper.closeDB();
        
		return null;
	}
	
}
