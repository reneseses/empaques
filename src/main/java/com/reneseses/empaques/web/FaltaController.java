package com.reneseses.empaques.web;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.reneseses.empaques.domain.Falta;
import com.reneseses.empaques.domain.Planilla;
import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.UsuarioId;
import com.reneseses.empaques.domain.service.FaltaServiceImpl;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@Autowired
	private PlanillaServiceImpl planillaServiceImpl;

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(Falta falta, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN"))){
    		return "accessFailure";
    	}
				
        if (bindingResult.hasErrors() || falta.getUsuario() == null || falta.getPlanilla() == null || falta.getTipoFalta() == null) {
        	List<Planilla> planillas= planillaServiceImpl.findAllPlanillasOrderByFechaDesc();
    		List<Usuario> usuarios= usuarioServiceImpl.lightFindAllUsuarios();

    		uiModel.addAttribute("planillas", planillas);
    		uiModel.addAttribute("usuarios", usuarios);
            populateEditForm(uiModel, falta);
            return "member/faltas/create";
        }
        uiModel.asMap().clear();
        faltaService.saveFalta(falta);
        return "redirect:/member/faltas/" + encodeUrlPathSegment(falta.getId().toString(), httpServletRequest);
		//return "redirect:/member/faltas/";
    }
	
	@RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN"))){
    		return "accessFailure";
    	}
		
		List<Planilla> planillas= planillaServiceImpl.findAllPlanillasOrderByFechaDesc();
		List<Usuario> usuarios= usuarioServiceImpl.lightFindAllUsuarios();

		uiModel.addAttribute("planillas", planillas);
		uiModel.addAttribute("usuarios", usuarios);
        populateEditForm(uiModel, new Falta());
        return "member/faltas/create";
    }

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Falta falta, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN"))){
    		return "accessFailure";
    	}
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
	
	@RequestMapping(params="planilla", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> getSolicitudes(@RequestParam ObjectId planilla){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		BasicDBList response= new BasicDBList();
		try{
			List<Falta> faltas= faltaServiceImpl.findFaltaByPlanilla(planilla);
			
			List<UsuarioId> ids= new ArrayList<UsuarioId>();
			
			for(Falta falta: faltas){
				if(!ids.contains(falta.getUsuario()))
					ids.add(falta.getUsuario());
			}
			
			List<Usuario> usuarios= usuarioServiceImpl.findUsuariosByIds(ids);
			
			HashMap<UsuarioId, Usuario> usuarioMap= new HashMap<UsuarioId, Usuario>();
			
			for(Usuario usuario: usuarios)
				usuarioMap.put(usuario.getId(), usuario);
			
			for(Falta falta: faltas){
				BasicDBObject jo= new BasicDBObject();
				
				BasicDBObject user= new BasicDBObject();
				user.append("numero", falta.getUsuario().getNumero());
				
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

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") ObjectId id, Model uiModel) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN"))){
    		return "accessFailure";
    	}
		populateEditForm(uiModel, faltaService.findFalta(id));
        return "member/faltas/update";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") ObjectId id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		Usuario principal = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("SUBENCARGADOLOCAL"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("ENCARGADOLOCAL"))
				&& !principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN"))){
    		return "accessFailure";
    	}
		Falta falta = faltaService.findFalta(id);
        faltaService.deleteFalta(falta);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/member/faltas";
    }
}
