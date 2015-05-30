package com.reneseses.empaques.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.reneseses.empaques.enums.BloqueEnum;
import com.reneseses.empaques.enums.DiasEnum;
import com.reneseses.empaques.enums.EstadoTurnoEnum;

@RooJavaBean
@RooToString
@RooMongoEntity(identifierType = ObjectId.class)
public class Planilla {
	
	@Column(unique=true)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fecha;

    private Boolean generada= false;
    
    private Boolean repechaje= false;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Bloque> bloques = new ArrayList<Bloque>();
    
    private ObjectId supermercado;
    
    public boolean buscarConflicto(LinkedHashMap<String, Object> turno, Integer empaque){
		int dia = ((DiasEnum) turno.get("dia")).ordinal();
		int inicio = ((BloqueEnum) turno.get("inicio")).ordinal();
		
		int rango1 = 7*(inicio - 6) + dia;
		int rango2 = 7*(inicio + 6) + dia;

		if(rango1 < dia)
			rango1 = dia;
		if(rango2 > (BloqueEnum.values().length - 1) * 7 + dia)
			rango2 = (BloqueEnum.values().length - 1) * 7 + dia;
		
		for(int i = rango1; i <= rango2; i = i + 7){
			List<Turno> turnos = this.getBloques().get(i).getTurnos();
			for(Turno turn : turnos)
				if(turn.getEstado().equals(EstadoTurnoEnum.OCUPADO) && turn.getUsuario() == empaque)
					return true;
		}
		return false;
	}
    
    
    public Bloque getBloque(DiasEnum dia, BloqueEnum hora){
    	return this.bloques.get(7*hora.ordinal() + dia.ordinal());
    }
	
    public void createPlanilla(String data){
    	BasicDBObject jo= new BasicDBObject();
		BasicDBList ja=  (BasicDBList) JSON.parse(data);

		BloqueEnum[] horas = BloqueEnum.values();
		DiasEnum[] dias= DiasEnum.values();
		ArrayList<Bloque> bloques= new ArrayList<Bloque>();
		
		Bloque bloque;
		Calendar cal;
		String hora;
		
		for(int i=0; i < horas.length; i++){
			jo= (BasicDBObject) ja.get(i);

			hora= horas[i].getBloque();
			String[] aux= hora.split(":");
			for(int j=0; j < 7; j++){
				bloque = new Bloque();
				bloque.setCupos(Integer.valueOf(jo.getString(dias[j].getDia())));
				for(int k=0; k < bloque.getCupos(); k++)
					bloque.getTurnos().add(new Turno());
				cal= Calendar.getInstance();
				cal.setTime(this.getFecha());
				cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + j);
				cal.set(Calendar.HOUR, Integer.valueOf(aux[0]));
				cal.set(Calendar.MINUTE, Integer.valueOf(aux[1]));
				bloque.setFecha(cal.getTime());
				bloques.add(bloques.size(), bloque);
			}
		}

		this.setBloques(bloques);
    }
    
	public String getTurnos(){
		BasicDBList ja	= new BasicDBList();
		BasicDBList data= new BasicDBList();
		BasicDBList rows= new BasicDBList();
		BasicDBObject jo;
		
		DiasEnum[] dias= DiasEnum.values();
		BloqueEnum[] bloquesEnum = BloqueEnum.values();
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		for(int i=0; i< bloquesEnum.length; i++){
			int mayor= 1;
			for(int j=0; j<dias.length; j++)
				if(mayor < this.bloques.get(i*dias.length + j).getCupos())
					mayor= this.bloques.get(i*dias.length + j).getCupos();
			list.add(mayor);
			rows.add(bloquesEnum[i].getBloque());
			for(int j=1; j<mayor + 1; j++)
				rows.add("");
		}
		
		for(int i=0; i< bloquesEnum.length; i++){
			for(int k=0; k <= list.get(i); k++){
				BasicDBList row = new BasicDBList();
				jo = new BasicDBObject();
				jo.put("bloque", i);
				jo.put("turno", k);
				for(int j= 0; j< dias.length; j++){
					if(k >= this.bloques.get(i*dias.length + j).getCupos()){
						jo.put(dias[j].getDia(), "");
						row.add("");
					}
					else{
						if(this.bloques.get(i*dias.length + j).getTurnos().get(k).getUsuario() == null){
							jo.put(dias[j].getDia(), "-");
							row.add("-");
						}
						else{
							jo.put(dias[j].getDia(), this.bloques.get(i*dias.length + j).getTurnos().get(k).getUsuario());
							row.add(this.bloques.get(i*dias.length + j).getTurnos().get(k).getUsuario());
						}
					}
				}
				row.add(i);
				data.add(jo);
			}
		}
		ja.add(rows);
		ja.add(data);
		return ja.toString();
	}
	
	public String getCupos(){
		BasicDBList ja = new BasicDBList();
		BasicDBList rows = new BasicDBList();
		BasicDBList data = new BasicDBList();
		BasicDBObject jo;
		DiasEnum[] dias= DiasEnum.values();
		BloqueEnum[] bloquesEnum = BloqueEnum.values();
		
		for(int i=0; i< bloquesEnum.length; i++){
			jo = new BasicDBObject();
			for(int j=0; j<dias.length; j++)
				jo.put(dias[j].getDia(), this.bloques.get(i*dias.length + j).getCupos());
			data.add(jo);
			rows.add(bloquesEnum[i].getBloque());
		}
		ja.add(rows);
		ja.add(data);
		return ja.toString();
	}
	
	public HashMap<Integer, Integer> getTurnosUsuario(List<Usuario> usuarios){
		HashMap<Integer, Integer> map= new HashMap<Integer, Integer>();
		for(Usuario usuario: usuarios){
			map.put(usuario.getId().getNumero(), 0);
		}
		for(Bloque bloque: this.bloques){
			List<Turno> turnos = bloque.getTurnos();
			for(Turno turno: turnos){
				if(turno.getUsuario() != null)
					map.put(turno.getUsuario(), map.get(turno.getUsuario()) + 1);
			}
		}
		
		return map;
	}
	
	public static Integer turnos(Usuario usuario, Planilla planilla){
		Integer cont=0;
		if(planilla != null){
			for(Bloque bloque: planilla.getBloques()){
				List<Turno> turnos = bloque.getTurnos();
				for(Turno turno: turnos){
					if(turno.getUsuario() != null && turno.getUsuario() == usuario.getId().getNumero())
						cont++;
				}
			}
		}
		return cont;
	}
}
