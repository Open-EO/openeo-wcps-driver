package eu.openeo.dao;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JSONObjectSerializer extends StdSerializer<JSONObject> {
	
	private static final long serialVersionUID = -262872581635774633L;
	Logger log = Logger.getLogger(this.getClass());

	public JSONObjectSerializer() {
		this(null);
	}
	
	public JSONObjectSerializer(Class<JSONObject> t) {
		super(t);
	}
	
	@Override
    public void serialize(JSONObject jsonObject, JsonGenerator jsonGenerator, SerializerProvider serializer) {
		log.debug("serializing jsonobject to json!");
        try {
        	log.debug("JSONObject as string: " + jsonObject.toString());
			//jsonGenerator.writeEmbeddedObject(jsonObject.toString());
			jsonGenerator.writeString(jsonObject.toString());
		} catch (IOException e) {
			log.error("Error while serializing integer to json: " + e.getMessage());
			e.printStackTrace();
		}        
    }

}