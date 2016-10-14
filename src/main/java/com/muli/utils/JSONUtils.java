package com.muli.utils;

import com.muli.model.CustomerScore;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.node.MissingNode;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhangjunhong
 * Date: 6/30/12
 * Time: 2:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSONUtils {
    private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);
    public static final ObjectMapper mapper;
    private static boolean pretty=false;
    public static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<Map<String, Object>>() {
    };
    public static final TypeReference<List<Map<String, Object>>> LIST_MAP_TYPE_REF = new TypeReference<List<Map<String, Object>>>() {
    };
    public static final TypeReference<List<CustomerScore>> LIST_CUSTOMER_REF = new TypeReference<List<CustomerScore>>() {
    };

    static{
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static String toJsonWithException(Object obj)throws IOException{
        if(pretty){
           return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        }
        return mapper.writeValueAsString(obj);
    }

    public static <T> T toObject(String json, Class<T> classz){
        JsonNode jsonNode = MissingNode.getInstance();

        try {
            jsonNode = mapper.readValue(json, JsonNode.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return toObject(jsonNode,classz);
    }

    public static <T> T toObject(JsonNode jsonNode, Class<T> classz) {
        try {
            return (T)mapper.readValue(jsonNode,classz);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	public static <T> T toObject(String json, TypeReference<T> valueTypeRef) {
        if(null != json){
            try {
                return (T)mapper.readValue(json, valueTypeRef);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public static <T> T toObject(JsonNode node, TypeReference<T> valueTypeRef){
        if(null !=node){
            try {
                return (T)mapper.readValue(node, valueTypeRef);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return null;
    }

    public static JsonNode toObject(String json){
        JsonNode jsonNode = MissingNode.getInstance();

        try {
            jsonNode = mapper.readValue(json, JsonNode.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return jsonNode;
    }

    public static String toJson(Object obj){
        return toJson(obj,pretty);
    }

    public static String toJson(Object obj,boolean pretty){
        String json=null;
        try {
            if(pretty){
                json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }else{
                json = mapper.writeValueAsString(obj);
            }
        } catch (IOException e) {
        }

        return json;
    }

    public static synchronized void setPretty(boolean isPretty){
        pretty = isPretty;
    }
}
