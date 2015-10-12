package com.reneseses.empaques.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

import com.mongodb.BasicDBList;
import com.reneseses.empaques.enums.BloqueEnum;
import com.reneseses.empaques.enums.DiasEnum;
import com.reneseses.empaques.formularios.SolicitudList;

@RooJavaBean
@RooToString
@RooMongoEntity(identifierType = ObjectId.class)
public class Repechaje {

	private UsuarioId usuario;
	
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "M-")
	private Date fecha;
	
	private BasicDBList turnos;
	
	@SuppressWarnings("unchecked")
	public SolicitudList getSolicitudList(){
    	SolicitudList obj= new SolicitudList();
    	
    	obj.setId(this.getId());
    	obj.setUsuario(this.usuario);
    	obj.setFecha(this.fecha);

    	String turno="";
    	for(int i=0; i < this.turnos.size(); i++){
    		LinkedHashMap<String, Object> jo= (LinkedHashMap<String, Object>) this.turnos.get(i);
    		
    		DiasEnum dia= DiasEnum.valueOf((String) jo.get("dia"));
			
    		ArrayList<String> turnos= (ArrayList<String>) jo.get("inicio");
    		
			turno= dia.getDia() + ":";
			
			for(int j=0; j < turnos.size(); j++){
				BloqueEnum bloque= BloqueEnum.valueOf((String) turnos.get(j));
				turno+= " " + bloque.getBloque();
			}
			
    		switch(i){
    			case 0:
    				obj.setTurno1(turno);
    				break;
    			case 1:
    				obj.setTurno2(turno);
    				break;
    			case 2:
    				obj.setTurno3(turno);
    				break;
    			case 3:
    				obj.setTurno4(turno);
    				break;
    			case 4:
    				obj.setTurno5(turno);
    				break;
    		}
    	}
    	
    	return obj;
    }
	
}
