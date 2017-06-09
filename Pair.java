  /***********************************************************************
  * Pair.java
  * Author: Olivia Waring
  * Represents a pair of integers
  ************************************************************************/

public class Pair {
    private int num1; // first number
    private int num2; // second number
    
    // Returns a new pair with the specified membership
    public Pair(int i1, int i2)
    {
        num1 = i1;
        num2 = i2;
    }
    
    // Return first member of pair
    public int getFirst()
    {
        return num1;
    }
    
    // Return second member of pair
    public int getSecond()
    {
        return num2;
    }
}