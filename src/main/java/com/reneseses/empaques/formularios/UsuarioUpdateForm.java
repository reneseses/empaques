package com.reneseses.empaques.formularios;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;

import com.reneseses.empaques.domain.UsuarioId;
import com.reneseses.empaques.enums.RegimenTurnoEnum;
import com.reneseses.empaques.enums.TipoUsuarioEnum;

@RooJavaBean
public class UsuarioUpdateForm {
	
	private UsuarioId id;
	
	@NotNull
	private Integer numero;
	
	private String supermercado;

    @NotNull
    @Pattern(regexp = "[a-zA-Z\\'\\-]+[ [a-zA-Z\\'\\-]+]*")
    private String nombre;

    @NotNull
    @Pattern(regexp = "[0-9]{1,8}-[K|k|0-9]")
    private String rut;

    private TipoUsuarioEnum tipo = TipoUsuarioEnum.EMPAQUE;

    private String celular;

    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}")
    @Column(unique = true)
    private String email;

    private Boolean disabled = false;

    private Boolean locked = false;

    private RegimenTurnoEnum regimen = RegimenTurnoEnum.R2X1;

    private Integer prioridad = 3;
    
    private String facebook;
    
    @Past
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fechaNacimiento;

    private String carrera;

    private String universidad;
}
