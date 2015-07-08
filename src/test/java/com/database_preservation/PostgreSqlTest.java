package com.database_preservation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.HashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import com.database_preservation.diff_match_patch.Diff;

public class PostgreSqlTest {
	File tmpFile;

	final String db_source = "dpttest";
	final String db_target = "dpttest_siard";
	final String db_tmp_username = "dpttest";
	final String db_tmp_password = RandomStringUtils.randomAlphabetic(10);

	Roundtrip rt;

	@BeforeClass
	public void setup() throws IOException, InterruptedException, URISyntaxException{
		HashMap<String, String> env_var_source = new HashMap<String, String>();
		env_var_source.put("PGUSER", db_tmp_username);
		env_var_source.put("PGPASSWORD", db_tmp_password);
		env_var_source.put("PGDATABASE", db_source);

		HashMap<String, String> env_var_target = new HashMap<String, String>();
		env_var_target.put("PGUSER", db_tmp_username);
		env_var_target.put("PGPASSWORD", db_tmp_password);
		env_var_target.put("PGDATABASE", db_target);

		rt = new Roundtrip(
				String.format("./testing/postgresql/setup.sh \"%s\" \"%s\" \"%s\" \"%s\"",
						db_source, db_target, db_tmp_username, db_tmp_password),
				String.format("./testing/postgresql/teardown.sh \"%s\" \"%s\" \"%s\"",
						db_source, db_target, db_tmp_username),
				"psql -q",
				"pg_dump --format plain --no-owner --no-privileges --column-inserts --no-security-labels --no-tablespaces",
				"pg_dump --format plain --no-owner --no-privileges --column-inserts --no-security-labels --no-tablespaces",
				new String[]{"-i", "PostgreSQLJDBC", "localhost", db_source, db_tmp_username, db_tmp_password, "false",
					"-o", "SIARD", Roundtrip.TMP_FILE_SIARD_VAR, "store"},
				new String[]{"-i", "SIARD", Roundtrip.TMP_FILE_SIARD_VAR,
					"-o", "PostgreSQLJDBC", "localhost", db_target, db_tmp_username, db_tmp_password, "false"},
				env_var_source,
				env_var_target);

		System.out.println("setup complete for postgresql-siard1.0");
	}

	@Test(description="Tests small examples", groups={"postgresql-siard1.0"})
	public void testQueries() throws IOException, InterruptedException{
		String format ="CREATE TABLE datatypes (col1 %s);\nINSERT INTO datatypes(col1) VALUES(%s);";

		assert rt.testTypeAndValue(format, "\"char\"", "'a'") : "CHAR failed";
		assert rt.testTypeAndValue(format, "bigint", "1") : "BIGINT failed";
		assert rt.testTypeAndValue(format, "boolean", "TRUE") : "BOOLEAN failed";
		assert rt.testTypeAndValue(format, "bytea", "1") : "BYTEA failed";
	}

	@Test(description="Tests PostgreSQL files in src/test/resources", groups={"postgresql-siard1.0"})
	public void testFiles() throws IOException, InterruptedException, URISyntaxException{
		assert rt.testFile(Paths.get(getClass().getResource("/postgresql_1.sql").toURI()).toFile()) : "Failed to convert file postgresql_1.sql";
	}
}
