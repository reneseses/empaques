// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.reneseses.empaques.domain.service;

import com.reneseses.empaques.domain.Supermercado;
import com.reneseses.empaques.domain.repository.SuperMercadoRepository;
import com.reneseses.empaques.domain.service.SupermercadoServiceImpl;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

privileged aspect SupermercadoServiceImpl_Roo_Service {
    
    declare @type: SupermercadoServiceImpl: @Service;
    
    declare @type: SupermercadoServiceImpl: @Transactional;
    
    @Autowired
    SuperMercadoRepository SupermercadoServiceImpl.superMercadoRepository;
    
    public long SupermercadoServiceImpl.countAllSupermercadoes() {
        return superMercadoRepository.count();
    }
    
    public void SupermercadoServiceImpl.deleteSupermercado(Supermercado supermercado) {
        superMercadoRepository.delete(supermercado);
    }
    
    public Supermercado SupermercadoServiceImpl.findSupermercado(ObjectId id) {
        return superMercadoRepository.findOne(id);
    }
    
    public List<Supermercado> SupermercadoServiceImpl.findAllSupermercadoes() {
        return superMercadoRepository.findAll();
    }
    
    public List<Supermercado> SupermercadoServiceImpl.findSupermercadoEntries(int firstResult, int maxResults) {
        return superMercadoRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }
    
    public void SupermercadoServiceImpl.saveSupermercado(Supermercado supermercado) {
        superMercadoRepository.save(supermercado);
    }
    
    public Supermercado SupermercadoServiceImpl.updateSupermercado(Supermercado supermercado) {
        return superMercadoRepository.save(supermercado);
    }
    
}
