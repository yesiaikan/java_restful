package com.muli.rest.model;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.restlet.data.MediaType;
import org.restlet.data.Tag;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: weiwei
 * Date: 11-8-31
 * Time: 下午7:17
 * To change this template use File | Settings | File Templates.
 */
public class RetrievalResponse {
    private static final ObjectMapper RESPONSE_OBJECT_MAPPTER = new ObjectMapper();
    private static final ObjectWriter RESPONSE_OBJECT_WRITER = RESPONSE_OBJECT_MAPPTER.writerWithDefaultPrettyPrinter();

    static {
        RESPONSE_OBJECT_MAPPTER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        RESPONSE_OBJECT_MAPPTER.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

//    public static enum Status {ok, not_found, wrong_input, fail, retry, deny}


    /**
     * {
     * "result_info":{"status":"ok/wrong_input/fail/retry/deny","errcode":XXX,"message":"纯文本描述","desc":"html描述"},
     * "response":{返回内容},
     * }
     */
    public static class ResponseHeader {
        //        public Status status = Status.ok;
        public int status = 200;
        public long time = System.currentTimeMillis();
        public String msg = null;
        public String version = null;

        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

    }

    private Map<String, Object> response = new HashMap<String, Object>();

    private ResponseHeader responseHeader = new ResponseHeader();
    private Date lastModifyDate;
    private String etag;

    public void setEtag(String etag){
        this.etag = etag;
    }

    public Date getLastModifyDate() {
        if (null == lastModifyDate) return null;
        return (Date) lastModifyDate.clone();
    }

    public void setLastModifyDate(Date lastModifyDate) {
        if (null != lastModifyDate) {
            this.lastModifyDate = new Date(lastModifyDate.getTime());
        } else {
            this.lastModifyDate = null;
        }
    }

    public Map<String, Object> getResponse() {
        return response;
    }

    public void setResponse(Map<String, Object> response) {
        this.response = response;
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Representation buildJsonResponse(boolean pretty) {
        return buildJsonResponse(null, pretty);
    }

    public Representation buildJsonResponse(Object _response, boolean pretty) {
        try {
            Map<String, Object> result = new HashMap<String, Object>();
            String data = null;
            result.put("responseHeader", this.responseHeader);
            if (_response != null){
                result.put("response", _response);
                data = RESPONSE_OBJECT_MAPPTER.writeValueAsString(_response);
            }else{
                result.put("response", this.response);
                data = RESPONSE_OBJECT_MAPPTER.writeValueAsString(this.response);
            }

            String json = null;
            if (pretty) {
                json = RESPONSE_OBJECT_WRITER.withDefaultPrettyPrinter().writeValueAsString(result);
            } else {
                json = RESPONSE_OBJECT_MAPPTER.writeValueAsString(result);
            }
//            if (logger.isDebugEnabled()) {
//                logger.debug("[data]{}", json);
//            }
            Representation representation = new StringRepresentation(json, MediaType.APPLICATION_JSON);
            String etag = DigestUtils.md5Hex(data);
            if ( null == this.etag  ||  !this.etag.contains(etag) ){
                lastModifyDate = new Date();
            }

            if ( null == this.lastModifyDate ){
                lastModifyDate = new Date();
            }

            representation.setTag(new Tag(etag));
            representation.setModificationDate(lastModifyDate);
            return representation;
        } catch (IOException e) {
            return new StringRepresentation(e.toString(), MediaType.TEXT_HTML);
        }
    }

}
