// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.reneseses.empaques.domain;

import com.reneseses.empaques.domain.Falta;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

privileged aspect Falta_Roo_Mongo_Entity {
    
    declare @type: Falta: @Persistent;
    
    @Id
    private ObjectId Falta.id;
    
    public ObjectId Falta.getId() {
        return this.id;
    }
    
    public void Falta.setId(ObjectId id) {
        this.id = id;
    }
    
}
