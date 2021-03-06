// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.reneseses.empaques.domain.service;

import com.reneseses.empaques.domain.Repechaje;
import com.reneseses.empaques.domain.repository.RepechajeRepository;
import com.reneseses.empaques.domain.service.RepechajeServiceImpl;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

privileged aspect RepechajeServiceImpl_Roo_Service {
    
    declare @type: RepechajeServiceImpl: @Service;
    
    declare @type: RepechajeServiceImpl: @Transactional;
    
    @Autowired
    RepechajeRepository RepechajeServiceImpl.repechajeRepository;
    
    public long RepechajeServiceImpl.countAllRepechajes() {
        return repechajeRepository.count();
    }
    
    public void RepechajeServiceImpl.deleteRepechaje(Repechaje repechaje) {
        repechajeRepository.delete(repechaje);
    }
    
    public Repechaje RepechajeServiceImpl.findRepechaje(ObjectId id) {
        return repechajeRepository.findOne(id);
    }
    
    public List<Repechaje> RepechajeServiceImpl.findAllRepechajes() {
        return repechajeRepository.findAll();
    }
    
    public List<Repechaje> RepechajeServiceImpl.findRepechajeEntries(int firstResult, int maxResults) {
        return repechajeRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }
    
    public void RepechajeServiceImpl.saveRepechaje(Repechaje repechaje) {
        repechajeRepository.save(repechaje);
    }
    
    public Repechaje RepechajeServiceImpl.updateRepechaje(Repechaje repechaje) {
        return repechajeRepository.save(repechaje);
    }
    
}
