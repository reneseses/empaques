// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.reneseses.empaques.domain;

import com.reneseses.empaques.domain.Imagen;
import org.bson.types.ObjectId;

privileged aspect Imagen_Roo_JavaBean {
    
    public byte[] Imagen.getContent() {
        return this.content;
    }
    
    public void Imagen.setContent(byte[] content) {
        this.content = content;
    }
    
    public byte[] Imagen.getThumbnail() {
        return this.thumbnail;
    }
    
    public void Imagen.setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    public String Imagen.getNombre() {
        return this.nombre;
    }
    
    public void Imagen.setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String Imagen.getContentType() {
        return this.contentType;
    }
    
    public void Imagen.setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Long Imagen.getSize() {
        return this.size;
    }
    
    public void Imagen.setSize(Long size) {
        this.size = size;
    }
    
    public ObjectId Imagen.getUsuario() {
        return this.usuario;
    }
    
    public void Imagen.setUsuario(ObjectId usuario) {
        this.usuario = usuario;
    }
    
}
