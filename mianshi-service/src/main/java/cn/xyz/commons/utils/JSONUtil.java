package cn.xyz.commons.utils;

import org.bson.types.ObjectId;

import cn.xyz.commons.support.spring.converter.MappingFastjsonHttpMessageConverter.ObjectIdSerializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;

public class JSONUtil {

	private static final SerializeConfig serializeConfig;
	static {
		serializeConfig = new SerializeConfig();
		serializeConfig.put(ObjectId.class, new ObjectIdSerializer());
	}

	public static String toJSONString(Object obj) {
		return JSON.toJSONString(obj, serializeConfig);
	}

}
