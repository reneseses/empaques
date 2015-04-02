package com.reneseses.empaques.domain.service;

import java.util.List;

import com.reneseses.empaques.domain.Imagen;
import com.reneseses.empaques.domain.UsuarioId;


public class ImagenServiceImpl implements ImagenService {
	public List<Imagen> findImagenByUsuario(UsuarioId usuario){
		return imagenRepository.findImagenByUsuario(usuario);
	}
}
