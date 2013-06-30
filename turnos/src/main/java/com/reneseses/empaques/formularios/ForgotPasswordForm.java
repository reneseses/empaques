package com.reneseses.empaques.formularios;

import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class ForgotPasswordForm {

	@NotNull
    private String rut;
	
	@NotNull
    private String numero;
}
