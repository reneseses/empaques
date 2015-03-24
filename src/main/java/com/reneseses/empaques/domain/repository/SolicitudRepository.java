package com.reneseses.empaques.domain.repository;

import com.reneseses.empaques.domain.Solicitud;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = Solicitud.class)
public interface SolicitudRepository {

    List<com.reneseses.empaques.domain.Solicitud> findAll();
    List<Solicitud> findSolicitudesByFechaBetweenAndUsuario(Date minFecha, Date maxFecha, Integer usuario) ;
    List<Solicitud> findSolicitudesByFechaBetween(Date minFecha, Date maxFecha);
    List<Solicitud> findSolicitudesByUsuario(Integer usuario);
}
