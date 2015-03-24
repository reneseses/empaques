package com.reneseses.empaques.domain.service;

import java.util.List;

import org.bson.types.ObjectId;

import com.reneseses.empaques.domain.Falta;
import com.reneseses.empaques.enums.TipoFaltaEnum;


public class FaltaServiceImpl implements FaltaService {
	
    public List<Falta> findFaltaByUsuario(Integer usuario){
    	return faltaRepository.findFaltaByUsuario(usuario);
    }
    
    public List<Falta> findFaltaByTipoFalta(TipoFaltaEnum tipo){
    	return faltaRepository.findFaltaByTipoFalta(tipo);
    }
    
    public List<Falta> findFaltaByPlanilla(ObjectId planilla){
    	return faltaRepository.findFaltaByPlanilla(planilla);
    }
    
    public List<Falta> findFaltaByPlanillaAndUsuario(ObjectId planilla, Integer usuario){
    	return faltaRepository.findFaltaByPlanillaAndUsuario(planilla, usuario);
    }
    
    public int getCantidadTurnos(ObjectId planilla, Integer usuario){
    	List<Falta> faltas= faltaRepository.findFaltaByPlanillaAndUsuario(planilla, usuario);
    	int contGraves= 0;
    	int contLeves=0;
    	if(faltas.size() == 0)
    		return 3;
    	else{
    		for(Falta falta: faltas){
    			if(falta.getTipoFalta().equals(TipoFaltaEnum.SUSPENSION))
    				return 0;
    			if(falta.getTipoFalta().equals(TipoFaltaEnum.GRAVE))
    				contGraves++;
    			if(falta.getTipoFalta().equals(TipoFaltaEnum.LEVE))
    				contLeves++;
    		}
    	}
    	
    	if(contLeves >= 4)
    		return 0;
    	if(contLeves == 3)
    		contGraves+= 2;
    	if(contLeves == 2)
    		contGraves++;
    	
    	if(contGraves >= 3)
    		return 0;
    	if(contGraves == 2)
    		return 1;
    	if(contGraves== 1)
    		return 2;
    	return 3;
    }
    
    public int getCantidadTurnosRepechaje(ObjectId planilla, Integer usuario){
    	List<Falta> faltas= faltaRepository.findFaltaByPlanillaAndUsuario(planilla, usuario);
    	int contLeves=0;
    	if(faltas.size() == 0)
    		return 2;
    	else{
    		for(Falta falta: faltas){
    			if(falta.getTipoFalta().equals(TipoFaltaEnum.SUSPENSION))
    				return 0;
    			if(falta.getTipoFalta().equals(TipoFaltaEnum.GRAVE))
    				return 0;
    			if(falta.getTipoFalta().equals(TipoFaltaEnum.LEVE))
    				contLeves++;
    		}
    	}
    	if(contLeves >= 2)
    		return 0;
    	return 2;
    }
    
}
