package Manager;

/**
 * The Process class will implement the abstraction for a process along with the constructors for an unused and for an used process,
 * initializing the process parameters accordingly.
 * @author David García Santacruz, ID#: 51062654
 */
public class Process {
	
	// Name of the process. It must be a single char except for the 'init' process (whose name is 'init').
	String PID;
	
	// List of hold resources, result of a successful request.
	// States the number of units currently hold of each resource.
	int [] holdResources;
	
	// List of requested resources, result of an unsatisfied request.
	// States the number of units requested and waited of each resource.
	int [] requestedResources;
	
	// STATUS
	State type;		// State (running, ready, blocked).
	String list;	// List in which the process is currently (ready list or a resource waiting list).
	int next;		// Index of the next process in the list.
	
	// CREATION TREE
	int parent;
	int child;
	int olderSibling;
	int youngerSibling;
	
	int priority;	// Priority of the process. It must be 1 or 2, except for the 'init' process (whose priority is 0).
	
	boolean used;	// Allows to determine whether a PCB is being used or not.
	
	
	/**
	 * Constructor for an unusued PCB.
	 */
	public Process(){
		// The PCB is NOT used.
		used = false;
	}
	
	
	/**
	 * Constructor for a new PCB.
	 * @param thePID: name of the process. Single character.
	 * @param theState: state of the process (running, ready, blocked)
	 * @param theParent: parent process of the new process.
	 * @param theOlderSibling: process, sibling of the new process, created before it.
	 * @param thePriority: parent process of the new process.
	 */
	public Process(String thePID, State theState, int theParent, int theOlderSibling, int thePriority){
		PID = thePID;
		holdResources = new int [4];		// There are four possible resources (R1, R2, R3, R4).
		requestedResources = new int[4];	// There are four possible resources (R1, R2, R3, R4).
		type = theState;
		next = -1;							// A newly created process goes to the end of the ready list, so there is no next process.
		parent = theParent;
		child = -1;							// A newly created process doesn't have any children.
		olderSibling = theOlderSibling;
		youngerSibling = -1;				// A newly created process is the younger child of the parent process, thus it doesn't have a younger sibling.
		priority = thePriority;
		used = true;						// The PCB is used.
	}
}
