package Manager;

/**
 * The List class will implement the basics of a generic list used concretely for the ready lists and the waiting lists.
 * A List will consist of the index in the processes array of the first process of the list. Each process points to 
 * the next element in the list. It provides methods for insert, delete and check if it is contained a process into a list
 * (given the process index and the processes array where it belongs).
 * @author David García Santacruz, ID#: 51062654
 */
public class List {

	int head; // Index of the first process (in the process array) in the list.
	
	
	/**
	 * Constructor. It creates an empty list (first process' index is -1).
	 */
	public List(){
		head = -1;
	}
	
	
	/**
	 * Inserts a process from a process array into the list.
	 * @param processes		array of processes containing the given process.
	 * @param processIndex	index of the process in the processes list.
	 */
	public void insert(Process [] processes, int processIndex) {
		// If the list is empty, the process to be inserted is the first element.
		if(head == -1){
			head = processIndex;
		} else {
			// Otherwise, traverse the list until we find the last process: its new next process will be the one inserted.
			int currentProcess = head;
			while(processes[currentProcess].next != -1){
				currentProcess = processes[currentProcess].next;
			}
			processes[currentProcess].next = processIndex;
		}
	}
	
	
	/**
	 * Removes a process from the list.
	 * @param processes		array of processes containing the given process.
	 * @param processIndex	index of the process in the processes list.
	 */
	public void remove(Process [] processes, int processIndex) {
		// The process can only be removed if it exists. If not, there is an error.
		if(!contains(processes, processIndex)){
			return;
		}
			// If the process to be removed is the first element, the first element becomes its successor.
			if(head == processIndex){
				head = processes[processIndex].next;
			} else {
				// Otherwise, traverse the list until we find the process preceding the process to be removed: 
				// its new next process will be the process' next element.
				int currentProcess = head;
				while(processes[currentProcess].next != processIndex){
					currentProcess = processes[currentProcess].next;
				}
				processes[currentProcess].next = processes[processIndex].next;
			}
			processes[processIndex].next = -1;
	}
	
	
	/**
	 * Checks if a process belongs to the list.
	 * @param processes		array of processes containing the given process.
	 * @param processIndex	index of the process in the processes list.
	 * @return 				'true' if the process is contained, 'false' otherwise.
	 */
	public boolean contains(Process [] processes, int processIndex) {
		// If the list is empty, the process is not contained.
		if(head == -1){
			return false;
		} else {
			// Traverse the list until we find the process (it exists) or not (it doesn't).
			int currentProcess = head;
			while(currentProcess != -1){
				if (currentProcess == processIndex){
					return true;
				}
				currentProcess = processes[currentProcess].next;
			}
		}
		return false;
	}
}
