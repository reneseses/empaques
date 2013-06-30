package com.reneseses.empaques.domain.repository;

import com.reneseses.empaques.domain.Usuario;
import java.util.List;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = Usuario.class)
public interface UsuarioRepository {

    List<com.reneseses.empaques.domain.Usuario> findAll();
    Usuario findUsuarioByNumero(Integer numero);
    Usuario findUsuarioByRut(String rut);
}
