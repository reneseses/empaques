package com.reneseses.empaques.domain.service;

import java.util.List;

import com.reneseses.empaques.domain.Imagen;
import com.reneseses.empaques.domain.Usuario;


public class ImagenServiceImpl implements ImagenService {
	public List<Imagen> findImagenByUsuario(Usuario usuario){
		return imagenRepository.findImagenByUsuario_Id(usuario.getId());
	}
}
