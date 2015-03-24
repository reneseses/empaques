package com.reneseses.empaques.domain;

import javax.persistence.ManyToOne;
import org.bson.types.ObjectId;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooMongoEntity(identifierType = ObjectId.class)
public class ImagenUsuario {

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Imagen imagen;
}
