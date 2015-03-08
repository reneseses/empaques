package com.reneseses.empaques.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.reneseses.empaques.domain.Bloque;
import com.reneseses.empaques.domain.ImagenUsuario;
import com.reneseses.empaques.domain.Planilla;
import com.reneseses.empaques.domain.Repechaje;
import com.reneseses.empaques.domain.Solicitud;
import com.reneseses.empaques.domain.Turno;
import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.service.FaltaServiceImpl;
import com.reneseses.empaques.domain.service.ImagenUsuarioServiceImpl;
import com.reneseses.empaques.domain.service.PlanillaServiceImpl;
import com.reneseses.empaques.domain.service.RepechajeServiceImpl;
import com.reneseses.empaques.domain.service.SolicitudServiceImpl;
import com.reneseses.empaques.domain.service.UsuarioServiceImpl;
import com.reneseses.empaques.enums.BloqueEnum;
import com.reneseses.empaques.enums.DiasEnum;
import com.reneseses.empaques.enums.EstadoTurnoEnum;
import com.reneseses.empaques.enums.RegimenTurnoEnum;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/member/planillas")
@Controller
@RooWebScaffold(path = "member/planillas", formBackingObject = Planilla.class)
public class PlanillaController {
	@Autowired
    private SolicitudServiceImpl solicitudService;
    
    @Autowired
    private UsuarioServiceImpl usuarioService;
    
    @Autowired
    private RepechajeServiceImpl repechajeService;
    
    @Autowired
    private PlanillaServiceImpl planillaServiceImpl;
    
    @Autowired
    private FaltaServiceImpl faltaService;
    
    @Autowired
    private ImagenUsuarioServiceImpl imagenUsuarioImpl;
    
    
    private int delay = 4;
    
    @RequestMapping("getturnos/{id}")
    @ResponseBody
    public String getData(@PathVariable("id") ObjectId id) {
        Planilla planilla = planillaServiceImpl.findPlanilla(id);
        return planilla.getTurnos();
    }
    
    @RequestMapping("/save")
    public @ResponseBody String save(@RequestParam(value= "fecha", required=false) String fecha, @RequestParam(value= "id", required=false) ObjectId id, @RequestParam("data") String data) {
    	Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) return "false";
    	Planilla planilla = new Planilla();
        if(fecha != null){
    		String[] aux = fecha.split("-");
            Calendar planillaCal = Calendar.getInstance();
            planillaCal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(aux[0]));
            planillaCal.set(Calendar.MONTH, Integer.valueOf(aux[1]) - 1);
            planillaCal.set(Calendar.YEAR, Integer.valueOf(aux[2]));
            planillaCal.set(Calendar.HOUR_OF_DAY, 0);
            planillaCal.set(Calendar.MINUTE, 0);
            planillaCal.set(Calendar.SECOND, 0);
            planilla.setFecha(planillaCal.getTime());
            planilla.createPlanilla(data);
            planillaServiceImpl.savePlanilla(planilla);
            return "true";
        }
    	if(id != null){
    		Planilla bd= planillaService.findPlanilla(id);
    		if(bd== null)
    			return "false";
    		planilla.setFecha(bd.getFecha());
    		planilla.createPlanilla(data);
    		bd.setBloques(planilla.getBloques());
    		planillaService.updatePlanilla(bd);
    		return "true";
    	}
    	return "true";
    }
    
    @RequestMapping("/generarTurnos/{id}")
    public String generarTurnos(@PathVariable("id") ObjectId id, Model uiModel) {
        Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) return "accessFailure";
        Planilla planilla = planillaServiceImpl.findPlanilla(id);
        if (planilla == null) {
            uiModel.addAttribute("page", "1");
            uiModel.addAttribute("size", "25");
            uiModel.addAttribute("error", "No se ha encontrado planilla en la semana especificada");
            return "redirect:/member/planillas";
        }
        if (planilla.getGenerada()) {
            uiModel.addAttribute("page", "1");
            uiModel.addAttribute("size", "25");
            uiModel.addAttribute("error", "La planilla especificada ya ha sido generada");
            return "redirect:/member/planillas";
        }
        Calendar date1 = Calendar.getInstance();
        int day = date1.get(Calendar.DAY_OF_WEEK);
        int hour = date1.get(Calendar.HOUR_OF_DAY);
        int minute = date1.get(Calendar.MINUTE);
        if (day < Calendar.THURSDAY || (day == Calendar.THURSDAY && hour < delay && minute < 30)) {
            uiModel.addAttribute("page", "1");
            uiModel.addAttribute("size", "25");
            uiModel.addAttribute("error", "La recepcion de turnos aun esta activa");
            return "redirect:/member/planillas";
        }
        
        BasicDBObject jo;
        BasicDBList ja;
		
		Calendar date= Calendar.getInstance();
		date.setTime(planilla.getFecha());
		date1 = (Calendar) date.clone();
		Calendar date2 = (Calendar) date.clone();
		date1.set(Calendar.DAY_OF_YEAR, date1.get(Calendar.DAY_OF_YEAR) - 8);
		date2.set(Calendar.DAY_OF_YEAR, date2.get(Calendar.DAY_OF_YEAR) - 1);

		Planilla pasada= planillaServiceImpl.findPlanillaByFecha(date1.getTime(), date2.getTime());
		//System.out.println(pasada.getFecha());
		Map<ObjectId, Integer> turnosUsuario= pasada.getTurnosUsuario(usuarioService.findAllUsuarios());
		Map<ObjectId, Usuario> usuarios= usuarioService.findAll();
		List<Solicitud> solicitudes= solicitudService.findSolicitudesByFechaBetween(date1.getTime(), date2.getTime());
		ordenarSolicitudes(solicitudes, usuarios, turnosUsuario);
		
		date1.set(Calendar.DAY_OF_YEAR, date1.get(Calendar.DAY_OF_YEAR) - 6);
		date1.set(Calendar.HOUR_OF_DAY, 23);
		date2.set(Calendar.DAY_OF_YEAR, date2.get(Calendar.DAY_OF_YEAR) - 5);
		//System.out.println(date1.getTime() + " " + date2.getTime());
		
		for(Solicitud sol: solicitudes){
			Usuario empaque = usuarios.get(sol.getUsuario().getId());
			Integer asignados=0;
			ja = (BasicDBList) JSON.parse(sol.getTurnos());
			Integer maxTurnos= 0;
			if(!empaque.getRegimen().equals(RegimenTurnoEnum.LIBRE)){
				maxTurnos= faltaService.getCantidadTurnos(date1.getTime(), date2.getTime(), empaque);
				//System.out.println(empaque.getNumero() + ": " + maxTurnos);
				if(empaque.getRegimen().equals(RegimenTurnoEnum.NUEVO))
					maxTurnos--;
				
				List<Integer> noAsignado= new ArrayList<Integer>();
				for(int i= 0; i< ja.size(); i++){
					if(asignados >= maxTurnos)
						break;
					
					jo = (BasicDBObject) ja.get(i);
					
					DiasEnum dia = DiasEnum.valueOf(jo.getString("dia"));
					if(empaque.getRegimen().equals(RegimenTurnoEnum.NUEVO))
						if(dia.equals(DiasEnum.DOMINGO) || dia.equals(DiasEnum.SABADO))
							continue;
					BloqueEnum inicio = BloqueEnum.valueOf(jo.getString("inicio"));
					BloqueEnum fin = BloqueEnum.valueOf(jo.getString("fin"));
					boolean asignado= true;
					for(int j= inicio.ordinal(); j <= fin.ordinal(); j++){
						jo = new BasicDBObject();
						jo.put("dia", dia);
						jo.put("inicio", BloqueEnum.values()[j]);
											
						int index = 7*j + dia.ordinal();
						Bloque bloque = planilla.getBloques().get(index);
												
						if(planilla.buscarConflicto(jo, empaque))
							continue;
							
						if(!bloque.addTurno(empaque, i)){
							asignado= false;
							continue;
						}
						
						asignado= true;
						asignados++;
						break;
					}
					if(!asignado)
						noAsignado.add(i);
				}
				if(asignados < maxTurnos){
					ajustarPlanilla(planilla,ja, noAsignado, empaque, maxTurnos, asignados);
				}
			}

			if(empaque.getRegimen().equals(RegimenTurnoEnum.NUEVO) && asignados <= maxTurnos){
				List<Integer> libres= new ArrayList<Integer>();
				for(int i= DiasEnum.values().length* BloqueEnum.DOCE.ordinal() + DiasEnum.DOMINGO.ordinal(); i <  planilla.getBloques().size(); i= i + DiasEnum.values().length){
					Bloque bloque= planilla.getBloques().get(i);
					if(bloque.hasFree())
						libres.add(i);
				}
				if(libres.size() > 0){
					if(!sol.hasSunday()){
						jo = new BasicDBObject();
						jo.put("dia", DiasEnum.DOMINGO);
						jo.put("inicio", BloqueEnum.DOCE);
						jo.put("fin", BloqueEnum.VIENTE);
						ja.add(jo);
						sol.setTurnos(ja.toString());
						solicitudService.saveSolicitud(sol);
					}
					int aux= (int) Math.floor(Math.random() * libres.size());
					Bloque bloque= planilla.getBloques().get(libres.get(aux));
					bloque.addTurno(empaque, ja.size() - 1);
				}
			}
		}

		planilla.setGenerada(true);
		planillaServiceImpl.savePlanilla(planilla);
        return "redirect:/member/planillas/" + String.valueOf(id);
    }
    
    @RequestMapping("/generarRepechaje/{id}")
    public String generarRepechaje(@PathVariable("id") ObjectId id, Model uiModel) {
        Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) return "accessFailure";
        Planilla planilla = planillaServiceImpl.findPlanilla(id);
        if (planilla == null) {
            uiModel.addAttribute("page", "1");
            uiModel.addAttribute("size", "25");
            uiModel.addAttribute("error", "No se ha encontrado planilla en la semana especificada");
            return "redirect:/member/planillas";
        }
        if (planilla.getRepechaje()) {
            uiModel.addAttribute("page", "1");
            uiModel.addAttribute("size", "25");
            uiModel.addAttribute("error", "El repechaje de esta semana ya ha sido generado");
            return "redirect:/member/planillas";
        }
        Calendar date1 = Calendar.getInstance();
        int day = date1.get(Calendar.DAY_OF_WEEK);
        int hour = date1.get(Calendar.HOUR_OF_DAY);
        int minute = date1.get(Calendar.MINUTE);
        if (day < Calendar.SATURDAY || (day == Calendar.SATURDAY && hour < delay && minute < 30)) {
            uiModel.addAttribute("page", "1");
            uiModel.addAttribute("size", "25");
            uiModel.addAttribute("error", "La recepcion de turnos de repechaje aun esta activa");
            return "redirect:/member/planillas";
        }
        
        BasicDBObject jo;
        BasicDBList ja;
		
		Calendar date= Calendar.getInstance();
		date.setTime(planilla.getFecha());
		
		date1 = (Calendar) date.clone();
		Calendar date2 = (Calendar) date.clone();
		
		date1.set(Calendar.DAY_OF_YEAR, date1.get(Calendar.DAY_OF_YEAR) - 8);
		date2.set(Calendar.DAY_OF_YEAR, date2.get(Calendar.DAY_OF_YEAR) - 1);
				
		Map<ObjectId, Usuario> usuarios= usuarioService.findAll();
		Map<ObjectId, Integer> turnosUsuario= planilla.getTurnosUsuario(usuarioService.findAllUsuarios());
		
		List<Repechaje> repechajes= repechajeService.findRepechajesByFechaBetween(date1.getTime(), date2.getTime());
		ordenarRepechaje(repechajes, usuarios, turnosUsuario);
		
		date1.set(Calendar.DAY_OF_YEAR, date1.get(Calendar.DAY_OF_YEAR) + 2);
		date2.set(Calendar.DAY_OF_YEAR, date2.get(Calendar.DAY_OF_YEAR) + 2);
		for(Repechaje repechaje : repechajes){
			int repCont = 0;
			Usuario empaque = usuarios.get(repechaje.getUsuario().getId());
			Integer maxTurnos= faltaService.getCantidadTurnosRepechaje(date1.getTime(), date2.getTime(), empaque);
			if(maxTurnos== 0)
				continue;
			
			ja=  (BasicDBList) JSON.parse(repechaje.getTurnos());
			
			for(int i =0; i < ja.size() && repCont < maxTurnos; i++){
				jo = (BasicDBObject) ja.get(i);
				BasicDBList turnos= (BasicDBList) jo.get("inicio");
				for(int j= 0; j< turnos.size() && repCont < 2; j++){
					BasicDBObject solicitud= new BasicDBObject();
					solicitud.put("dia", jo.getString("dia"));
					solicitud.put("inicio", turnos.get(j));
					if(planilla.buscarConflicto(solicitud, empaque))
						continue;
					Bloque bloque = planilla.getBloque(DiasEnum.valueOf(jo.getString("dia")), BloqueEnum.valueOf((String)turnos.get(j)));
					//System.out.println(bloque);

					if(!bloque.addTurno(empaque, i))
						continue;
					repCont++;
				}
			}
		}
		planilla.setRepechaje(true);
		planillaServiceImpl.savePlanilla(planilla);
        
        return "redirect:/member/planillas/" + String.valueOf(id);
    }
    
    private void ajustarPlanilla(Planilla planilla, BasicDBList turnos, List<Integer> noAsignados, Usuario empaque, Integer maxTurnos,Integer asignados){
		for(Integer solicitud: noAsignados){
			BasicDBObject jo= (BasicDBObject) turnos.get(solicitud);
			DiasEnum dia = DiasEnum.valueOf(jo.getString("dia"));
			BloqueEnum inicio = BloqueEnum.valueOf(jo.getString("inicio"));
			BloqueEnum fin = BloqueEnum.valueOf(jo.getString("fin"));
			
			for(int i= fin.ordinal(); i> BloqueEnum.values().length - 3; i--){
				fin = BloqueEnum.values()[i];
			}
			Calendar date= Calendar.getInstance();
			date.setTime(planilla.getFecha());
			Calendar date1 = (Calendar) date.clone();
			Calendar date2 = (Calendar) date.clone();
			date1.set(Calendar.DAY_OF_YEAR, date1.get(Calendar.DAY_OF_YEAR) - 8);
			date2.set(Calendar.DAY_OF_YEAR, date2.get(Calendar.DAY_OF_YEAR) - 1);

			for(int i= fin.ordinal(); i>= inicio.ordinal(); i--){
				int index = 7*i + dia.ordinal();
				Bloque bloque= planilla.getBloques().get(index);
				
				date.setTime(bloque.getFecha());
				
				int dayOfWeek= date.get(Calendar.DAY_OF_WEEK) - 2;
				if(date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
					dayOfWeek= 6;
				
				DiasEnum day= DiasEnum.values()[dayOfWeek];
				
				String aux= String.valueOf(date.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(date.get(Calendar.MINUTE));
				if(date.get(Calendar.MINUTE) < 10)
					aux= aux + "0";
				BloqueEnum hour= BloqueEnum.bloqueFromValue(aux);
				BasicDBObject job = new BasicDBObject();
				job.put("dia", day);
				job.put("inicio", hour);
				
				if(planilla.buscarConflicto(job, empaque))
					continue;
				
				for(int k= bloque.getTurnos().size()- 1; k >= 0; k--){
					Turno turno= bloque.getTurnos().get(k);
					boolean cambio= false;
					if(turno.getEstado().equals(EstadoTurnoEnum.MOVIDO))
						continue;
					Usuario usuario = turno.getUsuario();
					turno.setUsuario(null);
					turno.setEstado(EstadoTurnoEnum.LIBRE);
					
					List<Solicitud> query= solicitudService.findSolicitudesByFechaBetweenAndUsuario(date1.getTime(), date2.getTime(), usuario);
					Solicitud sol= query.get(0);
					BasicDBList ja =  (BasicDBList) JSON.parse(sol.getTurnos());
					BasicDBObject solTurno= (BasicDBObject) ja.get(turno.getSolicitud());
					BloqueEnum solFin = BloqueEnum.valueOf(solTurno.getString("fin"));
					int cont=0;
					for(int j= hour.ordinal() + 1; j<= solFin.ordinal() && cont < 1; j++){
						cont++;
						Bloque block= planilla.getBloques().get(7*j + dia.ordinal());
						solTurno = new BasicDBObject();
						solTurno.put("dia", dia);
						solTurno.put("inicio", BloqueEnum.values()[j]);
						if(planilla.buscarConflicto(solTurno, usuario))
							continue;
						
						if(block.moveTurno(turno, empaque, usuario, solicitud)){
							cambio= true;
							asignados++;
							if(asignados >= maxTurnos)
								return;
							break;
						}
					}
					if(cambio)
						break;
					else{
						turno.setUsuario(usuario);
						turno.setEstado(EstadoTurnoEnum.OCUPADO);
					}
				}
			}
		}
	}
    
    @RequestMapping(value="getcupos")
    public @ResponseBody String getPlanilla(@RequestParam(value = "id", required = false) ObjectId id) {
    	BasicDBList ja = new BasicDBList();
    	BasicDBList rows = new BasicDBList();
    	BasicDBList data = new BasicDBList();
        BasicDBObject jo;
        String cupos;
        String[] dias = { "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo" };
        BloqueEnum[] bloques = BloqueEnum.values();
        if (id == null) {
            Long count = planillaService.countAllPlanillas();
            if (count > 0) {
                Planilla planilla = planillaService.findPlanillaEntries(count.intValue() - 1, count.intValue()).get(0);
                cupos = planilla.getCupos();
            } else {
                for (int i = 0; i < bloques.length; i++) {
                    jo = new BasicDBObject();
                    for (int j = 0; j < dias.length; j++) jo.put(dias[j], 0);
                    data.add(jo);
                    rows.add(bloques[i].getBloque());
                }
                ja.add(rows);
                ja.add(data);
                cupos = ja.toString();
            }
        } else {
            Planilla planilla = planillaService.findPlanilla(id);
            cupos = planilla.getCupos();
        }
        return cupos;
    }
    
	@RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("planillas", planillaService.findPlanillaEntries(firstResult, sizeNo));
            float nrOfPages = (float) planillaService.countAllPlanillas() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("planillas", planillaServiceImpl.findAllPlanillasOrderByFechaDesc());
        }
        addDateTimeFormatPatterns(uiModel);
        return "member/planillas/list";
    }

	void populateEditForm(Model uiModel, Planilla planilla) {
        uiModel.addAttribute("planilla", planilla);
        uiModel.addAttribute("date", new Date());
        addDateTimeFormatPatterns(uiModel);
    }
	
	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) return "accessFailure";
        int count = (int) planillaService.countAllPlanillas();
        Planilla planilla;
        if (count == 0) {
            planilla = new Planilla();
            populateEditForm(uiModel, planilla);
        } else {
            planilla = planillaService.findPlanillaEntries(count - 1, count).get(0);
            uiModel.addAttribute("date", planilla.getFecha());
        }
        return "member/planillas/create";
    }
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") ObjectId id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) return "accessFailure";
        Planilla planilla = planillaService.findPlanilla(id);
        planillaService.deletePlanilla(planilla);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "25" : size.toString());
        return "redirect:/member/planillas";
    }
	
	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") ObjectId id, Model uiModel) {
        Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) return "accessFailure";
        populateEditForm(uiModel, planillaService.findPlanilla(id));
        return "member/planillas/update";
    }
	
	public void ordenarSolicitudes(List<com.reneseses.empaques.domain.Solicitud> solicitudes, Map<ObjectId, Usuario> usuarios, Map<ObjectId, Integer> turnosUsuarios) {
        for (int i = 0; i < solicitudes.size()- 1; i++) {
            int menor = i;
            for (int j = i + 1; j < solicitudes.size(); j++) {
            	Solicitud aux1= solicitudes.get(menor);
                Solicitud aux2= solicitudes.get(j);
                Usuario user1= usuarios.get(aux1.getUsuario().getId());
                Usuario user2= usuarios.get(aux2.getUsuario().getId());
                if (user1.getPrioridad() < user2.getPrioridad()) continue;
                if (user1.getPrioridad() == user2.getPrioridad()) {
                	int turno1= (turnosUsuarios.get(user1.getId()) < 3) ? turnosUsuarios.get(user1.getId()):3;
                	int turno2= (turnosUsuarios.get(user2.getId()) < 3) ? turnosUsuarios.get(user2.getId()):3;
                    if (turno1 < turno2) continue;
                    if (turno1 == turno2 && aux1.getFecha().before(aux2.getFecha())) continue;
                }
                menor= j;
            }
            Collections.swap(solicitudes,i,menor);
        }
    }
	
	public static void ordenarRepechaje(List<com.reneseses.empaques.domain.Repechaje> repechajes, Map<ObjectId, Usuario> usuarios, Map<ObjectId, Integer> turnosUsuarios) {
	    for (int i = 0; i < repechajes.size() - 1; i++) {
	        int menor = i;
	        for (int j = i + 1; j < repechajes.size(); j++) {
	            Repechaje rep1= repechajes.get(menor);
	            Repechaje aux= repechajes.get(j);
	            Usuario user1= usuarios.get(rep1.getUsuario().getId());
                Usuario user2= usuarios.get(aux.getUsuario().getId());
	            if (user1.getPrioridad() < user2.getPrioridad()) continue;
	            if (user1.getPrioridad() == user2.getPrioridad()) {
	            	int turno1= (turnosUsuarios.get(user1.getId()) < 3) ? turnosUsuarios.get(user1.getId()):3;
                	int turno2= (turnosUsuarios.get(user2.getId()) < 3) ? turnosUsuarios.get(user2.getId()):3;
                    if (turno1 < turno2) continue;
	                if (turno1==turno2 && rep1.getFecha().before(aux.getFecha())) continue;
	            }
	            menor = j;
	        }
	        Collections.swap(repechajes,i,menor);
	    }
	}

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") ObjectId id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("planilla", planillaService.findPlanilla(id));
        uiModel.addAttribute("itemId", id);
        Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	if (principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")))
    		return "member/planillas/updateTurnos";
        return "member/planillas/show";
    }
	
	@RequestMapping("/saveTurnos")
    public @ResponseBody String saveTurnos(@RequestParam("id") ObjectId id, @RequestParam("data") String data) {
        Planilla planilla = planillaService.findPlanilla(id);
        BasicDBList ja = (BasicDBList) JSON.parse(data);
        List<Bloque> bloques = planilla.getBloques();
        for (int i = 0; i < ja.size(); i++) {
        	BasicDBList change = (BasicDBList) ja.get(i);
        	
        	Integer bloq= (Integer) change.get(0);
        	String day= (String) change.get(1);
        	Integer nume= (Integer) change.get(2);
        	Integer numero= (Integer) change.get(3);
        	
            DiasEnum dia = DiasEnum.valueOf(day.toUpperCase());
            Bloque bloque = bloques.get(7 * bloq + dia.ordinal());
            Usuario usuario= usuarioService.findUsuarioByNumero(numero);
            List<Turno> turnos = bloque.getTurnos();
            Turno turno = turnos.get(nume);
            turno.setUsuario(usuario);
        }
        
        planillaService.savePlanilla(planilla);
        return "true";
    }
	
    @RequestMapping("getuser")
    public @ResponseBody String getData(@RequestParam("numero") Integer numero, @RequestParam("id") ObjectId id) {
    	Usuario usuario = usuarioService.findUsuarioByNumero(numero);
    	BasicDBObject jo = new BasicDBObject();
        jo.put("nombre", usuario.getNombre());
        jo.put("id", usuario.getId());
        ImagenUsuario imgUser= imagenUsuarioImpl.findByUsuario(usuario);
        if (imgUser == null || imgUser.getImagen() == null) jo.put("imagen", "null"); else jo.put("imagen", imgUser.getImagen().getId());
        jo.put("numero", numero);
        jo.put("turnos", Planilla.turnos(usuario, planillaService.findPlanilla(id)));

        return jo.toString();
    }
}
