package br.com.dotofcodex.spring_jdbc.datasource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class DataSource {

	private static JdbcTemplate instance;
	private static ApplicationContext ctx;

	private DataSource() {

		ctx = new ClassPathXmlApplicationContext("application-context.xml");

		instance = ctx.getBean("template", JdbcTemplate.class);
	}

	public static JdbcTemplate getInstance() {

		synchronized (DataSource.class) {

			if (instance == null) {
				new DataSource();
			}
		}

		return instance;
	}

}
