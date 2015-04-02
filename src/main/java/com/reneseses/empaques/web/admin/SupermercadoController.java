package com.reneseses.empaques.web.admin;
import com.reneseses.empaques.domain.Supermercado;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin/supermercado")
@Controller
@RooWebScaffold(path = "admin/supermercado", formBackingObject = Supermercado.class)
public class SupermercadoController {
}
