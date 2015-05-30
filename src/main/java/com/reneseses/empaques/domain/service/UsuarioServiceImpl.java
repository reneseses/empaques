package com.reneseses.empaques.domain.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

	public List<Usuario> findAllOrderByNumero(){
		Sort order= new Sort(Sort.Direction.ASC, "id");
		
		Query query= new Query();
		query.with(order);
		
		return mongoTemplate.find(query, Usuario.class);
	}
	
	public List<Usuario> findUsuarioEntriesOrderByNumero(int firstResult, int maxResults){
		PageRequest pg= new PageRequest(firstResult / maxResults, maxResults);
        return usuarioRepository.findAll();
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
