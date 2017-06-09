/*****************************************************************************
  * Atom.java
  * Author: Olivia Waring
  * Represents an "atom" within the framework of Type Logical Grammar
  *****************************************************************************/

import java.util.ArrayList;

public class Atom {
    private String simpleType; // atom type
    private int adjoint; // adjoint value
    private boolean isGeneric; // whether the atom is generic
    private ArrayList<Marker> markers; // morphological markings
    private ArrayList<Atom> atomList; // list of the atoms a generic atom represents

    // Return a new, completely neutral atom
    public Atom()
    {
        simpleType = "";
        adjoint = 0;
        isGeneric = false;
        markers = null;
        atomList = null;
    }
    
    // Return a new atom with a specified type and adjoint
    public Atom(String s, int a)
    {
        simpleType = s;
        adjoint = a;
        if (simpleType.contains("*"))
            isGeneric = true;
        else
            isGeneric = false;
        markers = null;
        atomList = null;
    }
    
    // Return a new atom in accordance with a descriptive string
    public Atom(String s)
    {
        String delims = "[\\^]+";
        String[] subatomic, subsubatomic;
        subatomic = s.split(delims);
        assert(subatomic.length == 2):"Incorrect atom format:" 
            + subatomic[0].toString();
        adjoint = Integer.parseInt(subatomic[1]);
        delims = "[_]+";
        subsubatomic = subatomic[0].split(delims);
        simpleType = subsubatomic[0];
        if (simpleType.contains("*"))
            isGeneric = true;
        else
            isGeneric = false;
        
        // Iterate through any markers and add to list (a marker can be null!)
        markers = new ArrayList<Marker>();
        for (int k = 1; k < subsubatomic.length; k++)
        {
            markers.add(new Marker(k-1, Integer.parseInt(subsubatomic[k])));
        }
        atomList = null;
    }

    // Return a new atom with a specified type, adjoint, and marker set
    public Atom(String s, int a, ArrayList<Marker> marks)
    {
        simpleType = s;
        adjoint = a; 
        if (simpleType.contains("*"))
            isGeneric = true;
        else
            isGeneric = false;
        markers = marks; // person, case, tense-mood, gender, number, etc.
        atomList = null;
    } 
   
    // Return a new atom with a specified type, adjoint, and generic character
    public Atom(String s, int a, boolean generic)
    {
        simpleType = s;
        adjoint = a; 
        isGeneric = generic;
        markers = null;
        atomList = null;
    }
    
    // Return a new atom with a specified type, adjoint, generic character, and
    // atom list
    public Atom(String s, int a, boolean generic, ArrayList<Atom> atoms)
    {
        simpleType = s;
        adjoint = a; 
        isGeneric = generic;
        markers = null;
        atomList = atoms; // list of possible values the generic atom stands for
    }
    
    // Return a new atom with a specified type, adjoint, generic character, 
    // marker set, and atom list
    public Atom(String s, int a, boolean generic, ArrayList<Marker> marks, 
                ArrayList<Atom> atoms)
    {
        simpleType = s;
        adjoint = a; 
        isGeneric = generic;
        markers = marks; // person, case, tense-mood, gender, number, etc.
        atomList = atoms; // list of possible atoms the generic atom stands for
    } 

    // Return a copy of the atom in question
    public Atom copy()
    {
        Atom newAtom;
        
        // Copy atom list for generic atoms
        if ((isGeneric) && (atomList != null))
        {
            ArrayList<Atom> newAtoms = new ArrayList<Atom>();
            for (Atom a: atomList)
            {
                Atom tempAtom = a.copy();
                newAtoms.add(tempAtom);
            }
            newAtom = new Atom(simpleType, adjoint, true, newAtoms);
        }
        
        // Copy marker list (if necessary) for nongeneric atoms
        else
        {
            assert(atomList == null):"Nongeneric atom should not have atom list:" + this.toString();
            ArrayList<Marker> newMarkers = new ArrayList<Marker>();
            if (markers != null)
            {
                
                for (Marker m: markers)
                {
                    Marker tempMarker = m.copy();
                    newMarkers.add(tempMarker);   
                }
            }
            else 
            {
                newMarkers = null;
            }
            newAtom = new Atom(simpleType, adjoint, isGeneric, newMarkers, atomList);
        }
        return newAtom;
    }
    
    // Return string representation of atom 
    public String toString()
    {
        String atom = "";
        atom += simpleType;
        if (isGeneric)
        {
            assert(markers == null):"Generic atom should not have marker list.";
            atom += "(" + adjoint + ")[ ";
            if (atomList != null)
            {
                for (Atom a: atomList)
                {
                    atom += (a.toString() + " ");
                }
            }
            atom +="]";
        }
        
        else 
        {
            if ((markers != null) && (markers.size() > 0))
            {
                for (int i = 0; i < markers.size(); i++)
                {
                    atom += "_" + markers.get(i).getType();
                }
            }
            atom += "(" + adjoint + ")";
        }
        return atom;
    }
    
    // Return atom type
    public String getSimpleType()
    {
        return simpleType;
    }
    
    // Return adjoint value
    public int getAdjoint()
    {
        return adjoint;
    }
    
    // Set adjoint value
    public void setAdjoint(int i) 
    {
        adjoint = i;
    }
    
    // Return true if atom is generic, false otherwise
    public boolean isGeneric()
    {
        return isGeneric;
    }
    
    // Return list of atoms that a generic atom represents
    public ArrayList<Atom> getAtoms()
    {
        return atomList;
    }
    
    // Return list of markers
    public ArrayList<Marker> getMarkers()
    {
        return markers;
    }
    
    // Represent markers as a string with appropriate labeling
    public String getMarkerString(LambekGrammar lg)
    {
        ArrayList<ArrayList<String>> list = lg.getMarkers();
        String markerString = "";
        for (int i = 0; i < markers.size(); i++)
        {
            markerString += "" + list.get(markers.get(i).getHeading()).get(0) + ": " 
                + list.get(markers.get(i).getHeading()).get(markers.get(i).getType()) + "\n";
        }
        return markerString;
    }
    
    // Set list of atoms represented by a generic atom
    public void setAtoms(ArrayList<Atom> atoms)
    {
        atomList = atoms;
    }
    
    // Return true if two sets of markers are equal, false otherwise
    public boolean equalMarkers(Atom other)
    {
        boolean areEqual = true; 
        if ((this.markers != null) && (other.markers != null))
        {
            if ((this.markers.size() != 0) && (other.markers.size() != 0))
            {
                for (int i = 0; i < markers.size(); i++)
                {
                    if (!(this.markers.get(i).equals(other.getMarkers().get(i))))
                    {
                        areEqual = false;
                        break;
                    }
                }
            }
        }
        return areEqual;
    }
    
    // Return true if two atoms have equivalent types, adjoints, and marker 
    // sets, false otherwise
    public boolean equals(Atom other)
    {
        return ((this.simpleType.equals(other.simpleType)) && (this.adjoint == other.adjoint)
               && (this.equalMarkers(other)));     
    }
    
    // Return true if two atoms have equivalent types, false otherwise
    public boolean equalTypes(Atom other)
    {
        return this.simpleType.equals(other.simpleType);
    }
    
}