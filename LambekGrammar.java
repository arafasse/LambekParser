/*****************************************************************************
  * LambekGrammar.java
  * Author: Olivia Waring
  * Constructs a representation of a Lambek Grammar 
  *****************************************************************************/

import java.util.*;
import java.io.*;

public class LambekGrammar {
    private File file; // grammar specification file 
    Atom[] atomList; // inventory of possible atom types 
    Atom dummyAtom; // used to "cancel" certain atom types during parsing
    int numMarkers; // number of marker categories
    ArrayList<Entry> entries; // list of lexcial entries
    ArrayList<ReductionRule> reductionrules; // list of reduction rules
    ArrayList<MetaRule> metarules; // list of metarules 
    ArrayList<ArrayList<String>> markerList; // list of marker types and members
    ArrayList<ArrayList<String>> irregulars; // list of irregular forms
    ArrayList<String> verbalSuffixes; // list of verbal suffixes
    ArrayList<String> nounSuffixes; // list of nominal suffixes
    ArrayList<String> adjectiveSuffixes; // list of adjectival suffixes
    ArrayList<Atom> standalones; // list of standalone atom types
    boolean[][] mappings; // compact representation of reduction rules
    
    // Build a Lambek Grammar according to specifications from a given text file 
    public LambekGrammar(String filename) throws java.io.IOException
    {
        file = new File(filename);
        dummyAtom = new Atom("x^0");
        entries = new ArrayList<Entry>();
        reductionrules = new ArrayList<ReductionRule>();
        metarules = new ArrayList<MetaRule>();
        irregulars = new ArrayList<ArrayList<String>>();
        verbalSuffixes = new ArrayList<String>();
        nounSuffixes = new ArrayList<String>();
        adjectiveSuffixes = new ArrayList<String>(); 
        standalones = new ArrayList<Atom>();
        
        // Read the grammar specifications line by line
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line; // current line
            String delims; // delimiters
            String[] tokens; // list of delimited substrings
            int section = 1, count = -1; // to keep track of location within file
            int scenario = 0; // type of substitution 
            
            while ((line = reader.readLine()) != null)
            {
                // Section break
                if (line.equals("**********")) 
                {
                    section++;
                    count = -2;
                }
                
                // Section 1: ATOM TYPES
                else if (section == 1) 
                {
                    int numTypes = 0; // number of atom types
                    Atom a; // atom type to be added
                    
                    // Read number of atom types and initialize data structures
                    if (count == -1)
                    {
                        numTypes = Integer.parseInt(line);
                        atomList = new Atom[numTypes];
                        mappings = new boolean[numTypes][numTypes];
                        for (int i = 0; i < numTypes; i++)
                        {
                            for (int j = 0; j < numTypes; j++)
                            {
                                if (i == j)
                                    mappings[i][j] = true;
                            }
                        }     
                    }
                    
                    // Add atom types (with adjoint set to 0) 
                    else
                    {
                        if (line.contains("#"))
                        {
                            a = new Atom(line.substring(1,line.length()), 0);
                            standalones.add(a);
                        }
                        else
                        {
                            a = new Atom(line, 0);
                        }
                        atomList[count] = a;
                    }  
                    count++;
                }
                
                // Section 2: MARKERS
                else if (section == 2) 
                {
                    // Read number of marker types
                    if (count == -2)
                    {
                        numMarkers = Integer.parseInt(line);
                        markerList = new ArrayList<ArrayList<String>>(); 
                        count++;
                    }
                    
                    // Generate marker data structure
                    else
                    {
                        ArrayList<String> s = new ArrayList<String>();
                        if (!(line.contains("#")))
                        {
                            count++;
                            s.add(line); 
                            markerList.add(s);
                        }
                        else
                        {
                            delims = "[#]+";
                            tokens = line.split(delims);
                            markerList.get(count).add(tokens[1]);
                        }
                    }
                    assert(count <= numMarkers):"Incorrect number of marker types.";
                }
                
                // Section 3: SUBSTITUTIONS
                else if (section == 3) 
                {                
                    // Verbal substitutions
                    if (line.contains("#Verbs:"))
                    {
                        scenario = 1;
                    }
                    
                    // Nominal substitutions
                    else if (line.contains("#Nouns:"))
                    {
                        scenario = 2;
                    }
                    
                    // Adjectival substitutions
                    else if (line.contains("#Adjectives:"))
                    {
                        scenario = 3;
                    }
                    
                    // Irregular forms
                    else if (line.contains("#Irregulars:"))
                    {
                        scenario = 4;
                    }
                    
                    // Add each substitution to appropriate list
                    else
                    {
                        switch (scenario) 
                        {
                            case 1:  verbalSuffixes.add(line);
                            break;
                            case 2:  nounSuffixes.add(line);
                            break;
                            case 3:  adjectiveSuffixes.add(line);
                            break;
                            case 4:  
                            {
                                delims = "[>]+";
                                tokens = line.split(delims);
                                ArrayList<String> couplet = new ArrayList<String>();
                                couplet.add(tokens[0]);
                                couplet.add(tokens[1]);       
                                irregulars.add(couplet);
                            }
                            break;
                        }
                    }  
                }           
                
                // Section 4: METARULES
                else if (section == 4) 
                {
                    int n = 0; // metarule index
                   
                    // Read metarule index
                    if (line.contains(":"))
                    {
                        delims = "[:]+";
                        tokens = line.split(delims);
                        n = Integer.parseInt(tokens[0]);
                    }
                    
                    // Add metarule to list
                    else if (line.contains("<"))
                    {
                        delims = "[<]+";
                        tokens = line.split(delims);
                        Type type1 = null, type2 = null;
                        String[] atoms, subatomic, subsubatomic;
                        assert(tokens.length == 2):"Incorrect metarule format.";
                        
                        // Process both types represented in the formula
                        for (int i = 0; i < tokens.length; i++)
                        {
                            Type t; // type in question
                            ArrayList<Atom> atomList = new ArrayList<Atom>();
                            delims = "[ ]+";
                            atoms = tokens[i].split(delims);
                                                      
                            // Process the individual atoms of each type
                            for (int j = 0; j < atoms.length; j++)
                            {
                                Atom a;
                                delims = "[\\^]+";
                                subatomic = atoms[j].split(delims);
                                int adj = Integer.parseInt(subatomic[1]);
                                
                                // Process generic types 
                                if (subatomic[0].contains("*"))
                                {
                                    a = new Atom(subatomic[0], adj, true);
                                }
                                
                                // Process markers (if any) 
                                else 
                                {
                                    ArrayList<Marker> markers = new 
                                        ArrayList<Marker>(); // marker list
                                    delims = "[_]+";
                                    subsubatomic = subatomic[0].split(delims);
                                    for (int k = 1; k < subsubatomic.length; k++)
                                    {
                                        markers.add(new Marker(k-1, 
                                                Integer.parseInt(subsubatomic[k])));
                                    }
                                    a = new Atom(subsubatomic[0], Integer.parseInt(subatomic[1]),
                                                 markers);
                                }
                                atomList.add(a);
                            }
                            t = new Type(atomList);
                            if (i == 0)
                                type1 = t;
                            else
                                type2 = t;
                        }
                        
                        // Build and add metarule
                        MetaRule m = new MetaRule(n, type1, type2);
                        metarules.add(m);
                    }
                }           
                
                // Section 5: LEXICAL ENTRIES
                else if (section == 5) 
                {
                    Entry newEntry;
                    String lexeme, types;
                    Atom a1, a2;
                    ArrayList<Type> typeList = new ArrayList<Type>(); 
                    ArrayList<Type> typeList_temp = new ArrayList<Type>();
                    ArrayList<Type> moreTypes = new ArrayList<Type>();
                    boolean alreadySeen = false;
                    delims = "[:]+"; 
                    tokens = line.split(delims);
                    assert(tokens.length == 2):"Incorrect entry format: " + tokens[0];
                    lexeme = tokens[0];
                    types = tokens[1];
                    delims = "[,]+";
                    tokens = types.split(delims);
                    
                    // Determine whether entry already exists
                    for (Entry e: entries) 
                    {
                        if (lexeme.equals(e.getLex()))
                            alreadySeen = true;
                    }
                    
                    // Characterize and generate new entry
                    if (!alreadySeen)
                    {
                        newEntry = new Entry(lexeme, tokens); 
                        int index = 0;
                        
                        // Perform three successive metarule applications
                        while (index < 3)
                        {
                            typeList = newEntry.getTypes();
                            typeList_temp = new ArrayList<Type>();
                            moreTypes = new ArrayList<Type>();
                            
                            // Genrate a copy of typeList that can be safely
                            // modified within the inner loop
                            for (Type toCopy: typeList)
                            {
                                typeList_temp.add(toCopy); 
                            }
                            
                            // Apply metarules to each type
                            for (Type t_cur: typeList)
                            {
                                for (MetaRule m: metarules)
                                {
                                    Type t_meta = m.getType1(); 
                                    ArrayList<Atom> atoms_cur = t_cur.getAtoms();
                                    ArrayList<Atom> atoms_meta = t_meta.getAtoms();
                                    boolean isCaseOf = false;
                                    int nongenerics = 0;
                                    int matches = 0;
                                    
                                    // Create template for use with generics
                                    int[] template = new int[atoms_cur.size()];
                                    for (int i=0; i < template.length; i++)
                                    {
                                        template[i] = -1;
                                    }
                                    for (int j=0; j<atoms_meta.size(); j++)
                                    {
                                        for (int i=0; i<atoms_cur.size(); i++)
                                        {
                                            a1 = atoms_cur.get(i);
                                            a2 = atoms_meta.get(j);
                                            if ((!(a2.isGeneric())) && (a1.equals(a2)))
                                                // adjoint taken into account in this equality
                                            {
                                                template[i] = j;
                                            }
                                        }
                                    }
                                    
                                    /* Determine whether t_cur is a case of t_meta
                                     NB: This algorithm *FAILS* if a compound type 
                                     contains two identical atoms */
                                    for (Atom a: atoms_meta)
                                    {
                                        if (!(a.isGeneric()))
                                            nongenerics++;
                                    }
                                    for (int i = 0; i < template.length; i++)
                                    {
                                        // This precludes boundary problems 
                                        if ((template[i] == 0) && (i != 0))
                                            matches += 0;
                                        else if ((template[i] == atoms_meta.size() - 1) 
                                                     && (i != template.length - 1))
                                            matches += 0;
                                        else if (template[i] >= 0)
                                            matches++;     
                                    }
                                    if (matches == nongenerics)
                                    {
                                        isCaseOf = true;
                                    }
                                    
                                    // If t_cur is a case of t_meta, instantiate generic atoms
                                    if (isCaseOf) 
                                    {
                                        ArrayList<Atom> atomList = new ArrayList<Atom>();
                                        int i = 0;
                                        int j = 0;
                                        while ((i < template.length) && (j < atoms_meta.size()))
                                        {     
                                            // Compile list of atoms a generic atom might represent
                                            ArrayList<Atom> shortList = new ArrayList<Atom>();
                                            while ((i < template.length) && (template[i] < 0))
                                            {
                                                shortList.add(atoms_cur.get(i).copy());
                                                i++;
                                            }
                                            
                                            if (atoms_meta.get(j).isGeneric()) 
                                            {
                                                atomList.add(new Atom(atoms_meta.get(j).getSimpleType(), 
                                                                      atoms_meta.get(j).getAdjoint(), 
                                                                      true, shortList));
                                                j++;
                                            }
                                            
                                            while ((i < template.length) && (template[i] >= 0))
                                            {
                                                atomList.add(atoms_cur.get(i).copy());
                                                i++;
                                                j++;
                                            }
                                        }
                                        
                                        // Apply metarule to instantiated generic atom
                                        Type t1_new = new Type(atomList);
                                        Type t2 = m.getType2();
                                        Type t2_new = t2.copy(); 
                                        for (Atom x: t1_new.getAtoms())
                                        {
                                            for (Atom y: t2_new.getAtoms())
                                            {
                                                // If they have the same generic type
                                                if (x.isGeneric() && y.isGeneric()
                                                        && x.equalTypes(y))
                                                {
                                                    y.setAtoms(x.getAtoms());
                                                }
                                            }
                                        }
                                        
                                        // Assign appropriate adjoints and expand type representation
                                        ArrayList<Atom> a_temp = new ArrayList<Atom>();
                                        for (Atom z: t2_new.getAtoms())
                                        {
                                            if ((z.isGeneric()) && (z.getAtoms() != null))
                                            {
                                                for (Atom w: z.getAtoms())
                                                {
                                                    w.setAdjoint(z.getAdjoint());
                                                    a_temp.add(w);
                                                }
                                            }
                                            else if (!(z.isGeneric()))
                                            {
                                                a_temp.add(z);
                                            }
                                        }
                                        
                                        // Add extra types generated by the metarule,
                                        // provided they are not already listed
                                        Type t_temp = new Type(a_temp);
                                        boolean typeExists = false;
                                        for (Type q: typeList_temp)
                                        {
                                            if (q.equals(t_temp))
                                            {
                                                typeExists = true;
                                                break;
                                            }
                                        }
                                        if (!typeExists)
                                        {
                                            moreTypes.add(t_temp);
                                            typeList_temp.add(t_temp);
                                        }
                                    }
                                }
                            }
                            newEntry.addTypes(moreTypes);
                            index++;
                        }
                        entries.add(newEntry);
                    }
                }
                
                // Section 6: REDUCTION RULES
                else 
                {
                    Atom a1, a2;
                    int h1, h2;
                    delims = "[<]+";
                    tokens = line.split(delims);
                    assert(tokens.length == 2):"Incorrect reduction rule format.";
                    a1 = new Atom(tokens[0]);
                    a2 = new Atom(tokens[1]);
                    reductionrules.add(new ReductionRule(a1, a2));
                    h1 = getHash(a1);
                    h2 = getHash(a2);
                    mappings[h1][h2] = true;
                }
            }
            reader.close();
            Collections.sort(entries);
            Collections.sort(reductionrules);
        } 
        catch(FileNotFoundException fnfe) { 
            System.out.println(fnfe.getMessage());
        }
    } 
    
    // Return list of lexical entries
    public ArrayList<Entry> getEntries()
    {
        return entries;
    }
    
    // Return list of reduction rules
    public ArrayList<ReductionRule> getReductionRules()
    {
        return reductionrules;
    }
    
    // Return hash table representation of reduction rules
    public boolean[][] getMapping()
    {
        return mappings;
    }
     
    // Return list of possible atom types
    public Atom[] getAtomList()
    {
        return atomList;
    }
    
    // Return number of atom types
    public int getNumAtomTypes()
    {
        return atomList.length;
    }
    
    // Return dummy atom
    public Atom getDummy()
    {
        return dummyAtom;
    }
    
    // Return marker list
    public ArrayList<ArrayList<String>> getMarkers()
    {
        return markerList;
    }
    
    // Return list of verbal suffixes
    public ArrayList<String> getVerbalSuffixes()
    {
        return verbalSuffixes;
    }
    
    // Return list of nominal suffixes
    public ArrayList<String> getNounSuffixes()
    {
        return  nounSuffixes;
    }
    
    // Return list of adjectival suffixes
    public ArrayList<String> getAdjectiveSuffixes()
    {
        return adjectiveSuffixes;
    }
    
    // Return list of irregular forms
    public ArrayList<ArrayList<String>> getIrregulars()
    {
        return irregulars;
    }
    
    // Return list of standalone atom types
    public ArrayList<Atom> getStandalones()
    {
        return standalones;
    }
    
    // Return true if a1 reduces to a2 according to reduction rules, false otherwise
    public boolean reductionExists(Atom a1, Atom a2)
    {
        boolean flag = false;
        int h1 = getHash(a1);
        int h2 = getHash(a2);
        if (mappings[h1][h2])
            flag = true;
        return flag;
    }
    
    // Return string representation of Lambek grammar
    public String toString()
    {
        // List atom types
        String grammarString = "***Atom Types***\n";
        int k;
        for (k = 0; k < atomList.length-1; k++)
        {
            grammarString += atomList[k].getSimpleType() + ", ";
        }
        grammarString += atomList[k].getSimpleType() + "\n";
        
        // List marker inventory
        grammarString += "***Markers***\n";
        for (int i = 0; i < markerList.size(); i++)
        {
            grammarString += markerList.get(i).get(0) + ":\n";
            for (int j = 1; j < markerList.get(i).size(); j++)
            {
                grammarString += "  " + markerList.get(i).get(j) + "\n";
            }
        }
       
        // List lexical items and accompanying types
        grammarString += "***Dictionary Entries***\n";
        for (int i = 0; i < entries.size(); i++)
        {
            grammarString += entries.get(i).toString() + "\n";
        }        
        
        // Include replacement tables
        grammarString += "***Replacements***\n";
        grammarString += "Verbal suffixes: ";
        int n;
        for (n = 0; n < verbalSuffixes.size()-1; n++)
        {
            grammarString += verbalSuffixes.get(n) + ", ";
        }
        if (verbalSuffixes.size() != 0)
            grammarString += verbalSuffixes.get(n) + "\n";
        
        grammarString += "Noun suffixes: ";
        for (n = 0; n < nounSuffixes.size()-1; n++)
        {
            grammarString += nounSuffixes.get(n) + ", ";
        }
        if (nounSuffixes.size() != 0)
            grammarString += nounSuffixes.get(n) + "\n";
        
        grammarString += "Adjective suffixes: ";
        for (n = 0; n < adjectiveSuffixes.size()-1; n++)
        {
            grammarString += adjectiveSuffixes.get(n) + ", ";
        }
        if (adjectiveSuffixes.size() != 0)
            grammarString += adjectiveSuffixes.get(n) + "\n";
        grammarString += "Irregular forms:\n";
        
        for (ArrayList<String> s: irregulars)
        {
            grammarString += s.get(0) + "-->" + s.get(1) + "\n";
        }
        
        // List metarules
        grammarString += "***MetaRules***\n";
        for (int i = 0; i < metarules.size(); i++)
        {
            grammarString += metarules.get(i).toString() + "\n";
        }
       
        // Include reduction rules (in both list and table forms)
        grammarString += "***Reduction Rules***\n";
        for (int i = 0; i < reductionrules.size(); i++)
        {
            grammarString += reductionrules.get(i).toString() + "\n";
        }
        
        for (int i = -1; i < atomList.length; i++)
        {
            if (i < 0)
                grammarString += "  ";
            else 
                grammarString += atomList[i].getSimpleType() + " ";
            for (int j = 0; j < atomList.length; j++)
            {
                if (i < 0)
                {
                    grammarString += atomList[j].getSimpleType() + " ";
                }
                else
                {
                    if (mappings[i][j])
                    {
                        grammarString += "T ";
                    }
                    else
                    { 
                        grammarString += "F ";
                    }
                }
            }
            grammarString += "\n"; 
        }

        return grammarString;
    }
    
    // Return hash index of corresponding atom type
    public int getHash (Atom atom)
    {
        boolean notSeen = false;
        int i;
        for (i = 0; i < atomList.length; i++)
        {
            notSeen = false;
            if (atomList[i].equalTypes(atom))
                break; 
            else if (dummyAtom.equalTypes(atom))
                break;
            else 
                notSeen = true;
        }
        assert(!notSeen):"Atom not found in type list: " + atom.toString();
        return i;
    }
}