// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.reneseses.empaques.domain;

import com.reneseses.empaques.domain.ImagenUsuario;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

privileged aspect ImagenUsuario_Roo_Mongo_Entity {
    
    declare @type: ImagenUsuario: @Persistent;
    
    @Id
    private ObjectId ImagenUsuario.id;
    
    public ObjectId ImagenUsuario.getId() {
        return this.id;
    }
    
    public void ImagenUsuario.setId(ObjectId id) {
        this.id = id;
    }
    
}
