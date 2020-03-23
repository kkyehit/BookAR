package server.controller;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import server.DAO.TBDAO;
import server.model.TBModel;

@Controller
@RequestMapping(value = "/table")
public class tableController {
	@Autowired
	private TBDAO tbdao;

	@RequestMapping(value = "add", method = RequestMethod.POST)
	@ResponseBody
	public String addTable(@RequestBody TBModel tbModel){
		System.out.println("table/addTable Called");
		tbdao.insertTBModel(tbModel.getName(), tbModel.getX(), tbModel.getY(), tbModel.getZ());
		return "a";
	}

	@RequestMapping(value = "getListByName/{name}")
	@ResponseBody
	public TBModel getListByName(@PathVariable("name") String name){
		System.out.println("table/getList Called");
		return tbdao.findByName(name);
	}

	@RequestMapping(value = "getAll")
	@ResponseBody
	public List<TBModel> getAll(){
		System.out.println("table/getAll Called");
		return tbdao.findAll();
	}

}
