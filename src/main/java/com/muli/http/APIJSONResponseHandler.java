package com.muli.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;

/**
 * Despt: 支持httpclient 4.0以上版本；
 * Author: shijunbo
 */
public class APIJSONResponseHandler implements ResponseHandler<JsonNode> {
    public static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public JsonNode handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }

        JsonNode contentNode = null;
        int errcode = 400;
        try {
            if (null != entity) {
                String respContent = EntityUtils.toString(entity);
                if (null != respContent) {
                    try {
                        return mapper.readTree(respContent);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            throw new HttpResponseException(errcode, e.getMessage());
        } finally {
            EntityUtils.consume(entity);
        }
        return contentNode;
    }
}
