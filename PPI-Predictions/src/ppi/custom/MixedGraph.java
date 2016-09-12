package ppi.custom;

import org.jgrapht.*;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.builder.*;


/**
 * A modified simple graph, this graph allows self-loops. A simple graph is an undirected graph for which at most one
 * edge connects any two vertices, and loops are not permitted. If you're unsure
 * about simple graphs, see: <a
 * href="http://mathworld.wolfram.com/SimpleGraph.html">
 * http://mathworld.wolfram.com/SimpleGraph.html</a>.
 */
public class MixedGraph<V, E>
    extends AbstractBaseGraph<V, E>
    implements UndirectedGraph<V, E>
{
    private static final long serialVersionUID = 3545796589454112304L;

    /**
     * Creates a new simple graph with the specified edge factory.
     *
     * @param ef the edge factory of the new graph.
     */
    public MixedGraph(EdgeFactory<V, E> ef)
    {
        super(ef, false, true); // @author JRW changed allow loops to true 9/1/2016
    }

    /**
     * Creates a new simple graph.
     *
     * @param edgeClass class on which to base factory for edges
     */
    public MixedGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<V, E>(edgeClass));
    }

    public static <V, E> UndirectedGraphBuilderBase<V,
        E, ? extends MixedGraph<V, E>, ?> builder(Class<? extends E> edgeClass)
    {
        return new UndirectedGraphBuilder<V, E, MixedGraph<V, E>>(
            new MixedGraph<V, E>(edgeClass));
    }

    public static <V, E> UndirectedGraphBuilderBase<V,
        E, ? extends MixedGraph<V, E>, ?> builder(EdgeFactory<V, E> ef)
    {
        return new UndirectedGraphBuilder<V, E, MixedGraph<V, E>>(
            new MixedGraph<V, E>(ef));
    }
}

// End SimpleGraph.java
