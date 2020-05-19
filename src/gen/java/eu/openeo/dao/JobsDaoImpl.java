package eu.openeo.dao;

import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import eu.openeo.model.BatchJobResponse;

public class JobsDaoImpl extends BaseDaoImpl<BatchJobResponse, String> implements JobsDao {
	
	public JobsDaoImpl(ConnectionSource connectionSource)
		      throws SQLException {
		super(connectionSource, BatchJobResponse.class);
	}

}
