/*****************************************************************************
  * Node.java
  * Author: Olivia Waring
  * Encodes the fundamental unit for use in the Tree data structure 
  *****************************************************************************/

import java.util.ArrayList;

public class Node
{
    private Node ancestor; // preceding node
    private ArrayList<Node> descendents; // collection of subsequent nodes
    private Type type; // node type 
    
    // Return a new node, given ancestors and descendents
    public Node (Node backwards, ArrayList<Node> forwards, Type aType)
    {
        ancestor = backwards;
        descendents = forwards;
        type = aType;
    }
    
    // Return a new node with no ancestors or descendents
    public Node (Type aType)
    {
        ancestor = null;
        descendents = new ArrayList<Node>();
        type = aType;
    }
    
    // Return preceding node
    public Node getAncestor()
    {
        return ancestor;
    }
    
    // Return collection of subsequent nodes
    public ArrayList<Node> getDescendents()
    {
        return descendents;
    }
    
    // Return node type
    public Type getType()
    {
        return type;
    }
    
    // Set preceding node
    public void setAncestor(Node node)
    {
        ancestor = node;
        return;
    }
    
    // Set subsequent nodes
    public void setDescendents(ArrayList<Node> branches)
    {
        descendents = branches;
        return;
    }
    
    // Add subsequent node
    public void addDescendent(Node node)
    {
        descendents.add(node);
        return;
    }
     
    // Set node type
    public void setType(Type aType)
    {
        type = aType;
        return;
    }
    
    // Return number of subsequent nodes
    public int getNumDescendents()
    {
        return descendents.size();
    }
}