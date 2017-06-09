/*****************************************************************************
  * ReductionRule.java
  * Author: Olivia Waring
  * Represents a reduction rule within a Lambek Grammar
  *****************************************************************************/

public class ReductionRule implements Comparable<ReductionRule>{
    private Atom atom1; // starting atom
    private Atom atom2; // target atom
    
    // Return a new reduction rule with the specified type sequence
    public ReductionRule(Atom a1, Atom a2)
    {
        atom1 = a1;
        atom2 = a2;
    }
    
    // Return starting atom
    public Atom getAtom1 ()
    {
        return atom1;
    }
    
    // Return target atom
    public Atom getAtom2 ()
    {
        return atom2;
    }
    
    // Return a string representation of the reduction rule
    public String toString()
    {
        String ruleString = atom1 + "-->" + atom2;
        return ruleString;
    }
    
    // Compare reduction rules alphabetically by starting atom type
    public int compareTo(ReductionRule other)
    {
        return (this.atom1.getSimpleType()).compareToIgnoreCase(other.atom1.getSimpleType());
    }
}