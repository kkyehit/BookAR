package server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import server.DAO.BooksDAO;
import server.DAO.TBDAO;
import server.model.BooksModel;
import server.model.TBModel;

@Controller
@RequestMapping(value = "/books")
public class BookController {
	@Autowired
	private BooksDAO booksDAO;

	@RequestMapping(value = "add", method = RequestMethod.POST)
	@ResponseBody
	public String addTable(@RequestBody BooksModel booksModel){
		System.out.println("books/addTable Called ("+booksModel.getBookName()+","+ booksModel.getAuthorName()+","+ booksModel.getTableName()+","+booksModel.getFloor()+")");
		booksDAO.insertBooksModel(booksModel.getBookName(), booksModel.getAuthorName(), booksModel.getTableName(), booksModel.getFloor());
		return "a";
	}

	@RequestMapping(value = "getListByName/{name}")
	@ResponseBody
	public List<BooksModel> getListByName(@PathVariable("name") String name){
		System.out.println("books/getListByName Called");
		return booksDAO.findByName(name);
	}

	@RequestMapping(value = "getListByAuthor/{author}")
	@ResponseBody
	public List<BooksModel> getListByAuthor(@PathVariable("author") String author){
		System.out.println("books/getListByAuthor Called");
		return booksDAO.findByAuthor(author);
	}

	@RequestMapping(value = "getListByState/{state}")
	@ResponseBody
	public List<BooksModel> getListByState(@PathVariable("state") String state){
		System.out.println("books/getListByState Called");
		return booksDAO.findByState(state);
	}

	@RequestMapping(value = "getListById/{id}")
	@ResponseBody
	public BooksModel getById(@PathVariable("id") String id){
		System.out.println("books/getListById Called");
		return booksDAO.findById(id);
	}


	@RequestMapping(value = "getAll")
	@ResponseBody
	public List<BooksModel> getAll(){
		System.out.println("books/getAll Called");
		return booksDAO.findAll();
	}
}
