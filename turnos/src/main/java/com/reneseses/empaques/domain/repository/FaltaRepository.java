package com.reneseses.empaques.domain.repository;

import com.reneseses.empaques.domain.Falta;
import com.reneseses.empaques.enums.TipoFaltaEnum;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = Falta.class)
public interface FaltaRepository {

    List<com.reneseses.empaques.domain.Falta> findAll();
    List<Falta> findFaltaByUsuario_Id(ObjectId id);
    List<Falta> findFaltaByTipoFalta(TipoFaltaEnum tipo);
    List<Falta> findFaltaByFechaBetween(Date minFecha,Date maxFecha);
    List<Falta> findFaltaByFechaBetweenAndUsuario_Id(Date minFecha, Date maxFecha,ObjectId id);
}
