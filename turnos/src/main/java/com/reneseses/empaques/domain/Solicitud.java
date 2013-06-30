package com.reneseses.empaques.domain;

import java.util.Date;

import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

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
    	JSONArray ja=  (JSONArray) JSONSerializer.toJSON(this.turnos);
    	for(int i=0; i< ja.size(); i++){
    		JSONObject jo = ja.getJSONObject(i);
    		if(DiasEnum.DOMINGO.equals(DiasEnum.valueOf(jo.getString("dia"))))
    			return true;
    	}
    	return false;
    }
    
}
