package com.reneseses.empaques.formularios;

import com.reneseses.empaques.enums.TipoUsuarioEnum;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class ExcelForm {

    @NotNull
    private TipoUsuarioEnum tipo;

    @NotNull
    private byte[] content;
}
