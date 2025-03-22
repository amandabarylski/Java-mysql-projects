package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
	
	ProjectDao projectDao = new ProjectDao();

	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}

	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException("Project with Project Id:" + projectId + " does not exist."));
	}

	public void modifyProjectDetails(Project project) {
		//Even though the project ID is pulled from the current project and should exist,
		//it doesn't hurt to get in the habit of this sort of checks, so I followed the example in the instructions.
		if(!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
		}

	}

	public void deleteProject(Integer projectId) {
		if(!projectDao.deleteProject(projectId)) {
			throw new DbException("Project with ID=" + projectId + " does not exist.");
		}
	}

}
