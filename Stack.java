/*****************************************************************************
  * Stack.java
  * Author: Olivia Waring
  * Represents a FILO data structure for processing atom sequences
  *****************************************************************************/

import java.util.ArrayList;

public class Stack
{
    ArrayList<Integer> stack; // list of atom indices
    
    // Return new stack object
    public Stack()
    {
        stack = new ArrayList<Integer>();
    }
    
    // Add new atom index to top of stack
    public void push(Integer i)
    {
        stack.add(i);
    }
    
    // Pop most recently-added item off stack
    public Integer pop()
    {
        return stack.remove(stack.size()-1);
    }
    
    // Return most recently-added item, but keep it on the stack; if stack is 
    // empty, return -1
    public Integer top()
    {
        Integer i = -1;
        if (stack.size() > 0)
        {
            i = stack.get(stack.size()-1);
        }
        return i;
    }
    
    // Return height of stack
    public int getHeight()
    {
        return stack.size();
    }
    
    // Return a string representation of stack
    public String toString()
    {
        String stackString = "[";
        if (stack.size() == 0)
            stackString += "]";
        else
        {
            int i;
            for (i = 0; i < stack.size()-1; i++)
            {
                stackString += stack.get(i);
                stackString += ",";
            } 
            stackString += stack.get(i) + "]";
        }
        return stackString;
    }
}

