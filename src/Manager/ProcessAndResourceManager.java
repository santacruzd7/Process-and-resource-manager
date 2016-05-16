package Manager;

/**
 * The ProcessAndResourceManager class will implement the manager for the processes and the resources along with the required functions
 * to intectact with it, including: create/destroy a process, request/release a resource and timeout. Moreover, a set of additional, 
 * private functions have been developed to aid in the functionality of the aforementioned functions (including the mandatory "scheduler").
 * @author David García Santacruz, ID#: 51062654
 */
public class ProcessAndResourceManager {
	
	private static final int NUM_PROCESSES = 15;
	private static final int NUM_RESOURCES = 4;
	
	Process [] processes;	// Array of currently active processes
	Resource [] resources;	// Array of resources
	List [] readyList;		// Array of indexes of the first element of each priority in the ready list
	int currentProcess;		// Index of the currently running process in the processes array
	
	
	/**
	 * Constructor.
	 */
	public ProcessAndResourceManager(){
		// Initialize the processes list and create all the potential PCBs marking them as unused
		processes = new Process[NUM_PROCESSES];
		for(int i = 0; i<processes.length; i++){
		processes[i] = new Process();
		}
		
		// Initialize the resources list and create all the RCBs
		resources = new Resource[NUM_RESOURCES];
		resources[0] = new Resource("R1", 1);
		resources[1] = new Resource("R2", 2);
		resources[2] = new Resource("R3", 3);
		resources[3] = new Resource("R4", 4);
		
		// Initialize the ready list. It's empty at the beginning, so the first element of each priority has index -1.
		readyList = new List[3];
		for(int i = 0; i<readyList.length; i++){
			readyList[i] = new List();
		}
		
		// Create the init process: it has priority 0, no parents nor younger
		// sibling, and it's initially ready (like any new process)
		processes[0] = new Process("init", State.RUNNING, -1, -1, 0);
		readyList[0].insert(processes, 0);
		currentProcess = 0;
	}	
	
	
	/**
	 * Initializes the manager and its parameters to the initial state.
	 * @return String to be output as a result of calling this function: the currently running process (which is 'init' after initializing).
	 */
	public String init(){
		// Initialize the processes list and create all the potential PCBs marking them as unused.
		processes = new Process[NUM_PROCESSES];
		for(int i = 0; i<processes.length; i++){
			processes[i] = new Process();
		}
		
		// Initialize the resources list and create all the RCBs.
		resources = new Resource[NUM_RESOURCES];
		resources[0] = new Resource("R1", 1);
		resources[1] = new Resource("R2", 2);
		resources[2] = new Resource("R3", 3);
		resources[3] = new Resource("R4", 4);
		
		// Initialize the ready list. It's empty at the beginning, so the first element of each priority has index -1.
		readyList = new List[3];
		for(int i = 0; i<readyList.length; i++){
			readyList[i] = new List();
		}
		
		// Create the init process: it has priority 0, no parents nor younger
		// sibling, and it's initially ready (like any new process).
		processes[0] = new Process("init", State.RUNNING, -1, -1, 0);
		readyList[0].insert(processes, 0);
		currentProcess = 0;
		
		// Call the scheduler.
		return scheduler();
	}
	
	
	/**
	 * Creates a new process.
	 * @param name	name of the process. Must be a single character and must be unique.
	 * @param p		priority of the process. Must be 1 or 2 (0 reserved for the init process).
	 * @return		String to be output as a result of calling this function: the currently running process if success or "error('explanation')" if there was an error.
	 */
	public String create(String name, int p){
		// Check the priority is valid. If it is not, there is an error.
		if(!(p == 1 || p == 2)){
			return "error(non-existent priority: " + p + ")";
		}
		
		// Check the name is a single char. If it is not, there is an error.
		if (name.length() != 1){
			return "error(non-char process name: " + name + ")";
		}
		
		// Check the name is unique. If it is not, there is an error.{
		for(Process proc : processes){
			if(name.equals(proc.PID)){ 
				return "error(duplicate process name: " + name + ")";
			}
		}
		
		// Find the first free entry in the processes list.
		int index = -1;
		for(int i = 0; i<processes.length; i++){
			if(!processes[i].used){
				index = i;
				break;
			}
		}
		
		// If there are not free entries in the processes list, there is an error.
		if(index == -1){
			return "error(all PCBs are used)";
		}
		
		// The currently running process will be the parent process of the new process.
		int parent = currentProcess;
		
		// Determine which will be the older sibling process of the new process.
		int olderSibling = findYoungestChild(parent);
		
		// Set parent's child if necessary.
		if(processes[parent].child == -1){
			processes[parent].child = index;
		}
		
		// Set older sibling's younger sibling if necessary.
		if(olderSibling != -1){
			processes[olderSibling].youngerSibling = index;
		}
		
		// Initialize the new process with the initialization parameters.
		processes[index] = new Process(name, State.READY, parent, olderSibling, p);
		
		// Insert the process into the ready list
		readyList[p].insert(processes, index);
		
		// Call the scheduler.
		return scheduler();
	}
	
	
	/**
	 * Destroys a process.
	 * @param name	name of the process. Must be a single character.
	 * @return		String to be output as a result of calling this function: the currently running process if success or "error: explanation" if there was an error.
	 */
	public String destroy(String name){
		// Check the name is a single char. If it is not, there is an error.
		if (name.length() != 1){
			return "error(non-char process name: " + name + ")";
		}
		
		// Determine the process index in the processes list.
		int index = -1;
		for(int i = 0; i<processes.length; i++){
			if(name.equals(processes[i].PID)){
				index = i;
			}
		}
		
		// If the process does not exist in the processes list, there is an error.
		if(index == -1){
			return "error(non-existent process:" + name + ")";
		}

		// Determine which is the currently running process.
		int currentlyRunning = currentProcess;
		
		// Check if the currently running process is an ancestor of the process to be destroyed.
		boolean ancestor = false;
		int parent = processes[index].parent;
		while(parent != -1){
			if(parent == currentlyRunning){
				ancestor = true;
				break;
			}
			parent = processes[parent].parent;
		}
		
		// The process can only be destroyed by itself or by any of its ancestors.
		if(!(index == currentlyRunning || ancestor)){
			return "error(a process can only be destroyed by itself or by any of its ancestors)";
		}
		
		// Destroy all the process' offspring
		killTree(index);
		
		// Call the scheduler
		return scheduler();
	}
	
	
	/**
	 * Recursively wipes out all the descendant processes of the process 
	 * to be destroyed as well as the process itself.
	 * @param processIndex	index of the process in the processes list.
	 */
	private void killTree(int processIndex) {
		// Determine the child of the process to be killed to recursively kill the whole process' offspring.
		int currentProcess = processes[processIndex].child;
		int previousProcess = -1;
		
		// Traverse the process children killing the tree of each child.
		while(currentProcess != -1){
			previousProcess = currentProcess;
			currentProcess = processes[currentProcess].youngerSibling;
			killTree(previousProcess);
		}
		
		// Release all the resources the process was holding and make them available.
		// Also, check if any of the requests for blocked processes can be satisfied.
		for(int i = 0; i<processes[processIndex].holdResources.length; i++){
			// Only update resource-related structures when the process is actually holding the resource.
			if(processes[processIndex].holdResources[i] > 0){
				resources[i].availableUnits += processes[processIndex].holdResources[i];
				checkResourceGranting(i);
			}
		}
		
		// Update family tree links
		int parent = processes[processIndex].parent;
		int olderSibling = processes[processIndex].olderSibling;
		int youngerSibling = processes[processIndex].youngerSibling;
		
		if (processes[parent].child == processIndex){
			processes[parent].child = youngerSibling;
		}
		
		if(olderSibling != -1){
			processes[olderSibling].youngerSibling = youngerSibling;
		}
		
		if(youngerSibling != -1){
			processes[youngerSibling].olderSibling = olderSibling;
		}
		
		// Update the ready list links
		int p = processes[processIndex].priority;
		readyList[p].remove(processes, processIndex);
		
		// Update the resources' waiting lists links
		for(int i = 0; i<resources.length; i++){
			resources[i].waitingList.remove(processes, processIndex);
		}
		
		// Delete PCB (mark as unused)
		processes[processIndex] = new Process();
	}
	
	
	/**
	 * Requests a given number of units of the specified resource for the currently running process.
	 * @param RID		name of the resource. Must be a valid name.
	 * @param numUnits	number of units requested. Must be positive.
	 * @return			String to be output as a result of calling this function: the currently running process if success or "error: explanation" if there was an error.
	 */
	public String request(String RID, int numUnits){
		// Check that the number of units is greater than 0. If not, there is an error.
		if(numUnits <= 0){
			return "error(the number of units requested must be positive)";
		}
		
		// Get the index of the resource in the resources list.
		int resIndex = getResourceIndex(RID);
		
		// If the resource does not exist, there is an error.
		if(resIndex == -1){
			return "error(non-existent resource: " + RID + ")";
		}
		
		// If we try to request more than the maximum possible units of a resource, there is an error.
		if(numUnits > resources[resIndex].totalUnits){
			return "error(request too many units: " + numUnits + "/" + RID + ")";
		}
		
		// Determine whether the request can be granted or not.
		int currentlyRunning = currentProcess;
		// If the resource can be granted (enough available units), the units are assigned to the process.
		if(numUnits <= resources[resIndex].availableUnits){
			resources[resIndex].availableUnits -= numUnits;
			processes[currentlyRunning].holdResources[resIndex] += numUnits;
		// If the resource cannot be granted (not enough available units), the process is updated.
		} else {
			processes[currentlyRunning].requestedResources[resIndex] += numUnits;	// The process' requested resources are incremented
			processes[currentlyRunning].type = State.BLOCKED;	// The process becomes block
			processes[currentlyRunning].list = RID;				// The process is listed in the resource's waiting list
			int p = processes[currentlyRunning].priority;		
			readyList[p].remove(processes, currentlyRunning);	// The process is removed from the ready list
			resources[resIndex].waitingList.insert(processes, currentlyRunning);	// and inserted into the resource's waiting list
		}
		
		// Call the scheduler
		return scheduler();
	}
	
	
	/**
	 * Releases the given number of units of the specified resource from the current process.
	 * @param RID		name of the resource. Must be a valid name.
	 * @param numUnits	number of units requested. Must be positive.
	 * @return		String to be output as a result of calling this function: the currently running process if success or "error: explanation" if there was an error.
	 */
	public String release(String RID, int numUnits){
		// Check that the number of units is greater than 0. If not, there is an error.
		if(numUnits <= 0){
			return "error(the number of units requested must be positive)";
		}
		
		// Get the index of the resource in the resources list.
		int resIndex = getResourceIndex(RID);
		
		// If the resource does not exist, there is an error.
		if(resIndex == -1){
			return "error(non-existent resource:" + RID + ")";
		}
		
		// If we try to release a resource the currently running process is not holding, there is an error.
		if(processes[currentProcess].holdResources[resIndex] == 0){
			return "error(not holding resource: " + RID + ")";
		}
		
		// If we try to release more than the maximum possible units of a resource, there is an error.
		if(numUnits > processes[currentProcess].holdResources[resIndex]){
			return "error(release too many units: " + numUnits + "/" + RID + ":" + processes[currentProcess].holdResources[resIndex] + ")";
		}
		
		resources[resIndex].availableUnits += numUnits;		// The resource's available units are updated.
		
		int currentlyRunning = currentProcess;
		processes[currentlyRunning].holdResources[resIndex] -= numUnits;	// The processe's hold resources are updated.
		
		// After releasing a resource, check if the first process in that resource's waiting list can have its request satisfied.
		checkResourceGranting(resIndex);	
		
		// Call the scheduler
		return scheduler();
	}
	
	
	/**
	 * Schedules the next process to be run.
	 */
	private String scheduler(){
		// Find the highest priority process, p
		int p = highestPriorityProcess();
		// Determine if the current process should still be the current process
		if (processes[currentProcess].priority < processes[p].priority ||
				processes[currentProcess].type != State.RUNNING || !processes[currentProcess].used){
			// If the current process is not still the running process, the highest priority process runs.
			currentProcess = p;
			processes[currentProcess].type = State.RUNNING;
		}
		
		return processes[currentProcess].PID;
	}
	
	
	/**
	 * Simulates a hardware interrupt.
	 */
	public String timeout(){
		// Take the head process in the highest priority ready list, remove it and insert it again.
		int p = processes[currentProcess].priority;
		readyList[p].remove(processes, currentProcess);
		processes[currentProcess].type = State.READY;
		readyList[p].insert(processes, currentProcess);
		
		// Call the scheduler
		return scheduler();
	}
	
	
	/**
	 * Finds the first process in the highest priority of the ready list.
	 * @return the index of the highest priority process in the ready list.
	 */
	private int highestPriorityProcess() {
		int procIndex = -1;
		// Check each priority ready list. The first priority list with a process provides the highest priority process.
		for(int i = 2; i>=0; i--){
			if(readyList[i].head != -1){
				procIndex = readyList[i].head;
				break;
			}
		}
		return procIndex;
	}
	
	
	/**
	 * Provides the index of the resource in the resource array.
	 * @param RID	name of the resource.
	 * @return		index of the resource in the resource array. Returns -1 if it is an invalid resource name.
	 */
	private int getResourceIndex(String RID) {
		int resIndex;
		// The RID determines the index of the resource in the resources array.
		switch (RID){
			case "R1":
				resIndex = 0;
				break;
			case "R2":
				resIndex = 1;
				break;
			case "R3":
				resIndex = 2;
				break;
			case "R4":
				resIndex = 3;
				break;
			default:
				resIndex = -1;
				break;
		}
		return resIndex;
	}
	
	
	/**
	 * Finds the most recently created child for a given process.
	 * @param processIndex	index of the given process in the processes array.
	 * @return				index of the last child of the given process in the processes array. Returns -1 if the process do not have any children.
	 */
	private int findYoungestChild(int processIndex) {
		int currentProcess = processes[processIndex].child;
		int previousProcess = -1;
		
		// Traverse the process children until we reach the last process.
		while(currentProcess != -1){
			previousProcess = currentProcess;
			currentProcess = processes[currentProcess].youngerSibling;
		}
		return previousProcess;
	}

	
	/**
	 * Checks, in a FIFO fashion, if the requests made by processes in the waiting list can now be satisfied. If so, the resources are granted.
	 * @param resIndex	index of the resource in the resources list.
	 */
	private void checkResourceGranting(int resIndex){
		int headProcess = resources[resIndex].waitingList.head;
		// As long as there is a process in the resource's waiting list and its request can be satisfied...
		while(headProcess != -1 && 
				resources[resIndex].availableUnits >= processes[headProcess].requestedResources[resIndex]){
			// The resources units are granted to the process
			resources[resIndex].availableUnits -= processes[headProcess].requestedResources[resIndex];	// The units are no longer available for the resource...
			processes[headProcess].holdResources[resIndex] += processes[headProcess].requestedResources[resIndex];	// The units are given to the process to hold...
			processes[headProcess].requestedResources[resIndex] -= processes[headProcess].requestedResources[resIndex];	// and they are no longer requested.
			
			// The process heading the resource's waiting list, is removed from the waiting list and inserted in the ready list
			resources[resIndex].waitingList.remove(processes, headProcess); // Removed from waiting list
			processes[headProcess].type = State.READY;	// Become ready
			processes[headProcess].list = "RL";
			int p = processes[headProcess].priority;
			readyList[p].insert(processes, headProcess);	// Inserted in ready list
			
			// The head process of the resource's waiting list is updated for next iteration
			headProcess = resources[resIndex].waitingList.head;
		}
	}
}
