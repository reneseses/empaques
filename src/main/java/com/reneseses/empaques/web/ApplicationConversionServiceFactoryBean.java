package com.reneseses.empaques.web;

import com.reneseses.empaques.domain.Usuario;
import com.reneseses.empaques.domain.UsuarioId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.converter.RooConversionService;

/**
 * A central place to register application converters and formatters. 
 */
@RooConversionService
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

	@Override
	protected void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
		// Register application converters and formatters
	}
	
	public Converter<String, UsuarioId> getStringToUsuarioIdConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.reneseses.empaques.domain.UsuarioId>() {
            public com.reneseses.empaques.domain.UsuarioId convert(String id) {
                return UsuarioId.fromString(id);
            }
        };
    }
	
	public Converter<UsuarioId, String> getUsuarioIdToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.reneseses.empaques.domain.UsuarioId, java.lang.String>() {
            public String convert(UsuarioId id) {
                return id.toString();
            }
        };
    }
	
	public Converter<String, Usuario> getStringToUsuarioConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.reneseses.empaques.domain.Usuario>() {
            public com.reneseses.empaques.domain.Usuario convert(String id) {
                return getObject().convert(UsuarioId.fromString(id), Usuario.class);
            }
        };
    }

	public Converter<Usuario, String> getUsuarioToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.reneseses.empaques.domain.Usuario, java.lang.String>() {
            public String convert(Usuario usuario) {
                return new StringBuilder().append(usuario.getPassword()).append(' ').append(usuario.getNombre()).append(' ').append(usuario.getRut()).append(' ').append(usuario.getCelular()).toString();
            }
        };
    }
	
	public void installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getFaltaToStringConverter());
        registry.addConverter(getIdToFaltaConverter());
        registry.addConverter(getStringToFaltaConverter());
        registry.addConverter(getPlanillaToStringConverter());
        registry.addConverter(getIdToPlanillaConverter());
        registry.addConverter(getStringToPlanillaConverter());
        registry.addConverter(getSolicitudToStringConverter());
        registry.addConverter(getIdToSolicitudConverter());
        registry.addConverter(getStringToSolicitudConverter());
        registry.addConverter(getUsuarioToStringConverter());
        registry.addConverter(getIdToUsuarioConverter());
        registry.addConverter(getStringToUsuarioConverter());
        registry.addConverter(getStringToUsuarioIdConverter());
        registry.addConverter(getUsuarioIdToStringConverter());
    }

}
