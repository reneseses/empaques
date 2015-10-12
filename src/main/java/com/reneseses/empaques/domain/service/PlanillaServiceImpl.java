package com.reneseses.empaques.domain.service;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
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
	
	public List<Planilla> findAllPlanillasOrderByFechaDesc(ObjectId supermercado){
		Query query= new Query(Criteria.where("supermercado").is(supermercado));
		
		query.with(new Sort(Sort.Direction.DESC, "fecha"));
		query.fields().exclude("bloques");
		
    	return mongoTemplate.find(query, Planilla.class);
    }
	
	public List<Planilla> findAllPlanillasOrderByFechaDesc(Integer limit){
		Query query= new Query();
		
		query.with(new Sort(Sort.Direction.DESC, "fecha"));
		query.fields().exclude("bloques");
		query.limit(limit);
		
    	return mongoTemplate.find(query, Planilla.class);
    }
	
	public List<Planilla> findAllPlanillasOrderByFechaDesc(ObjectId supermercado, Integer limit){
		Query query= new Query(Criteria.where("supermercado").is(supermercado));
		
		query.with(new Sort(Sort.Direction.DESC, "fecha"));
		query.fields().exclude("bloques");
		query.limit(limit);
		
    	return mongoTemplate.find(query, Planilla.class);
    }

	public List<Planilla> findAllPlanillasBySupermercado(ObjectId supermercado){
		Query query= new Query();

		query.addCriteria(Criteria.where("supermercado").is(supermercado));
		query.with(new Sort(Sort.Direction.DESC, "fecha"));
		query.fields().exclude("bloques");

		return mongoTemplate.find(query, Planilla.class);
	}

	public List<Planilla> findAllPlanillasBySupermercado(ObjectId supermercado,Integer limit){
		Query query= new Query();

		query.addCriteria(Criteria.where("supermercado").is(supermercado));
		query.with(new Sort(Sort.Direction.DESC, "fecha"));
		query.fields().exclude("bloques");
		query.limit(limit);

		return mongoTemplate.find(query, Planilla.class);
	}

	public Planilla findLastPlanillaBySuperMercado(ObjectId supermercado){
		Query query= new Query();

		query.addCriteria(Criteria.where("supermercado").is(supermercado));
		query.with(new Sort(Sort.Direction.DESC, "fecha"));
		query.limit(1);

		return mongoTemplate.findOne(query, Planilla.class);
	}
    
	public Planilla findPlanillaByFecha(ObjectId supermercado, Date fecha1, Date fecha2){
    	List<Planilla> list= planillaRepository.findPlanillasBySupermercadoAndFechaBetween(supermercado, fecha1, fecha2);
    	if(list.size() == 0)
    		return null;
    	return list.get(0);
    }
    
}
