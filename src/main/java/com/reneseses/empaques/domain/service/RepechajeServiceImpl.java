package com.reneseses.empaques.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Sort;

import com.reneseses.empaques.domain.Repechaje;
import com.reneseses.empaques.domain.UsuarioId;


public class RepechajeServiceImpl implements RepechajeService {
	
	public List<Repechaje> findRepechajesByFechaBetweenAndUsuario(Date minFecha, Date maxFecha, UsuarioId usuario) {
		return repechajeRepository.findRepechajesByFechaBetweenAndUsuario(minFecha, maxFecha, usuario);
	}
    public List<Repechaje> findAllOrderByFecha(){
    	Sort sort= new Sort(Sort.Direction.ASC, "fecha");
    	Iterator<Repechaje> it= repechajeRepository.findAll(sort).iterator();
    	List<Repechaje> list= new ArrayList<Repechaje>();
    	while(it.hasNext()){
    		list.add(it.next());
    	}
    	return list;
    }
    public List<Repechaje> findRepechajesByFechaBetween(Date minFecha, Date maxFecha){
    	return repechajeRepository.findRepechajesByFechaBetween(minFecha, maxFecha);
    }
    public List<Repechaje> findRepechajesByUsuario(UsuarioId usuario) {
    	return repechajeRepository.findRepechajesByUsuario(usuario);
    }
	
}
