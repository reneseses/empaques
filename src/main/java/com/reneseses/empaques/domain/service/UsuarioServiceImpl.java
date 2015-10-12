package com.reneseses.empaques.domain.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.UsuarioId;


public class UsuarioServiceImpl implements UsuarioService {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public List<Usuario> findUsuariosByIds(List<UsuarioId> ids){
		Query query= new Query(Criteria.where("id").in(ids));
		
		return mongoTemplate.find(query, Usuario.class);
	}
	
	public Usuario findUsuarioByRut(String rut){
		return usuarioRepository.findUsuarioByRut(rut);
	}

	public List<Usuario> findAll(ObjectId supermercado){
		Query q= new Query(Criteria.where("_id.supermercado").is(supermercado));
		
		return mongoTemplate.find(q, Usuario.class);
	}
	
	public List<Usuario> findUsuarioEntries(ObjectId supermercado, int firstResult, int maxResults){
		Query q= new Query(Criteria.where("_id.supermercado").is(supermercado));
		Pageable paginator= new PageRequest(firstResult / maxResults, maxResults);
		q.with(paginator);
		
        return mongoTemplate.find(q, Usuario.class);
	}
	
	public List<Usuario> lightFindAllUsuarios(){
		Query query= new Query();
		
		query.with(new Sort(Direction.ASC, "id"));
		query.fields().include("nombre").include("id");
		
		return mongoTemplate.find(query, Usuario.class);
	}
	
	public Map<UsuarioId, Usuario> findAll(){
		List<Usuario> usuarios= usuarioRepository.findAll();
		Map<UsuarioId, Usuario> map= new HashMap<UsuarioId, Usuario>();
		for(Usuario usuario: usuarios)
			map.put(usuario.getId(), usuario);
		return map;
	}
}
