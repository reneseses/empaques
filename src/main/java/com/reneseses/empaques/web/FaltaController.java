package com.reneseses.empaques.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.reneseses.empaques.domain.Falta;
import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.service.FaltaServiceImpl;
import com.reneseses.empaques.domain.service.PlanillaService;
import com.reneseses.empaques.domain.service.PlanillaServiceImpl;
import com.reneseses.empaques.domain.service.UsuarioServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.userdetails.memory.UserMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/member/faltas")
@Controller
@RooWebScaffold(path = "member/faltas", formBackingObject = Falta.class)
public class FaltaController {
	
	@Autowired
	private FaltaServiceImpl faltaServiceImpl;
	
	@Autowired
	private UsuarioServiceImpl usuarioServiceImpl;

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid Falta falta, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, falta);
            return "member/faltas/create";
        }
        uiModel.asMap().clear();
        faltaService.saveFalta(falta);
        return "redirect:/member/faltas/" + encodeUrlPathSegment(falta.getId().toString(), httpServletRequest);
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Falta falta, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, falta);
            return "member/faltas/update";
        }
        uiModel.asMap().clear();
        faltaService.updateFalta(falta);
        return "redirect:/member/faltas/" + encodeUrlPathSegment(falta.getId().toString(), httpServletRequest);
    }

	@RequestMapping(produces = "text/html")
    public String list() {
        return "member/faltas/list";
    }
	
	@RequestMapping(params="planilla")
    public @ResponseBody ResponseEntity<String> getSolicitudes(@RequestParam ObjectId planilla){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		BasicDBList response= new BasicDBList();
		try{
			List<Falta> faltas= faltaServiceImpl.findFaltaByPlanilla(planilla);
			
			List<Integer> numeros= new ArrayList<Integer>();
			
			for(Falta falta: faltas){
				if(!numeros.contains(falta.getUsuario()))
					numeros.add(falta.getUsuario());
			}
			
			List<Usuario> usuarios= usuarioServiceImpl.findUsuariosByNumeros(numeros);
			
			HashMap<Integer, Usuario> usuarioMap= new HashMap<Integer, Usuario>();
			
			for(Usuario usuario: usuarios)
				usuarioMap.put(usuario.getNumero(), usuario);
			
			for(Falta falta: faltas){
				BasicDBObject jo= new BasicDBObject();
				
				BasicDBObject user= new BasicDBObject();
				user.append("numero", falta.getUsuario());
				
				if(usuarioMap.containsKey(falta.getUsuario())){
					Usuario usuario= usuarioMap.get(falta.getUsuario());	
					user.append("nombre", usuario.getNombre());
				}
				
				jo.append("id", falta.getId().toString());
				jo.append("tipo", falta.getTipoFalta().name());
				jo.append("usuario", user);
				
				response.add(jo);
			}
						
			return new ResponseEntity<String>(response.toString(), headers, HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
