package com.reneseses.empaques.enums;


public enum BloqueEnum {
	//SIETE("7:00"), SIETE30("7:30"),
	OCHO("8:15"), /*OCHO30("8:30"),*/ NUEVE("9:00"), NUEVE30("9:30"), DIEZ("10:00"), DIEZ30("10:30"),
	ONCE("11:00"), ONCE30("11:30"), DOCE("12:00"), DOCE30("12:30"), TRECE("13:00"), TRECE30("13:30"), CATORCE("14:00"),
	CATORCE30("14:30"), QUINCE("15:00"), QUINCE30("15:30"), DIECISEIS("16:00"), DIECISEIS30("16:30"), DIECISIETE("17:00"),
	DIECISIETE30("17:30"), DIECIOCHO("18:00"), DIECIOCHO30("18:30"),DIECINUEVE("19:00"), DIECINUEVE30("19:30"), VIENTE("20:00");
	//VIENTE30("20:30"),VIENTIUNO("21:00"), VIENTIUNO30("21:30"), VIENTIDOS("22:00"), VIENTDOS30("22:30"), VIENTITRES("23:00"),
	//VIENTITRES30("23:30");
	
	private String bloque;
	
	private BloqueEnum(String bloque){
		this.bloque= bloque;
	}
	
	public String getBloque(){
		return this.bloque;
	}
	
	public static BloqueEnum bloqueFromValue(String value){
		BloqueEnum[] values= BloqueEnum.values();
		for(int i= 0; i< values.length; i++){
			if(values[i].getBloque().equals(value)){
				return values[i];
			}
		}
		return null;
	}
	
}
