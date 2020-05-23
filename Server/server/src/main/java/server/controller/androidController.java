package server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import server.DAO.BooksDAO;
import server.DAO.TBDAO;
import server.model.TBModel;

@Controller
@RequestMapping("/event")
public class androidController {

	@Autowired
	private TBDAO tbdao;
	@Autowired
	private BooksDAO booksDAO;

	@RequestMapping("/test")
	public String testFun(){
		System.out.println("testFun is Called + " + tbdao.findByName("test").toString());
		return "returned from Test Fun";
	}
	@RequestMapping(value = "/addTest", method = RequestMethod.POST)
	public void addTest(@RequestBody TBModel tbModel){
		System.out.println("addTest is called ");
		tbdao.insertTBModel(tbModel.getName(), tbModel.getX(), tbModel.getY(), tbModel.getZ(), tbModel.getFloor());
	}
	@RequestMapping(value = "/addTest", method = RequestMethod.GET)
	public void addTest2(){
		System.out.println("addTest(get)is called ");
	}
}
