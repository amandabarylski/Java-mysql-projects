package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
	
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	public Project insertProject(Project project) {
		// @formatter:off
		String sql = ""
			+ "INSERT INTO " + PROJECT_TABLE + " "
			+ "(project_name, estimated_hours, actual_hours, difficulty, notes)"
			+ "VALUES "
			+ "(?, ?, ?, ?, ?)";
		// @formatter:on
		
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				project.setProjectId(projectId);
				return project;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch(SQLException e) {
			throw new DbException(e);
		}
	}

	public List<Project> fetchAllProjects() {
		// I started with the SQL String.
		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
		
		//Next I started the try with resources block, with the SQLException in the outermost catch.
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			//The second try with resources has the rollback and general exception catch.
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				try(ResultSet rs = stmt.executeQuery()) {
					List<Project> projects = new LinkedList<>();
					//Since the examples in the video used the extract method, that's what I did as well.
					while(rs.next()) {
						projects.add(extract(rs, Project.class));
					}
					return projects;
				}
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public Optional<Project> fetchProjectById(Integer projectId) {
		//This method is set up fairly similarly to the fetchAllProjects with an inner and outer try with resources.
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try {
				Project project = null;
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)) {
					setParameter(stmt, 1, projectId, Integer.class);
					
					try(ResultSet rs = stmt.executeQuery()) {
						if(rs.next()) {
							project = extract(rs, Project.class);
						}
					}
				}
				//I wrote the getMaterials line, then copied and pasted and edited it for the other two.
				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}
				
				commitTransaction(conn);
				return Optional.ofNullable(project);
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	//I opted to have these three methods throw SQLExceptions rather than adding try/catch blocks.
	//I followed the example given in the instructions for the Category one as it had the different SQL string,
	//then copied and pasted the try with resources sections into the other two, editing it where needed.
	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
		// @formatter:off
		String sql = ""
			+ "SELECT c.* FROM " + CATEGORY_TABLE + " c "
			+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
			+ "WHERE project_id = ?";
		// @formatter:on
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
			
			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();
				
				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				
				return categories;
			}
		}
	}

	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
			
			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();
				
				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}
				
				return steps;
			}
		}
	}

	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
			
			try (ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();
				
				while (rs.next()) {
					materials.add(extract(rs, Material.class));
				}
				
				return materials;
			}
		}
	}

	public boolean modifyProjectDetails(Project project) {
		// @formatter:off
		//I was failing to update the project while testing.
		//I wasn't seeing the error message until I added the sysouts in the catch blocks, which seems strange as it was the error message that helped me.
		//Probably my own error in somehow not noticing it before, but once I did it was a quick fix as I had a syntax error
		//where I had accidentally added a + in the quotation marks right after UPDATE. On removing it the code immediately ran as intended.
		String sql = ""
			+ "UPDATE " + PROJECT_TABLE + " SET "
			+ "project_name = ?, "
			+ "estimated_hours = ?, "
			+ "actual_hours = ?, "
			+ "difficulty = ?, "
			+ "notes = ? "
			+ "WHERE project_id = ?";
		// @formatter:on
		
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				setParameter(stmt, 6, project.getProjectId(), Integer.class);
				
				boolean updated = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				return updated;
				
			} catch(Exception e) {
				rollbackTransaction(conn);
//				System.out.println("Rolled back transaction");
				throw new DbException(e);
			}
		} catch(SQLException e) {
//			System.out.println("SQL exception when starting transaction");
			throw new DbException(e);
		}
	}

	public boolean deleteProject(Integer projectId) {
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, projectId, Integer.class);
				
				boolean deleted = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				return deleted;
			} catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch(SQLException e) {
			throw new DbException(e);
		}
	}

	
	
}
