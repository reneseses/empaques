package com.reneseses.empaques.domain.repository;

import com.reneseses.empaques.domain.Imagen;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = Imagen.class)
public interface ImagenRepository {

    List<com.reneseses.empaques.domain.Imagen> findAll();
    List<Imagen> findImagenByUsuario_Id(ObjectId id);
}
