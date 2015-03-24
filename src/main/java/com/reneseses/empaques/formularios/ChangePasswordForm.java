package com.reneseses.empaques.formularios;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class ChangePasswordForm {

    private String oldPassword;

    private String newPassword;

    private String newPasswordAgain;
}
