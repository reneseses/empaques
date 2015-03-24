package com.reneseses.empaques.domain.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.reneseses.empaques.domain.Planilla;


public class PlanillaServiceImpl implements PlanillaService {
    
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public List<Planilla> findAllPlanillasOrderByFechaDesc(){
		Query query= new Query();
		
		query.with(new Sort(Sort.Direction.DESC, "fecha"));
		query.fields().exclude("bloques");
		
    	return mongoTemplate.find(query, Planilla.class);
    }
    
	public Planilla findPlanillaByFecha(Date fecha1, Date fecha2){
    	List<Planilla> list= planillaRepository.findPlanillasByFechaBetween(fecha1, fecha2);
    	if(list.size() == 0)
    		return null;
    	return list.get(0);
    }
    
}
