package com.MADA.mada_SeoulBike;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //스케쥴러 추가
public class MadaSeoulBikeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MadaSeoulBikeApplication.class, args);
	}

}
