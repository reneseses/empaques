package com.reneseses.empaques.domain.repository;

import com.reneseses.empaques.domain.Planilla;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = Planilla.class)
public interface PlanillaRepository {

    List<com.reneseses.empaques.domain.Planilla> findAll();
    List<Planilla> findPlanillasBySupermercadoAndFechaBetween(ObjectId supermercado, Date fecha1, Date fecha2);
}
