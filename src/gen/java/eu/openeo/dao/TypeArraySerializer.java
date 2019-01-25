package eu.openeo.dao;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class TypeArraySerializer extends StdSerializer<String[]> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7438951486864108308L;
	Logger log = Logger.getLogger(this.getClass());

	public TypeArraySerializer() {
		this(null);
	}
	
	public TypeArraySerializer(Class<String[]> t) {
		super(t);
	}
	
	@Override
    public void serialize(String[] types, JsonGenerator jsonGenerator, SerializerProvider serializer) {
		log.debug("serializing type to json!");
        try {        	
			if(types.length > 1) {
				jsonGenerator.writeStartObject();
	        	jsonGenerator.writeArrayFieldStart("type");
	        	for(String typeString: types) {
	        		jsonGenerator.writeString(typeString);
	        	}
	        	jsonGenerator.writeEndArray();
	        	jsonGenerator.writeEndObject();
	        }else {
	        	jsonGenerator.writeString(types[0].toString());
	        }
		} catch (IOException e) {
			log.error("Error while serializing type array to json: " + e.getMessage());
			e.printStackTrace();
		}
        
    }

}
