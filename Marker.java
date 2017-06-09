/*****************************************************************************
  * Marker.java
  * Author: Olivia Waring
  * Represents a grammatical marking, i.e. number, gender, case, etc.
  *****************************************************************************/

public class Marker
{
    private int heading; // marker category
    private int type; // marker designation within that category
    
    // Return a new, neutral marker
    public Marker()
    {
        heading = -1;
        type = -1;
    }
    
    // Return a marker with the specified category and subdesignation
    public Marker(int bigtype, int subtype)
    {
        heading = bigtype;
        type = subtype;
    }
    
    // Return marker category
    public int getHeading()
    {
        return heading;
    }
    
    // Return marker subdesignation
    public int getType()
    {
        return type;
    }
    
    // Return a copy of the given marker object
    public Marker copy()
    {
        return (new Marker(heading, type));
    }
    
    // Return true if two markers are equivalent, false otherwise
    public boolean equals (Marker other)
    {
        return ((this.heading == other.heading) && (this.type == other.type));
    }
    
}