// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.reneseses.empaques.domain.service;

import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.UsuarioId;
import com.reneseses.empaques.domain.service.UsuarioService;
import java.util.List;

privileged aspect UsuarioService_Roo_Service {
    
    public abstract long UsuarioService.countAllUsuarios();    
    public abstract void UsuarioService.deleteUsuario(Usuario usuario);    
    public abstract Usuario UsuarioService.findUsuario(UsuarioId id);    
    public abstract List<Usuario> UsuarioService.findAllUsuarios();    
    public abstract List<Usuario> UsuarioService.findUsuarioEntries(int firstResult, int maxResults);    
    public abstract void UsuarioService.saveUsuario(Usuario usuario);    
    public abstract Usuario UsuarioService.updateUsuario(Usuario usuario);    
}
