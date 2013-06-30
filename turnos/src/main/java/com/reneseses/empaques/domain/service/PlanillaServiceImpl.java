package com.reneseses.empaques.domain.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Sort;

import com.reneseses.empaques.domain.Planilla;


public class PlanillaServiceImpl implements PlanillaService {
    public List<Planilla> findAllPlanillasOrderByFechaDesc(){
    	Sort sort= new Sort(Sort.Direction.DESC, "fecha");
    	Iterator<Planilla> it= planillaRepository.findAll(sort).iterator();
    	List<Planilla> list= new ArrayList<Planilla>();
    	while(it.hasNext()){
			list.add(it.next());
		}
		return list;
    }
    public Planilla findPlanillaByFecha(Date fecha1, Date fecha2){
    	List<Planilla> list= planillaRepository.findPlanillasByFechaBetween(fecha1, fecha2);
    	if(list.size() == 0)
    		return null;
    	return list.get(0);
    }
    
}
