/**
 * 
 */
package com.reneseses.empaques.web;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.formularios.ChangePasswordForm;

@Service("changePasswordValidator")
public class ChangePasswordValidator implements Validator {

	@Autowired
	private MessageDigestPasswordEncoder messageDigestPasswordEncoder;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return ChangePasswordForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ChangePasswordForm form = (ChangePasswordForm) target;

		try {
			if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
				Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				if(null!=usuario){
					String storedPassword = usuario.getPassword();
					String currentPassword = form.getOldPassword();
					if (!messageDigestPasswordEncoder.isPasswordValid(storedPassword, currentPassword, null)) {
						errors.rejectValue("oldPassword",
								"changepassword.invalidpassword");
					}
					String newPassword = form.getNewPassword();
					String newPasswordAgain = form.getNewPasswordAgain();
					if (!newPassword.equals(newPasswordAgain)) {
						errors.reject("changepassword.passwordsnomatch");
					}
				}
			}
		} catch (EntityNotFoundException e) {
			errors.rejectValue("emailAddress",
					"changepassword.invalidemailaddress");
		} catch (NonUniqueResultException e) {
			errors.rejectValue("emailAddress",
					"changepassword.duplicateemailaddress");
		}
	}

}
