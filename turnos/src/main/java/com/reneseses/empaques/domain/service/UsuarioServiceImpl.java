package com.reneseses.empaques.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.reneseses.empaques.domain.Usuario;


public class UsuarioServiceImpl implements UsuarioService {
	public Usuario findUsuarioByNumero(Integer numero){
		return usuarioRepository.findUsuarioByNumero(numero);
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
	public List<Usuario> findUsuarioEntriesOrderByNumero(int firstResult,int maxResults){
		PageRequest pg= new PageRequest(firstResult / maxResults, maxResults);
        return usuarioRepository.findAll(pg).getContent();
	}
	
	public Map<ObjectId, Usuario> findAll(){
		List<Usuario> usuarios= usuarioRepository.findAll();
		Map<ObjectId, Usuario> map= new HashMap<ObjectId, Usuario>();
		for(Usuario usuario: usuarios)
			map.put(usuario.getId(), usuario);
		return map;
	}
}
