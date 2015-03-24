package com.reneseses.empaques.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.reneseses.empaques.domain.Usuario;


public class UsuarioServiceImpl implements UsuarioService {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public Usuario findUsuarioByNumero(Integer numero){
		return usuarioRepository.findUsuarioByNumero(numero);
	}
	
	public List<Usuario> findUsuariosByNumeros(List<Integer> numeros){
		Query query= new Query(Criteria.where("numero").in(numeros));
		
		return mongoTemplate.find(query, Usuario.class);
	}
	
	public Usuario findUsuarioByRut(String rut){
		return usuarioRepository.findUsuarioByRut(rut);
	}

	public List<Usuario> findAllOrderByNumero(){
		Sort order= new Sort(Sort.Direction.ASC, "numero");
		Iterable<Usuario> it= usuarioRepository.findAll(order);
		Iterator<Usuario> itr= it.iterator();
		List<Usuario> list= new ArrayList<Usuario>();
		while(itr.hasNext()){
			list.add(itr.next());
		}
		return list;
	}
	
	public List<Usuario> findUsuarioEntriesOrderByNumero(int firstResult, int maxResults){
		PageRequest pg= new PageRequest(firstResult / maxResults, maxResults);
        return usuarioRepository.findAll(pg).getContent();
	}
	
	public Map<Integer, Usuario> findAll(){
		List<Usuario> usuarios= usuarioRepository.findAll();
		Map<Integer, Usuario> map= new HashMap<Integer, Usuario>();
		for(Usuario usuario: usuarios)
			map.put(usuario.getNumero(), usuario);
		return map;
	}
}
