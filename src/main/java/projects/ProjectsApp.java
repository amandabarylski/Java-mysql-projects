package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	//I prefer having my scanner up at the top.
	private Scanner scanner = new Scanner(System.in);
	
	//I followed the instructions to turn the formatter off for the list.
	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project"
	);
	// @formatter:on
	
	ProjectService projectService = new ProjectService();

	public static void main(String[] args) {
		//After writing this line, I had Eclipse create processUserSelections for me.
		new ProjectsApp().processUserSelections();
		
	}

	//I followed the instructions after generating the outer portion of the method with Eclipse.
	private void processUserSelections() {
		boolean done = false;
		
		while(!done) {
			try {
				int selection = getUserSelection();
				
				switch(selection) {
				//After adding the -1 and default cases, I tested the code.
				//The check for an integer worked, as well as the default case and the exitMenu() method.
					case -1:
						done = exitMenu();
						System.out.println("Exiting menu.");
						break;
					case 1:
						createProject();
						break;
					default:
						System.out.println("\n" + selection + " is not a valid selection. Try again.");
						break;
				}
			} catch(Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		}
	}

	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("Successfully created project: " + dbProject);
	}

	private boolean exitMenu() {
		return true;
	}

	private int getUserSelection() {
		printOperations();
		Integer input = getIntInput("Enter a menu selection: ");
		return Objects.isNull(input) ? -1 : input;
	}
	
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press enter to quit:");
		//I opted to use the Lambda expression
		operations.forEach(line -> System.out.println("   " + line));
	}
	
	//After generating this method with Eclipse, I changed the name for the input to prompt.
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		if(Objects.isNull(input)) {
			return null;
		}
			
		try {
			return Integer.valueOf(input);
		} catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number. Try again.");
		}
	}
	
	//After generating this method, I moved it to sit below getIntInput so I could more easily keep them organized.
	//I then followed the suggestion in the instructions and copied and pasted the body of IntInput and made my modifications.
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		if(Objects.isNull(input)) {
			return null;
		}
			
		try {
			return new BigDecimal(input).setScale(2);
		} catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number. Try again.");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		return input.isBlank() ? null : input.trim();
	}


}
