package com.coremedia.maven.graphsimplifier;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GraphSimplifier {
  public static void main(String[] args) throws IOException, TransformerException {
    checkArguments(args);
    File inputFile = new File(args[0]);
    checkInputFile(inputFile);
    File outputFile = new File(args[1]);

    Graph graph = readGraph(inputFile);

    Set<Object> idsOfIndirectEdges = idsOfIndirectEdges(graph);

    Document document = readGraphDom(inputFile);
    reduceGraphDom(document, idsOfIndirectEdges);
    writeDom(outputFile, document);
  }

  private static void checkArguments(String[] args) {
    if (args.length != 2) {
      System.out.println("Usage: java -jar GraphSimplifier.jar <infile> <outfile>");
      System.out.println();
      System.out.println("The input file is generated with the command:");
      System.out.println("  mvn dependency:tree -DoutputType=graphml -DoutputFile=<infile>");
      System.exit(1);
    }
  }

  private static void checkInputFile(File inputFile) {
    if (!inputFile.exists()) {
      System.out.println("Input file " + inputFile + "does not exist. Exiting.");
      System.exit(2);
    }
  }

  private static Graph readGraph(File inputFile) throws IOException {
    Graph graph = new TinkerGraph();
    GraphMLReader graphMLReader = new GraphMLReader(graph);
    FileInputStream inputStream = new FileInputStream(inputFile);
    try {
      graphMLReader.inputGraph(inputStream);
    } finally {
      inputStream.close();
    }
    return graph;
  }

  private static Document readGraphDom(File inputFile) throws TransformerException {
    DOMResult domResult = new DOMResult();
    TransformerFactory.newInstance().newTransformer().transform(new StreamSource(inputFile), domResult);
    return (Document) domResult.getNode();
  }

  private static void writeDom(File outputFile, Document document) throws TransformerException {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.transform(new DOMSource(document), new StreamResult(outputFile));
  }

  private static void reduceGraphDom(Document document, Set<Object> idsOfIndirectEdges) {
    NodeList edgeNodes = document.getElementsByTagName("edge");
    Collection<Element> toRemove = new ArrayList<Element>();
    for (int i = 0; i < edgeNodes.getLength(); i++) {
      Element edgeElement = (Element) edgeNodes.item(i);
      String id = edgeElement.getAttribute("id");
      if (idsOfIndirectEdges.contains(id)) {
        toRemove.add(edgeElement);
      }
    }
    for (Element edgeElement : toRemove) {
      edgeElement.getParentNode().removeChild(edgeElement);
    }
  }

  public static Set<Object> idsOfIndirectEdges(Graph graph) {
    Set<Object> result = new HashSet<Object>();
    for (Vertex vertex : graph.getVertices()) {
      idsOfIndirectEdges(vertex, result);
    }
    return result;
  }

  private static void idsOfIndirectEdges(Vertex vertex, Collection<Object> removableEdges) {
    Set<Vertex> indirectlyReachables = new HashSet<Vertex>();
    indirectlyReachableVertices(vertex, indirectlyReachables, 0, new HashSet<Vertex>());

    for (Edge edge : vertex.getEdges(Direction.IN)) {
      final Vertex predecessor = edge.getVertex(Direction.OUT);
      if (indirectlyReachables.contains(predecessor)) {
        removableEdges.add(edge.getId());
      }
    }
  }

  private static void indirectlyReachableVertices(Vertex current, Set<Vertex> indirectlyReachables, int depth, Set<Vertex> visited) {
    if (depth >= 2) {
      indirectlyReachables.add(current);
    }

    if (visited.add(current)) {
      for (Edge edge : current.getEdges(Direction.IN)) {
        final Vertex predecessor = edge.getVertex(Direction.OUT);

        indirectlyReachableVertices(predecessor, indirectlyReachables, depth + 1, visited);
      }
    }
  }
}
