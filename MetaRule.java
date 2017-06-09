/*****************************************************************************
  * Metarule.java
  * Author: Olivia Waring
  * Represents a metarule within a Lambek Grammar 
  *****************************************************************************/

public class MetaRule {
    private int index; // thematic reference number
    private Type type1; // starting type
    private Type type2; // target type
    
    // Return a new metarule with the specified reference number and type sequence
    public MetaRule(int num, Type t1, Type t2)
    {
        index = num;
        type1 = t1;
        type2 = t2;
    }
    
    // Return a string representation of the metarule
    public String toString()
    {
        String ruleString = index + ": " + type1 + "-->" + type2;
        return ruleString;
    }
    
    // Return thematic reference number
    public int getIndex()
    {
        return index;
    }
    
    // Return starting type
    public Type getType1()
    {
        return type1;
    }
    
    // Return target type
    public Type getType2()
    {
        return type2;
    }
}