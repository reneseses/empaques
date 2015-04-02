package com.reneseses.empaques.domain;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

import com.reneseses.empaques.enums.DiasEnum;

@RooMongoEntity(identifierType=ObjectId.class)
@RooToString
@RooJavaBean
public class Supermercado {

	private String nombre;
	
	private String direccion;
	
	private String telefono;
	
	private Integer maxTurnosTotal=3;
	
	private Integer maxRepechaje=2;
	
	private Integer maxTurnos= 3;
	
	private DiasEnum turnosDia=DiasEnum.DOMINGO;
	
	private Integer turnosHora=23;
	
	private Integer turnosMinutos=30;
	
	private DiasEnum repechajeDia=DiasEnum.JUEVES;
	
	private Integer repechajeHora= 23;
	
	private Integer repechajeMinutos= 30;
	
	private Float delayZonaHoraria= -3f;
}
