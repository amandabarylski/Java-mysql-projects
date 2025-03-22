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
			"1) Add a project",
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project"
	);
	// @formatter:on
	
	ProjectService projectService = new ProjectService();
	Project curProject = null;

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
					case 2:
						listProjects();
						break;
					case 3:
						selectProject();
						break;
					case 4:
						updateProjectDetails();
						break;
					case 5:
						deleteProject();
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

	private void deleteProject() {
		listProjects();
		
		Integer idToDelete = getIntInput("Enter the ID of the project you wish to delete (press Enter to cancel)");
		//I added this integer as I could not use getProjectId if the current project was null and it would throw an error.
		Integer currentId = Objects.isNull(curProject) ? null : curProject.getProjectId();
		
		//Without this outer if statement, if the user did not enter a number, then the projectService error would trigger,
		//with "project with ID=null" shown. I wanted it to just exit to the menu with a null, so I added the Objects.nonNull check.
		if(Objects.nonNull(idToDelete)) {
			projectService.deleteProject(idToDelete);
			System.out.println("Project with ID=" + idToDelete + " was deleted.");
			
			if(currentId.equals(idToDelete)) {
				curProject = null;
			}
		}

	}

	//Before testing my update method, I created the projects_data.sql file,
	//so that I could fully reset my SQL database in Workbench afterward.
	private void updateProjectDetails() {
		if (Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}
		
		//I modeled my printed messages for user inputs after the example given in the instructions.
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getEstimatedHours() + "]");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5) [" + curProject.getDifficulty() + "]");
		String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
		
		Project project = new Project();
		project.setProjectId(curProject.getProjectId());
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		
		projectService.modifyProjectDetails(project);
		
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}

	private void selectProject() {
		listProjects();
		
		Integer projectId = getIntInput("Enter a project Id to select a project");
		
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
	}

	private void listProjects() {
		//I left fetchAllProjects with the compile error until I finished the rest of the steps for this method.
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		projects.forEach(project -> System.out.println("   " + project.getProjectId() + ":   " + project.getProjectName()));
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
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not currently working with a project.");
		} else {
			System.out.println("\nYou are currently working with project: " + curProject);
		}
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
