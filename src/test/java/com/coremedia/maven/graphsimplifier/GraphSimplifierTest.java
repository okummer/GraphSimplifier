package com.coremedia.maven.graphsimplifier;

import com.coremedia.maven.graphsimplifier.GraphSimplifier;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

/**
 * Created by bthomsen on 11.12.2015.
 */
public class GraphSimplifierTest {

  @Test
  public void test2() throws Exception {
    Graph graph = new TinkerGraph();
    GraphMLReader graphMLReader = new GraphMLReader(graph);
    graphMLReader.inputGraph(GraphSimplifier.class.getResourceAsStream("test.graphml"));

    Set<Object> ids = GraphSimplifier.idsOfIndirectEdges(graph);
    Assert.assertEquals(Sets.newHashSet("e2"), ids);
  }

  @Test
  public void testNoEdges() throws Exception {
    Graph graph = new TinkerGraph();
    graph.addVertex("0");

    Set<Object> ids = GraphSimplifier.idsOfIndirectEdges(graph);
    Assert.assertEquals(Collections.emptySet(), ids);
  }

  @Test
  public void testOneEdge() throws Exception {
    Graph graph = new TinkerGraph();
    final Vertex vertex0 = graph.addVertex("0");
    final Vertex vertex1 = graph.addVertex("1");
    graph.addEdge("a", vertex0, vertex1, "a");

    Set<Object> ids = GraphSimplifier.idsOfIndirectEdges(graph);
    Assert.assertEquals(Collections.emptySet(), ids);
  }

  @Test
  public void testTwoEdges() throws Exception {
    Graph graph = new TinkerGraph();
    final Vertex vertex0 = graph.addVertex("0");
    final Vertex vertex1 = graph.addVertex("1");
    final Vertex vertex2 = graph.addVertex("2");
    graph.addEdge("a", vertex0, vertex1, "a");
    graph.addEdge("b", vertex1, vertex2, "b");

    Set<Object> ids = GraphSimplifier.idsOfIndirectEdges(graph);
    Assert.assertEquals(Collections.emptySet(), ids);
  }

  @Test
  public void testRemoveOneTransitive() throws Exception {
    Graph graph = new TinkerGraph();
    final Vertex vertex0 = graph.addVertex("0");
    final Vertex vertex1 = graph.addVertex("1");
    final Vertex vertex2 = graph.addVertex("2");
    graph.addEdge("a", vertex0, vertex1, "a");
    graph.addEdge("b", vertex1, vertex2, "b");
    graph.addEdge("c", vertex0, vertex2, "c");

    Set<Object> ids = GraphSimplifier.idsOfIndirectEdges(graph);
    Assert.assertEquals(Sets.newHashSet("c"), ids);
  }

  @Test
  public void testRemoveLongTransitive() throws Exception {
    Graph graph = new TinkerGraph();
    final Vertex vertex0 = graph.addVertex("0");
    final Vertex vertex1 = graph.addVertex("1");
    final Vertex vertex2 = graph.addVertex("2");
    final Vertex vertex3 = graph.addVertex("3");
    final Vertex vertex4 = graph.addVertex("4");
    graph.addEdge("a", vertex0, vertex1, "a");
    graph.addEdge("b", vertex1, vertex2, "b");
    graph.addEdge("c", vertex2, vertex3, "c");
    graph.addEdge("d", vertex3, vertex4, "d");
    graph.addEdge("e", vertex0, vertex4, "e");

    Set<Object> ids = GraphSimplifier.idsOfIndirectEdges(graph);
    Assert.assertEquals(Sets.newHashSet("e"), ids);
  }


}