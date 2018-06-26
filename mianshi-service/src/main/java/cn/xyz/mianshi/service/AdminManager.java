package cn.xyz.mianshi.service;

import java.util.List;

import cn.xyz.mianshi.vo.Config;


public interface AdminManager {


	

	
	

	Config getConfig();

	Config initConfig();

	void setConfig(Config dbObj);
}
