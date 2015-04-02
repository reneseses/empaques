package com.reneseses.empaques.domain.repository;

import com.reneseses.empaques.domain.Solicitud;
import com.reneseses.empaques.domain.UsuarioId;

import java.util.Date;
import java.util.List;

import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = Solicitud.class)
public interface SolicitudRepository {

    List<com.reneseses.empaques.domain.Solicitud> findAll();
    List<Solicitud> findSolicitudesByFechaBetweenAndUsuario(Date minFecha, Date maxFecha, UsuarioId usuario) ;
    List<Solicitud> findSolicitudesByFechaBetween(Date minFecha, Date maxFecha);
    List<Solicitud> findSolicitudesByUsuario(UsuarioId usuario);
}
