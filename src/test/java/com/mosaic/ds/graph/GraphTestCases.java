package com.mosaic.ds.graph;

import com.mosaic.jc.utils.MapUtils;
import com.mosaic.jc.utils.SetUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class GraphTestCases {

    private final Graph<String,Map,Map> graph;

    protected GraphTestCases( Graph<String,Map,Map> g ) {
        this.graph = g;
    }


    @Test
    public void givenEmptyGraph_getNodeDataOfNonExistentNode_expectNull() {
        assertNull( graph.getVertexData("v1") );
    }

    @Test
    public void givenSingleNodeGraph_getDataOfNonExistentNode_expectNull() {
        graph.touchVertex( "v1" );

        assertNull(graph.getVertexData("v2"));
    }

    @Test
    public void givenSingleNodeGraph_getDataOfExistentNode_expectEmptyMap() {
        graph.touchVertex( "v1" );

        assertEquals(0, graph.getVertexData("v1").size());
    }

    @Test
    public void givenSingleNodeWithDataGraph_getDataOfNonExistentNode_expectNull() {
        graph.setVertexData("v1", MapUtils.toMap("a", 1, "b", 2));

        assertNull(graph.getVertexData("v2"));
    }

    @Test
    public void givenSingleNodeWithDataGraph_getDataOfExistentNode_expectData() {
        graph.setVertexData("v1", MapUtils.toMap("a", 1, "b", 2));

        assertEquals(2, graph.getVertexData("v1").size());
    }

    @Test
    public void givenSingleNodeGraph_getDataOfExistentNodeAndModifyIt_expectChangesToBeVisibleFromTheNextCallToGetVertexData() {
        graph.setVertexData("v1", MapUtils.toMap("a", 1, "b", 2));

        graph.getVertexData("v1").put("c", 3);

        assertEquals(3, graph.getVertexData("v1").size());
    }

    @Test
    public void givenNodeWithData_touchNode_expectNoChange() {
        graph.setVertexData("v1", MapUtils.toMap("a", 1, "b", 2));

        graph.touchVertex("v1");

        assertEquals(2, graph.getVertexData("v1").size());
    }

    @Test
    public void givenNodeWithData_removeNodeThenRetrieveData_expectNull() {
        graph.setVertexData("v1", MapUtils.toMap("a", 1, "b", 2));

        graph.removeVertex("v1");

        assertNull(graph.getVertexData("v1"));
    }

    @Test
    public void givenNodeWithData_setNodeDataToNullThenRetrieveData_expectEmptyMap() {
        graph.setVertexData("v1", MapUtils.toMap("a", 1, "b", 2));

        graph.setVertexData("v1", null);

        assertEquals(0, graph.getVertexData("v1").size());
    }

    @Test
    public void givenEmptyNode_removeNonExistentNodeThenRetrieveData_expectNull() {
        graph.removeVertex("v1");

        assertNull(graph.getVertexData("v1"));
    }

    @Test
    public void givenEmptyGraph_fetchEdgesForNonExistentNode_expectNull() {
        List<String> edges = graph.getOutgoingEdges("v1");

        assertNull(edges);
    }

    @Test
    public void givenOneNodeGraph_fetchEdgesForNonExistentNode_expectNull() {
        graph.touchVertex("v1");

        List<String> edges = graph.getOutgoingEdges("v2");

        assertNull(edges);
    }

    @Test
    public void givenOneNodeGraph_fetchEdgesForExistentNode_expectEmptyList() {
        graph.touchVertex("v1");

        List<String> edges = graph.getOutgoingEdges("v1");

        assertEquals(0, edges.size());
    }

    @Test
    public void givenTwoTouchedNodes_expectNeitherNodeToHaveAnEdge() {
        graph.touchVertex("v1");
        graph.touchVertex("v2");

        assertEquals(0, graph.getOutgoingEdges("v1").size());
        assertEquals(0, graph.getOutgoingEdges("v2").size());
    }

    @Test
    public void givenTwoConnectedNodeGraph_fetchEdges_expectEdge() {
        graph.touchEdge("v1", "v2");

        assertEquals(Arrays.asList("v2"), graph.getOutgoingEdges("v1"));
        assertEquals(0, graph.getOutgoingEdges("v2").size());
    }

    @Test
    public void touchOneEdgeWithoutDeclaringNodes_expectBothNodesToHaveBlankEdgeData() {
        graph.touchEdge("v1", "v2");

        assertEquals(0, graph.getEdgeData("v1","v2").size());
        assertNull(graph.getEdgeData("v2", "v1"));
    }

    @Test
    public void givenEmptyGraph_setDataOnNonExistentEdge_expectNodesAndEdgeToBeCreatedAndRetrievable() {
        graph.setEdgeData("v1", "v2", MapUtils.toMap("a", 1, "b", 2));

        assertEquals(MapUtils.toMap("a",1, "b",2), graph.getEdgeData("v1", "v2"));
        assertNull(graph.getEdgeData("v2", "v1"));
    }

    @Test
    public void givenTwoNodesConnectedByEdgeWithData_touchEdge_expectTheEdgeDataToNotBeTouched() {
        graph.touchEdge("v1", "v2");

        assertEquals(0, graph.getEdgeData("v1","v2").size());
        assertNull(graph.getEdgeData("v2", "v1"));
    }

    @Test
    public void givenTwoNodesConnectedByEdgeWithData_touchEdge_expectTheNumberOfEdgesInTheGraphToBeUnmodified() {
        graph.touchEdge("v1", "v2");

        assertEquals(0, graph.getEdgeData("v1","v2").size());
        assertNull(graph.getEdgeData("v2", "v1"));
    }

    @Test
    public void givenThreeNodeGraph_connectOneNodeToBothOthersAndRetrieveEdges_expectBothOtherNodesFromCentralNode() {
        graph.touchEdge("v1", "v2");
        graph.touchEdge("v1", "v3");

        assertEquals(Arrays.asList("v2", "v3"), graph.getOutgoingEdges("v1"));
    }

    @Test
    public void givenTwoNodesWithDataOnEdge_touchEdge_expectEdgeDataToBeUnchanged() {
        graph.setEdgeData("v1", "v2", MapUtils.toMap("a", 1, "b", 2));

        graph.touchEdge("v1", "v2");

        assertEquals(MapUtils.toMap("a",1, "b",2), graph.getEdgeData("v1","v2"));
        assertEquals(Arrays.asList("v2"), graph.getOutgoingEdges("v1"));
        assertEquals(Arrays.<String>asList(), graph.getOutgoingEdges("v2"));
        assertNull(graph.getEdgeData("v2", "v1"));
    }

    @Test
    public void givenThreeNodeGraph_connectOneNodeToBothOthersAndRetrieveEdgesOfLeafNode_expectEmptyListAsEdgesAreUnidirectional() {
        graph.touchEdge("v1", "v2");
        graph.touchEdge("v1", "v3");

        assertEquals(Arrays.<String>asList(), graph.getOutgoingEdges("v2"));
        assertEquals(Arrays.<String>asList(), graph.getOutgoingEdges("v3"));
    }

    @Test
    public void givenTwoNodesConnectedBiDirectionally_fetchEdgesForBothNodes_expectBothToReturnTheOtherNode() {
        graph.touchEdge("v1", "v2");
        graph.touchEdge("v2", "v1");

        assertEquals(Arrays.asList("v2"), graph.getOutgoingEdges("v1"));
        assertEquals(Arrays.asList("v1"), graph.getOutgoingEdges("v2"));
    }

    @Test
    public void givenTwoNodesConnectedBiDirectionally_removeANode_expectOtherNodeToHaveNoEdges() {
        graph.touchEdge("v1", "v2");
        graph.touchEdge("v2", "v1");

        graph.removeVertex("v1");

        assertEquals(0, graph.getOutgoingEdges("v2").size());
    }

    @Test
    public void givenTwoNodesConnectedBiDirectionally_removeANode_expectRemovedNodeToHaveNullEdges() {
        graph.touchEdge("v1", "v2");
        graph.touchEdge("v2", "v1");

        graph.removeVertex("v1");

        assertNull(graph.getOutgoingEdges("v1"));
    }

    @Test
    public void givenTwoNodesConnectedBiDirectionally_removeOneEdge_expectOtherNodeToHaveSameListOfEdges() {
        graph.touchEdge("v1", "v2");
        graph.touchEdge("v2", "v1");

        graph.removeEdge("v1", "v2");

        assertEquals(Arrays.asList("v1"), graph.getOutgoingEdges("v2"));
    }

    @Test
    public void givenTwoNodesConnectedBiDirectionally_removeOneEdge_expectModifiedNodeToNoEdges() {
        graph.touchEdge("v1", "v2");
        graph.touchEdge("v2", "v1");

        graph.removeEdge("v1", "v2");

        assertEquals(0, graph.getOutgoingEdges("v1").size());
    }

    @Test
    public void givenTwoNodesConnectedBiDirectionally_removeOneEdge_expectModifiedNodeDataToReturnNull() {
        graph.setEdgeData("v1", "v2", MapUtils.toMap("a", 1));
        graph.touchEdge("v2", "v1");

        graph.removeEdge("v1", "v2");

        assertNull(graph.getEdgeData("v1", "v2"));
    }

    @Test
    public void givenOneNodeWithEdgesToTwoOtherNodes_removeCentralNode_expectOtherNodesToHaveNoEdges() {
        graph.touchEdge("v1", "v2");
        graph.touchEdge("v1", "v3");

        graph.removeVertex("v1");

        assertEquals(0, graph.getOutgoingEdges("v2").size());
        assertEquals(0, graph.getOutgoingEdges("v3").size());
    }

    @Test
    public void givenThreeNodesWhereEachNodeIsConnectedToEveryOtherNode_removeOneNode_expectRemovedNodeToHaveNullEdges() {
        graph.touchEdge("v1", "v2");
        graph.touchEdge("v2", "v1");

        graph.touchEdge("v1", "v3");
        graph.touchEdge("v3", "v1");

        graph.touchEdge("v2", "v3");
        graph.touchEdge("v3", "v2");

        graph.removeVertex("v1");

        assertNull(graph.getOutgoingEdges("v1"));
    }

    @Test
    public void givenThreeNodesWhereEachNodeIsConnectedToEveryOtherNode_removeOneNode_expectBothRemainingNodesToStillBeConnectedToEachOther() {
        graph.touchEdge("v1", "v2");
        graph.touchEdge("v2", "v1");

        graph.touchEdge("v1", "v3");
        graph.touchEdge("v3", "v1");

        graph.touchEdge("v2", "v3");
        graph.touchEdge("v3", "v2");


        graph.removeVertex("v1");

        assertEquals(Arrays.asList("v3"), graph.getOutgoingEdges("v2"));
        assertEquals(Arrays.asList("v2"), graph.getOutgoingEdges("v3"));
    }

    @Test
    public void givenThreeNodesWhereEachNodeIsConnectedToEveryOtherNode_removeOneNode_expectNullDataFromRemovedNode() {
        graph.touchEdge("v1", "v2");
        graph.touchEdge("v2", "v1");

        graph.touchEdge("v1", "v3");
        graph.touchEdge("v3", "v1");

        graph.touchEdge("v2", "v3");
        graph.touchEdge("v3", "v2");


        graph.removeVertex("v1");

        assertNull(graph.getVertexData("v1"));
    }

    @Test
    public void givenThreeNodesWhereEachNodeIsConnectedToEveryOtherNode_removeOneNode_expectDataToRemainUnmodifiedOnRemainingNodes() {
        graph.touchEdge("v1", "v2");
        graph.touchEdge("v2", "v1");

        graph.touchEdge("v1", "v3");
        graph.touchEdge("v3", "v1");

        graph.touchEdge("v2", "v3");
        graph.touchEdge("v3", "v2");

        graph.setVertexData("v2", MapUtils.toMap("b", 2));
        graph.setVertexData("v3", MapUtils.toMap("c",3));

        graph.removeVertex("v1");

        assertEquals(MapUtils.toMap("b",2), graph.getVertexData("v2"));
        assertEquals(MapUtils.toMap("c", 3), graph.getVertexData("v3"));
    }

    @Test
    public void givenTwoNodesWithOneEdgeThatHasData_removeEdge_expectAttemptToRetrieveEdgesDataToReturnNull() {
        graph.setEdgeData("v1", "v2", MapUtils.toMap("b", 2));

        graph.removeEdge("v1", "v2");

        assertNull(graph.getEdgeData("v1", "v2"));
    }

    @Test
    public void givenTwoNodesWithOneEdgeThatHasData_setEdgeDataToNull_expectAttemptToRetrieveEdgesDataToBeBlank() {
        graph.setEdgeData("v1", "v2", MapUtils.toMap("b", 2));

        graph.setEdgeData("v1", "v2", null);

        assertEquals(0, graph.getEdgeData("v1", "v2").size());
    }

    @Test
    public void givenTwoNodesWithOneEdgeThatHasData_setEdgeDataToNewValue_expectAttemptToRetrieveEdgesDataToReturnNewData() {
        graph.setEdgeData("v1", "v2", MapUtils.toMap("b", 2));

        graph.setEdgeData("v1", "v2", MapUtils.toMap("a", 1));

        assertEquals(MapUtils.toMap("a", 1), graph.getEdgeData("v1", "v2"));
    }

    @Test
    public void givenTwoNodesWithOneEdgeThatHasData_getEdgeDataAndModify_expectNextRetrievalOfDataToHaveBeenModified() {
        graph.setEdgeData("v1", "v2", MapUtils.toMap("b", 2));

        Map edgeData = graph.getEdgeData("v1", "v2");
        edgeData.put("c", 3);

        assertEquals(MapUtils.toMap("b", 2, "c", 3), graph.getEdgeData("v1", "v2"));
    }

    @Test
    public void givenEmptyGraph_getIncomingEdgesForNonExistentNode_expectNull() {
        assertNull(graph.getIncomingEdges("v1"));
    }

    @Test
    public void givenSingleNodeGraph_getIncomingEdges_expectEmptySet() {
        graph.touchVertex("v1");

        assertEquals(0, graph.getIncomingEdges("v1").size());
    }

    @Test
    public void givenConnectedNodeGraph_getIncomingEdgesOfSourceNode_expectEmptySet() {
        graph.touchEdge("v1", "v2");

        assertEquals(0, graph.getIncomingEdges("v1").size());
    }

    @Test
    public void givenConnectedNodeGraph_getIncomingEdgesOfDestinationNode_expectSourceNode() {
        graph.touchEdge("v1", "v2");

        assertEquals(SetUtils.asSet("v1"), graph.getIncomingEdges("v2"));
    }

    @Test
    public void givenConnectedNodeGraph_removeEdgeAndRetrieveIncomingEdgesOfDestination_expectEmptySet() {
        graph.touchEdge("v1", "v2");

        graph.removeEdge("v1", "v2");

        assertEquals(0, graph.getIncomingEdges("v2").size());
    }

    @Test
    public void givenConnectedNodeGraph_getIncomingEdgesOfDestinationNodeAndTryModifyingIt_expectException() {
        graph.touchEdge("v1", "v2");

        try {
            graph.getIncomingEdges("v2").add("v3");
            fail( "expected exception" );
        } catch ( UnsupportedOperationException e ) {

        }
    }

}
