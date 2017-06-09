/*****************************************************************************
  * Entry.java
  * Author: Olivia Waring
  * Represents a lexical entry in a Lambek dictionary
  *****************************************************************************/

import java.util.ArrayList;
import java.io.*;
import java.lang.Integer;

public class Entry implements Comparable<Entry> {
    private String lexeme; // lexical item
    private ArrayList<Type> types; // list of possible types
    
    // Return a new entry
    public Entry(String word, String[] tokens)
    {
        lexeme = word;
        types = new ArrayList<Type>();
        String delims;
        String[] atoms, subatomic, subsubatomic;
        int len1 = tokens.length;
        
        // Iterate through each possible type
        for (int i = 0; i < len1; i++)
        {
            delims = "[ ]+";
            atoms = tokens[i].split(delims);
            int len2 = atoms.length;
            ArrayList<Atom> atomList = new ArrayList<Atom>();
            
            // Iterate through each atom in the compound type
            for (int j = 0; j < len2; j++)
            {
                ArrayList<Marker> markers = new ArrayList<Marker>();
                Atom a;
                delims = "[\\^]+";
                subatomic = atoms[j].split(delims);
                assert(subatomic.length == 2):"Incorrect atom format: "+atoms[j].toString();
                delims = "[_]+";
                subsubatomic = subatomic[0].split(delims);
                
                // Iterate through any markers and add to list (a marker can be null!)
                for (int k = 1; k < subsubatomic.length; k++)
                {
                    markers.add(new Marker(k-1, Integer.parseInt(subsubatomic[k])));
                }

                // Create a new atom with the appropriate type, marker set, and adjoint
                a = new Atom(subsubatomic[0], Integer.parseInt(subatomic[1]), markers);
                atomList.add(a);
            }
            Type t = new Type(atomList);
            types.add(t);      
        }
    }
    
    // Return string representation of entry
    public String toString()
    {
        String entryString = lexeme + ":";
        int i;
        for (i = 0; i < types.size()-1; i++)
        {
            entryString += types.get(i).toString() + ",";
        }
        entryString += types.get(i).toString();
        return entryString;
    }
    
    // Return lexical entry
    public String getLex()
    {
        return lexeme;
    }
      
    // Return list of possible types
    public ArrayList<Type> getTypes()
    {
        return types;
    }
    
    // Add type to list of possible types
    public void addTypes(ArrayList<Type> moreTypes)
    {
        for (Type t: moreTypes)
        {
            types.add(t);
        }
        return;
    }
    
    // Compare the key values of two entries
    public int compareTo(Entry other)
    {
        return this.lexeme.compareToIgnoreCase(other.lexeme);
    }
}