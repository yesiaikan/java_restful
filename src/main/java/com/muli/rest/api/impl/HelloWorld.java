package com.muli.rest.api.impl;

import com.google.inject.Inject;
import com.muli.rest.api.AbstractServerResource;
import com.muli.rest.model.RetrievalResponse;
import org.apache.commons.configuration.Configuration;
import org.apache.http.HttpStatus;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by muli1 on 16/10/31.
 */
public class HelloWorld extends AbstractServerResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServerResource.class);

    @Inject
    public HelloWorld(Configuration configuration){
        this.configuration = configuration;
    }

    public Representation processGetRequest(Form form){
        RetrievalResponse.ResponseHeader responseHeader = retrievalResponse.getResponseHeader();
        Map<String, Object> content = retrievalResponse.getResponse();

        try {
            long freeMemory = Runtime.getRuntime().freeMemory();
            content.put("freeMemory", freeMemory);
            content.put("say", "hello world!");

            if (freeMemory < 20 * 1024 * 1024){
                responseHeader.status = HttpStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE;
                responseHeader.msg = "memory alarm:" + freeMemory;
                LOGGER.warn("memory alarm:{}", freeMemory);
            }

        }catch (Exception e){
            LOGGER.warn(e.toString(), e);
            responseHeader.status = Status.SERVER_ERROR_INTERNAL.getCode();
            responseHeader.msg = e.toString();
        }
        return retrievalResponse.buildJsonResponse(pretty);
    }

}
