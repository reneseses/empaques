package com.reneseses.empaques.domain.repository;

import com.reneseses.empaques.domain.Repechaje;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = Repechaje.class)
public interface RepechajeRepository {

    List<com.reneseses.empaques.domain.Repechaje> findAll();
    List<Repechaje> findRepechajesByFechaBetweenAndUsuario(Date minFecha, Date maxFecha, Integer id) ;
    List<Repechaje> findRepechajesByFechaBetween(Date minFecha, Date maxFecha) ;
    List<Repechaje> findRepechajesByUsuario(Integer id) ;
}
