/*
 */

import java.io.*;
import java.awt.*;
import javax.swing.*;
import trubgp.*;  // TRU Board Games Playground package

@SuppressWarnings("unchecked")
public class DijkstraSearch {
    static Board board;
    static int SIZE = 20;
    static Graph graph;
    static NodePriorityQueue<VertexString> nodes[];


// Game board

    public static void main(String[] args) {
        // Create a game board
        create();
    }


    // Create a new board

    static void create() {
        // Construct a new board

        board = new Board(SIZE, SIZE, 20 * SIZE, 20 * SIZE, "Line", Color.WHITE);  // Line or NoLine
        board.setTitle("DijkstraSearch");

        board.button1SetName("Read a Graph data file");
        board.button1ClickEventListener(new BGPEventListener() {
            @Override
            public void clicked(int row, int col) {
                read();
            }
        });

        board.button2SetName("Find shortest path");
        board.button2ClickEventListener(new BGPEventListener() {
            @Override
            public void clicked(int row, int col) {
                shortPath();
            }
        });

        board.setText("graph.txt");
    }


    static void read() {

        String fileName;
        String line;

        try {
            fileName = board.getText();

            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);


            String words[];

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0)
                    continue;
                if (line.charAt(0) == '/' && line.charAt(1) == '/')
                    continue;


                nodes = new NodePriorityQueue[Integer.parseInt(line)];

                graph = new Graph(Integer.parseInt(line));
                for (int i = 0; i < Integer.parseInt(line); i++) {
                    nodes[i] = new NodePriorityQueueVertex(new VertexString(" "));
                }

                break;
            }


            int id = 0;
            while (id < graph.size()) {
                line = bufferedReader.readLine();
                line = line.trim();
                if (line.length() == 0)
                    continue;
                if (line.charAt(0) == '/' && line.charAt(1) == '/')
                    continue;

                words = line.split("[ \t]+");

                //Read each line and store it as vertex in the Graph
                graph.keepVertex(Integer.parseInt(words[0]), new VertexString(words[1]));


                VertexString pass = new VertexString(Integer.parseInt(words[0]), words[1]);

                //Store each vertex in a node
                nodes[id] = new NodePriorityQueue(pass);

                id++;
            }
            String costs[];

            id = 0;
            while (id < graph.size()) {
                line = bufferedReader.readLine();
                line = line.trim();
                if (line.length() == 0)
                    continue;
                if (line.charAt(0) == '/' && line.charAt(1) == '/')
                    continue;

                costs = line.split("[ \t]+");
                for (int i = 0; i < graph.size(); i++)
                    graph.setNeighbors(id, i, Double.parseDouble(costs[i]));

                id++;
            }

            // Always close files.
            bufferedReader.close();

            // Display the graph on the board
            displayGraph();

            JOptionPane.showMessageDialog(null, "Reading has been done");

            // Just for finding Gvalue between any vertex_1 to vertex_2
            board.setText("find Vertex_1 to Vertex_2");
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "File not found");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "File i/o error");
        }

    }

    static void displayGraph() {
        for (int col = 1; col < graph.size() + 1; col++) {
            board.cellContent(0, col, "" + (col - 1));
            board.cellBackgroundColor(0, col, Color.YELLOW);
        }

        for (int row = 1; row < graph.size() + 1; row++) {
            board.cellContent(row, 0, "" + (row - 1));
            board.cellBackgroundColor(row, 0, Color.YELLOW);
            for (int col = 1; col < graph.size() + 1; col++) {
                board.cellContent(row, col, "" + graph.cost(row - 1, col - 1));
                board.cellBackgroundColor(row, col, Color.CYAN);
            }
        }

        for (int row = 1; row < graph.size() + 1; row++) {
            board.cellContent(row, graph.size() + 2, graph.find(row - 1).getContent());
            board.cellBackgroundColor(row, graph.size() + 2, Color.CYAN);
        }

        for (int row = graph.size() + 1; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board.cellContent(row, col, "");
                board.cellBackgroundColor(row, col, Color.WHITE);
            }
        }
    }


    static void shortPath() {
        String line = board.getText().trim();
        if (line.length() == 0) return;
        String words[] = line.split("[ \t]+");
        if (words.length < 3) return;

        String calculateMethod = words[0];
        String initial = words[1];
        String destination = words[3];

        if (calculateMethod.equals("find")) {
            bfs(initial, destination, SIZE - 1);

        } else
            System.out.println("Wrong calculation method!");


    }

    static void bfs(String initial, String destination, int row) {
        double gvalue = 0.0;
        double tvalue = 0.0;
        VertexString parentVertex;
        VertexString currentVertex;
        VertexString lastVertex;
        graph.reset();
        displayGraph();
        PriorityQueue<VertexString> queue = new PriorityQueue<VertexString>();

        StackVertex stack = new StackVertex();


        int row1A = row -3;
        int col1A = 0;

        int row1B = row -2;
        int col1B = 0;

        int row2 = row -1;
        int colc = 0;

        NodePriorityQueue<VertexString> initialNode;


        NodePriorityQueue<VertexString> currentNode;


        int col = 0;

        initialNode = nodes[graph.findId(initial)];

        initialNode.setPriority(0);
        // Add a vertex into the priority queue
        queue.addElement(initialNode);  // ...(priority and string)




        int count= 0;
        int x = 0;
        while (!queue.isEmpty()) {

            currentVertex = queue.removeMin();



            if (currentVertex.isVisited())
                continue;
            else {
                currentVertex.visited();
                board.cellContent(row1A, col1A, "VISITED: ");
                board.cellContent(row1B, col1B, "" + nodes[currentVertex.getId()].getContent().getContent());
                board.cellBackgroundColor(row, col, Color.CYAN);
                col1B++;
            }

            if (currentVertex.getContent().equals(destination)) {
                stack.push(currentVertex);
                parentVertex = (VertexString) currentVertex.getParent();


                while (parentVertex != null)
                {
                    stack.push((VertexString) parentVertex);
                    parentVertex = (VertexString) parentVertex.getParent();

                }

                while (!stack.isEmpty())
                {
                    board.cellContent(row2, colc, "PATH: ");
                    lastVertex = stack.pop();
                    board.cellContent(row, col, "" + lastVertex.getContent()+ " : " +nodes[lastVertex.getId()].getPriority());
                    board.cellBackgroundColor(row, col, Color.CYAN);
                    col++;
                }



                JOptionPane.showMessageDialog((Component)null, "Gvalue for path is - " + nodes[currentVertex.getId()].getPriority());


                break;
            }


            boolean[] neighbors = graph.getNeighbors(currentVertex.getId());
            tvalue = nodes[currentVertex.getId()].getPriority();

            for (int i = 0; i < neighbors.length; i++) {
                gvalue = graph.cost(currentVertex.getId(), i);
                if (!nodes[i].getContent().isVisited() && neighbors[i]) {
                    if (!nodes[i].getContent().isExpanded())
                    {
                        nodes[i].getContent().expanded();
                        nodes[i].setPriority(tvalue + gvalue);
                        nodes[i].getContent().setParent(currentVertex);
                        queue.addElement(nodes[i]);
                    }


                   else if ((tvalue + gvalue) < nodes[i].getPriority() && nodes[i].getContent().isExpanded()) {

                        double nvalue = tvalue + gvalue;
                        nodes[i].setPriority(nvalue);
                        currentNode = nodes[i];
                        nodes[i].getContent().setParent(currentVertex);
                        queue.update(currentNode);

                    }





                }


            }




        }
    }
}





