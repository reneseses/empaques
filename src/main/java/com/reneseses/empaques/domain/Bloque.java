package com.reneseses.empaques.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.reneseses.empaques.enums.EstadoTurnoEnum;

@RooJavaBean
@RooToString
public class Bloque {
	
	@Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fecha;

    private Integer cupos= 0;
    
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Turno> turnos = new ArrayList<Turno>();
    
    public boolean hasFree(){
    	for(Turno turno : this.turnos)
			if(turno.getEstado().equals(EstadoTurnoEnum.LIBRE))
				return true;
    	return false;
    }
    
    public boolean addTurno(Usuario empaque, int solicitud){
    	for(Turno turno : this.turnos)
			if(turno.getEstado().equals(EstadoTurnoEnum.LIBRE)){
				turno.setUsuario(empaque.getNumero());
				turno.setEstado(EstadoTurnoEnum.OCUPADO);
				turno.setSolicitud(solicitud);
				//turno.merge();
				return true;
			}
    	return false;
    }
    
    public boolean moveTurno(Turno prev, Integer usuario1, Integer usuario2, int solicitud){
    	for(Turno turno : turnos)
			if(turno.getEstado().equals(EstadoTurnoEnum.LIBRE)){
				turno.setUsuario(usuario2);
				turno.setEstado(EstadoTurnoEnum.MOVIDO);
				turno.setSolicitud(prev.getSolicitud());
				//turno.merge();
				
				prev.setUsuario(usuario1);
				prev.setEstado(EstadoTurnoEnum.MOVIDO);
				prev.setSolicitud(solicitud);
				//prev.merge();
				return true;
			}
    	
    	return false;
    }
}
