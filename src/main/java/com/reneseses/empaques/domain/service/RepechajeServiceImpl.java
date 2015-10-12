package com.reneseses.empaques.domain.service;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.reneseses.empaques.domain.Repechaje;
import com.reneseses.empaques.domain.UsuarioId;


public class RepechajeServiceImpl implements RepechajeService {
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public List<Repechaje> findRepechajesByFechaBetweenAndUsuario(Date minFecha, Date maxFecha, UsuarioId usuario) {
		return repechajeRepository.findRepechajesByFechaBetweenAndUsuario(minFecha, maxFecha, usuario);
	}

    public List<Repechaje> findRepechajesByFechaBetween(ObjectId supermercado, Date minFecha, Date maxFecha){
    	Query q= new Query(Criteria.where("fecha").gte(minFecha).lte(maxFecha).and("usuario.supermercado").is(supermercado));
    	q.with(new Sort(Direction.ASC, "fecha"));
    	
    	return mongoTemplate.find(q, Repechaje.class);
    }
    public List<Repechaje> findRepechajesByUsuario(UsuarioId usuario) {
    	return repechajeRepository.findRepechajesByUsuario(usuario);
    }
	
}
