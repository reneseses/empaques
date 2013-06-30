package com.reneseses.empaques.enums;


public enum DiasEnum {
	LUNES("Lunes"), MARTES("Martes"), MIERCOLES("Miercoles"), JUEVES("Jueves"), VIERNES("Viernes"), SABADO("Sabado"), DOMINGO("Domingo");

	private String dia;
	
	private DiasEnum(String dia){
		this.dia= dia;
	}
	
	public String getDia(){
		return this.dia;
	}
}
