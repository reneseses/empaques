package com.reneseses.empaques.domain.repository;
import com.reneseses.empaques.domain.AppProperties;
import java.util.List;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoRepository;

@RooMongoRepository(domainType = AppProperties.class)
public interface AppPropertiesRepository {

    List<AppProperties> findAll();
}
