package me.longbow122.bot.repository;

import me.longbow122.bot.repository.entities.Copypasta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CopypastaRepository {

	private static final String CREATE_TABLE_SQL = """
		CREATE TABLE IF NOT EXISTS copypasta (
			name VARCHAR(32) PRIMARY KEY,
			description VARCHAR(100) NOT NULL,
			message VARCHAR(2000) NOT NULL,
			CONSTRAINT copypasta_name_not_blank CHECK (TRIM(name) <> ''),
			CONSTRAINT copypasta_description_not_blank CHECK (TRIM(description) <> ''),
			CONSTRAINT copypasta_message_not_blank CHECK (TRIM(message) <> '')
		)
		""";

	private final String url;

	private final String username;

	private final String password;

	public CopypastaRepository(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
		createSchema();
	}

	public Copypasta save(Copypasta copypasta) {
		validate(copypasta);
		try (Connection connection = getConnection()) {
			connection.setAutoCommit(false);
			try {
				upsert(connection, copypasta);
				connection.commit();
				return copypasta;
			} catch (SQLException e) {
				rollback(connection, e);
				throw e;
			}
		} catch (SQLException e) {
			throw databaseException("Unable to save copypasta " + safeName(copypasta), e);
		}
	}

	public List<Copypasta> saveAll(Iterable<Copypasta> copypastas) {
		List<Copypasta> toSave = new ArrayList<>();
		for (Copypasta copypasta : copypastas) {
			validate(copypasta);
			toSave.add(copypasta);
		}

		try (Connection connection = getConnection()) {
			connection.setAutoCommit(false);
			try {
				for (Copypasta copypasta : toSave) {
					upsert(connection, copypasta);
				}
				connection.commit();
				return toSave;
			} catch (SQLException e) {
				rollback(connection, e);
				throw e;
			}
		} catch (SQLException e) {
			throw databaseException("Unable to save copypastas", e);
		}
	}

	public void replaceCopypasta(String currentName, Copypasta replacement) {
		validate(replacement);
		try (Connection connection = getConnection()) {
			connection.setAutoCommit(false);
			try {
				insert(connection, replacement);
				if (deleteByName(connection, currentName) == 0) {
					connection.rollback();
					throw new NoSuchElementException("Copypasta with name " + currentName + " does not exist");
				}
				connection.commit();
			} catch (SQLException e) {
				rollback(connection, e);
				throw e;
			}
		} catch (SQLException e) {
			throw databaseException("Unable to replace copypasta " + currentName, e);
		}
	}

	public boolean existsById(String name) {
		if (name == null) return false;
		try (Connection connection = getConnection();
		     PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM copypasta WHERE name = ?")) {
			statement.setString(1, name);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next();
			}
		} catch (SQLException e) {
			throw databaseException("Unable to check whether copypasta exists", e);
		}
	}

	public Optional<Copypasta> findCopypastaByName(String name) {
		if (name == null) return Optional.empty();
		try (Connection connection = getConnection();
		     PreparedStatement statement = connection.prepareStatement("SELECT name, description, message FROM copypasta WHERE name = ?")) {
			statement.setString(1, name);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) return Optional.of(toCopypasta(resultSet));
				return Optional.empty();
			}
		} catch (SQLException e) {
			throw databaseException("Unable to find copypasta " + name, e);
		}
	}

	public List<Copypasta> findCopypastaByNameStartingWith(String name) {
		if (name == null) return List.of();
		try (Connection connection = getConnection();
		     PreparedStatement statement = connection.prepareStatement("SELECT name, description, message FROM copypasta WHERE name LIKE ? ESCAPE '\\' ORDER BY name")) {
			statement.setString(1, escapeLikePrefix(name));
			try (ResultSet resultSet = statement.executeQuery()) {
				List<Copypasta> copypastas = new ArrayList<>();
				while (resultSet.next()) {
					copypastas.add(toCopypasta(resultSet));
				}
				return copypastas;
			}
		} catch (SQLException e) {
			throw databaseException("Unable to find copypastas starting with " + name, e);
		}
	}

	public List<Copypasta> findAll() {
		try (Connection connection = getConnection();
		     PreparedStatement statement = connection.prepareStatement("SELECT name, description, message FROM copypasta ORDER BY name");
		     ResultSet resultSet = statement.executeQuery()) {
			List<Copypasta> copypastas = new ArrayList<>();
			while (resultSet.next()) {
				copypastas.add(toCopypasta(resultSet));
			}
			return copypastas;
		} catch (SQLException e) {
			throw databaseException("Unable to find copypastas", e);
		}
	}

	public long count() {
		try (Connection connection = getConnection();
		     PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM copypasta");
		     ResultSet resultSet = statement.executeQuery()) {
			if (resultSet.next()) return resultSet.getLong(1);
			return 0;
		} catch (SQLException e) {
			throw databaseException("Unable to count copypastas", e);
		}
	}

	public void deleteAll() {
		try (Connection connection = getConnection();
		     PreparedStatement statement = connection.prepareStatement("DELETE FROM copypasta")) {
			statement.executeUpdate();
		} catch (SQLException e) {
			throw databaseException("Unable to delete copypastas", e);
		}
	}

	public int deleteCopypastaByNameNaturalId(String name) {
		try (Connection connection = getConnection()) {
			return deleteByName(connection, name);
		} catch (SQLException e) {
			throw databaseException("Unable to delete copypasta " + name, e);
		}
	}

	private void createSchema() {
		try (Connection connection = getConnection();
		     Statement statement = connection.createStatement()) {
			statement.execute(CREATE_TABLE_SQL);
		} catch (SQLException e) {
			throw databaseException("Unable to create copypasta table", e);
		}
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

	private void upsert(Connection connection, Copypasta copypasta) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("UPDATE copypasta SET description = ?, message = ? WHERE name = ?")) {
			statement.setString(1, copypasta.getDescription());
			statement.setString(2, copypasta.getMessage());
			statement.setString(3, copypasta.getName());
			if (statement.executeUpdate() > 0) return;
		}
		insert(connection, copypasta);
	}

	private void insert(Connection connection, Copypasta copypasta) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("INSERT INTO copypasta (name, description, message) VALUES (?, ?, ?)")) {
			statement.setString(1, copypasta.getName());
			statement.setString(2, copypasta.getDescription());
			statement.setString(3, copypasta.getMessage());
			statement.executeUpdate();
		}
	}

	private int deleteByName(Connection connection, String name) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("DELETE FROM copypasta WHERE name = ?")) {
			statement.setString(1, name);
			return statement.executeUpdate();
		}
	}

	private Copypasta toCopypasta(ResultSet resultSet) throws SQLException {
		return new Copypasta(
			resultSet.getString("name"),
			resultSet.getString("description"),
			resultSet.getString("message")
		);
	}

	private void validate(Copypasta copypasta) {
		if (copypasta == null) throw new IllegalArgumentException("Copypasta cannot be null");
		copypasta.validate();
	}

	private String escapeLikePrefix(String prefix) {
		return prefix
			.replace("\\", "\\\\")
			.replace("%", "\\%")
			.replace("_", "\\_") + "%";
	}

	private void rollback(Connection connection, SQLException original) {
		try {
			connection.rollback();
		} catch (SQLException rollbackException) {
			original.addSuppressed(rollbackException);
		}
	}

	private RuntimeException databaseException(String message, SQLException e) {
		return new IllegalStateException(message, e);
	}

	private String safeName(Copypasta copypasta) {
		return copypasta == null ? "<null>" : copypasta.getName();
	}
}
