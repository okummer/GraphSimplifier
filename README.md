# GraphSimplifier
A tool for simplifying Maven dependency graphs.

# Usage

An existing graph definition is read, simplified and written to another file.
The input must exist.
The output file is overwritten if it exists.

    java -jar GraphSimplifier.jar <infile> <outfile>

The input file is generated with the following command,
which generates a .graphml file in the target directory:

    mvn com.github.janssk1:maven-dependencygraph-plugin:1.2:graph

Having generated a simplified dependency graph,
you can either layout the graph manually or run an auto-layout program.