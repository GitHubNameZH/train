package test.demo.services;

import com.sgcc.uap.rest.support.RequestCondition;
import com.sgcc.uap.rest.support.TreeNode;
import com.sgcc.uap.rest.support.IDRequestObject;
import com.sgcc.uap.rest.support.QueryResultObject;
import test.demo.domain.Department;

import java.util.List;
import java.util.Map;


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
public interface IDepartmentService {
	//--------------------------------------------------------------
	
	public List<TreeNode> listNodes (String nodeId);
//--------------------------------------------------------------------
	/**
	 * 根据主键 查询
	 * @param queryCondition
	 * @return
	 */
	public QueryResultObject getDepartmentById(String id);
	/**
	 * 根据idObject删除
	 * @param idObject
	 */
	public void remove(IDRequestObject idObject);
	/**
	 * 保存
	 * @param map
	 * @return
	 */
	public Department saveDepartment(Map map) throws Exception;
	/**
	 * 根据查询条件 查询
	 * @param queryCondition
	 * @return
	 */
	public QueryResultObject query(RequestCondition queryCondition);



}
