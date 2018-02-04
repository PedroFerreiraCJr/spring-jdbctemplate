package br.com.dotofcodex.spring_jdbc.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import br.com.dotofcodex.spring_jdbc.datasource.DataSource;
import br.com.dotofcodex.spring_jdbc.model.Person;

public class App {

	public static void main(String[] args) {

		JdbcTemplate template = DataSource.getInstance();

		// Criação de consulta simples: consulta a versão do banco de dados
		String version = template.query("SELECT version() as version", new ResultSetExtractor<String>() {
			@Override
			public String extractData(ResultSet result) throws SQLException, DataAccessException {
				String version = null;
				if (result.next()) {
					version = result.getString("version");
				}
				return version;
			}
		});

		System.out.println(version);

		
		// Criação da tabela persons
		// O comando execute serve para uso com comandos SQL DDL
		StringBuilder table = new StringBuilder();
		table
			.append("CREATE TABLE IF NOT EXISTS persons (")
				.append("id bigserial,")
				.append("name varchar(255) not null,")
				.append("age integer,")
				.append("birthday timestamp,")
			.append("CONSTRAINT pk_persons_id PRIMARY KEY (id)").append(");");

		template.execute(table.toString());

		System.out.println("SQL de criação de tabelas executadas");

		
		// Criação de um objeto de domínio
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1992);
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		calendar.set(Calendar.DAY_OF_MONTH, 6);
		Date birhtday = calendar.getTime();

		Person person = new Person(null, "Pedro Ferreira", 26, birhtday);

		boolean insert = false;
		if (insert) {

			int update = template.update("INSERT INTO persons(name, age, birthday) VALUES(?,?,?);",
					new PreparedStatementSetter() {
						@Override
						public void setValues(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setString(1, person.getName());
							preparedStatement.setInt(2, person.getAge());
							preparedStatement.setDate(3, new java.sql.Date(person.getBirthday().getTime()));
						}
					});

			System.out.println("Quantidade de registros inseridos: " + update);
		}

		
		// Criação de um objeto do domínio
		// retorna os id's gerados na criação do objeto
		boolean withReturn = false;
		if (withReturn) {

			String sql = "INSERT INTO persons(name, age, birthday) VALUES(?,?,?);";

			KeyHolder keyHolder = new GeneratedKeyHolder();

			template.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, person.getName());
					ps.setInt(2, person.getAge());
					ps.setDate(3, new java.sql.Date(person.getBirthday().getTime()));
					return ps;
				}
			}, keyHolder);

			String id = null;

			for (String key : keyHolder.getKeys().keySet()) {
				id = key;
				break;
			}
			;

			System.out.println("ID gerado automaticamente: " + keyHolder.getKeys().get(id));
		}

		
		// Atualiza um registro do banco de dados
		// Atualiza um único objeto por causa da clausula WHERE
		boolean update = false;
		if (update) {

			int result = template.update("UPDATE persons SET name = ? WHERE id = ?;", new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setString(1, "Pedro Ferreira de Carvalho Júnior");
					preparedStatement.setLong(2, 1l);
				}
			});

			System.out.println("Quantidade de registro atualizados: " + result);
		}

		
		// Consulta o objeto de dominio inserido anteriormente
		// Consulta um único objeto
		Person queriedObject = template.queryForObject("SELECT * FROM persons WHERE id = ?;", new Object[] { 1l },
				new RowMapper<Person>() {
					@Override
					public Person mapRow(ResultSet rs, int rowCount) throws SQLException {
						return new Person(rs.getLong("id"), rs.getString("name"), rs.getInt("age"),
								new java.util.Date(rs.getDate("birthday").getTime()));
					}

				});

		System.out.println(queriedObject);

		
		// Consulta uma lista de objetos
		// Consulta todos os objetos da tabela especificada
		// O BeanPropertyRowMapper requer um construtor default
		List<Person> query = template.query("SELECT * FROM persons", new BeanPropertyRowMapper<Person>(Person.class));
		query.forEach(new Consumer<Person>() {
			@Override
			public void accept(Person person) {
				System.out.println("ID: " + person.getId() + "\nName: " + person.getName());
			}
		});
		
		
		// Consultando um unico atributo da tabela
		String name = template.queryForObject("SELECT name FROM persons WHERE id = ?;", new Object[] {1l}, String.class);
		System.out.println("Name: " + name);
		
		
		// Executando uma função de agregação
		Integer count = template.queryForObject("SELECT COUNT(*) FROM persons;", Integer.class);
		System.out.println("Quantidade de resultados: " + count);
		
		
		// Consultando a data de nascimento
		// Neste caso precisa-se do RowMapper para extrair o resultado do ResultSet
		Date birthday = template.queryForObject("SELECT birthday FROM persons WHERE id = ?",new Object[] {1l}, new RowMapper<Date>() {
			@Override
			public Date mapRow(ResultSet rs, int rowCount) throws SQLException {
				return new java.util.Date(rs.getDate("birthday").getTime());
			}
		});
		System.out.println("Data de Nascimento: " + new SimpleDateFormat("dd/MM/yyyy").format(birthday));
	}
}
