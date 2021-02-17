package com.aitrades.blockchain.gateway.rest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aitrades.blockchain.gateway.domain.Login;

@RestController
@RequestMapping("/api")
public class LoginController {

	public String login(@RequestBody Login login) {
		return null;
	}
}
