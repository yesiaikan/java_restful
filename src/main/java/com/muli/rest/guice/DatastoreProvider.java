package com.muli.rest.guice;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.Mongo;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: wangweiwei
 * Date: 2/23/12
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */

public class DatastoreProvider implements Provider<Datastore> {
    private Configuration configuration;
    private Mongo mongo;
    private Morphia morphia;

    @Inject
    public DatastoreProvider(Configuration configuration, Mongo mongo, Morphia morphia) {
        this.configuration = configuration;
        this.mongo = mongo;
        this.morphia = morphia;
    }

    @Override
    public Datastore get() {
        if (configuration.containsKey("mongodb.username") && null != StringUtils.stripToNull(configuration.getString("mongodb.username"))) {
            return morphia.createDatastore(mongo,
                    configuration.getString("mongodb.db")
                    , configuration.getString("mongodb.username"),
                    configuration.getString("mongodb.password")
                            .toCharArray());
        }
        return morphia.createDatastore(mongo,
                configuration.getString("mongodb.db"));
    }
}
