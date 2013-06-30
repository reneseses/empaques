package com.reneseses.empaques.web;

import java.util.Calendar;

import com.reneseses.empaques.domain.Falta;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/member/faltas")
@Controller
@RooWebScaffold(path = "member/faltas", formBackingObject = Falta.class)
public class FaltaController {

	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid Falta falta, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, falta);
            return "member/faltas/create";
        }
        uiModel.asMap().clear();
        Calendar cal= Calendar.getInstance();
		cal.setTime(falta.getFecha());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		falta.setFecha(cal.getTime());
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
        Calendar cal= Calendar.getInstance();
		cal.setTime(falta.getFecha());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		falta.setFecha(cal.getTime());
        faltaService.updateFalta(falta);
        return "redirect:/member/faltas/" + encodeUrlPathSegment(falta.getId().toString(), httpServletRequest);
    }
}
