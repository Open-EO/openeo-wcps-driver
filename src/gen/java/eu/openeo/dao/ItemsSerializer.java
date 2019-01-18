package eu.openeo.dao;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import eu.openeo.model.Items;

public class ItemsSerializer extends StdSerializer<Items> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7438951486864108308L;
	Logger log = Logger.getLogger(this.getClass());

	public ItemsSerializer() {
		this(null);
	}
	
	public ItemsSerializer(Class<Items> t) {
		super(t);
	}
	
	@Override
    public void serialize(Items items, JsonGenerator jsonGenerator, SerializerProvider serializer) {
		log.debug("serializing item to json!");
        try {
			jsonGenerator.writeStartObject();
			if(items.getType().length > 1) {
	        	jsonGenerator.writeArrayFieldStart("type");
	        	for(String typeString: items.getType()) {
	        		jsonGenerator.writeString(typeString);
	        	}
	        	jsonGenerator.writeEndArray();
	        }else {
	        	jsonGenerator.writeStringField("type", items.getType()[0].toString());
	        }
	        if(items.getFormat() != null) {
		        jsonGenerator.writeStringField("format", items.getFormat());
	        }
	        jsonGenerator.writeEndObject();
		} catch (IOException e) {
			log.error("Error while serializing items object to json: " + e.getMessage());
			e.printStackTrace();
		}
        
    }

}
