// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.reneseses.empaques.domain.service;

import com.reneseses.empaques.domain.Repechaje;
import com.reneseses.empaques.domain.service.RepechajeService;
import java.util.List;
import org.bson.types.ObjectId;

privileged aspect RepechajeService_Roo_Service {
    
    public abstract long RepechajeService.countAllRepechajes();    
    public abstract void RepechajeService.deleteRepechaje(Repechaje repechaje);    
    public abstract Repechaje RepechajeService.findRepechaje(ObjectId id);    
    public abstract List<Repechaje> RepechajeService.findAllRepechajes();    
    public abstract List<Repechaje> RepechajeService.findRepechajeEntries(int firstResult, int maxResults);    
    public abstract void RepechajeService.saveRepechaje(Repechaje repechaje);    
    public abstract Repechaje RepechajeService.updateRepechaje(Repechaje repechaje);    
}
