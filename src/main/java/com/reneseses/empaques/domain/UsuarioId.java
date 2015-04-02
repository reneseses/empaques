package com.reneseses.empaques.domain;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooSerializable
public class UsuarioId {

	/**
	 */
	private ObjectId supermercado;
	
	/**
	 */
	private Integer numero;
	
	@Override
	public int hashCode(){
		int time= 0;
		int num= 0;
		
		if(this.supermercado == null)
			time= this.supermercado.getTimestamp();
		
		if(this.numero == null)
			num=0;
		
		return time + num;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		
		if(obj == this)
			return true;
		
		if(!(obj instanceof UsuarioId))
			return false;
		
		UsuarioId other= (UsuarioId) obj;
		
		if(this.supermercado == null && other.getSupermercado() == null){
			if(this.getNumero() == null && other.getNumero() == null)
				return true;
			
			if(this.getNumero() == null)
				return false;
			
			if(this.getNumero().equals(other.getNumero()))
				return true;
		}
		
		if(this.supermercado== null)
			return false;
		
		if(this.supermercado.equals(other.getSupermercado())){
			if(this.getNumero() == null && other.getNumero() == null)
				return true;
			
			if(this.getNumero() == null)
				return false;
			
			if(this.getNumero().equals(other.getNumero()))
				return true;
		}
		
		return false;
	}
	
	public String toString(){
		String sup= "super";
		String num= "num";
		
		if(this.supermercado != null)
			sup= this.supermercado.toString();
		
		if(this.numero != null)
			num= String.valueOf(this.numero);
		
		return num + "@" + sup;
	}
	
	public static UsuarioId fromString(String string){
		UsuarioId id= new UsuarioId();
		
		if(string== null)
			return null;
		
		String[] split= string.split("@");
		
		if(split.length < 2)
			return null;
		
		try{
			Integer numero= Integer.valueOf(split[0]);
			ObjectId supermercado= new ObjectId(split[1]);
			
			id.setNumero(numero);
			id.setSupermercado(supermercado);
			
			return id;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
