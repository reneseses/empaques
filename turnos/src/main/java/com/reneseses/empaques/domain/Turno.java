package com.reneseses.empaques.domain;

import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

import com.reneseses.empaques.enums.EstadoTurnoEnum;

@RooJavaBean
@RooToString
@RooMongoEntity(identifierType = ObjectId.class)
public class Turno {
	@Enumerated
    private EstadoTurnoEnum estado = EstadoTurnoEnum.LIBRE;

    @ManyToOne
    private Usuario usuario;

    private int solicitud;
}
