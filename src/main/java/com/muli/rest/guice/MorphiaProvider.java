package com.muli.rest.guice;

import com.google.code.morphia.Morphia;
import com.google.inject.Provider;

/**
 * Created by IntelliJ IDEA.
 * User: wangweiwei
 * Date: 2/23/12
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */

public class MorphiaProvider implements Provider<Morphia> {

    @Override
    public Morphia get() {
       return new Morphia();
    }
}
