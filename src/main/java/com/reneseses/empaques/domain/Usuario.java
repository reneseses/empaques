package com.reneseses.empaques.domain;

import com.reneseses.empaques.enums.RegimenTurnoEnum;
import com.reneseses.empaques.enums.TipoUsuarioEnum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RooJavaBean
@RooToString
@RooMongoEntity(identifierType = UsuarioId.class)
@RooSerializable
public class Usuario implements UserDetails {

    @NotNull
    @Pattern(regexp = "[a-zA-Z\\'\\-]+[ [a-zA-Z\\'\\-]+]*")
    private String nombre;

    @NotNull
    @Pattern(regexp = "[0-9]{1,8}-[K|k|0-9]")
    @Column(unique = true)
    private String rut;

    @NotNull
    @Size(min = 4)
    private String password;

    @NotNull
    @Enumerated
    private TipoUsuarioEnum tipo = TipoUsuarioEnum.EMPAQUE;

    @Enumerated
    private RegimenTurnoEnum regimen = RegimenTurnoEnum.R2X1;

    private String celular;
    
    private Boolean disabled = false;

    private Boolean locked = false;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date lastSolicitud;

    private int prioridad;

    private String email;

    @Column(unique = true)
    private String facebook;

    @Past
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fechaNacimiento;

    private String carrera;

    private String universidad;

    private ObjectId image;
    
    @Transient
    private Integer numero;
    
    public Integer getNumero(){
    	return this.getId() == null? null: this.getId().getNumero();
    }
    
    @Override
    public List<org.springframework.security.core.GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(this.tipo.toString()));

	    if(TipoUsuarioEnum.ENCARGADOLOCAL.equals(this.tipo) || TipoUsuarioEnum.SUBENCARGADOLOCAL.equals(this.tipo))
		    authorities.add(new SimpleGrantedAuthority(TipoUsuarioEnum.LOCALADMIN.toString()));
        return authorities;
    }

    @Override
    public String getUsername() {
        if (this.rut != null) {
            String[] rol = this.rut.split("-");
            return rol[0];
        }
        return this.rut;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !(this.locked);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !this.disabled;
    }

    @Override
    public String getPassword() {
        return this.password;
    }
}
