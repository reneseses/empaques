package com.reneseses.empaques.formularios;

import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class ImagenForm {

    @NotNull
    private byte[] content;
}
