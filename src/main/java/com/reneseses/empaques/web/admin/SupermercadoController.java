package com.reneseses.empaques.web.admin;
import com.reneseses.empaques.domain.Supermercado;
import com.reneseses.empaques.domain.Usuario;
import org.bson.types.ObjectId;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/admin/supermercado")
@Controller
@RooWebScaffold(path = "admin/supermercado", formBackingObject = Supermercado.class)
public class SupermercadoController {

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String create(@Valid Supermercado supermercado, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		Usuario principal= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")))
			return "accessFailure";

		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, supermercado);
			return "admin/supermercado/create";
		}
		uiModel.asMap().clear();
		supermercadoService.saveSupermercado(supermercado);
		return "redirect:/admin/supermercado/" + encodeUrlPathSegment(supermercado.getId().toString(), httpServletRequest);
	}

	@RequestMapping(params = "form", produces = "text/html")
	public String createForm(Model uiModel) {
		Usuario principal= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")))
			return "accessFailure";

		populateEditForm(uiModel, new Supermercado());
		return "admin/supermercado/create";
	}

	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") ObjectId id, Model uiModel) {
		Usuario principal= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN")) && !principal.getId().getSupermercado().equals(id))
			return "accessFailure";

		uiModel.addAttribute("supermercado", supermercadoService.findSupermercado(id));
		uiModel.addAttribute("itemId", id);
		return "admin/supermercado/show";
	}

	@RequestMapping(produces = "text/html")
	public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
		Usuario principal= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")))
			return "accessFailure";

		if (page != null || size != null) {
			int sizeNo = size == null ? 10 : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			uiModel.addAttribute("supermercadoes", supermercadoService.findSupermercadoEntries(firstResult, sizeNo));
			float nrOfPages = (float) supermercadoService.countAllSupermercadoes() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		} else {
			uiModel.addAttribute("supermercadoes", supermercadoService.findAllSupermercadoes());
		}
		return "admin/supermercado/list";
	}

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@Valid Supermercado supermercado, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		Usuario principal= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN")) && !principal.getId().getSupermercado().equals(supermercado.getId()))
			return "accessFailure";
		if (bindingResult.hasErrors()) {
			populateEditForm(uiModel, supermercado);
			return "admin/supermercado/update";
		}
		uiModel.asMap().clear();
		supermercadoService.updateSupermercado(supermercado);
		return "redirect:/admin/supermercado/" + encodeUrlPathSegment(supermercado.getId().toString(), httpServletRequest);
	}

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	public String updateForm(@PathVariable("id") ObjectId id, Model uiModel) {
		Usuario principal= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(principal.getAuthorities().contains(new SimpleGrantedAuthority("LOCALADMIN")) && !principal.getId().getSupermercado().equals(id))
			return "accessFailure";
		populateEditForm(uiModel, supermercadoService.findSupermercado(id));
		return "admin/supermercado/update";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String delete(@PathVariable("id") ObjectId id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		Usuario principal= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(!principal.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")))
			return "accessFailure";
		Supermercado supermercado = supermercadoService.findSupermercado(id);
		supermercadoService.deleteSupermercado(supermercado);
		uiModel.asMap().clear();
		uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
		uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
		return "redirect:/admin/supermercado";
	}
}
