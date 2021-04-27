package com.lymn.bugTracker.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.lymn.bugTracker.dto.ProjectViewDto;
import com.lymn.bugTracker.model.Bug;
import com.lymn.bugTracker.model.Project;

@Repository
public class ModifiedRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
	@Transactional
	public void saveBug(Bug bug) {
	    entityManager.createNativeQuery("INSERT INTO bug (name, description, project_id, severity, report_time, user_id, status) VALUES (?,?,?,?,?,?,?)")
	      .setParameter(1, bug.getName())
	      .setParameter(2, bug.getDescription())
	      .setParameter(3, bug.getProject().getId())
	      .setParameter(4, bug.getSeverity())
	      .setParameter(5, bug.getReportTime())
	      .setParameter(6, bug.getUser())//
	      .setParameter(7, bug.getStatus())
	      .executeUpdate();
	}
	
	@Transactional
	public boolean bugIsPresent(Long bugId, Long userId) {
		if(!entityManager.createNativeQuery("SELECT * FROM users_bugs WHERE bug_id = ? AND user_id = ?")
				.setParameter(1, bugId)
				.setParameter(2, userId)
				.getResultList().isEmpty()) {
			
			return true;
		}
		return false;
	}
	
	@Transactional
	public boolean projectIsPresent(Long projectId, Long userId) {
		if(!entityManager.createNativeQuery("SELECT * FROM users_projects WHERE project_id = ? AND user_id = ?")
				.setParameter(1, projectId)
				.setParameter(2, userId)
				.getResultList().isEmpty()) {
			
			return true;
		}else {			
			return false;
		}
	}
	//edit this add not completed
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Bug> findBugByUserId(Long projectId, Long userId){
		return entityManager.createNativeQuery("SELECT bug.id, bug.description, bug.name, bug.project_id, bug.severity, bug.report_time, bug.user_id, bug.status FROM bug "
				+ "JOIN users_bugs ON bug.id = users_bugs.bug_id "
				+ "JOIN user ON users_bugs.user_id = user.id "
				+ "WHERE user.id = ?1 AND bug.project_id = ?2 AND bug.status = 'ongoing'", Bug.class)//added ongoing
				.setParameter(1, userId)
				.setParameter(2, projectId)
				.getResultList();
	}
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Bug> findBugByStatus(Long projectId, String status){
		return entityManager.createNativeQuery("SELECT * from bug WHERE bug.project_id = ?1 AND bug.status = ?2",Bug.class)
				.setParameter(1, projectId)
				.setParameter(2, status)
				.getResultList();
	}
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Bug> findBugByCompletedStatus(){
		return entityManager.createNativeQuery("SELECT * from bug WHERE bug.status = 'completed'",Bug.class)
				.getResultList();
	}
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Project> findProjectByUserId(Long userId){
		 return entityManager.createNativeQuery("SELECT project.id, project.description, project.name, project.status FROM project "
				+ "JOIN users_projects on project.id = users_projects.project_id "
				+ "JOIN user on users_projects.user_id = user.id "
				+ "WHERE user.id = ? AND project.status = 'ongoing'", Project.class)
				.setParameter(1, userId)
				.getResultList();
		
	}
	
	
	@Transactional
	public void saveBugAuthority(Long bugId, Long userId) {
		entityManager.createNativeQuery("INSERT INTO users_bugs (user_id, bug_id) VALUES (?,?)")
	      .setParameter(1, userId)
	      .setParameter(2, bugId)
	      .executeUpdate();
	}
	@Transactional
	public void removeBugAuthority(Long bugId, Long userId) {
		entityManager.createNativeQuery("DELETE from users_bugs where user_id = ?1 and bug_id = ?2")
	      .setParameter(1, userId)
	      .setParameter(2, bugId)
	      .executeUpdate();
	}
	
	@Transactional
	public void saveProjectAuthority(Long projectId, Long userId) {
		entityManager.createNativeQuery("INSERT INTO users_projects (user_id, project_id) VALUES (?,?)")
	      .setParameter(1, userId)
	      .setParameter(2, projectId)
	      .executeUpdate();
	}
	@Transactional
	public void removeProjectAuthority(Long projectId, Long userId) {
		entityManager.createNativeQuery("DELETE from users_projects where user_id = ?1 and project_id = ?2")
	      .setParameter(1, userId)
	      .setParameter(2, projectId)
	      .executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ProjectViewDto> findProjectViewerByAuthority(Long id){
		List<ProjectViewDto> assignedUserList = new LinkedList<>();
		List<Object[]> ob1 = entityManager.createNativeQuery("SELECT user.id, user.first_name, user.last_name, user.email, role.name from user"
				+ " join users_projects on users_projects.user_id = user.id"
				+ " join users_roles on users_roles.user_id = user.id"
				+ " join role on role.role_id = users_roles.role_id"
				+ " where users_projects.project_id = ?")
				.setParameter(1, id)
				.getResultList();
		
		for(Object[] item : ob1) {
			ProjectViewDto projectViewDto = new ProjectViewDto();
			projectViewDto.setId(((BigInteger)item[0]).longValue());
			projectViewDto.setFirstName((String)item[1]);
			projectViewDto.setLastName((String) item[2]);
			projectViewDto.setEmail((String)item[3]);
			projectViewDto.setRole((String)item[4]);
			assignedUserList.add(projectViewDto);		
		}
		
		
		return assignedUserList;
	}
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ProjectViewDto> remainingUser(){
		List<ProjectViewDto> remainingUserList = new ArrayList<ProjectViewDto>();
		List<Object[]> ob1 = entityManager.createNativeQuery("SELECT user.id, user.first_name, user.last_name, user.email, role.name from user"
				+ " join users_roles on users_roles.user_id = user.id"
				+ " join role on role.role_id = users_roles.role_id"
				+ " where not role.name = \"ADMIN\" and not role.name=\"MANAGER\"").getResultList();
		for(Object[] item : ob1) {
			ProjectViewDto projectViewDto = new ProjectViewDto();
			projectViewDto.setId(((BigInteger)item[0]).longValue());
			projectViewDto.setFirstName((String)item[1]);
			projectViewDto.setLastName((String) item[2]);
			projectViewDto.setEmail((String)item[3]);
			projectViewDto.setRole((String)item[4]);
			remainingUserList.add(projectViewDto);		
		}
		return remainingUserList;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<ProjectViewDto> findBugViewerByAuthority(Long id){
		List<ProjectViewDto> assignedUserList = new LinkedList<>();
		List<Object[]> ob1 = entityManager.createNativeQuery("SELECT user.id, user.first_name, user.last_name, user.email, role.name from user"
				+ " join users_bugs on users_bugs.user_id = user.id"
				+ " join users_roles on users_roles.user_id = user.id"
				+ " join role on role.role_id = users_roles.role_id"
				+ " where users_bugs.bug_id = ?")
				.setParameter(1, id)
				.getResultList();
		
		for(Object[] item : ob1) {
			ProjectViewDto projectViewDto = new ProjectViewDto();
			projectViewDto.setId(((BigInteger)item[0]).longValue());
			projectViewDto.setFirstName((String)item[1]);
			projectViewDto.setLastName((String) item[2]);
			projectViewDto.setEmail((String)item[3]);
			projectViewDto.setRole((String)item[4]);
			assignedUserList.add(projectViewDto);		
		}
		
		
		return assignedUserList;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Project> findProjectByBugId(Long id){
		 return entityManager.createNativeQuery("select project.id, project.description, project.name from project "
				+ "join bug on bug.project_id = project.id "
				+ "where bug.id = ?", Project.class)
				.setParameter(1, id)
				.getResultList();
		
	}
	
	@Transactional
	public byte[] findImage(Long id) {
		return (byte[]) entityManager.createNativeQuery("select external_resources.content from external_resources where id = ?")
				.setParameter(1, id)
				.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ProjectViewDto> viewUserByRole(){
		List<ProjectViewDto> userList = new ArrayList<ProjectViewDto>();
		List<Object[]> ob1 = entityManager.createNativeQuery("SELECT user.id, user.first_name, user.last_name, user.email, role.name from user"
				+ " join users_roles on users_roles.user_id = user.id"
				+ " join role on role.role_id = users_roles.role_id"
				+ " where not role.name = \"ADMIN\"").getResultList();
		for(Object[] item : ob1) {
			ProjectViewDto projectViewDto = new ProjectViewDto();
			projectViewDto.setId(((BigInteger)item[0]).longValue());
			projectViewDto.setFirstName((String)item[1]);
			projectViewDto.setLastName((String) item[2]);
			projectViewDto.setEmail((String)item[3]);
			projectViewDto.setRole((String)item[4]);
			userList.add(projectViewDto);		
		}
		return userList;
	}
	
	@Transactional
	public boolean userRolePresent(Long userId) {
		return entityManager.createNativeQuery("SELECT * FROM users_roles where user_id = ?")
				.setParameter(1, userId).getResultList().isEmpty();
	}
	
	@Transactional
	public void AssignRole(Long userId, Long roleId) {
		entityManager.createNativeQuery("INSERT INTO users_roles (user_id, role_id) VALUES (?,?)")
	      .setParameter(1, userId)
	      .setParameter(2, roleId)
	      .executeUpdate();
	}
	
	@Transactional
	public void UpdateRole(Long userId, Long roleId) {
		entityManager.createNativeQuery("UPDATE users_roles SET role_id = ?"
				+ "where user_id = ?")
	      .setParameter(1, roleId)
	      .setParameter(2, userId)
	      .executeUpdate();
	}
	
	@Transactional
	public void bugStatus(Long bugId,String status) {
		entityManager.createNativeQuery("Update bug SET status = ?1 "
				+ "where id = ?2")
		.setParameter(1, status)
		.setParameter(2, bugId)
		.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ProjectViewDto> viewEditUserRoles(){
		List<ProjectViewDto> userList = new ArrayList<ProjectViewDto>();
		List<Object[]> ob1 = entityManager.createNativeQuery("SELECT user.id, user.first_name, user.last_name, user.email, role.name from user"
				+ " left outer join users_roles on users_roles.user_id = user.id"
				+ " left join role on role.role_id = users_roles.role_id").getResultList();
		for(Object[] item : ob1) {
			ProjectViewDto projectViewDto = new ProjectViewDto();
			projectViewDto.setId(((BigInteger)item[0]).longValue());
			projectViewDto.setFirstName((String)item[1]);
			projectViewDto.setLastName((String) item[2]);
			projectViewDto.setEmail((String)item[3]);
			projectViewDto.setRole((String)item[4]);
			userList.add(projectViewDto);		
		}
		return userList;
	}
	@Transactional
	public void bugDelete(Long bugId) {
		entityManager.createNativeQuery("DELETE from bug WHERE id = ?1")
		.setParameter(1, bugId)
		.executeUpdate();
		
	}
	@Transactional
	public void projectStatus(Long projectId, String status) {
		entityManager.createNativeQuery("UPDATE project SET status = ?1 WHERE id = ?2")
		.setParameter(1, status)
		.setParameter(2, projectId)
		.executeUpdate();
	}
	@Transactional
	public void projectDelete(Long projectId) {
		entityManager.createNativeQuery("DELETE from project WHERE id = ?1")
		.setParameter(1, projectId)
		.executeUpdate();
		
	}
	@Transactional
	public String getOldPassword(Long user) {
		return entityManager.createNativeQuery("select password from user where id = ?1")
		.setParameter(1, user)
		.getSingleResult().toString();
	}
	@Transactional
	public void setNewPassword(Long id, String password) {
		entityManager.createNativeQuery("UPDATE user SET password = ?1 WHERE id = ?2")
		.setParameter(1, password)
		.setParameter(2, id)
		.executeUpdate();
		
	}
	@Transactional
	public void setNewEmail(Long id, String email) {
		entityManager.createNativeQuery("UPDATE user SET email = ?1 WHERE id = ?2")
		.setParameter(1, email)
		.setParameter(2, id)
		.executeUpdate();
		
	}
	@Transactional
	public void editBug(Long bugId, String name, String description, String severity) {
		entityManager.createNativeQuery("UPDATE bug set name = ?1, description = ?2, severity=?3 "
				+ "WHERE id = ?4")
		.setParameter(1, name)
		.setParameter(2, description)
		.setParameter(3, severity)
		.setParameter(4, bugId)
		.executeUpdate();
		
	}
	
//	@Transactional
//	public  getTopReported() {
//		return entityManager.createNativeQuery("SELECT user_id "
//				+ "from bug "
//				+ "group by user_id "
//				+ "order by count(*) desc "
//				+ "limit 1")
//				.getResultList().get(0);
//	}
	
}
