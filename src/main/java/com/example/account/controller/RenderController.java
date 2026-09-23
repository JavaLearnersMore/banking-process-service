package com.example.account.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RenderController {
	
    @RequestMapping(value="/" , method = RequestMethod.GET)
	public ModelAndView accountPage() {
    	System.out.println("Inside controller");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("account_form");
		return mav;
		
	}
    
    
}
