package server.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import server.model.TBModel;

@Mapper
public interface TBDAO {
	/*protected static final String NAMESPACE = "server.DAO.api";

	//@Autowired
	private SqlSession sqlSession;

	public TBModel findByName(String name){
		return sqlSession.selectOne(NAMESPACE + ".findByName", name);
	}*/

	TBModel findByName(String name);
	List<TBModel> findAll();
	void insertTBModel(String name, String x, String y, String z);
}
