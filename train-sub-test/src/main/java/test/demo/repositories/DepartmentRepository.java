package test.demo.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import test.demo.domain.Department;


/**
 * <b>概述</b>：<br>
 *	TODO
 * <p>
 * <b>功能</b>：<br>
 *	TODO
 *	
 * @author Lenovo
 * @date 2018-05-03 14:47:09
 * @since 1.0
 */
public interface DepartmentRepository extends JpaRepository<Department,String>,JpaSpecificationExecutor<Department> {
//------------------------------------------------
	//查询根节点
	public List<Department> findBydepParentIdIsNull();
	//查询子节点
	public List<Department> findBydepParentId(String nodeId);
	
}
