package eu.openeo.dao;

import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import eu.openeo.model.StoredProcessGraphResponse;

public class GraphDaoImpl extends BaseDaoImpl<StoredProcessGraphResponse, String> implements GraphDao {
	
	public GraphDaoImpl(ConnectionSource connectionSource)
		      throws SQLException {
		super(connectionSource, StoredProcessGraphResponse.class);
	}

}
