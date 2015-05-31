package com.reneseses.empaques.domain;

import java.util.Calendar;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

import com.reneseses.empaques.enums.DiasEnum;

@RooMongoEntity(identifierType=ObjectId.class)
@RooToString
@RooJavaBean
public class Supermercado {

	private String nombre;
	
	private String direccion;
	
	private String telefono;
	
	private Integer maxTurnosTotal=3;
	
	private Integer maxRepechaje=2;
	
	private Integer maxTurnos= 3;
	
	private int delayZonaHoraria= -3;
	
	private DiasEnum turnosDia=DiasEnum.DOMINGO;
	
	private Integer turnosHora=23;
	
	private Integer turnosMinutos=30;
	
	private DiasEnum turnosDiaEnd=DiasEnum.MIERCOLES;
	
	private Integer turnosHoraEnd=23;
	
	private Integer turnosMinutosEnd=30;
	
	
	private DiasEnum repechajeDia=DiasEnum.JUEVES;
	
	private Integer repechajeHora= 23;
	
	private Integer repechajeMinutos= 30;
	
	private DiasEnum repechajeDiaEnd=DiasEnum.VIERNES;
	
	private Integer repechajeHoraEnd= 23;
	
	private Integer repechajeMinutosEnd= 30;
	
	public boolean isTurnosActivo(){
		Date now= new Date();
		Date begin= this.getTurnosBegin();
		Date end= this.getTurnosEnd();

		if(now.before(begin) || now.after(end))
			return false;
		
		return true;
	}
	
	public boolean isRepechajeActivo(){
		Date now= new Date();
		Date begin= this.getRepechajeBegin();
		Date end= this.getRepechajeEnd();

		if(now.before(begin) || now.after(end))
			return false;
		
		return true;
	}
	
	public Date getTurnosBegin(){
		Calendar calendar= Calendar.getInstance();
		
		int firstDay= calendar.getFirstDayOfWeek();
		int dayIndex= this.turnosDia.getDiaIndex();
		
		if(dayIndex < firstDay){
			calendar.add(Calendar.DATE, -7);
		}
		
		calendar.set(Calendar.DAY_OF_WEEK, dayIndex);
		calendar.set(Calendar.HOUR_OF_DAY, this.turnosHora);
		calendar.set(Calendar.MINUTE, this.turnosMinutos);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		calendar.add(Calendar.HOUR_OF_DAY, -this.delayZonaHoraria);
		
		return calendar.getTime();
	}
	
	public Date getTurnosEnd(){
		Calendar calendar= Calendar.getInstance();
		
		int firstDay= calendar.getFirstDayOfWeek();
		int dayIndex= this.turnosDiaEnd.getDiaIndex();
		
		if(dayIndex < firstDay){
			calendar.add(Calendar.DATE, -7);
		}
		
		calendar.set(Calendar.DAY_OF_WEEK, dayIndex);
		calendar.set(Calendar.HOUR_OF_DAY, this.turnosHoraEnd);
		calendar.set(Calendar.MINUTE, this.turnosMinutosEnd);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		calendar.add(Calendar.HOUR_OF_DAY, -this.delayZonaHoraria);
		
		return calendar.getTime();
	}
	
	public Date getRepechajeBegin(){
		Calendar calendar= Calendar.getInstance();
		
		int firstDay= calendar.getFirstDayOfWeek();
		int dayIndex= this.repechajeDia.getDiaIndex();
		
		if(dayIndex < firstDay){
			calendar.add(Calendar.DATE, -7);
		}
		
		calendar.set(Calendar.DAY_OF_WEEK, dayIndex);
		calendar.set(Calendar.HOUR_OF_DAY, this.repechajeHora);
		calendar.set(Calendar.MINUTE, this.repechajeMinutos);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		calendar.add(Calendar.HOUR_OF_DAY, -this.delayZonaHoraria);
		
		return calendar.getTime();
	}
	
	public Date getRepechajeEnd(){
		Calendar calendar= Calendar.getInstance();
		
		int firstDay= calendar.getFirstDayOfWeek();
		int dayIndex= this.repechajeDiaEnd.getDiaIndex();
		
		if(dayIndex < firstDay){
			calendar.add(Calendar.DATE, -7);
		}
		
		calendar.set(Calendar.DAY_OF_WEEK, dayIndex);
		calendar.set(Calendar.HOUR_OF_DAY, this.repechajeHoraEnd);
		calendar.set(Calendar.MINUTE, this.repechajeMinutosEnd);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		calendar.add(Calendar.HOUR_OF_DAY, -this.delayZonaHoraria);
		
		return calendar.getTime();
	}
}
