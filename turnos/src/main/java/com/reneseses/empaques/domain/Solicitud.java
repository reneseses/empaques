package com.reneseses.empaques.domain;

import java.util.Date;

import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.reneseses.empaques.enums.DiasEnum;

@RooJavaBean
@RooToString
@RooMongoEntity(identifierType = ObjectId.class)
public class Solicitud {
	@Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fecha;

    @ManyToOne
    private Usuario usuario;
    
    @Lob
    private String turnos;
    
    public boolean hasSunday(){
    	BasicDBList ja=  (BasicDBList) JSON.parse(this.turnos);
    	for(int i=0; i< ja.size(); i++){
    		BasicDBObject jo = (BasicDBObject) ja.get(i);
    		if(DiasEnum.DOMINGO.equals(DiasEnum.valueOf(jo.getString("dia"))))
    			return true;
    	}
    	return false;
    }
    
}
