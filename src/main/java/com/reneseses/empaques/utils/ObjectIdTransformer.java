package com.reneseses.empaques.utils;

import flexjson.transformer.AbstractTransformer;
import org.bson.types.ObjectId;

public class ObjectIdTransformer extends AbstractTransformer {

	@Override
	public void transform(Object id) {
		if(id instanceof ObjectId){
			ObjectId transformed= (ObjectId) id;
			getContext().writeQuoted(transformed.toString());
		}else{
			getContext().writeQuoted("");
		}
	}

}
