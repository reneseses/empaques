package com.reneseses.empaques.web;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.reneseses.empaques.domain.Usuario;

@Controller
public class DefaultController {
	
	@RequestMapping(value="/")
	public String member(){
		Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(principal instanceof Usuario)
			return "redirect:/member";
		
		return "index";
	}

}
