package it.polimi.tiw.progetto.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.progetto.beans.User;
import it.polimi.tiw.progetto.utils.JdbcHelper;

public class UserDao {
	
	public User checkCredential(String username, String password) throws SQLException{
        JdbcHelper helper = new JdbcHelper();
        
        
        ResultSet resultSet = helper.executeQuery("select * from tb_user where username = ? AND password = ?",username, password);
        
       
            if(resultSet.next()){
                User u = new User();
                u.setUsername( resultSet.getString("username"));
                u.setPassword( resultSet.getString("password"));
                return u;
            }
         
            helper.closeDB();
         
   
        return null;
    }
	
	public boolean findUser(String userName) throws SQLException{
		
		
			JdbcHelper helper = new JdbcHelper();    
	        
	        ResultSet resultSet = helper.executeQuery("select * from tb_user where username = ?",userName);
			
			if(resultSet.next()) {
				return true;
			}
			
			helper.closeDB();
			return false;
		
	}
	
	public int addUser(User u) {
		JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate("insert into tb_user values(?,?,?)", u.getUsername(), u.getMail(), u.getPassword());
                
        helper.closeDB();
        return res;
		
	}
}
