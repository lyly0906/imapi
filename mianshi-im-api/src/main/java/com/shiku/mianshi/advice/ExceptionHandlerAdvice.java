package com.shiku.mianshi.advice;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import cn.xyz.commons.ex.ServiceException;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.shiku.mianshi.ResponseUtil;

@ControllerAdvice
public class ExceptionHandlerAdvice {

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public void handleErrors(HttpServletRequest request,
			HttpServletResponse response, Exception e) throws Exception {
		System.out.println(request.getRequestURI() + "错误：");
		e.printStackTrace();

		int resultCode = 1020101;
		String resultMsg = "接口内部异常";
		String detailMsg = "";

		if (e instanceof MissingServletRequestParameterException
				|| e instanceof BindException) {
			resultCode = 1010101;
			resultMsg = "请求参数验证失败，缺少必填参数或参数错误";
		} else if (e instanceof ServiceException) {
			ServiceException ex = ((ServiceException) e);

			resultCode = null == ex.getResultCode() ? 0 : ex.getResultCode();
			resultMsg = ex.getMessage();
		} else {
			detailMsg = e.getMessage();
		}

		Map<String, Object> map = Maps.newHashMap();
		map.put("resultCode", resultCode);
		map.put("resultMsg", resultMsg);
		map.put("detailMsg", detailMsg);

		String text = JSON.toJSONString(map);

		ResponseUtil.output(response, text);
	}

}
