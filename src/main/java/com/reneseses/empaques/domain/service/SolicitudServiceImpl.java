package com.reneseses.empaques.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.reneseses.empaques.domain.Solicitud;

public class SolicitudServiceImpl implements SolicitudService {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public List<Solicitud> findSolicitudesByFechaBetweenAndUsuario(Date minFecha, Date maxFecha, Integer usuario){
		return solicitudRepository.findSolicitudesByFechaBetweenAndUsuario(minFecha, maxFecha, usuario);
	}
    public List<Solicitud> findAllSolicitudesOrderByFecha(){
    	Sort sort= new Sort(Sort.Direction.ASC, "fecha");
    	Iterator<Solicitud> it= solicitudRepository.findAll(sort).iterator();
    	List<Solicitud> list= new ArrayList<Solicitud>();
    	while(it.hasNext()){
    		list.add(it.next());
    	}
    	return list;
    }
    public List<Solicitud> findSolicitudesByFechaBetween(Date minFecha, Date maxFecha){
    	Query q= new Query(Criteria.where("fecha").gte(minFecha).lte(maxFecha));
    	q.with(new Sort(Direction.ASC, "fecha"));
    	
    	return mongoTemplate.find(q, Solicitud.class);
    }
    
    public List<Solicitud> findSolicitudesByUsuario(Integer usuario){
    	return solicitudRepository.findSolicitudesByUsuario(usuario);
    }

}
