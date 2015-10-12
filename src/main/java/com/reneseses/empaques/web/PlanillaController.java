package com.reneseses.empaques.web;

import java.text.SimpleDateFormat;
import java.util.*;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.reneseses.empaques.domain.Bloque;
import com.reneseses.empaques.domain.Planilla;
import com.reneseses.empaques.domain.Repechaje;
import com.reneseses.empaques.domain.Solicitud;
import com.reneseses.empaques.domain.Supermercado;
import com.reneseses.empaques.domain.Turno;
import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.UsuarioId;
import com.reneseses.empaques.domain.service.FaltaServiceImpl;
import com.reneseses.empaques.domain.service.PlanillaServiceImpl;
import com.reneseses.empaques.domain.service.RepechajeServiceImpl;
import com.reneseses.empaques.domain.service.SolicitudServiceImpl;
import com.reneseses.empaques.domain.service.SupermercadoServiceImpl;
import com.reneseses.empaques.domain.service.UsuarioServiceImpl;
import com.reneseses.empaques.enums.BloqueEnum;
import com.reneseses.empaques.enums.DiasEnum;
import com.reneseses.empaques.enums.EstadoTurnoEnum;
import com.reneseses.empaques.enums.RegimenTurnoEnum;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private SupermercadoServiceImpl supermercadoServiceImpl;
        
    @RequestMapping("getturnos/{id}")
    @ResponseBody
    public String getData(@PathVariable("id") ObjectId id) {
        Planilla planilla = planillaServiceImpl.findPlanilla(id);
        return planilla.getTurnos().toString();
    }
    
    @RequestMapping("ajax/getall")
    public @ResponseBody ResponseEntity<String> getPlanillas(){   	
    	HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
    	
    	BasicDBList response= new BasicDBList();
    	
    	try{
	    	Usuario usuario= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	
	    	List<Planilla> planillas= planillaServiceImpl.findAllPlanillasOrderByFechaDesc(usuario.getId().getSupermercado());
	    	
	    	SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy");
	    	
	    	for(Planilla planilla: planillas){
	    		BasicDBObject jo= new BasicDBObject();
	    		
	    		jo.append("id", planilla.getId().toString());
	    		jo.append("fecha", sdf.format(planilla.getFecha()));
	    		
	    		response.add(jo);
	    	}
	    	
	    	return new ResponseEntity<String>(response.toString(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		e.printStackTrace();
    		return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    @RequestMapping("/save")
    public @ResponseBody ResponseEntity<String> save(@RequestParam(value= "fecha", required=false) String fecha, @RequestParam(value= "id", required=false) ObjectId id, @RequestParam("data") String data) {
    	Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	
    	HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/text; charset=utf-8");

	    List<GrantedAuthority> authorities= principal.getAuthorities();
	    if (!authorities.contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
			    && !authorities.contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
			    && !authorities.contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
			    && !authorities.contains(new SimpleGrantedAuthority("LOCALADMIN")))
		    return new ResponseEntity<String>(headers, HttpStatus.FORBIDDEN);
    	
    	Planilla planilla = new Planilla();
    	try{
    		if(id != null){
	    		Planilla bd= planillaServiceImpl.findPlanilla(id);
	    		if(bd== null)
	    			return new ResponseEntity<String>(headers, HttpStatus.BAD_REQUEST);
	    		planilla.setFecha(bd.getFecha());
	    		planilla.createPlanilla(data);
	    		bd.setBloques(planilla.getBloques());
	    		planillaServiceImpl.updatePlanilla(bd);
	    		
	    		return new ResponseEntity<String>(headers, HttpStatus.OK);
	    	}else if(fecha != null){		
	        	SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy");
	        	
	            Date date= sdf.parse(fecha);
	            planilla.setFecha(date);
	            planilla.createPlanilla(data);
			    planilla.setSupermercado(principal.getId().getSupermercado());
	            planillaServiceImpl.savePlanilla(planilla);
	            
	            return new ResponseEntity<String>(headers, HttpStatus.OK);
	        }
    	}catch(Exception e){
    		e.printStackTrace();
    		return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	
		return new ResponseEntity<String>(headers, HttpStatus.BAD_REQUEST);
    }
    
    @RequestMapping("/generarTurnos/{id}")
    public String generarTurnos(@PathVariable("id") ObjectId id, RedirectAttributes uiModel) {
        Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))
		        && !principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN")))
	        return "accessFailure";
        
        Planilla planilla = planillaServiceImpl.findPlanilla(id);
		
        if (planilla == null) {
            uiModel.addFlashAttribute("error", "No se ha encontrado planilla en la semana especificada");
            return "redirect:/member/planillas";
        }
        if (planilla.getGenerada()) {
            uiModel.addFlashAttribute("error", "La planilla especificada ya ha sido generada");
            return "redirect:/member/planillas";
        }
        
        Supermercado supermercado= supermercadoServiceImpl.findSupermercado(planilla.getSupermercado());
        Calendar date1 = Calendar.getInstance();

        Date now= new Date();
                
        if (supermercado.isTurnosActivo() && now.before(planilla.getFecha())) {
            uiModel.addFlashAttribute("error", "La recepcion de turnos aun esta activa");
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

		Planilla pasada= planillaServiceImpl.findPlanillaByFecha(supermercado.getId(), date1.getTime(), date2.getTime());
		Map<Integer, Integer> turnosUsuario= new HashMap<Integer, Integer>();
		if(pasada != null)
			turnosUsuario= pasada.getTurnosUsuario(usuarioService.findAllUsuarios());
		
		Map<UsuarioId, Usuario> usuarios= usuarioService.findAll();
		List<Solicitud> solicitudes= solicitudService.findSolicitudesByFechaBetween(supermercado.getId(), date1.getTime(), date2.getTime());
		ordenarSolicitudes(solicitudes, usuarios, turnosUsuario, supermercado);
		
		date1.set(Calendar.DAY_OF_YEAR, date1.get(Calendar.DAY_OF_YEAR) - 6);
		date1.set(Calendar.HOUR_OF_DAY, 23);
		date2.set(Calendar.DAY_OF_YEAR, date2.get(Calendar.DAY_OF_YEAR) - 5);
		//System.out.println(date1.getTime() + " " + date2.getTime());
		
		for(Solicitud sol: solicitudes){
			Usuario empaque = usuarios.get(sol.getUsuario());
			Integer asignados=0;
			ja = sol.getTurnos();
			Integer maxTurnos= 0;
			if(!empaque.getRegimen().equals(RegimenTurnoEnum.LIBRE)){
				Integer limite= supermercado.getMaxTurnos()> supermercado.getMaxTurnosTotal()? supermercado.getMaxTurnosTotal(): supermercado.getMaxTurnos();
				
				maxTurnos= faltaService.getCantidadTurnos(planilla.getId(), empaque.getId(), limite);
				if(empaque.getRegimen().equals(RegimenTurnoEnum.NUEVO))
					maxTurnos--;
				
				List<Integer> noAsignado= new ArrayList<Integer>();
				for(int i= 0; i< ja.size(); i++){
					if(asignados >= maxTurnos)
						break;
					
					LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) ja.get(i);
					
					DiasEnum dia = DiasEnum.valueOf(map.get("dia"));
					if(empaque.getRegimen().equals(RegimenTurnoEnum.NUEVO))
						if(dia.equals(DiasEnum.DOMINGO) || dia.equals(DiasEnum.SABADO))
							continue;
					BloqueEnum inicio = BloqueEnum.valueOf(map.get("inicio"));
					BloqueEnum fin = BloqueEnum.valueOf(map.get("fin"));
					boolean asignado= true;
					for(int j= inicio.ordinal(); j <= fin.ordinal(); j++){
						jo = new BasicDBObject();
						jo.put("dia", dia);
						jo.put("inicio", BloqueEnum.values()[j]);
											
						int index = 7*j + dia.ordinal();
						Bloque bloque = planilla.getBloques().get(index);
												
						if(planilla.buscarConflicto(jo, empaque.getId().getNumero()))
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
						sol.setTurnos(ja);
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
    public String generarRepechaje(@PathVariable("id") ObjectId id, RedirectAttributes uiModel) {
        Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))
		        && !principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN")))
	        return "accessFailure";
        
        Planilla planilla = planillaServiceImpl.findPlanilla(id);
                
        if (planilla == null) {
            uiModel.addFlashAttribute("error", "No se ha encontrado planilla en la semana especificada");
            return "redirect:/member/planillas";
        }
        if (planilla.getRepechaje()) {
            uiModel.addFlashAttribute("error", "El repechaje de esta semana ya ha sido generado");
            return "redirect:/member/planillas";
        }
        
        Supermercado supermercado= supermercadoServiceImpl.findSupermercado(planilla.getSupermercado());
        Calendar date1 = Calendar.getInstance();
        
        Date now= new Date();
        
        if ((supermercado.isTurnosActivo() || supermercado.isRepechajeActivo()) && now.before(planilla.getFecha())) {
            uiModel.addFlashAttribute("error", "La recepcion de turnos de repechaje aun esta activa");
            return "redirect:/member/planillas";
        }
        
        BasicDBList ja;
		
		Calendar date= Calendar.getInstance();
		date.setTime(planilla.getFecha());
		
		date1 = (Calendar) date.clone();
		Calendar date2 = (Calendar) date.clone();
		
		date1.set(Calendar.DAY_OF_YEAR, date1.get(Calendar.DAY_OF_YEAR) - 8);
		date2.set(Calendar.DAY_OF_YEAR, date2.get(Calendar.DAY_OF_YEAR) - 1);
				
		Map<UsuarioId, Usuario> usuarios= usuarioService.findAll();
		Map<Integer, Integer> turnosUsuario= planilla.getTurnosUsuario(usuarioService.findAllUsuarios());
		
		List<Repechaje> repechajes= repechajeService.findRepechajesByFechaBetween(planilla.getSupermercado(), date1.getTime(), date2.getTime());
		ordenarRepechaje(repechajes, usuarios, turnosUsuario, supermercado);
		
		date1.set(Calendar.DAY_OF_YEAR, date1.get(Calendar.DAY_OF_YEAR) + 2);
		date2.set(Calendar.DAY_OF_YEAR, date2.get(Calendar.DAY_OF_YEAR) + 2);
		
		for(Repechaje repechaje : repechajes){
			int repCont = 0;
			
			Usuario empaque = usuarios.get(repechaje.getUsuario());
			Integer maxTurnos= faltaService.getCantidadTurnosRepechaje(planilla.getId(), empaque.getId(), supermercado.getMaxRepechaje());
			Integer currentTurnos= turnosUsuario.get(empaque.getId().getNumero());
						
			maxTurnos= maxTurnos > supermercado.getMaxTurnosTotal() - currentTurnos? supermercado.getMaxTurnosTotal()-currentTurnos: maxTurnos;
			if(maxTurnos<= 0)
				continue;
			
			ja= repechaje.getTurnos();
			
			for(int i =0; i < ja.size() && repCont < maxTurnos; i++){
				LinkedHashMap<String, Object> map= (LinkedHashMap<String, Object>) ja.get(i);
				ArrayList<String> turnos= (ArrayList<String>) map.get("inicio");
				for(int j= 0; j< turnos.size() && repCont < 2; j++){
					BasicDBObject solicitud= new BasicDBObject();
					String dia= (String) map.get("dia");

					solicitud.put("dia", DiasEnum.valueOf(dia));
					solicitud.put("inicio", BloqueEnum.valueOf(turnos.get(j)));
					if(planilla.buscarConflicto(solicitud, empaque.getId().getNumero()))
						continue;
					Bloque bloque = planilla.getBloque(DiasEnum.valueOf(dia), BloqueEnum.valueOf((String)turnos.get(j)));
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
			LinkedHashMap<String, Object> jo= (LinkedHashMap<String, Object>) turnos.get(solicitud);
			DiasEnum dia = DiasEnum.valueOf((String) jo.get("dia"));
			BloqueEnum inicio = BloqueEnum.valueOf((String) jo.get("inicio"));
			BloqueEnum fin = BloqueEnum.valueOf((String) jo.get("fin"));
			
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
				
				if(planilla.buscarConflicto(job, empaque.getId().getNumero()))
					continue;
				
				for(int k= bloque.getTurnos().size()- 1; k >= 0; k--){
					Turno turno= bloque.getTurnos().get(k);
					boolean cambio= false;
					if(turno.getEstado().equals(EstadoTurnoEnum.MOVIDO))
						continue;
					
					Integer usuario = turno.getUsuario();
					
					UsuarioId id= new UsuarioId();
					id.setNumero(usuario);
					id.setSupermercado(planilla.getSupermercado());
					
					turno.setUsuario(null);
					turno.setEstado(EstadoTurnoEnum.LIBRE);
					
					List<Solicitud> query= solicitudService.findSolicitudesByFechaBetweenAndUsuario(date1.getTime(), date2.getTime(), id);
					Solicitud sol= query.get(0);
					BasicDBList ja = sol.getTurnos();
					LinkedHashMap<String, Object> solTurno= (LinkedHashMap<String, Object>) ja.get(turno.getSolicitud());
					BloqueEnum solFin = BloqueEnum.valueOf((String)solTurno.get("fin"));
					int cont=0;
					for(int j= hour.ordinal() + 1; j<= solFin.ordinal() && cont < 1; j++){
						cont++;
						Bloque block= planilla.getBloques().get(7*j + dia.ordinal());
						solTurno = new BasicDBObject();
						solTurno.put("dia", dia);
						solTurno.put("inicio", BloqueEnum.values()[j]);
						if(planilla.buscarConflicto(solTurno, usuario))
							continue;
						
						if(block.moveTurno(turno, empaque.getId().getNumero(), usuario, solicitud)){
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
            Long count = planillaServiceImpl.countAllPlanillas();
            if (count > 0) {
                Planilla planilla = planillaServiceImpl.findPlanillaEntries(count.intValue() - 1, count.intValue()).get(0);
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
            Planilla planilla = planillaServiceImpl.findPlanilla(id);
            cupos = planilla.getCupos();
        }
        return cupos;
    }
    
	@RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		Usuario principal= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
			uiModel.addAttribute("planillas", planillaServiceImpl.findAllPlanillasOrderByFechaDesc());
		} else{
			uiModel.addAttribute("planillas", planillaServiceImpl.findAllPlanillasBySupermercado(principal.getId().getSupermercado()));
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
		List<GrantedAuthority> authorities= principal.getAuthorities();
        if (!authorities.contains(new SimpleGrantedAuthority("ADMIN"))
		        && !authorities.contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
		        && !authorities.contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
		        && !authorities.contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
		        && !authorities.contains(new SimpleGrantedAuthority("LOCALADMIN")))
	        return "accessFailure";

		if(principal.getId() == null || principal.getId().getSupermercado() == null)
			return "accessFailure";

        Planilla planilla= planillaServiceImpl.findLastPlanillaBySuperMercado(principal.getId().getSupermercado());
        if (planilla== null) {
            planilla = new Planilla();
            populateEditForm(uiModel, planilla);
        } else {
            uiModel.addAttribute("date", planilla.getFecha());
        }
        return "member/planillas/create";
    }
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") ObjectId id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<GrantedAuthority> authorities= principal.getAuthorities();
		if (!authorities.contains(new SimpleGrantedAuthority("ADMIN"))
				&& !authorities.contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				&& !authorities.contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				&& !authorities.contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				&& !authorities.contains(new SimpleGrantedAuthority("LOCALADMIN")))
			return "accessFailure";
        Planilla planilla = planillaServiceImpl.findPlanilla(id);
        planillaServiceImpl.deletePlanilla(planilla);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "25" : size.toString());
        return "redirect:/member/planillas";
    }
	
	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") ObjectId id, Model uiModel) {
        Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<GrantedAuthority> authorities= principal.getAuthorities();
		if (!authorities.contains(new SimpleGrantedAuthority("ADMIN"))
				&& !authorities.contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				&& !authorities.contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				&& !authorities.contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				&& !authorities.contains(new SimpleGrantedAuthority("LOCALADMIN")))
			return "accessFailure";
        populateEditForm(uiModel, planillaServiceImpl.findPlanilla(id));
        return "member/planillas/update";
    }
	
	public void ordenarSolicitudes(List<Solicitud> solicitudes, Map<UsuarioId, Usuario> usuarios, Map<Integer, Integer> turnosUsuarios, Supermercado supermercado) {
        int maxTurnos= supermercado.getMaxTurnosTotal();
		
		for (int i = 0; i < solicitudes.size()- 1; i++) {
            int menor = i;
            for (int j = i + 1; j < solicitudes.size(); j++) {
            	Solicitud aux1= solicitudes.get(menor);
                Solicitud aux2= solicitudes.get(j);
                Usuario user1= usuarios.get(aux1.getUsuario());
                Usuario user2= usuarios.get(aux2.getUsuario());
                Integer num1= user1.getId().getNumero();
                Integer num2= user2.getId().getNumero();
                
                if (user1.getPrioridad() < user2.getPrioridad()) continue;
                if (user1.getPrioridad() == user2.getPrioridad()) {
                	int turno1= turnosUsuarios.containsKey(num1) && turnosUsuarios.get(num1) < maxTurnos ? turnosUsuarios.get(num1):maxTurnos;
                	int turno2= turnosUsuarios.containsKey(num2) && turnosUsuarios.get(num2) < maxTurnos ? turnosUsuarios.get(num2):maxTurnos;
                	if (turno1 < turno2) continue;
                    if (turno1 == turno2 && aux1.getFecha().before(aux2.getFecha())) continue;
                }
                menor= j;
            }
            Collections.swap(solicitudes, i, menor);
        }
    }
	
	public static void ordenarRepechaje(List<Repechaje> repechajes, Map<UsuarioId, Usuario> usuarios, Map<Integer, Integer> turnosUsuarios, Supermercado supermercado) {
		int maxTurnos= supermercado.getMaxTurnosTotal();
		
		for (int i = 0; i < repechajes.size() - 1; i++) {
	        int menor = i;
	        for (int j = i + 1; j < repechajes.size(); j++) {
	            Repechaje rep1= repechajes.get(menor);
	            Repechaje aux= repechajes.get(j);
	            Usuario user1= usuarios.get(rep1.getUsuario());
                Usuario user2= usuarios.get(aux.getUsuario());
                Integer num1= user1.getId().getNumero();
                Integer num2= user2.getId().getNumero();
                
	            if (user1.getPrioridad() < user2.getPrioridad()) continue;
	            if (user1.getPrioridad() == user2.getPrioridad()) {
	            	int turno1= turnosUsuarios.containsKey(num1) && turnosUsuarios.get(num1) < maxTurnos ? turnosUsuarios.get(num1):maxTurnos;
                	int turno2= turnosUsuarios.containsKey(num2) && turnosUsuarios.get(num2) < maxTurnos ? turnosUsuarios.get(num2):maxTurnos;
                	if (turno1 < turno2) continue;
	                if (turno1==turno2 && rep1.getFecha().before(aux.getFecha())) continue;
	            }
	            menor = j;
	        }
	        if(i != menor)
	        	Collections.swap(repechajes,i,menor);
	    }
	}

	@RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") ObjectId id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("planilla", planillaServiceImpl.findPlanilla(id));
        uiModel.addAttribute("itemId", id);
        Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<GrantedAuthority> authorities= principal.getAuthorities();
		if (authorities.contains(new SimpleGrantedAuthority("ADMIN"))
				|| authorities.contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				|| authorities.contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				|| authorities.contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				|| authorities.contains(new SimpleGrantedAuthority("LOCALADMIN")))
			return "member/planillas/updateTurnos";
        return "member/planillas/show";
    }
	
	@RequestMapping("/saveTurnos")
    public @ResponseBody ResponseEntity<String> saveTurnos(@RequestParam("id") ObjectId id, @RequestParam("data") String data) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/text; charset=utf-8");
		
		Usuario principal= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Planilla planilla = planillaServiceImpl.findPlanilla(id);

        try{
	        BasicDBList ja = (BasicDBList) JSON.parse(data);
	        List<Bloque> bloques = planilla.getBloques();
	        for (int i = 0; i < ja.size(); i++) {
	        	BasicDBList change = (BasicDBList) ja.get(i);
	        	
	        	Integer bloq= (Integer) change.get(0);
	        	String day= (String) change.get(1);
	        	Integer nume= (Integer) change.get(2);
	        	String value= (String)change.get(3);
	        	Integer numero= null;
	        	
	        	try{
	        		numero= Integer.valueOf(value);
	        	}catch(Exception e){};
	        	
	        	Usuario usuario= null;
	        	if(numero != null){
	        		UsuarioId usuarioId= new UsuarioId();
		        	usuarioId.setNumero(numero);
		        	usuarioId.setSupermercado(principal.getId().getSupermercado());
		        	usuario= usuarioService.findUsuario(usuarioId);
	        	}
	        	
	            DiasEnum dia = DiasEnum.valueOf(day.toUpperCase());
	            Bloque bloque = bloques.get(7 * bloq + dia.ordinal());
	            List<Turno> turnos = bloque.getTurnos();
	            
	            Turno turno = turnos.get(nume);
	            turno.setUsuario(usuario==null?null: usuario.getId().getNumero());
	        }
	        
	        planillaServiceImpl.savePlanilla(planilla);
        }catch(Exception e){
        	e.printStackTrace();
        	return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
	
    @RequestMapping("getuser")
    public @ResponseBody String getData(@RequestParam("numero") Integer numero, @RequestParam("id") ObjectId id) {
    	Usuario principal= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	
    	UsuarioId usuarioId= new UsuarioId();
    	usuarioId.setNumero(numero);
    	usuarioId.setSupermercado(principal.getId().getSupermercado());
    	
    	Usuario usuario = usuarioService.findUsuario(usuarioId);
    	BasicDBObject jo = new BasicDBObject();
        jo.put("nombre", usuario.getNombre());
        jo.put("id", usuario.getId());
        jo.put("imagen", usuario.getImage() == null? null: usuario.getImage().toString());
        jo.put("numero", numero);
        jo.put("turnos", Planilla.turnos(usuario, planillaServiceImpl.findPlanilla(id)));

        return jo.toString();
    }
}
