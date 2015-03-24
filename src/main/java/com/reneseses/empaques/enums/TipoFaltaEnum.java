package com.reneseses.empaques.enums;


public enum TipoFaltaEnum {
	SINFALTA("Sin Falta"),LEVE("Leve"), GRAVE("Grave"), SUSPENSION("Suspension");
	
	private String falta;
		
	private TipoFaltaEnum(String falta){
		this.falta= falta;
	}
	
	public String getFalta(){
		return this.falta;
	}
}
