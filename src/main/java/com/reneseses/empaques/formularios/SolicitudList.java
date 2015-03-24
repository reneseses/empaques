package com.reneseses.empaques.formularios;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
public class SolicitudList {
	
	private ObjectId id;
	
	private Integer numero;
	
	private Date fecha;
	
	private String turno1;
	
	private String turno2;
	
	private String turno3;
	
	private String turno4;
	
	private String turno5;

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getTurno1() {
		return turno1;
	}

	public void setTurno1(String turno1) {
		this.turno1 = turno1;
	}

	public String getTurno2() {
		return turno2;
	}

	public void setTurno2(String turno2) {
		this.turno2 = turno2;
	}

	public String getTurno3() {
		return turno3;
	}

	public void setTurno3(String turno3) {
		this.turno3 = turno3;
	}

	public String getTurno4() {
		return turno4;
	}

	public void setTurno4(String turno4) {
		this.turno4 = turno4;
	}

	public String getTurno5() {
		return turno5;
	}

	public void setTurno5(String turno5) {
		this.turno5 = turno5;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

}
