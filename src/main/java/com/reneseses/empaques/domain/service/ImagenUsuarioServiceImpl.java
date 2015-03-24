package com.reneseses.empaques.domain.service;

import com.reneseses.empaques.domain.ImagenUsuario;
import com.reneseses.empaques.domain.Usuario;


public class ImagenUsuarioServiceImpl implements ImagenUsuarioService {
	public ImagenUsuario findByUsuario(Usuario usuario){
		return imagenUsuarioRepository.findByUsuario_Id(usuario.getId());
	}
}
