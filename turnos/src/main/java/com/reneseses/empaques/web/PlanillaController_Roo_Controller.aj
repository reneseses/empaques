// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.reneseses.empaques.web;

import com.reneseses.empaques.domain.Planilla;
import com.reneseses.empaques.domain.service.PlanillaService;
import com.reneseses.empaques.web.PlanillaController;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect PlanillaController_Roo_Controller {
    
    @Autowired
    PlanillaService PlanillaController.planillaService;
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String PlanillaController.create(@Valid Planilla planilla, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, planilla);
            return "member/planillas/create";
        }
        uiModel.asMap().clear();
        planillaService.savePlanilla(planilla);
        return "redirect:/member/planillas/" + encodeUrlPathSegment(planilla.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String PlanillaController.update(@Valid Planilla planilla, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, planilla);
            return "member/planillas/update";
        }
        uiModel.asMap().clear();
        planillaService.updatePlanilla(planilla);
        return "redirect:/member/planillas/" + encodeUrlPathSegment(planilla.getId().toString(), httpServletRequest);
    }
    
    void PlanillaController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("planilla_fecha_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }
    
    String PlanillaController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
