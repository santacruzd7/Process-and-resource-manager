package Shell;

import java.io.*;

import Manager.ProcessAndResourceManager;

/**
 * The TestShell class will implement the driver for the manager. It provides for reading commands from and writing results to file from 
 * an USB memory.
 * @author David García Santacruz, ID#: 51062654
 */
public class TestShell {
	
	ProcessAndResourceManager manager;
	File inputFile;		// File where commands are to be read.
	File outputFile;	// File where output is to be written.
	
	
	/**
	 * Class constructor.
	 * Initializes the input and output file paths as well as the manager.
	 */
	public TestShell(){
		manager = new ProcessAndResourceManager();
		inputFile = new File("E:/input.txt");
		outputFile = new File("E:/54062651.txt"); //54062651
    	
		// Check existance of input and output files
    	try {
    		if(!inputFile.exists()) {
    			System.out.println("ERROR: input file doesn't exist");
    		}
    		outputFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Reads the input file commands and executes them individually.
	 */
	public void run(){
		// This will reference one line - one command of the input file - at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(inputFile);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            // Every output starts with "init" even if the "init" command is not explicitly issued in the first case
            // (it is the system initialization)
            executeCommand("init");

            // Read all the lines - commands - of the input file and execute the command
            while((line = bufferedReader.readLine()) != null) {
            	System.out.println(line);
            	executeCommand(line);
            }    

            // Always close files.
            bufferedReader.close();            
        } catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + inputFile + "'");                
        } catch(IOException ex) {
            System.out.println("Error reading file '" + inputFile + "'");                   
        }
	}
	
	
	/**
	 * Executes the given command, writing the output to the output file.
	 * @param input		String containing the command to be executed.
	 */
	private void executeCommand(String input){
        try {
            // Assume default encoding.
            FileWriter fileWriter = new FileWriter(outputFile, true);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // For each blank line in the input file (indicating end of a test sequence), 
            // generate a blank line in the output file (for visual separation).
            if (input.trim().equals("")){
    			bufferedWriter.newLine();
    			
    			// Always close files.
                bufferedWriter.close();
    			return;
    		}
            
            // Omit comments
            if (input.charAt(0) == ('#')){
    			// Always close files.
                bufferedWriter.close();
    			return;
    		}
               		
    		// Parse the input String into an array containing each word
    		String [] command = input.split("\\s+");
    		for(String s : command){
    			s = s.replaceAll("[^\\w]", "");
    		}
    		
    		// Check the number of parameters (number of words in the command), if too much error
    		int num_params = command.length;
    		if(!(num_params >= 1 && num_params<=3)){
    			bufferedWriter.write("error");
    			bufferedWriter.newLine();
    			
    			// Always close files.
                bufferedWriter.close();
    			return;
    		}
    		
    		// Determine which manager function to call depending on the command and its paramenters
    		if(num_params == 1 && command[0].equals("init")){
    			bufferedWriter.write(manager.init());
    		} else if (num_params == 3 && command[0].equals("cr")){
    			bufferedWriter.write(manager.create(command[1], Integer.parseInt(command[2])));
    		} else if (num_params == 2 && command[0].equals("de")){
    			bufferedWriter.write(manager.destroy(command[1]));
    		} else if (num_params == 3 && command[0].equals("req")){
    			bufferedWriter.write(manager.request(command[1], Integer.parseInt(command[2])));
    		} else if (num_params == 3 && command[0].equals("rel")){
    			bufferedWriter.write(manager.release(command[1], Integer.parseInt(command[2])));
    		} else if (num_params == 1 && command[0].equals("to")){
    			bufferedWriter.write(manager.timeout());
    		} else {
    			bufferedWriter.write("error");
    		}
    		
    		bufferedWriter.write(" ");
    		
            // Always close files.
            bufferedWriter.close();
        } catch(IOException ex) {
            System.out.println("Error writing to file '" + outputFile + "'");
        }
	}

	
	public static void main(String[] args) {
		TestShell driver = new TestShell();
		driver.run();
	}

}
