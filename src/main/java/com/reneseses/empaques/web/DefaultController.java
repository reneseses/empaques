package com.reneseses.empaques.web;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletContext;

import com.mongodb.BasicDBList;
import com.reneseses.empaques.domain.*;
import com.reneseses.empaques.domain.service.*;
import com.reneseses.empaques.enums.EstadoTurnoEnum;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.reneseses.empaques.enums.RegimenTurnoEnum;
import com.reneseses.empaques.enums.TipoUsuarioEnum;

@Controller
public class DefaultController {
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private UsuarioServiceImpl usuarioServiceImpl;

    @Autowired
    private SolicitudServiceImpl solicitudService;

    @Autowired
    private RepechajeServiceImpl repechajeService;

    @Autowired
    private PlanillaServiceImpl planillaService;

	@Autowired
	private SupermercadoServiceImpl supermercadoService;

	@Autowired
	private MessageDigestPasswordEncoder messageDigestPasswordEncoder;
	
	@RequestMapping(value="/", produces = "text/html")
	public String index(Model uiModel){
		Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(principal instanceof Usuario){
			return "redirect:/member";
		}
		
		return "index";
	}
	
	@RequestMapping(value="/member", produces = "text/html")
	public String member(Model uiModel){
		Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(principal instanceof Usuario){
			Usuario usuario= (Usuario) principal;
			
			if(usuario.getId() == null || usuario.getId().getSupermercado() == null){
				uiModel.addAttribute("delay", 0);
			}else{
				Supermercado supermercado= supermercadoService.findSupermercado(usuario.getId().getSupermercado());
				uiModel.addAttribute("delay", supermercado.getDelayZonaHoraria());
			}
			return "member";
		}
		
		return "redirect:/index";
	}

	@RequestMapping(value="readjsondb")
	public String readDB(){
		Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(!(principal instanceof Usuario))
			return "resourceNotFound";

		Usuario user= (Usuario) principal;

		if(!user.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")))
			return "resourceNotFound";

		String path= context.getRealPath("/WEB-INF/dbjson/");
		
		BufferedReader br = null;
		
		ObjectId supermercado= new ObjectId("5567df388b1faec2966c1714");

		Supermercado sup= supermercadoService.findSupermercado(supermercado);

		if(sup == null){
			sup= new Supermercado();
			sup.setNombre("Jumbo Valparaiso");
			sup.setId(supermercado);
			supermercadoService.saveSupermercado(sup);

			Usuario usuario= new Usuario();

			usuario.setNombre("Jumbo Valpo Admin");
			UsuarioId id= new UsuarioId();
			id.setNumero(0);
			id.setSupermercado(supermercado);
			usuario.setId(id);
			usuario.setRut("JVadmin");
			usuario.setPassword(messageDigestPasswordEncoder.encodePassword("passJVAdmin", null));
			usuario.setTipo(TipoUsuarioEnum.LOCALADMIN);

			usuarioServiceImpl.saveUsuario(usuario);
		}
		
		try{
			String sCurrentLine;
			br = new BufferedReader(new FileReader(path + "/usuario.json"));

            Calendar aux= Calendar.getInstance();
			while ((sCurrentLine = br.readLine()) != null) {
				BasicDBObject jo= (BasicDBObject) JSON.parse(sCurrentLine);
				
				Usuario usuario= new Usuario();
				
				usuario.setNombre(jo.getString("nombre"));
				usuario.setCarrera(jo.getString("carrera"));
				usuario.setCelular(jo.getString("celular"));
				usuario.setDisabled(jo.getBoolean("disabled"));
				usuario.setEmail(jo.getString("email"));
				usuario.setFacebook(jo.getString("facebook"));
				usuario.setLocked(jo.getBoolean("locked"));
				usuario.setPassword(jo.getString("password"));
				usuario.setPrioridad(jo.getInt("prioridad"));
				usuario.setRegimen(RegimenTurnoEnum.valueOf(jo.getString("regimen")));
				usuario.setRut(jo.getString("rut"));
				usuario.setTipo(TipoUsuarioEnum.valueOf(jo.getString("tipo")));
				usuario.setUniversidad(jo.getString("universidad"));
				
				if(jo.containsField("fechaNacimiento")){
                    Date date= jo.getDate("fechaNacimiento");
                    aux.setTime(date);
                    aux.add(Calendar.HOUR_OF_DAY, 3);

					usuario.setFechaNacimiento(aux.getTime());
				}
				
				UsuarioId id= new UsuarioId();
				
				id.setNumero(jo.getInt("numero"));
				id.setSupermercado(supermercado);
				
				usuario.setId(id);
				
				usuarioServiceImpl.saveUsuario(usuario);
			}
			br.close();

            br = new BufferedReader(new FileReader(path + "/solicitud.json"));
            while ((sCurrentLine = br.readLine()) != null) {
                BasicDBObject jo= (BasicDBObject) JSON.parse(sCurrentLine);

                Date date= jo.getDate("fecha");
                aux.setTime(date);
                aux.add(Calendar.HOUR_OF_DAY, 3);

                Integer empaque= jo.getInt("usuario");
                String turnos= jo.getString("turnos");

                UsuarioId usuarioId= new UsuarioId();
                usuarioId.setNumero(empaque);
                usuarioId.setSupermercado(supermercado);

                Solicitud solicitud= new Solicitud();
                solicitud.setFecha(aux.getTime());
                solicitud.setTurnos((BasicDBList)JSON.parse(turnos));
                solicitud.setUsuario(usuarioId);
                solicitud.setId(jo.getObjectId("_id"));

                solicitudService.saveSolicitud(solicitud);
            }
            br.close();

            br = new BufferedReader(new FileReader(path + "/repechaje.json"));
            while ((sCurrentLine = br.readLine()) != null) {
                BasicDBObject jo= (BasicDBObject) JSON.parse(sCurrentLine);

                Date date= jo.getDate("fecha");
                aux.setTime(date);
                aux.add(Calendar.HOUR_OF_DAY, 3);

                Integer empaque= jo.getInt("usuario");
                String turnos= jo.getString("turnos");

                UsuarioId usuarioId= new UsuarioId();
                usuarioId.setNumero(empaque);
                usuarioId.setSupermercado(supermercado);

                Repechaje solicitud= new Repechaje();
                solicitud.setFecha(aux.getTime());
                solicitud.setTurnos((BasicDBList)JSON.parse(turnos));
                solicitud.setUsuario(usuarioId);
                solicitud.setId(jo.getObjectId("_id"));

                repechajeService.saveRepechaje(solicitud);
            }
            br.close();

            br = new BufferedReader(new FileReader(path + "/planilla.json"));
            while ((sCurrentLine = br.readLine()) != null) {
                BasicDBObject jo= (BasicDBObject) JSON.parse(sCurrentLine);

                Date date= jo.getDate("fecha");
                aux.setTime(date);
                aux.add(Calendar.HOUR_OF_DAY, 3);

                boolean generada= jo.getBoolean("generada");
                boolean repechaje= jo.getBoolean("repechaje");

                BasicDBList bloques= (BasicDBList) jo.get("bloques");

                Planilla planilla= new Planilla();
                planilla.setFecha(aux.getTime());
                planilla.setGenerada(generada);
                planilla.setSupermercado(supermercado);
                planilla.setRepechaje(repechaje);
                planilla.setId(jo.getObjectId("_id"));

                for(int i=0; i < bloques.size(); i++){
                    BasicDBObject bloq= (BasicDBObject) bloques.get(i);

                    Calendar calendar= Calendar.getInstance();
                    Date bloqDate= bloq.getDate("fecha");
                    calendar.setTime(bloqDate);
                    calendar.add(Calendar.HOUR_OF_DAY, 3);

                    int cupos= bloq.getInt("cupos");
                    BasicDBList turnos= (BasicDBList) bloq.get("turnos");

                    Bloque bloque= new Bloque();
                    bloque.setCupos(cupos);
                    bloque.setFecha(calendar.getTime());

                    if(turnos != null) {
                        for (int j = 0; j < turnos.size(); j++) {
                            BasicDBObject turn = (BasicDBObject) turnos.get(j);

                            EstadoTurnoEnum estado = EstadoTurnoEnum.valueOf(turn.getString("estado"));
                            Turno turno = new Turno();
                            turno.setEstado(estado);

                            if(!estado.equals(EstadoTurnoEnum.LIBRE)){
                                Integer empaque = turn.getInt("usuario");
                                int solicitud = turn.getInt("solicitud");

                                turno.setSolicitud(solicitud);
                                turno.setUsuario(empaque);
                            }

                            bloque.getTurnos().add(turno);
                        }
                    }

                    planilla.getBloques().add(bloque);
                }

                planillaService.savePlanilla(planilla);
            }
            br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/member";
	}
	
}
