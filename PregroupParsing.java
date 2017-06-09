  /***********************************************************************
  * PregroupParsing.java
  * Author: Olivia Waring
  * Executes the central Lambek parsing algorithm 
  ************************************************************************/

import java.io.*;
import java.util.*;

public class PregroupParsing {
    public static int wordL; // index of word currently being processed
    public static Stack stack; // stack of atom indices to be processed
    public static LambekGrammar g; // representation of a Lambek Grammar
    public static boolean[] usedLinks; // list of linked atoms
    public static ArrayList<Link> links; // list of links
    public static ArrayList<Atom> atoms; // atom sequence for processing
    public static ArrayList<Atom> offendingAtoms; // list of atoms repeated more 
                                                  // than 4x in any given parse
   
    public static void main(String[] args) throws java.io.IOException { 
        
        // Build Lambek Grammar and print for verification
        g = new LambekGrammar("TibetanTest.txt");
        System.out.println(g.toString());
        File file = new File("TibetanCorpus.txt");   
        String line, delims, word;
        String[] tokens;
        ArrayList<ArrayList<Type>> toProcess; 
        ArrayList<ArrayList<Type>> allPaths; // all possible type sequences
        ArrayList<Type> currentPath = new ArrayList<Type>(); // current type sequence
        ArrayList<Entry> dictionary = g.getEntries(); // comprehensive list of entries
        double avgNesting = 0; // corpus-wide average degree of nesting
        int lineCount = 0; // number of lines in the data set
        boolean grammatical, redoVal;
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            
            // Read the corpus line by line
            while ((line = reader.readLine()) != null) 
            {
                lineCount++;
                delims = "[ .,;:?!-()]+"; // Remove punctuation
                tokens = line.split(delims); // Separate individual words
                toProcess = new ArrayList<ArrayList<Type>>();
                allPaths = new ArrayList<ArrayList<Type>>();
                
                // Process each word of the line, in order
                for (int i = 0; i < tokens.length; i++)
                {
                    int pass = 0; // Tracks the number of times a word has been modified
                    boolean found = false;
                    String storage;
                    storage = tokens[i];
                    word = storage;
                    
                    // Implement morphological modifications where necessary
                    while ((!found) && (pass <= 4))
                    {
                        int j = 0;
                        String newWord = "";
                        while ((j < dictionary.size()) && 
                                   (!(word.equalsIgnoreCase(dictionary.get(j).getLex()))))
                                j++;
                       
                        // If word is not found in dictionary, modify morphemes
                        if (j == dictionary.size()) 
                        {
                            if (pass == 4)
                                pass = 0;
                            else
                                word = storage; // reset word
                            System.out.println(word);
                            
                            // Verbal modifications
                            if (pass == 0) 
                            {
                                loop:
                                for (String s: g.getVerbalSuffixes())
                                {
                                    if (word.endsWith(s))
                                    {
                                        newWord = word.substring(0,word.length()-s.length());
                                        System.out.println(newWord);
                                      // newWord += "en"; Only for Deutsch!!! Hard Coded!
                                        word = newWord;
                                        System.out.println(word);
                                        break loop;
                                    }
                                }
                            }
                            
                            // Nominal modifications
                            else if (pass == 1) 
                            { 
                                for (String s: g.getNounSuffixes())
                                {
                                    if (word.endsWith(s))
                                    {
                                        newWord = word.substring(0,word.length()-s.length());
                                        word = newWord; 
                                    }
                                }
                            }
                            
                            // Adjectival modifications
                            else if (pass == 2) 
                            {
                                for (String s: g.getAdjectiveSuffixes())
                                {
                                    if (word.endsWith(s))
                                    {
                                        newWord = word.substring(0,word.length()-s.length());
                                        word = newWord; 
                                    }
                                }
                            }
                            
                            // Irregularities
                            else if (pass == 3) 
                            {
                                for (ArrayList<String> s: g.getIrregulars())
                                {
                                    if (word.contains(s.get(0)))
                                    {
                                        newWord = s.get(1);
                                        word = newWord; 
                                    }
                                }
                            }
                        }
                        else
                        {
                            found = true;
                            ArrayList<Type> types = dictionary.get(j).getTypes();
                            assert(types != null):"No type associated with this word.";
                            toProcess.add(i, types);
                        }
                        pass++;
                    }
                }
                
                // Generate all possible type sequences
                Tree tree = new Tree();
                for (ArrayList<Type> aWord: toProcess)
                {
                    tree.addGeneration(aWord);
                }
                allPaths = tree.getPaths();
                
                // Execute parsing algorithm for each possible path
                mainloop:
                for (ArrayList<Type> path: allPaths)
                {
                    grammatical = parse(path);
                    
                    // If first attempted parse successful, terminate process
                    if (grammatical) 
                    { 
                        System.out.println("First try!");
                        currentPath = path;
                        break mainloop;
                    }
                    else 
                    {          
                        // If path contains any high-frequency atom types, repeat analysis 
                        if (isSpecialCase(atoms)) 
                        {
                            redoVal = redo(path);
                            if(redoVal)
                            { 
                                System.out.println("Second try");
                                currentPath = path;
                                break mainloop;
                            }
                        }
                    } 
                }
                int i;
                
                // If successfully parsed, print "winning" path and list of links
                if (currentPath.size() > 0)
                {
                    for (i=0; i < currentPath.size()-1; i++)
                    {
                        System.out.print(currentPath.get(i).toString() + ", ");
                    }
                    System.out.println(currentPath.get(i).toString());
                    for (Link l: links)
                        System.out.print(l.toString()+" ");
                    avgNesting += degreeOfNesting(currentPath);
                }
                else
                    System.out.println("No solution. :(");
            }
            
            // Calculate and print corpus-wide average degree of nesting
            avgNesting = avgNesting / lineCount;
            System.out.println("Average degree of nesting: " + avgNesting);
        } 
        catch(FileNotFoundException fnfe) { 
            System.out.println(fnfe.getMessage());
        }
    } 
    
    // Main parsing engine
    public static boolean parse(ArrayList<Type> path)
    {
        boolean parsable = false;
        Stage next = new Stage();
        ArrayList<Atom> unlinked = new ArrayList<Atom>(); // list of unlinked atoms
        wordL = 0; // start from first word
        stack = new Stack(); // reset stack for a new parse
        links = new ArrayList<Link>(); // reset list of links
        atoms = typesToAtoms(path);
        usedLinks = new boolean[atoms.size()];
        
        // Download and process successive types
        for (Type t: path)
        {
            next = download(next, t);
            next = update(next);
        }

        // Generate list of unlinked atoms
        for (int i = 0; i < usedLinks.length; i++)
        {
            if (!(usedLinks[i]))
            {
                unlinked.add(atoms.get(i));
            }
        }
        
        // Determine whether unlinked atom is a standalone type
        for (Atom a: g.getStandalones())
        {
            if (unlinked.size() == 1)
            {
                if ((unlinked.get(0).equalTypes(a)) || 
                    (g.reductionExists(unlinked.get(0),a)))
                {
                    parsable = true;
                    break;
                }
            }
        }
        return parsable;
    }
    
    // Add new word to the current stage: return previous stage with new type  
    // added and word index incremented
    public static Stage download (Stage prevStage, Type nextT)
    {
        ArrayList<Type> typesSoFar = prevStage.getTypes();
        typesSoFar.add(nextT);
        int pos = prevStage.getPos();
        Stage curStage = new Stage(wordL++, pos, typesSoFar);
        return curStage;    
    }
    
    // Perform any reductions possible on previous stage and return updated stage
    public static Stage update(Stage prevStage)
    {
        ArrayList<Type> typesSoFar = prevStage.getTypes();
        int q = typesSoFar.get(typesSoFar.size()-1).getLength(); // length of latest type
        int pos = prevStage.getPos();
        int index1, index2, i;
        
        // Perform reductions or push new atoms onto stack
        for (i = pos; i < pos + q; i++)
        {
            index1 = stack.top(); 
            index2 = i;
            if ((index1 >= 0) && (reduces(atoms.get(index1),atoms.get(index2),g)))
            {
                stack.pop();
                links.add(new Link(index1, index2));
                usedLinks[index1] = true;
                usedLinks[index2] = true;
            }
            else
            {
                stack.push(i);
              //  System.out.println(stack.toString());
            }
        }
        Stage curStage = new Stage(wordL, i, typesSoFar);
        return curStage;    
    }  
    
    // Return true if a1 reduces to a2 according to the reduction rules in g,
    // false otherwise
    public static boolean reduces(Atom a1, Atom a2, LambekGrammar g)
    {
        boolean reduces = false;   
        int i1 = a1.getAdjoint();
        int i2 = a2.getAdjoint();
        
        // 'reduces' is true if atoms have equal markers and compatible adjoint
        // values, and if the relevant reduction rules exist
        if ((g.reductionExists(a1,a2)) || (g.reductionExists(a2,a1)))      
        {
            if ((i2 - i1 == 1) && (a1.equalMarkers(a2)))
            {
                reduces = true;
            }
        }
        
        // 'reduces' is false if one atom is a dummy and the other is not
        if ((a1.equalTypes(g.getDummy())) && (!(a2.equalTypes(g.getDummy()))))
        {
            reduces = false;
        }  
        if ((a2.equalTypes(g.getDummy())) && (!(a1.equalTypes(g.getDummy()))))
        {
            reduces = false;
        }  
        return reduces;
    } 
    
    // Convert list of types to corresponding list of atoms
    public static ArrayList<Atom> typesToAtoms(ArrayList<Type> types)
    {
        ArrayList<Atom> subset, allAtoms = new ArrayList<Atom>();
        for (Type t: types)
        {
            subset = t.getAtoms();
            for (Atom a: subset)
            {
                allAtoms.add(a);
            }
        }
        return allAtoms;
    }
    
    // Return true if the atom list contains a simple type that occurs 4 or more  
    // times (taking reduction rules into account), false otherwise
    public static boolean isSpecialCase(ArrayList<Atom> currentAtoms)
    {
        boolean isSpecial = false;
        Atom a; // successively assumes each atom type
        offendingAtoms = new ArrayList<Atom>();
        int[] counts = new int[g.getAtomList().length];
        
        // Determine number of atoms of each type (accounting for reduction rules)
        for (int i = 0; i < g.getAtomList().length; i++)
        {
            a = g.getAtomList()[i];  
            for (Atom b: currentAtoms)
            {
                if (g.reductionExists(b,a))
                {
                    counts[i]++;
                }
            }
            if (counts[i] >= 4)
            {
                isSpecial = true;
                offendingAtoms.add(a);
            }
        }
        return isSpecial;
    }
    
    // Systematically cancel pairs of offending atoms and reparse
    public static boolean redo(ArrayList<Type> oldPath) 
    {
        boolean grammatical = false;
        Atom dummyAtom = g.getDummy();
        Atom offendingAtom = offendingAtoms.get(0); // assume (for now) that only one exists
        ArrayList<Atom> oldSubset, newSubset, oldAtoms = typesToAtoms(oldPath);
        ArrayList<Type> newPath; // modified path
        ArrayList<Integer> positions = new ArrayList<Integer>();  
       
        // Determine positions at which the repeated atoms occur
        for (int i = 0; i < oldAtoms.size(); i++)
        {
            if ((oldAtoms.get(i).equalTypes(offendingAtom) || 
                 g.reductionExists(oldAtoms.get(i),offendingAtom)))
            {
                positions.add(i); 
            }
        }
        
        // "Cancel" each possible pair in turn and attempt a new parse
        ArrayList<Pair> pairs = computePairs(positions);
        for (Pair p: pairs)
        {
            int index = 0;
            newPath = new ArrayList<Type>();
            for (Type t: oldPath)
            {
                oldSubset = t.getAtoms();
                newSubset = new ArrayList<Atom>();
                
                // if a is a member of the pair, add dummy atom; otherwise add a
                for (Atom a: oldSubset)
                {
                    if ((index == p.getFirst()) || (index == p.getSecond()))
                    {
                        newSubset.add(new Atom(dummyAtom.getSimpleType(), 
                                                   a.getAdjoint()));
                    }
                    else 
                    {
                        newSubset.add(a);
                    } 
                    index++;
                }
                newPath.add(new Type(newSubset));
            }
            if (parse(newPath))
            {
                grammatical = true;
                break;
            }
        }
        return grammatical; 
    }
    
    // Return all possible pairs from the given list of positions
    public static ArrayList<Pair> computePairs(ArrayList<Integer> nums)
    {
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        for (int i = 0; i < nums.size(); i++)
        {
            for (int j = i+1; j < nums.size(); j++)
            {
                Pair p = new Pair(nums.get(i), nums.get(j));
                pairs.add(p);
            }
        }
        return pairs;
    }
    
    // Return maximum degree of link nesting for a given path
    public static int degreeOfNesting(ArrayList<Type> typePath)
    {
        ArrayList<Atom> atomicPath = typesToAtoms(typePath);
        Stack linkStack = new Stack();
        int maxHeight = 0;
        for (int i = 0; i < atomicPath.size(); i++)
        {
            if (areLinked(linkStack.top(), i))
                linkStack.pop();
            else if (hasLink(i))
                linkStack.push(i);
            if (linkStack.getHeight() > maxHeight)
            {
                maxHeight = linkStack.getHeight();
            }
        }
        return maxHeight;
    }
    
    // Return true if i and j are nodes of the same link, false otherwise
    public static boolean areLinked(int i, int j)
    {
        boolean flag = false;
        for (Link l: links)
        {
            if ((l.getNode1() == i) && (l.getNode2() == j))
            {
                flag = true;
                break;
            }
        }
        return flag;
    }
    
    // Return true if i appears in the list of links, false otherwise
    public static boolean hasLink(int i)
    {
        boolean flag = false;
        for (Link l: links)
        {
            if ((l.getNode1() == i) || (l.getNode2() == i))
            {
                flag = true;
                break;
            }
        }
        return flag;
    }

}  