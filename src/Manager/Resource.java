package Manager;

/**
 * The Resource class will implement the abstraction for a resource with multiple units along with the constructor for it.
 * @author David García Santacruz, ID#: 51062654
 */
public class Resource {
	
	String RID; // Name of the resource. It can only be R1, R2, R3, R4 (as per the format described).
	
	// STATUS
	int totalUnits;
	int availableUnits;
	
	List waitingList;		// Index of the first process (in the processes array) in the waiting list for the resource.
	
	
	/**
	 * Constructor
	 * @param name	name of the resource. It must be R1, R2, R3, R4.
	 * @param units	number of total units of the resource (1 for R1, 2 for R2, etc).
	 */
	public Resource(String name, int units){
		RID = name;
		totalUnits = units;
		availableUnits = units;
		waitingList = new List();			// Initially, the waiting list is empty.
	}
}
