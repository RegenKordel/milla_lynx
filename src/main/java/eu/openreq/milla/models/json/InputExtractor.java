package eu.openreq.milla.models.json;

import java.util.List;

//This class extracts models from the input Json

public class InputExtractor {
	Project project;
	Requirement requirement;
	List<Project> projects;
	List<Requirement> requirements;
	List<Requirement> dependent_requirements;
	List<Dependency> dependencies;
	
	public void setProject(Project project) {
		this.project = project;
	}
	
	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}
	
	public void setRequirement(Requirement requirement) {
		this.requirement = requirement;
	}
	
	public void setDependentRequirements(List<Requirement> dependent_requirements) {
		this.dependent_requirements = dependent_requirements;
	}

	public Project getProject() {
		return project;
	}
	
	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public Requirement getRequirement() {
		return requirement;
	}
	
	public List<Requirement> getRequirements() {
		return requirements;
	}
	
	public List<Requirement> getDependentRequirements() {
		return dependent_requirements;
	}
	
	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	public List<Dependency> getDependencies() {
		return dependencies;
	}
	
	public Requirement findRequirementById(String reqId) {
		for (Requirement r: requirements) {
			if (r.getId().equals(reqId)) {
				return r;
			}
		}
		return null;
	}
}
