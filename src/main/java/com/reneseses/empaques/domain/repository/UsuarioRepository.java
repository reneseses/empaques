package com.reneseses.empaques.domain.repository;

import com.reneseses.empaques.domain.Usuario;

import java.util.List;

import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = Usuario.class)
public interface UsuarioRepository {

    List<Usuario> findAll();
    Usuario findUsuarioByRut(String rut);
}
