package com.reneseses.empaques.enums;


public enum TipoUsuarioEnum {
	EMPAQUE("Empaque"),
	ENCARGADO("Encargado"),
	SUBENCARGADOLOCAL("Subencargado de Local"),
	ENCARGADOLOCAL("Encargado de Local"),
	LOCALADMIN("Supermercado Admin"),
	ADMIN("Administrador");
	
	private String tipo;
	
	private TipoUsuarioEnum(String tipo){
		this.tipo= tipo;
	}
	
	public String getTipo(){
		return this.tipo;
	}
}
