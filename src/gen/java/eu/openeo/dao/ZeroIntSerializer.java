package eu.openeo.dao;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ZeroIntSerializer extends StdSerializer<Integer> {
	
	private static final long serialVersionUID = -4795613131607443358L;
	Logger log = LogManager.getLogger();

	public ZeroIntSerializer() {
		this(null);
	}
	
	public ZeroIntSerializer(Class<Integer> t) {
		super(t);
	}
	
	@Override
    public void serialize(Integer integer, JsonGenerator jsonGenerator, SerializerProvider serializer) {
		log.debug("serializing integer to json!");
        try {        	
			if(integer.intValue() > 0) {
				jsonGenerator.writeNumber(integer.intValue());
			}
		} catch (IOException e) {
			log.error("Error while serializing integer to json: " + e.getMessage());
			e.printStackTrace();
		}
        
    }

}