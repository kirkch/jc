package com.mosaic.ds.graph;

import com.mosaic.jc.utils.Function0;
import com.mosaic.jc.utils.Function1;
import com.mosaic.jc.utils.ListUtils;
import com.mosaic.jc.utils.VoidFunction1;
import com.mosaic.jc.utils.reflection.ClassFactory;

import java.util.*;

/**
 * A compact graph representation.  It stores a list of a nodes edges within
 * a hash map keyed by vertex.
 */
@SuppressWarnings("unchecked")
public class AdjacencyListGraph<VID,V,E> implements Graph<VID,V,E> {

    private Function0<V> blankVertexDataFactory;
    private Function0<E> blankEdgeDataFactory;


    private Map<VID,List<Edge>> outgoingEdgesMap = new HashMap<VID,List<Edge>>();
    private Map<VID,Set<VID>>   incomingVertexMap = new HashMap<VID,Set<VID>>();

    private Map<VID,V> vertexDataMap = new HashMap<VID, V>();


    public AdjacencyListGraph( Class<? extends V> vertexDataType, Class<? extends E> edgeDataType ) {
        this(new ClassFactory(vertexDataType), new ClassFactory(edgeDataType));
    }

    public AdjacencyListGraph( Function0<V> blankVertexDataFactory, Function0<E> blankEdgeDataFactory ) {
        this.blankVertexDataFactory = blankVertexDataFactory;
        this.blankEdgeDataFactory = blankEdgeDataFactory;
    }

    @Override
    public void touchVertex(VID vertexId) {
        if ( !vertexDataMap.containsKey(vertexId) ) {
            V vertexData = blankVertexDataFactory.invoke();

            vertexDataMap.put( vertexId, vertexData );
            outgoingEdgesMap.put(vertexId, new ArrayList<Edge>());
            incomingVertexMap.put(vertexId, new HashSet<VID>());
        }
    }

    @Override
    public V getVertexData( VID vertexId ) {
        return vertexDataMap.get(vertexId);
    }

    @Override
    public void setVertexData(VID vertexId, V vertexData) {
        if ( vertexData == null ) {
            vertexData = blankVertexDataFactory.invoke();
        }

        vertexDataMap.put( vertexId, vertexData );
    }

    @Override
    public void removeVertex( final VID vertexId ) {
        vertexDataMap.remove(vertexId);
        outgoingEdgesMap.remove(vertexId);

        ListUtils.forEach( incomingVertexMap.get(vertexId), new VoidFunction1<VID>() {
            public void invoke( VID incomingVID ) {
                List<Edge> edges = outgoingEdgesMap.get(incomingVID);

                ListUtils.removeFirstMatch( edges, new Function1<Edge,Boolean>() {
                    public Boolean invoke(Edge e) {
                        return Objects.equals(vertexId, e.destinationVertexId);
                    }
                });
            }
        });
    }

    @Override
    public List<VID> getOutgoingEdges(VID vertexId) {
        List<Edge> edges = outgoingEdgesMap.get(vertexId);
        if ( edges == null ) {
            return null;
        }

        return ListUtils.map(edges, new Function1<Edge,VID>() {
            public VID invoke( Edge e ) {
                return (VID) e.destinationVertexId;
            }
        });
    }

    @Override
    public Set<VID> getIncomingEdges(VID vertexId) {
        Set<VID> vertexIds = incomingVertexMap.get(vertexId);

        return vertexIds == null ? null : Collections.unmodifiableSet(vertexIds);
    }

    @Override
    public void touchEdge( final VID sourceVertexId, final VID destinationVertexId ) {
        touchVertex( sourceVertexId );
        touchVertex( destinationVertexId );

        List<Edge> edges = outgoingEdgesMap.get(sourceVertexId);

        Edge matchingEdge = ListUtils.selectFirstMatch(edges, new Function1<Edge, Boolean>() {
            public Boolean invoke(Edge e) {
                return Objects.equals(destinationVertexId, e.destinationVertexId);
            }
        });

        if ( matchingEdge == null ) {
            Edge newEdge = new Edge(destinationVertexId, blankEdgeDataFactory.invoke());
            edges.add( newEdge );

            incomingVertexMap.get(destinationVertexId).add(sourceVertexId);
        }
    }

    @Override
    public E getEdgeData( final VID sourceVertexId, final VID destinationVertexId ) {
        Edge matchingEdge = getEdgeFor(sourceVertexId, destinationVertexId);

        if ( matchingEdge == null ) {
            return null;
        }

        return (E) matchingEdge.edgeData;
    }

    @Override
    public void setEdgeData( VID sourceVertexId, VID destinationVertexId, E newEdgeData ) {
        touchEdge( sourceVertexId, destinationVertexId );

        if ( newEdgeData == null ) {
            newEdgeData = blankEdgeDataFactory.invoke();
        }

        Edge edge = getEdgeFor( sourceVertexId, destinationVertexId );
        edge.edgeData = newEdgeData;
    }

    @Override
    public void removeEdge( final VID sourceVertexId, final VID destinationVertexId ) {
        List<Edge> outgoingEdges = outgoingEdgesMap.get(sourceVertexId);

        boolean entryRemovedFlag = ListUtils.removeAll(outgoingEdges, new Function1<Edge, Boolean>() {
            public Boolean invoke( Edge e ) {
                return Objects.equals(destinationVertexId,e.destinationVertexId);
            }
        });

        if ( entryRemovedFlag ) {
            incomingVertexMap.get(destinationVertexId).remove(sourceVertexId);
        }
    }

    private Edge getEdgeFor(VID sourceVertexId, final VID destinationVertexId) {
        List<Edge> edges = outgoingEdgesMap.get(sourceVertexId);

        return ListUtils.selectFirstMatch(edges, new Function1<Edge, Boolean>() {
            public Boolean invoke(Edge e) {
                return Objects.equals(destinationVertexId, e.destinationVertexId);
            }
        });
    }

    private static class Edge {
        public Object destinationVertexId;
        public Object edgeData;

        public Edge(Object destinationVertexId, Object edgeData) {
            this.destinationVertexId = destinationVertexId;
            this.edgeData = edgeData;
        }
    }

}
