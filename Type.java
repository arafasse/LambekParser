/*****************************************************************************
  * Type.java
  * Author: Olivia Waring
  * Represents a compound type in Type Categorial Grammar
  *****************************************************************************/

import java.util.ArrayList;

public class Type // Blind to generics
{
    private int length; // number of atoms (q)
    private ArrayList<Atom> compoundType; // list of atoms comprising the compound type
    
    // Return a new, netural type
    public Type()
    {
        length = 0;
        compoundType = null;
    }
    
    // Return a new type with a specified list of atoms
    public Type (ArrayList<Atom> atoms)
    {
        length = atoms.size();
        compoundType = atoms;
    }
    
    // Return number of atoms
    public int getLength()
    {
        return length;
    }
    
    // Return a string representation of the type
    public String toString()
    {
        String atomList = "";
        for (int i = 0; i < compoundType.size(); i++)
        {
            atomList += compoundType.get(i).toString() + "";
        } 
        return atomList;
    }
    
    // Return atom list
    public ArrayList<Atom> getAtoms()
    {
        return compoundType;
    }
    
    // Return true if equivalent to other, false otherwise
    public boolean equals(Type other)
    {
        boolean flag = false;
        if (other.length != this.length)
        {
            return flag;
        }
        else
        {
            for (int i = 0; i < this.length; i++)
            {
                if (this.compoundType.get(i).equals(other.compoundType.get(i)))
                {
                    flag = true;
                }
                else
                {
                    flag = false;
                    return flag;
                }
            }
        }
        return flag;
    }
    
    // Create a new copy of the type in question
    public Type copy()
    {
        ArrayList<Atom> atoms = new ArrayList<Atom>();
        for (Atom a: compoundType)
        {
            Atom tempAtom = a.copy();
            atoms.add(tempAtom);
        }
        return (new Type(atoms));
    }
}
    