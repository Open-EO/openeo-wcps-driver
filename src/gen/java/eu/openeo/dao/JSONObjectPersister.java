package eu.openeo.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import com.j256.ormlite.support.DatabaseResults;

public class JSONObjectPersister extends StringType {
	
	private static final JSONObjectPersister singleTon = new JSONObjectPersister();
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private static final JSONParser parser = new JSONParser();
	
	Logger log = Logger.getLogger(this.getClass());


	private JSONObjectPersister() {
		super(SqlType.LONG_STRING, new Class<?>[] {Object.class});
	}
	
	
	public static JSONObjectPersister getSingleton() {
		return singleTon;
	}
	
	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
		try {
			return ((JSONObject) parser.parse(mapper.writeValueAsString(javaObject))).toJSONString();
		} catch (JsonProcessingException e) {
			log.error("Error javaToSqlArg: " + e.getMessage());
			log.error(javaObject.toString());
		} catch (ParseException e) {
			log.error("Error javaToSqlArg: " + e.getMessage());
			log.error(javaObject.toString());
		}
		return null;
	}
	
	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		Map<String, Object> returnObject = new LinkedHashMap<String, Object>();
		try {
			returnObject =  mapper.readValue((String) results.getObject(columnPos), new TypeReference<Map<String, Object>>(){});
			return returnObject; 
		} catch (JsonProcessingException e) {
			log.error("Error resultToSqlArg: " + e.getMessage());
			log.error(results.getObject(columnPos).toString());
			return returnObject;
		} catch (IOException e) {
			log.error("Error resultToSqlArg: " + e.getMessage());
			log.error(results.getObject(columnPos).toString());
			return returnObject;
		} catch(NullPointerException e) {
			return null;
		}
	}

	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
		return sqlArg;
	}

}
