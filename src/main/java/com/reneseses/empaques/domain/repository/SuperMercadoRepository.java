package com.reneseses.empaques.domain.repository;
import com.reneseses.empaques.domain.Supermercado;
import java.util.List;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = Supermercado.class)
public interface SuperMercadoRepository {

    List<Supermercado> findAll();
}
