package com.reneseses.empaques.enums;


public enum RegimenTurnoEnum {
	R2X1("2x1"), //Dos turnos en la semana y uno el fin de semana
	R1X2("1x2"), //Un turno en la semana y dos el fin de semana
	R3x0("3x0"), //Tres turnos en la semana
	R0x3("0x3"), //Tres turnos el fin de semana
	LIBRE("Asistencia Libre"), //Tres turnos el fin de semana
	NUEVO("Nuevo"); //Turno obligatorio el domingo

	private String regimen;
	
	private RegimenTurnoEnum(String regimen){
		this.regimen = regimen;
	}
	
	public String getRegimen(){
		return this.regimen;
	}
}
