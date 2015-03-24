package com.reneseses.empaques.domain.repository;

import com.reneseses.empaques.domain.ImagenUsuario;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = ImagenUsuario.class)
public interface ImagenUsuarioRepository {

    List<com.reneseses.empaques.domain.ImagenUsuario> findAll();
    ImagenUsuario findByUsuario_Id(ObjectId id);
}
