package br.com.dotofcodex.spring_jdbc.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Person {

	private Long id;
	private String name;
	private Integer age;
	private Date birthday;

	public Person() {
		super();
	}
	
	public Person(Long id, String name, Integer age, Date birthday) {
		this.id = id;
		this.name = name;
		this.age = age;
		this.birthday = birthday;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder
			.append("Person [id=").append(id)
				.append(", name=").append(name)
				.append(", age=").append(age)
				.append(", birthday=").append(new SimpleDateFormat("dd/MM/yyyy").format(birthday))
			.append("]");
		return builder.toString();
	}
}
