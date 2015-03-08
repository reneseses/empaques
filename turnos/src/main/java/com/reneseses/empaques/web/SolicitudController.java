package com.reneseses.empaques.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.reneseses.empaques.domain.Bloque;
import com.reneseses.empaques.domain.Planilla;
import com.reneseses.empaques.domain.Repechaje;
import com.reneseses.empaques.domain.Solicitud;
import com.reneseses.empaques.domain.Turno;
import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.service.PlanillaServiceImpl;
import com.reneseses.empaques.domain.service.RepechajeServiceImpl;
import com.reneseses.empaques.domain.service.SolicitudServiceImpl;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/member/solicitudes")
@Controller
@RooWebScaffold(path = "member/solicitudes", formBackingObject = Solicitud.class)
public class SolicitudController {
	
	@Autowired
    private RepechajeServiceImpl repechajeService;
	
	@Autowired
    private SolicitudServiceImpl solicitudServiceImpl;
	
	@Autowired
    private PlanillaServiceImpl planillaServiceImpl;
	
	private int delay = 3;

    @RequestMapping("/recibir")
    public @ResponseBody ResponseEntity<String> recibir(@RequestParam(value = "turnos", required = true) String turnos, @RequestParam(value = "id", required = false) ObjectId id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Solicitud solicitud = new Solicitud();
        
        HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/text; charset=utf-8");
        
		try{
	        if(id != null)
	        	solicitud= solicitudService.findSolicitud(id);
	        
	        Usuario usuario = (Usuario) principal;
	        
	        solicitud.setUsuario(usuario);
	        
	        BasicDBList ja = (BasicDBList) JSON.parse(turnos);
	        solicitud.setTurnos(ja.toString());
	        
	        Date date = new Date();
	        usuario.setLastSolicitud(date);
	        usuarioService.updateUsuario(usuario);
	        
	        solicitud.setFecha(date);
	        solicitudService.saveSolicitud(solicitud);
	        
	        return new ResponseEntity<String>(headers, HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    @RequestMapping("/recibirRepechaje")
    public @ResponseBody ResponseEntity<String> recibirRepechaje(@RequestParam(value = "turnos", required = true) String turnos, @RequestParam(value = "id", required = false) ObjectId id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/text; charset=utf-8");
        
        Repechaje repechaje = new Repechaje();
        try{
	        if(id != null)
	        	repechaje= repechajeService.findRepechaje(id);
	
	        Usuario usuario = (Usuario) principal;
	        repechaje.setUsuario(usuario);
	        
	        BasicDBList ja = (BasicDBList) JSON.parse(turnos);
	        repechaje.setTurnos(ja.toString());
	        
	        Date date = new Date();
	        usuario.setLastSolicitud(date);
	        usuarioService.updateUsuario(usuario);
	        
	        repechaje.setFecha(date);
	        repechajeService.saveRepechaje(repechaje);
	        
	        return new ResponseEntity<String>(headers, HttpStatus.OK);
        }catch(Exception e){
        	e.printStackTrace();
			return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/turnos")
    public String turno(Model uiModel) {
        Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        
        if(principal.getRegimen().equals(RegimenTurnoEnum.LIBRE)){
        	uiModel.addAttribute("error", "Ud tiene asistencia libre.");
        	return "member/solicitudes/error";
        }
                
        if (day <= Calendar.THURSDAY && day > Calendar.SUNDAY) {
            if (day == Calendar.MONDAY) {
            	if(hour < delay - 1 || (hour == delay - 1 && minute < 30) ){
            		uiModel.addAttribute("error", "La recepción de turnos comienza a las 23:30 horas");
                    return "member/solicitudes/error";
            	}
            }
            if (day == Calendar.THURSDAY && (hour > delay - 1 || (hour == delay - 1 && minute >= 30))) {
                uiModel.addAttribute("error", "La recepción de turnos terminó a las 23:29 horas");
                return "member/solicitudes/error";
            }
            cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_WEEK) + 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            Calendar date2 = Calendar.getInstance();
            day = cal.get(Calendar.DAY_OF_YEAR) + 6;
            date2.set(Calendar.DAY_OF_YEAR, day);
            date2.set(Calendar.HOUR_OF_DAY, 0);
            date2.set(Calendar.MINUTE, 0);
            date2.set(Calendar.SECOND, 0);
            List<Solicitud> sols = solicitudServiceImpl.findSolicitudesByFechaBetweenAndUsuario(cal.getTime(), date2.getTime(), principal);
            if (sols.size() > 0) {
                uiModel.addAttribute("solicitud", sols.get(0).getTurnos());
                uiModel.addAttribute("solicitudId", sols.get(0).getId());
            }
            uiModel.addAttribute("dias", DiasEnum.values());
            uiModel.addAttribute("bloques", BloqueEnum.values());
            return "member/solicitudes/turnos";
        }
        uiModel.addAttribute("error", "La recepción de turnos comienza los domingo a las 23:30 horas y termina los miércoles a las 23:29");
        return "member/solicitudes/error";
    }

    @RequestMapping("/repechaje")
    public String repechaje(Model uiModel) {
        Usuario user = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario principal= usuarioService.findUsuario(user.getId());
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        
        System.out.println(new Date());
        if (day <= Calendar.SATURDAY && day > Calendar.THURSDAY) {
            if (day == Calendar.FRIDAY) {
            	if(hour < delay - 1 || (hour == delay - 1 && minute < 30) ){
	                uiModel.addAttribute("error", "La recepcion de turnos de repechaje comienza a las 23:30 horas");
	                return "member/solicitudes/error";
            	}
            }
            if (day == Calendar.SATURDAY && (hour > delay - 1 || (hour == delay - 1 && minute >= 30))) {
                uiModel.addAttribute("error", "La recepcion de turnos de repechaje termino a las 23:29 horas");
                return "member/solicitudes/error";
            }
            cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_WEEK) + 1);
            Calendar date2 = Calendar.getInstance();
            day = cal.get(Calendar.DAY_OF_YEAR) + 7;
            cal.set(Calendar.HOUR_OF_DAY, 0);
            
            date2.set(Calendar.DAY_OF_YEAR, day);
            date2.set(Calendar.HOUR_OF_DAY, 0);
            date2.set(Calendar.MINUTE, 0);
            date2.set(Calendar.SECOND, 0);
            List<Solicitud> solicitudes= solicitudServiceImpl.findSolicitudesByFechaBetweenAndUsuario(cal.getTime(), date2.getTime(), principal);
            if (solicitudes.size() <= 0) {
                uiModel.addAttribute("error", "Ud no realizó su solicitud de Turnos");
                return "member/solicitudes/error";
            }
            List<Repechaje> repechajes = repechajeService.findRepechajesByFechaBetweenAndUsuario(cal.getTime(), date2.getTime(), principal);
            if (repechajes.size() > 0) {
                uiModel.addAttribute("repechaje", repechajes.get(0).getTurnos());
                uiModel.addAttribute("repechajeId", repechajes.get(0).getId());
            }
            date2 = Calendar.getInstance();
            day = date2.get(Calendar.DAY_OF_YEAR) + (7 - date2.get(Calendar.DAY_OF_WEEK)) + 2;
            date2.set(Calendar.DAY_OF_YEAR, day + 1);
            date2.set(Calendar.HOUR_OF_DAY, 4);
            date2.set(Calendar.MINUTE, 0);
            date2.set(Calendar.SECOND, 0);
            cal = (Calendar) date2.clone();
            date2.set(Calendar.DAY_OF_YEAR, day - 1);
            
            Planilla planilla = planillaServiceImpl.findPlanillaByFecha(date2.getTime(), cal.getTime());
            if (planilla == null) {
                uiModel.addAttribute("error", "Error al acceder a la planilla del " + date2.get(Calendar.DAY_OF_MONTH) + "/" + (date2.get(Calendar.MONTH) + 1) + "/" + date2.get(Calendar.YEAR));
                return "member/solicitudes/error";
            }
            List<Bloque> bloques = planilla.getBloques();
            List<List<String>> array = new ArrayList<List<String>>();
            for (int i = 0; i < 7; i++) {
                array.add(new ArrayList<String>());
            }
            for (Bloque bloque : bloques) {
                List<Turno> turnos = bloque.getTurnos();
                boolean hayTurnos = false;
                for (Turno turno : turnos) if (turno.getEstado().equals(EstadoTurnoEnum.LIBRE)) {
                    hayTurnos = true;
                    break;
                }
                if (hayTurnos) {
                    cal.setTime(bloque.getFecha());
                    if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) day = 6; else day = cal.get(Calendar.DAY_OF_WEEK) - 2;
                    String hora = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
                    String minuto = String.valueOf(cal.get(Calendar.MINUTE));
                    if (minuto.length() < 2) minuto = "0" + minuto;
                    array.get(day).add("'" + hora + ":" + minuto + "'");
                }
            }
            uiModel.addAttribute("dias", DiasEnum.values());
            uiModel.addAttribute("bloques", BloqueEnum.values());
            uiModel.addAttribute("turnos", array);
            return "member/solicitudes/repechaje";
        }
        uiModel.addAttribute("error", "La recepcion de turnos de repechaje comienza los jueves a las 23:30 horas y termina los viernes a las 23:29");
        return "member/solicitudes/error";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 25 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("solicituds", solicitudServiceImpl.findSolicitudEntries(firstResult, sizeNo));
            float nrOfPages = (float) solicitudServiceImpl.countAllSolicituds() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("solicituds", solicitudServiceImpl.findAllSolicitudesOrderByFecha());
        }
        addDateTimeFormatPatterns(uiModel);
        BasicDBObject jo = new BasicDBObject();
        for (int i = 0; i < BloqueEnum.values().length; i++) jo.put(BloqueEnum.values()[i].toString(), BloqueEnum.values()[i].getBloque());
        uiModel.addAttribute("bloques", jo.toString());
        jo = new BasicDBObject();
        for (int i = 0; i < DiasEnum.values().length; i++) jo.put(DiasEnum.values()[i].toString(), DiasEnum.values()[i].getDia());
        uiModel.addAttribute("dias", jo.toString());
        return "member/solicitudes/list";
    }

    @RequestMapping(value = "listRepechaje", produces = "text/html")
    public String listRepechaje(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 25 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("repechajes", repechajeService.findRepechajeEntries(firstResult, sizeNo));
            float nrOfPages = (float) repechajeService.countAllRepechajes() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("repechajes", repechajeService.findAllOrderByFecha());
        }
        addDateTimeFormatPatterns(uiModel);
        return "member/solicitudes/listRepechajes";
    }

    @RequestMapping("gensol")
    public String generarSolicitudes(@RequestParam(value = "number", required = true) Integer number) {
        List<Usuario> usuarios = usuarioService.findAllUsuarios();
        BloqueEnum[] bloques = BloqueEnum.values();
        DiasEnum[] dias = DiasEnum.values();
        List<Integer> numeros = new ArrayList<Integer>();
        for (int i = 0; i < number; i++) {
            int aux = (int) Math.floor(Math.random() * usuarios.size());
            while (numeros.contains(aux)) aux = (int) Math.floor(Math.random() * usuarios.size());
            numeros.add(aux);
            Usuario empaque = usuarios.get(aux);
            BasicDBList ja = new BasicDBList();
            Solicitud solicitud = new Solicitud();
            solicitud.setFecha(new Date());
            solicitud.setUsuario(empaque);
            int j = 0;
            int fin= (int) Math.floor(Math.random() * 3) + 3;
            while (j < fin) {
            	BasicDBObject jo = new BasicDBObject();
                aux = (int) Math.floor(Math.random() * 7);
                jo.put("dia", dias[aux].toString());
                aux = (int) Math.floor(Math.random() * bloques.length);
                jo.put("inicio", bloques[aux].toString());
                if (aux != bloques.length) aux = (int) Math.floor(Math.random() * (bloques.length - aux) + aux);
                jo.put("fin", bloques[aux].toString());
                if (!buscarConflicto(jo, ja)) {
                    ja.add(jo);
                    j++;
                }
            }
            solicitud.setTurnos(ja.toString());
            solicitudService.saveSolicitud(solicitud);
        }
        return "member/solicitudes/list";
    }

    private boolean buscarConflicto(BasicDBObject jo, BasicDBList ja) {
        for (int i = 0; i < ja.size(); i++) {
        	BasicDBObject aux = (BasicDBObject) ja.get(i);
            if (!aux.getString("dia").equals(jo.getString("dia"))) continue;
            int inicio = BloqueEnum.valueOf(aux.getString("inicio")).ordinal();
            int turnoIn = BloqueEnum.valueOf(jo.getString("inicio")).ordinal();
            if (turnoIn - inicio < 7 && turnoIn - inicio >= 0) {
                return true;
            }
            if (inicio - turnoIn < 7 && inicio - turnoIn >= 0) {
                return true;
            }
        }
        return false;
    }
    
    @RequestMapping(value="get")
    public @ResponseBody String getUsuarioSolicitud(@RequestParam(value = "planilla", required = false) ObjectId planillaId){
    	Usuario usuario= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	BasicDBList ja= new BasicDBList();
    	if(planillaId==null){
    		List<Solicitud> solicitudes= solicitudServiceImpl.findSolicitudesByUsuario(usuario);
    		for(Solicitud sol: solicitudes)
    			ja.add(sol.getTurnos());
    		List<Repechaje> repechajes= repechajeService.findRepechajesByUsuario(usuario);
    		for(Repechaje rep: repechajes)
    			ja.add(rep.getTurnos());
    	}
    	else{
    		Planilla planilla= planillaServiceImpl.findPlanilla(planillaId);
    		Calendar date= Calendar.getInstance();
    		date.setTime(planilla.getFecha());
    		Calendar date1 = (Calendar) date.clone();
    		Calendar date2 = (Calendar) date.clone();
    		date1.set(Calendar.DAY_OF_YEAR, date1.get(Calendar.DAY_OF_YEAR) - 8);
    		date2.set(Calendar.DAY_OF_YEAR, date2.get(Calendar.DAY_OF_YEAR) - 1);
    		List<Solicitud> solicitudes= solicitudServiceImpl.findSolicitudesByFechaBetweenAndUsuario(date1.getTime(), date2.getTime(), usuario);
    		for(Solicitud sol: solicitudes)
    			ja.add(sol.getTurnos());
    		List<Repechaje> repechajes= repechajeService.findRepechajesByFechaBetweenAndUsuario(date1.getTime(), date2.getTime(), usuario);
    		for(Repechaje rep: repechajes)
    			ja.add(rep.getTurnos());
    	}
		return ja.toString();
    }
    
}
