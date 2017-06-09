  /***********************************************************************
  * Link.java
  * Author: Olivia Waring
  * Represents the indices of two atoms which have been linked by a Lambek
  * parsing algorithm
  ************************************************************************/

public class Link
{
    private int node1; // first index
    private int node2; // second index
    
    // Return a new link with the specified membership
    public Link(int i1, int i2)
    {
        node1 = i1;
        node2 = i2;
    }
    
    // Return first member of link
    public int getNode1()
    {
        return node1;
    }
    
    // Return second member of link
    public int getNode2()
    {
        return node2;
    }
    
    // Return string representation of link
    public String toString()
    {
        String linkString = "(" + node1 + "," + node2 + ")";
        return linkString;
    }

}