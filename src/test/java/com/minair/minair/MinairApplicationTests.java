package com.minair.minair;

import com.minair.minair.testconfig.RestDocsConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.AnnotatedClassFinder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@SpringBootTest
class MinairApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void test(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(RestDocsConfiguration.class);

		String[] beans = ac.getBeanDefinitionNames();
		
		System.out.println("bean= " + ac.getBean(RestDocsConfiguration.class));

		for (String bean : beans) {
			System.out.println("bean names= " + bean);
			System.out.println("bean class= " + ac.getBean(bean));
		}
	}

}
