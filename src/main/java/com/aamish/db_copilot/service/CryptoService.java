package com.aamish.db_copilot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service

public class CryptoService {
	private final TextEncryptor encryptor;
	   public CryptoService(@Value("${copilot.crypto.password}") String password,
               @Value("${copilot.crypto.salt}") String salt) {
this.encryptor = Encryptors.delux(password, salt);
}
   public String encrypt(String plain) {
	return encryptor.encrypt(plain);  
   }
   public String decrypt(String encrypted) {
	   return encryptor.decrypt(encrypted);
   }
}
