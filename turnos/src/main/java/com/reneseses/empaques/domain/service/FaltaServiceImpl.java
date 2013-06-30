package com.reneseses.empaques.domain.service;

import java.util.Date;
import java.util.List;

import com.reneseses.empaques.domain.Falta;
import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.enums.TipoFaltaEnum;


public class FaltaServiceImpl implements FaltaService {
    public List<Falta> findFaltaByUsuario(Usuario usuario){
    	return faltaRepository.findFaltaByUsuario_Id(usuario.getId());
    }
    public List<Falta> findFaltaByTipoFalta(TipoFaltaEnum tipo){
    	return faltaRepository.findFaltaByTipoFalta(tipo);
    }
    public List<Falta> findFaltaByFechaBetween(Date minFecha,Date maxFecha){
    	return faltaRepository.findFaltaByFechaBetween(minFecha, maxFecha);
    }
    public List<Falta> findFaltaByFechaBetweenAndUsuario(Date minFecha, Date maxFecha,Usuario usuario){
    	return faltaRepository.findFaltaByFechaBetweenAndUsuario_Id(minFecha, maxFecha, usuario.getId());
    }
    public int getCantidadTurnos(Date minFecha, Date maxFecha,Usuario usuario){
    	List<Falta> faltas= faltaRepository.findFaltaByFechaBetweenAndUsuario_Id(minFecha, maxFecha, usuario.getId());
    	int contGraves= 0;
    	int contLeves=0;
    	if(faltas.size() == 0)
    		return 3;
    	else{
    		for(Falta falta: faltas){
    			System.out.println(falta.getUsuario().getNumero());
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
    
    public int getCantidadTurnosRepechaje(Date minFecha, Date maxFecha,Usuario usuario){
    	List<Falta> faltas= faltaRepository.findFaltaByFechaBetweenAndUsuario_Id(minFecha, maxFecha, usuario.getId());
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
