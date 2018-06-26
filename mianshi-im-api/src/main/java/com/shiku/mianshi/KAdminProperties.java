package com.shiku.mianshi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "admin")
public class KAdminProperties {

	private String users;

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

}
