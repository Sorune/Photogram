package com.sorune.photogram;

import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PhotogramApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void jasypt() {
		String url = "jdbc:mariadb://sorune.asuscomm.com:13917/Photogram";
		String username = "root";
		String password = "1234";

		String encryptUrl = jasyptEncrypt(url);
		String encryptUsername = jasyptEncrypt(username);
		String encryptPassword = jasyptEncrypt(password);

		System.out.println("encryptUrl : " + encryptUrl);
		System.out.println("encryptUsername : " + encryptUsername);
		System.out.println("encryptPassword : " + encryptPassword);
		Assertions.assertThat(url).isEqualTo(jasyptDecrypt(encryptUrl));

	}
	private String jasyptEncrypt(String input) {
		String key = "helloSpringBoot";
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm("PBEWithMD5AndDES");
		encryptor.setPassword(key);
		return encryptor.encrypt(input);
	}

	private String jasyptDecrypt(String input){
		String key = "helloSpringBoot";
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm("PBEWithMD5AndDES");
		encryptor.setPassword(key);
		return encryptor.decrypt(input);
	}
}
