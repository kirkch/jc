package com.mosaic.ds.graph;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class AdjacencyListGraphTest extends GraphTestCases {


    public AdjacencyListGraphTest() {
        super(
            new AdjacencyListGraph<String,Map,Map>(HashMap.class,HashMap.class)
        );
    }

}
