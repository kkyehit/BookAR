package server.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import server.model.BooksModel;

@Mapper
public interface BooksDAO {
	List<BooksModel> findByName(String name);
	List<BooksModel> findByAuthor(String author);
	List<BooksModel> findByState(String state);
	List<BooksModel> findAll();
	BooksModel findById(String id);
	void insertBooksModel(String bookName, String authorName, String tableName, String floor);
}
