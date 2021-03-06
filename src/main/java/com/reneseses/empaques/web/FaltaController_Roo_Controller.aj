// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.reneseses.empaques.web;

import com.reneseses.empaques.domain.Falta;
import com.reneseses.empaques.domain.service.FaltaService;
import com.reneseses.empaques.enums.TipoFaltaEnum;
import com.reneseses.empaques.web.FaltaController;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect FaltaController_Roo_Controller {
    
    @Autowired
    FaltaService FaltaController.faltaService;
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String FaltaController.show(@PathVariable("id") ObjectId id, Model uiModel) {
        uiModel.addAttribute("falta", faltaService.findFalta(id));
        uiModel.addAttribute("itemId", id);
        return "member/faltas/show";
    }
    
    void FaltaController.populateEditForm(Model uiModel, Falta falta) {
        uiModel.addAttribute("falta", falta);
        uiModel.addAttribute("tipofaltaenums", Arrays.asList(TipoFaltaEnum.values()));
    }
    
    String FaltaController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
