package com.reneseses.empaques.domain.repository;

import com.reneseses.empaques.domain.Falta;
import com.reneseses.empaques.domain.UsuarioId;
import com.reneseses.empaques.enums.TipoFaltaEnum;

import java.math.BigInteger;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = Falta.class)
public interface FaltaRepository {

    List<com.reneseses.empaques.domain.Falta> findAll();
    List<Falta> findFaltaByUsuario(UsuarioId usuario);
    List<Falta> findFaltaByTipoFalta(TipoFaltaEnum tipo);
    List<Falta> findFaltaByPlanilla(ObjectId planilla);
    List<Falta> findFaltaByPlanillaAndUsuario(ObjectId planilla, UsuarioId usuario);
}
