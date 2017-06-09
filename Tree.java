/*****************************************************************************
  * Tree.java
  * Author: Olivia Waring
  * Builds and maintains a data structure encoding all possible type lists that
  * could correspond to a particular sentence. 
  *****************************************************************************/

import java.util.ArrayList;

public class Tree 
{
    private Node root; // starting node (will simply be a dummy node)
    private ArrayList<Node> leaves; // terminal nodes
    private int generations; // tree depth
    
    // Return a new tree
    public Tree()
    {
        root = new Node(null); 
        leaves = new ArrayList<Node>();
        root.setDescendents(leaves);
        root.setAncestor(root); 
        generations = 0;
    } 
    
    // Add a generation of types to an existing tree
    public void addGeneration (ArrayList<Type> types)
    {
        Node newLeaf;
        ArrayList<Node> subTree;
        ArrayList<Node> newLeaves;
        
        // Add a first generation
        if (leaves.size() == 0)
        {
            for (Type type: types)
            {
                newLeaf = new Node(type);
                newLeaf.setAncestor(root);
                leaves.add(newLeaf);
            }
            root.setDescendents(leaves);
        }
        
        // Add subsequent generations
        else 
        {
            newLeaves = new ArrayList<Node>();
            for (Node oldLeaf: leaves)
            {
                subTree = new ArrayList<Node>();
                for (Type type: types)
                {
                    newLeaf = new Node(type); 
                    newLeaf.setAncestor(oldLeaf);
                    subTree.add(newLeaf); 
                    newLeaves.add(newLeaf);
                }
                oldLeaf.setDescendents(subTree);
            }
            leaves = newLeaves;
        }

        generations++; // increment tree depth
        return;
    } 
    
    // Print a text representation of the tree (for verification purposes)
    public void printTree ()   
    {
        ArrayList<Node> current = root.getDescendents();
        ArrayList<Node> temp;
        
        for (int i = 0; i < generations; i++)
        {
            assert(current != null):"Nonexistant generation accessed.";
            System.out.print("Generation " + i + ": ");
            temp = new ArrayList<Node>();
            for (Node n: current)
            {
                System.out.print(n.getType().toString() + ", ");
                for (Node m: n.getDescendents())
                    temp.add(m); 
            }
            current = temp;
            System.out.println("");
        }
        
        return;
    }
    
    // Return a list of all possible paths through the tree
    public ArrayList<ArrayList<Type>> getPaths ()
    {
        ArrayList<ArrayList<Type>> allParses = new ArrayList<ArrayList<Type>>();
        ArrayList<Type> parse;
        for (Node l: leaves)
        {
            parse = new ArrayList<Type>();
            while(l != root)
            {
                parse.add(0, l.getType()); // Add item to front of list
                l = l.getAncestor();
            }
            allParses.add(parse);
        }
        return allParses;
    }
    
    // Print all possible paths through the array
    public void printPaths()
    {
        ArrayList<ArrayList<Type>> allPaths = this.getPaths();
        for (ArrayList<Type> path: allPaths)
        {
            Type t;
            int i;
            for (i=0; i < path.size()-1; i++)
            {
                t = path.get(i);
                System.out.print(t.toString()+"-->");
            } 
            t = path.get(i);
            System.out.println(t.toString());
        } 
        return;
    }
}