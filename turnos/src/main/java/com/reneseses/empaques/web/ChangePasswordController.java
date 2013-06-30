package com.reneseses.empaques.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.service.UsuarioServiceImpl;
import com.reneseses.empaques.formularios.ChangePasswordForm;

@RequestMapping("/member/password")
@Controller
public class ChangePasswordController {
	
	@Autowired
	private ChangePasswordValidator validator;

	@Autowired
	private MessageDigestPasswordEncoder messageDigestPasswordEncoder;

	@Autowired
    private UsuarioServiceImpl usuarioServiceImpl;
	
	@ModelAttribute("changePasswordForm")
	public ChangePasswordForm formBackingObject() {
		return new ChangePasswordForm();
	}

	@RequestMapping(value = "/index")
	public String index() {
		if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
			return "changepassword/index";
		}
		else {
			return "login";
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(@ModelAttribute("changePasswordForm") ChangePasswordForm form, BindingResult result) {
		validator.validate(form, result);
		if (result.hasErrors()) {
			return "changepassword/index"; // back to form
		}
		else {
			if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
				Usuario person = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				String newPassword = form.getNewPassword();
				person.setPassword(messageDigestPasswordEncoder.encodePassword(newPassword, null));
				usuarioServiceImpl.saveUsuario(person);
				return "changepassword/thanks";
			} else {
				return "login";
			}
		}
	}

	@RequestMapping(value = "/thanks")
	public String thanks() {
		return "changepassword/thanks";
	}

}
