package eu.openeo.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import eu.openeo.model.Items;

public class ItemsDeserializer  extends StdDeserializer<Items>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6406503722293928959L;
	
	Logger log = Logger.getLogger(this.getClass());

	public ItemsDeserializer() {
		this(null);
	}
	
	public ItemsDeserializer(Class<?> vc) {
        super(vc);
    }
	
	 @Override
	    public Items deserialize(JsonParser parser, DeserializationContext deserializer) {
	        Items items = new Items();
	        ObjectCodec codec = parser.getCodec();
	        JsonNode node;
			try {
				node = codec.readTree(parser);
				JsonNode formatNode = node.get("format");
				if(formatNode != null) {
					items.setFormat(formatNode.asText());
				}
		        JsonNode typeNode = node.get("type");
		        if(typeNode.isArray()) {
		        	List<String> typeList = new ArrayList<String>();
		        	for (final JsonNode objNode : typeNode) {
		                typeList.add(objNode.asText());
		            }
		        	String[] types = new String[typeList.size()];
		        	items.setType(typeList.toArray(types));
		        }else {
			        String type = typeNode.asText(); 
			        items.setType(type);
		        }
			} catch (IOException e) {
				log.error("Error during deserialization of items node: " + e.getMessage());
				e.printStackTrace();
			}
	        return items;
	    }

}
