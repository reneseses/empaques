package com.reneseses.empaques.domain;

import java.util.Date;
import java.util.LinkedHashMap;

import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.reneseses.empaques.enums.BloqueEnum;
import com.reneseses.empaques.enums.DiasEnum;
import com.reneseses.empaques.formularios.SolicitudList;

@RooJavaBean
@RooToString
@RooMongoEntity(identifierType = ObjectId.class)
public class Solicitud {
	
	@Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fecha;

    private UsuarioId usuario;
    
    @Lob
    private BasicDBList turnos;
    
    public boolean hasSunday(){
    	BasicDBList ja=  this.turnos;
    	for(int i=0; i< ja.size(); i++){
    		BasicDBObject jo = (BasicDBObject) ja.get(i);
    		if(DiasEnum.DOMINGO.equals(DiasEnum.valueOf(jo.getString("dia"))))
    			return true;
    	}
    	return false;
    }
    
    @SuppressWarnings("unchecked")
	public SolicitudList getSolicitudList(){
    	SolicitudList obj= new SolicitudList();
    	
    	obj.setUsuario(this.usuario);
    	obj.setFecha(this.fecha);
    	    	
    	String turno="";
    	for(int i=0; i < this.turnos.size(); i++){
    		LinkedHashMap<String, String> jo= (LinkedHashMap<String, String>) this.turnos.get(i);
    		
    		DiasEnum dia= DiasEnum.valueOf(jo.get("dia"));
			BloqueEnum inicio= BloqueEnum.valueOf(jo.get("inicio"));
			BloqueEnum fin= BloqueEnum.valueOf(jo.get("fin"));
    		
			turno= dia.getDia() + ": " + inicio.getBloque() + "-" + fin.getBloque();
			
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
