// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.reneseses.empaques.domain;

import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.UsuarioId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;

privileged aspect Usuario_Roo_Mongo_Entity {
    
    declare @type: Usuario: @Persistent;
    
    @Id
    private UsuarioId Usuario.id;
    
    public UsuarioId Usuario.getId() {
        return this.id;
    }
    
    public void Usuario.setId(UsuarioId id) {
        this.id = id;
    }
    
}
