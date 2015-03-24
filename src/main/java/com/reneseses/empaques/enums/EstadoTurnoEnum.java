package com.reneseses.empaques.enums;


public enum EstadoTurnoEnum {
	LIBRE("Libre"),
	OCUPADO("Ocupado"),
	REGALANDO("Regalando"),
	MOVIDO("Movido"),
	REGALADO("Regalado");

	private String estado;
	
	private EstadoTurnoEnum(String estado){
		this.estado = estado;
	}
	
	public String getEstado(){
		return this.estado;
	}
}
