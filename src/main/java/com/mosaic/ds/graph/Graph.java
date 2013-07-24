package com.mosaic.ds.graph;


import java.util.List;
import java.util.Set;

/**
 * Represents a graph of nodes connected by uni-directional edges.  Data may be
 * stored on either the node (vertex) or edge.
 */
public interface Graph<VID,V,E> {

    /**
     * Creates an empty node within the graph.
     */
    public void touchVertex( VID vertexId );

    /**
     * Fetches the data associated with a node. Will return null if the
     * vertex does not exist.
     */
    public V getVertexData( VID vertexId );

    /**
     * Set the vertex data.
     */
    public void setVertexData(VID vertexId, V vertexData);

    /**
     * Remove the specified vertex.  Any incoming or outgoing edges will be dropped.
     */
    public void removeVertex( VID vertexId );

    /**
     * Retrieves the vertex ids of the nodes that are at the other end of outgoing
     * connections from the specified starting node.
     */
    public List<VID> getOutgoingEdges(VID vertexId);

    /**
     * Retrieves the vertex ids of the nodes that are at the other end of incoming
     * connections from the specified destination node.
     */
    public Set<VID> getIncomingEdges(VID vertexId);

    /**
     * Declares an edge between the two specified vertices.  If the vertex has not
     * been declared yet, the node will be implicitly 'touched'; that is created
     * with blank data.
     */
    public void touchEdge( VID sourceVertexId, VID destinationVertexId );

    /**
     * Retrieves the data stored on the edge connecting source and destination nodes.
     * Returns null if the nodes are not connected.
     */
    public E getEdgeData( VID sourceVertexId, VID destinationVertexId );

    /**
     * Specifies the data for an edge.  If the edge or nodes do not exist then
     * then will be created.  If the edge data is null then blank data will be
     * written for the edge.
     */
    public void setEdgeData( VID sourceVertexId, VID destinationVertexId, E newEdgeData );

    /**
     * Remove the specified edge.
     */
    public void removeEdge( VID sourceVertexId, VID destinationVertexId );

}
