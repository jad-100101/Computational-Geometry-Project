import java.util.ArrayList;
public class Vertex 
{
    private int index; 
    private double x;
    private double y;
    private ArrayList<Vertex> visibleVertices;
    private Vertex next;
    private Vertex prev;

    public Vertex(int index, double x, double y) 
    {
        this.index = index;
        this.x = x;
        this.y = y;
        this.visibleVertices = new ArrayList<>();
    }
    
    public int getIndex() 
    {
        return index + 1;
    }
    
    public double getX() 
    {
        return x;
    }
    public double getY() 
    {
        return y;
    }
    
    public ArrayList<Vertex> getVisibleVertices() 
    {
        return visibleVertices;
    }
    public void addVisibleVertex(Vertex vertex) 
    {
        visibleVertices.add(vertex);
    }

    public Vertex getNext() 
    {
        return next;
    }
    public void setNext(Vertex next) 
    {
        this.next = next;
    }
    public Vertex getPrev() 
    {
        return prev;
    }
    public void setPrev(Vertex prev) 
    {
        this.prev = prev;
    }

    public String toString() 
    {
        return index + ": (" + x + "," + y + ")";
    }
}

