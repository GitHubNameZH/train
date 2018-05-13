package test.demo.services;

import com.sgcc.uap.rest.utils.CrudUtils;
import com.sgcc.uap.rest.support.QueryResultObject;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.sgcc.uap.rest.support.QueryFilter;
import javax.persistence.criteria.Predicate;
import test.demo.domain.Department;
import test.demo.repositories.DepartmentRepository;
import com.sgcc.uap.rest.utils.RestUtils;
import org.springframework.data.domain.Page;
import java.util.Map;
import com.sgcc.uap.rest.support.RequestCondition;
import com.sgcc.uap.rest.support.TreeNode;

import org.springframework.data.jpa.domain.Specification;
import com.sgcc.uap.rest.support.IDRequestObject;
import javax.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.Path;


/**
 * <b>概述</b>：<br>
 * TODO
 * <p>
 * <b>功能</b>：<br>
 * TODO
 *
 * @author Lenovo
 * @date 2018-05-03 14:47:09
 * @since 1.0
 */
@Service
public class DepartmentService implements IDepartmentService{
	@Autowired
	private DepartmentRepository departmentRepository;
	
	
	@Override
	public QueryResultObject getDepartmentById(String id) {
		Department department = departmentRepository.findOne(id);
		return RestUtils.wrappQueryResult(department);
	}
	@Override
	public void remove(IDRequestObject idObject) {
		String[] ids = idObject.getIds();
		for (String id : ids){
			departmentRepository.delete(id);
		}
	}
	@Override
	public Department saveDepartment(Map map) throws Exception{
		Department department = new Department();
		if (map.containsKey("id")) {
			
			try {
				String id = (String) map.get("id");
				department = departmentRepository.findOne(id);
				CrudUtils.mapToObject(map, department,  "id");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				try {
					BeanUtils.populate(department, map);
				} catch (Exception e) {
					// TODO: handle exception
				}
				//CrudUtils.transMap2Bean(map, department);
				if (department.getDepParentId().equals("")) {
					department.setDepParentId(null);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return departmentRepository.save(department);
	}
	/** 
	 * 根据查询条件 查询
	 * @param queryCondition
	 * @return
	 */
	@Override
	public QueryResultObject query(RequestCondition queryCondition) {
		//--------------------------------------------------------
		List<QueryFilter> qList = queryCondition.getQueryFilter();
		PageRequest request = this.buildPageRequest(queryCondition);
		//--------------------------------------------------------
		//Object o = queryCondition.getFilter();
		//--------------------------------------------------------
		Specification<Department> specification = new Specification<Department>() {

			@Override
			public Predicate toPredicate(Root<Department> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = null;
				if(qList != null && !qList.isEmpty()){
					for(QueryFilter queryFilter : qList){
						Path<String> namePath = root.get(queryFilter.getFieldName());
						if(queryFilter.getValue()==null||queryFilter.getValue().equals("null")||queryFilter.getValue().equals("")){
							predicate = cb.isNull(namePath);
						}else{
							predicate = cb.equal(namePath, queryFilter.getValue());
						}
						query.where(predicate);
					}
				}
				return null;
			}
		};
		Page<Department> department = departmentRepository.findAll(specification,request);
		List<Department> result = null;
		long count = 0;
		result = department.getContent();
		count = department.getTotalElements();
		return RestUtils.wrappQueryResult(result, count);
		//----------------------------------------------------------------
/*		if(o != null && o instanceof List){
			List<Map<String, Object>> filter = (List<Map<String, Object>>)o;
			if(!filter.isEmpty()){
				for (Map<String, Object> map : filter) {
					List<Map<String, Object>> filter1 = (List<Map<String, Object>>) map.get("criterions");
					if(filter1 != null && !filter1.isEmpty()){
						return querySingle(queryCondition);
					}
				}
			}
		}*/ 
		//return queryCommon(queryCondition);
	}
	/**
	 * 字符串类型模糊查询示例：
	 * Predicate predicate = cb.like(root.get("employeeName"), "%" + 三 + "%");  查询名字中含有“三”的员工
	 * 
	 * 数字类型or时间类型示例:
	 * Predicate predicate = cb.equal(root.get("age"), 26); 查询年龄为26的员工
	 * Predicate predicate = cb.ge(root.get("age"), 26);	 查询年龄大于26的员工  同理大于等于为gt
     * Predicate predicate = cb.le(root.get("age"), 26);	 查询年龄小于26的员工  同理小于等于为lt
     * Predicate predicate = cb.between(root.get("age"), 26, 30);查询年龄在26与30之间的员工
	 * 若查询条件为时间类型，需用DateTimeConverter类的toDate方法将条件转换为对应的日期类型，然后进行查询
	 * 例如：Date date = (Date) DateTimeConverter.toDate(Date.class, queryFilter.getValue()); Predicate predicate = cb.equal(namePath, date);
	 * 主从表单页查询方法
	 * @param queryCondition
	 * @return
	 */
	private QueryResultObject querySingle(RequestCondition queryCondition) {
		List<QueryFilter> qList = getFilterList(queryCondition);
		Specification<Department> specification = new Specification<Department>() {
			@Override
			public Predicate toPredicate(Root<Department> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> preList = new ArrayList<Predicate>();
				if(qList != null && !qList.isEmpty()){
					for(QueryFilter queryFilter : qList){
					Path<String> namePath = root.get(queryFilter.getFieldName());
					Predicate predicate = cb.equal(namePath, queryFilter.getValue());
					preList.add(predicate);
					}
					query.where(preList.toArray(new Predicate[preList.size()]));
				}
				preList.clear();
				return null;
			}
		};
		PageRequest request = this.buildPageRequest(queryCondition);
		Page<Department> department = departmentRepository.findAll(specification,request);
		List<Department> result = new ArrayList<Department>();
		long count = 0;
		if(null != qList && !qList.isEmpty()){
			result = department.getContent();
			count = department.getTotalElements();
		}
		return RestUtils.wrappQueryResult(result, count);
	}
	/**
	 * 字符串类型模糊查询示例：
	 * Predicate predicate = cb.like(root.get("employeeName"), "%" + 三 + "%");  查询名字中含有“三”的员工
	 * 
	 * 数字类型or时间类型示例:
	 * Predicate predicate = cb.equal(root.get("age"), 26); 查询年龄为26的员工
	 * Predicate predicate = cb.ge(root.get("age"), 26);	 查询年龄大于26的员工  同理大于等于为gt
     * Predicate predicate = cb.le(root.get("age"), 26);	 查询年龄小于26的员工  同理小于等于为lt
     * Predicate predicate = cb.between(root.get("age"), 26, 30);查询年龄在26与30之间的员工
	 * 若查询条件为时间类型，需用DateTimeConverter类的toDate方法将条件转换为对应的日期类型，然后进行查询
	 * 例如：Date date = (Date) DateTimeConverter.toDate(Date.class, queryFilter.getValue()); Predicate predicate = cb.equal(namePath, date); 
	 * 查询方法
	 * @param queryCondition
	 * @param qList
	 * @return
	 */
	private QueryResultObject queryCommon(RequestCondition queryCondition) {
		List<QueryFilter> qList = queryCondition.getQueryFilter(); 
		Specification<Department> specification = new Specification<Department>() {
			@Override
			public Predicate toPredicate(Root<Department> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> preList = new ArrayList<Predicate>();
				if(qList != null && !qList.isEmpty()){
					for(QueryFilter queryFilter : qList){
						Path<String> namePath = root.get(queryFilter.getFieldName());
						Predicate predicate = cb.equal(namePath, queryFilter.getValue());
						preList.add(predicate);
					}
					query.where(preList.toArray(new Predicate[preList.size()]));
				}
				preList.clear();
				return null;
			}
		};
		PageRequest request = this.buildPageRequest(queryCondition);
		Page<Department> department = departmentRepository.findAll(specification,request);
		List<Department> result = new ArrayList<Department>();
		long count = 0;
		result = department.getContent();
		count = department.getTotalElements();
		return RestUtils.wrappQueryResult(result, count);
	}
	
	/**
	 * 获取条件列表
	 * @param queryCondition
	 * @return
	 */
	private List<QueryFilter> getFilterList(RequestCondition queryCondition) {
		List<QueryFilter> qList = new ArrayList<QueryFilter>();
		List<Map<String, Object>> filter = (List<Map<String, Object>>) queryCondition.getFilter();
		if(filter != null && !filter.isEmpty()){
			for (Map<String, Object> map : filter) {
				QueryFilter queryFilter = new QueryFilter();
				 List<Map<String, Object>> filter1 = (List<Map<String, Object>>) map.get("criterions");
				 Map<String, Object> map2 = filter1.get(0);
				 queryFilter.setFieldName(map2.get("fieldName").toString());
				 queryFilter.setValue(map2.get("value"));
				 qList.add(queryFilter);
			}
		}
		return qList;
	}
	/**
	 * 构建PageRequest
	 * @param queryCondition
	 * @return 页面请求对象
	 */
	private PageRequest buildPageRequest(RequestCondition queryCondition) {
		int pageIndex = 1, pageSize = 1;
		if (queryCondition.getPageIndex() != null && queryCondition.getPageSize() != null) {
			pageIndex = queryCondition.getPageIndex();
			pageSize = queryCondition.getPageSize();
		}
		return new PageRequest(pageIndex - 1, pageSize, null);
	}
	//-----------------------------------------------------------------------------
	@Override
	public List<TreeNode> listNodes(String nodeId) {
	
		List<TreeNode> treelist = new ArrayList<>();
		List<Department> deptList = new ArrayList<Department>();
		
		if (nodeId ==null) {
			deptList = departmentRepository.findBydepParentIdIsNull();
		}else {
			deptList = departmentRepository.findBydepParentId(nodeId);
		}
		
		for(Department dept : deptList){
			TreeNode node = new TreeNode();
			node.setId(dept.getId());
			node.setText(dept.getDepName());
			List<Department> subList = departmentRepository.findBydepParentId(dept.getId());
			if (subList.size()>0) {
				node.setHasChildren(true);
			}else {
				node.setHasChildren(false);
			}
			treelist.add(node);
			
		}
		return treelist;
	}


}
