package com.reneseses.empaques.formularios;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.reneseses.empaques.domain.UsuarioId;

@RooJavaBean
@RooToString
public class SolicitudList {

	private ObjectId id;
	
	private UsuarioId usuario;
	
	private Date fecha;
	
	private String turno1;
	
	private String turno2;
	
	private String turno3;
	
	private String turno4;
	
	private String turno5;
}
