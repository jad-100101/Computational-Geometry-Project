import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("");
        System.out.println("Enter the coordinates of the vertices: (x1,y1) (x2,y2) ...");
        String input = scanner.nextLine().trim();
        String cleanedInput = input.replaceAll("\\s+", "").replaceAll("\\),\\(", ")("); // remove spaces and commas between parentheses
        cleanedInput = cleanedInput.replaceAll("\\(\\s*", "(").replaceAll("\\s*\\)", ")"); // remove spaces inside parentheses

        String[] coordinatePairs = cleanedInput.split("[()]"); // split by parentheses to get coordinate pairs
        ArrayList<Vertex> vertices = new ArrayList<>(); // construct arraylist of vertices
        for (int i = 1; i < coordinatePairs.length; i += 2) 
        {
            String[] coordinates = coordinatePairs[i].split(",");
            double x = Double.parseDouble(coordinates[0]);
            double y = Double.parseDouble(coordinates[1]);

            vertices.add(new Vertex(i / 2, x, y));
        }

        int n = vertices.size();
        for (int i = 0; i < n; i++) // make arraylist a circular doubly linked list
        {
            Vertex current = vertices.get(i);
            Vertex next = vertices.get((i + 1) % n); // circular next
            Vertex prev = vertices.get((i + n - 1) % n); // circular prev
            current.setNext(next);
            current.setPrev(prev);
        }

        for (int i = 0; i < vertices.size(); i++) 
        {
            for (int j = i + 1; j < vertices.size(); j++) 
            {
                Vertex a = vertices.get(i);
                Vertex b = vertices.get(j);

                boolean isVisible = visibility(a, b, vertices); // check visibility between vertices a and b
                if (isVisible == true) // update visibility info for both a and b (symmetric relation)
                {
                    a.addVisibleVertex(b);
                    b.addVisibleVertex(a);
                }
            }
        }

        System.out.println();
        System.out.println();
        System.out.println();
        for (Vertex vertex : vertices) // print visibility information for each vertex
        {
            System.out.print("Vertex " + vertex.getIndex() + " can see: ");
            for (Vertex visibleVertex : vertex.getVisibleVertices()) 
            {
                System.out.print(visibleVertex.getIndex() + " ");
            }
            System.out.println("");
            System.out.println();
        }

        System.out.println();
        System.out.println();
        findWitnesses(vertices);
        System.out.println();
        
        scanner.close();
    }

    private static void findWitnesses(ArrayList<Vertex> vertices) 
    {
        vertices.sort((a, b) -> Integer.compare(a.getVisibleVertices().size(), b.getVisibleVertices().size())); // sort vertices based on the number of vertices they can see

        System.out.println("Vertices in sorted order: ");
        System.out.println();
        for (Vertex vertex : vertices)
        {
            System.out.println("Vertex " + vertex.getIndex() + " - (sees " + vertex.getVisibleVertices().size() + " vertices)");
            System.out.println();
        }
        System.out.println();

        int i = 0; // start from the first
        ArrayList<Vertex> witnesses = new ArrayList<>(vertices); // initialize witnesses arraylist with sorted vertices
        while (i < witnesses.size())  // iterate over witnesses arraylist
        {
            Vertex witness = witnesses.get(i); // get current witness from start of witness arraylist
            ArrayList<Vertex> visibleVertices = new ArrayList<>(witness.getVisibleVertices()); // vertices seen by the current witness
            for (Vertex visibleVertex : visibleVertices) // iterate over vertices seen by the current witness
            {
                int j = witnesses.size() - 1; // start from the last
                while (j >= 0)
                {
                    Vertex vertex = witnesses.get(j); // get current vertex from end of witness arraylist
                    if (vertex != witness && vertex.getVisibleVertices().contains(visibleVertex)) // check if the vertex visible by the current witness is also visible by the current vertex 
                    { 
                        witnesses.remove(j); 
                    }
                    j--;
                }
            }
            i++; 
        }

        System.out.println();
        System.out.println("Independent Witnesses: ");
        System.out.println();
        for (Vertex witness : witnesses)
        {
            System.out.print("Vertex " + witness.getIndex() + " (" + (int)witness.getX() + "," + (int)witness.getY() + "): ");
            for (Vertex visibleVertex : witness.getVisibleVertices()) 
            {
                System.out.print(visibleVertex.getIndex() + " ");
            }
            System.out.println("");
            System.out.println();
        }
        System.out.println();
    }

    // Citation - Code 1.12 from O'Rourke
    public static boolean visibility(Vertex a, Vertex b, ArrayList<Vertex> vertices) 
    {
        return incone(a, b) && incone(b, a) && diagonalie(a, b, vertices);
    }

    // Citation - Code 1.11 from O'Rourke
    private static boolean incone(Vertex a, Vertex b) 
    {
        Vertex prev = a.getPrev();
        Vertex next = a.getNext();

        // if a is a convex vertex
        if (leftOn(a, next, prev)) 
        {
            if (b.equals(prev) || b.equals(next)) // if b is one of the adjacent vertices, it's visible
            {
                return true;
            }
            return left(a, b, prev) && left(b, a, next); // else check for visibility
        }

        // else a is a reflex vertex
        if (b.equals(prev) || b.equals(next)) // if b is one of the adjacent vertices, it's visible
        {
            return true; 
        }
        return !(leftOn(a, b, next) && leftOn(b, a, prev)); // else check for visibility
    }

    // Citation - Code 1.7-1.10 from O'Rourke
    private static boolean diagonalie(Vertex a, Vertex b, ArrayList<Vertex> vertices) 
    {
        for (int i = 0; i < vertices.size(); i++) 
        {
            Vertex c = vertices.get(i);
            Vertex c1 = vertices.get((i + 1) % vertices.size());

            if (!(c == a || c1 == a || c == b || c1 == b) && intersect(a, b, c, c1)) 
            {
                return false;
            }
        }
        return true;
    }
    private static boolean intersect(Vertex a, Vertex b, Vertex c, Vertex d) 
    {
        if (intersectProp(a, b, c, d)) 
        {
            return true;
        } 
        else 
        {
            return between(a, b, c) || between(a, b, d) || between(c, d, a) || between(c, d, b);
        }
    }
    private static boolean intersectProp(Vertex a, Vertex b, Vertex c, Vertex d) 
    {
        if (collinear(a, b, c) || collinear(a, b, d) || collinear(c, d, a) || collinear(c, d, b)) 
        {
            return false;
        } 
        else 
        {
            return xor(left(a, b, c), left(a, b, d)) && xor(left(c, d, a), left(c, d, b));
        }
    }
    private static boolean xor(boolean x, boolean y) 
    {
        return (x && !y) || (!x && y);
    }
    private static boolean between(Vertex a, Vertex b, Vertex c) 
    {
        if (!collinear(a, b, c)) 
        {
            return false;
        }

        if (a.getX() != b.getX()) 
        {
            return (a.getX() <= c.getX() && c.getX() <= b.getX()) || (a.getX() >= c.getX() && c.getX() >= b.getX());
        } 
        else 
        {
            return (a.getY() <= c.getY() && c.getY() <= b.getY()) || (a.getY() >= c.getY() && c.getY() >= b.getY());
        }
    }

    // Citation - Code 1.5 and 1.6 from O'Rourke
    private static double area2(Vertex a, Vertex b, Vertex c) 
    {
        return ((b.getX() - a.getX()) * (c.getY() - a.getY()) -
                (c.getX() - a.getX()) * (b.getY() - a.getY()));
    }
    private static boolean left(Vertex a, Vertex b, Vertex c) 
    {
        return area2(a, b, c) > 0;
    }
    private static boolean leftOn(Vertex a, Vertex b, Vertex c) 
    {
        return area2(a, b, c) >= 0;
    }
    private static boolean collinear(Vertex a, Vertex b, Vertex c) 
    {
        return area2(a, b, c) == 0;
    }
}
