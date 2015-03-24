package com.reneseses.empaques.domain;

import com.reneseses.empaques.enums.TipoFaltaEnum;

import javax.persistence.Enumerated;
import org.bson.types.ObjectId;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooMongoEntity(identifierType = ObjectId.class)
public class Falta {

    private Integer usuario;

    @Enumerated
    private TipoFaltaEnum tipoFalta;
    
    private ObjectId planilla;

}
