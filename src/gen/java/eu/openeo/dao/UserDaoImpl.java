package eu.openeo.dao;

import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import eu.openeo.model.User;

public class UserDaoImpl extends BaseDaoImpl<User, String> implements UserDao {
	
	public UserDaoImpl(ConnectionSource connectionSource)
		      throws SQLException {
		super(connectionSource, User.class);
	}

}
