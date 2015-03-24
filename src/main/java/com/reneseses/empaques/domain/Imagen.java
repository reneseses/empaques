package com.reneseses.empaques.domain;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.layers.repository.mongo.RooMongoEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooMongoEntity(identifierType = ObjectId.class)
public class Imagen {
	@NotNull
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] thumbnail;

    private String nombre;

    private String contentType;

    private Long size;

    @ManyToOne
    private Usuario usuario;
    
    public void generateThumb(){
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	try {
    		BufferedImage original = ImageIO.read(new ByteArrayInputStream(this.content));
    		Rectangle rect = original.getData().getBounds();
    		Double mayor= rect.getHeight();
    		if(mayor > rect.getWidth())
    			mayor= rect.getWidth();
    		
    		BufferedImage dest = original.getSubimage(0, 0, mayor.intValue(), mayor.intValue());
			
    		Image scaledImage = dest.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
    		
    		BufferedImage imageBuff = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
    		imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0,0,0), null);
    		
			ImageIO.write(imageBuff, "jpg", baos);

    		this.thumbnail= baos.toByteArray();			
 
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
    }
    
    public void generateImagen(byte[] content){
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	try {
    		BufferedImage original = ImageIO.read(new ByteArrayInputStream(content));
    		Rectangle rect = original.getData().getBounds();
    		Double height= rect.getHeight();
    		Double width= rect.getWidth();
    		
    		BufferedImage dest = original.getSubimage(0, 0, width.intValue() , height.intValue());
    		
			ImageIO.write(dest, "jpg", baos);

    		this.content= baos.toByteArray();			
 
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
    }
}
