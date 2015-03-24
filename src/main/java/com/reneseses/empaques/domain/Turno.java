package com.reneseses.empaques.domain;

import javax.persistence.Enumerated;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.reneseses.empaques.enums.EstadoTurnoEnum;

@RooJavaBean
@RooToString
public class Turno {

	@Enumerated
    private EstadoTurnoEnum estado = EstadoTurnoEnum.LIBRE;

    private Integer usuario;

    private int solicitud;
}
