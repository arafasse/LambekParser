/*****************************************************************************
  * Stage.java
  * Author: Olivia Waring
  * Represents a processing stage in the pregroup parsing algorithm
  *****************************************************************************/

import java.util.ArrayList;

public class Stage
{
    int current_word; // index of current word being processed
    int position; // index of current atom being processed
    ArrayList<Type> typesSoFar; // list of types currently under consideration
    
    // Return a new, empty stage
    public Stage()
    {
        current_word = 0;
        position = 0;
        typesSoFar = new ArrayList<Type>();
    }
    
    // Return a new stage with the given current word index, current atom index,
    // and list of types
    public Stage(int word, int pos, ArrayList<Type> types)
    {
        current_word = word;
        position = pos;
        typesSoFar = types;
    }
    
    // Return index of current word being processed
    public int getCurrentWord()
    {
        return current_word;
    }
    
    // Return index of current atom being processed
    public int getPos()
    {
        return position;
    }
    
    // Return list of types
    public ArrayList<Type> getTypes()
    {
        return typesSoFar;
    }
    
    // Return string representation of state in question
    public String toString()
    {
        String typelist = "";
        int i;
        for (i = 0; i < typesSoFar.size() - 1; i++)
        {
            typelist += typesSoFar.get(i).toString();
            typelist += ",";
        } 
        typelist += typesSoFar.get(i).toString();
        String stageString = "("+current_word+";" + typelist + ";"+position+")";
        return stageString;
    }
    
    // Increment index of current atom
    public void incPos()
    {
        position += 1;
    }
}


