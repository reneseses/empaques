package com.reneseses.empaques.enums;

import java.util.Calendar;


public enum DiasEnum {
	LUNES("Lunes", Calendar.MONDAY),
	MARTES("Martes", Calendar.TUESDAY),
	MIERCOLES("Miercoles", Calendar.WEDNESDAY),
	JUEVES("Jueves", Calendar.THURSDAY),
	VIERNES("Viernes", Calendar.FRIDAY),
	SABADO("Sabado", Calendar.SATURDAY),
	DOMINGO("Domingo", Calendar.SUNDAY);

	private String dia;
	private int index;
	
	private DiasEnum(String dia, int index){
		this.dia= dia;
		this.index= index;
	}
	
	public String getDia(){
		return this.dia;
	}
	
	public Integer getDiaIndex(){
		return this.index;
	}
}
