// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.reneseses.empaques.domain.repository;

import com.reneseses.empaques.domain.Solicitud;
import com.reneseses.empaques.domain.repository.SolicitudRepository;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

privileged aspect SolicitudRepository_Roo_Mongo_Repository {
    
    declare parents: SolicitudRepository extends PagingAndSortingRepository<Solicitud, ObjectId>;
    
    declare @type: SolicitudRepository: @Repository;
    
}
