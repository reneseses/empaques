package com.reneseses.empaques.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;

import com.reneseses.empaques.domain.Solicitud;
import com.reneseses.empaques.domain.Usuario;


public class SolicitudServiceImpl implements SolicitudService {
	public List<Solicitud> findSolicitudesByFechaBetweenAndUsuario(Date minFecha, Date maxFecha, Usuario usuario){
		return solicitudRepository.findSolicitudesByFechaBetweenAndUsuario_Id(minFecha, maxFecha, usuario.getId());
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
    	return solicitudRepository.findSolicitudesByFechaBetween(minFecha, maxFecha);
    }
    public List<Solicitud> findSolicitudesByUsuario(Usuario usuario){
    	return solicitudRepository.findSolicitudesByUsuario_Id(usuario.getId());
    }
	public List<Solicitud> findSolicitudesByUsuario(ObjectId id) {
		return solicitudRepository.findSolicitudesByUsuario_Id(id);
	}
}
