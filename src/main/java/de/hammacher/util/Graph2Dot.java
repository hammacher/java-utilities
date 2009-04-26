package de.hammacher.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import de.hammacher.util.Graph.EdgeLabelProvider;
import de.hammacher.util.Graph.Node;

public class Graph2Dot<NodeType extends Node<NodeType>> {

	private String graphName = "graph";
	private String nodeShape;
	private final Map<String, String> graphAttributes = new HashMap<String, String>(4);
	private EdgeLabelProvider<? super NodeType> edgeLabelProvider = null;

	public void setEdgeLabelProvider(EdgeLabelProvider<? super NodeType> edgeLabelProvider) {
		this.edgeLabelProvider = edgeLabelProvider;
	}

	public void export(Graph<? extends NodeType> graph, File dotFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(dotFile);
		try {
			export(graph, new PrintStream(fos));
		} finally {
			fos.close();
		}
	}

	public void export(Graph<? extends NodeType> graph, PrintStream out) {
		out.format("digraph %s {%n", quoteDotString(this.graphName));
		if (!this.graphAttributes.isEmpty()) {
			out.println();
			for (Map.Entry<String, String> e: this.graphAttributes.entrySet())
				out.format("  %s=%s%n", e.getKey(), quoteDotString(e.getValue()));
		}
		if (this.nodeShape == null)
			out.println();
		else
			out.format("%n  node [shape=%s]%n%n", quoteDotString(this.nodeShape));

		@SuppressWarnings("unchecked")
		NodeType[] nodes = (NodeType[]) graph.getNodes().toArray(new Node<?>[0]);

		Map<Node<? extends NodeType>, Integer> nodeNumbers =
			new HashMap<Node<? extends NodeType>, Integer>(nodes.length*4/3 + 1);
		for (Node<? extends NodeType> node : nodes) {
			int nr = nodeNumbers.size();
			Integer old = nodeNumbers.put(node, nr);
			if (old != null)
				throw new AssertionError("node appears more than one time in graph.getNodes()");
			out.format("  %d [label=%s]%n", nr, quoteDotString(node.getLabel()));
		}

		out.println();

		for (NodeType node : nodes) {
			Integer nr1 = nodeNumbers.get(node);
			for (NodeType succ: node.getSuccessors()) {
				Integer nr2 = nodeNumbers.get(succ);
				if (nr1 == null || nr2 == null)
					throw new AssertionError("successor not in graph nodes");
				String label = this.edgeLabelProvider == null ? null : this.edgeLabelProvider.getLabel(node, succ);
				if (label == null)
					out.format("  %d -> %d%n", nr1, nr2);
				else
					out.format("  %d -> %d [label=%s]%n", nr1, nr2, quoteDotString(label));
			}
		}

		out.format("%n}%n%n");
	}

	public void setGraphName(String newName) {
		this.graphName = newName;
	}

	public void setNodeShape(String newShape) {
		this.nodeShape = newShape;
	}

	/**
	 * Set a graph attribute
	 * @param key
	 * @param value
	 * @return the old value
	 */
	public String setGraphAttribute(String key, String value) {
		return this.graphAttributes.put(key, value);
	}

    /**
     * Quote the supplied string for output.
     *
     * @param input
     *            the string to be quoted
     * @return the input string, possibly enclosed in double quotes and with
     *         internal double quotes protected.
     */
    // essentially the agstrcanon function from libgraph (by S. C. North)
    public static String quoteDotString(String input) {
        int len;

        if (input == null || (len = input.length()) == 0) {
            return ("\"\"");
        }

        char[] buffer = new char[len+4];
        int bufPos = 1;
        boolean hasSpecial = false;
        boolean atStart = true;

        for (int isub = 0; isub < len; ++isub) {
        	char c = input.charAt(isub);
        	switch (c) {
        	case '"':
        	case '\\':
        		buffer = appendToBuffer(buffer, bufPos++, '\\');
                hasSpecial = true;
                atStart = false;
                break;

        	case '\r':
                continue;

        	case '\n':
        		if (!atStart) {
	        		buffer = appendToBuffer(buffer, bufPos++, '\\');
	        		buffer = appendToBuffer(buffer, bufPos++, 'n');
        		}
                continue;

            default:
            	if (atStart) {
            		if (Character.isWhitespace(c))
            			continue;
            		else
            			atStart = false;
            	}
            	if (!hasSpecial && !(Character.isLetter(c) || Character.isDigit(c)
            			|| Character.getType(c) == Character.LETTER_NUMBER))
                    hasSpecial = true;
	            break;
            }
    		buffer = appendToBuffer(buffer, bufPos++, c);
        }

        // trim from behind
        while (bufPos > 1 && Character.isWhitespace(buffer[bufPos-1]))
        	--bufPos;

        // if special characters are included and the string is NOT probably html,
        // then wrap it in double quotes
        if (!hasSpecial || (bufPos > 2 && buffer[1] == '<' && buffer[bufPos-1] == '>'))
        	return String.valueOf(buffer, 1, bufPos-1);

        buffer[0] = '"';
        buffer = appendToBuffer(buffer, bufPos++, '"');
        return String.valueOf(buffer, 0, bufPos);
    }

	private static char[] appendToBuffer(char[] buffer, int pos, char c) {
		assert pos >= 0 && pos <= buffer.length;
		if (pos == buffer.length) {
			char[] newBuffer = new char[buffer.length*3/2];
			System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
			return appendToBuffer(newBuffer, pos, c);
		}
		buffer[pos] = c;
		return buffer;
	}


}
