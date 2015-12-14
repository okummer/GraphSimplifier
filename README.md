# GraphSimplifier
A tool for simplifying Maven dependency graphs.

# Usage

An existing graph definition is read, simplified and written to another file. The input must exist. The output file is overwritten if it exists.

    java -jar GraphSimplifier.jar <infile> <outfile>

The input file is generated with the following command:

    mvn dependency:tree -DoutputType=graphml -DoutputFile=<infile>
