package com.reneseses.empaques.web;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.servlet.ServletContext;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.UsuarioId;
import com.reneseses.empaques.domain.service.UsuarioServiceImpl;
import com.reneseses.empaques.enums.RegimenTurnoEnum;
import com.reneseses.empaques.enums.TipoUsuarioEnum;

@Controller
public class DefaultController {
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private UsuarioServiceImpl usuarioServiceImpl;
	
	@RequestMapping(value="/")
	public String member(){
		Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(principal instanceof Usuario)
			return "redirect:/member";
		
		return "index";
	}
	
	
	@RequestMapping(value="readjsondb")
	public String readDB(){
		String path= context.getRealPath("/WEB-INF/dbjson/");
		
		BufferedReader br = null;
		
		ObjectId supermercado= new ObjectId("5516e1f444ae2102a6915548");
		
		try {
			 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(path + "/usuario.json"));
 
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
					usuario.setFechaNacimiento(jo.getDate("fechaNacimiento"));
				}
				
				UsuarioId id= new UsuarioId();
				
				id.setNumero(jo.getInt("numero"));
				id.setSupermercado(supermercado);
				
				usuario.setId(id);
				
				usuarioServiceImpl.saveUsuario(usuario);
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/member";
	}
	
}
