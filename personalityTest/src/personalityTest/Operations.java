package personalityTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Operations {

	JSONParser parser = new JSONParser();
	JSONObject jsonObject = new JSONObject();
	JSONArray questions = new JSONArray();

	String jdbcURL = "jdbc:postgresql://localhost:5432/personalityTest";// Creating database URL
	String username = "postgres";// Username to access db
	String password = "cak9294dal";// Password to access db

	Connection connection = null;
	Statement statement = null;
	ResultSet resultSet = null;

	public JSONObject readJson() {

		try {

			jsonObject = (JSONObject) parser.parse("personality_test.json"); // Parsing json file to jsonObject
			//questions = (JSONArray) jsonObject.get("questions"); // Turning jsonObject to jsonArray to achieve data by
																	// iterations

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public boolean checkJsonString(String info) {

		boolean controller = false;

		try {
			jsonObject = (JSONObject) parser.parse(info);// Parsing jsonString to jsonObject
			JSONArray results = (JSONArray) jsonObject.get("results");// Turning jsonObject to jsonArray to achieve data
																		// by iterations
			String[] questions = new String[results.size() / 2];// Creating string array to keep questions half of
																// results array size
			String[] answers = new String[results.size() / 2];// Creating string array to keep answers half of results
																// array size
			Iterator<Object> iterator = results.iterator();

			// Seperating answers and questions and adding them to arrays
			for (int i = 0; i < results.size() / 2; i++) {
				JSONObject innerObject = (JSONObject) iterator.next();
				questions[i] = (String) innerObject.get("question");
				answers[i] = (String) innerObject.get("answer");
			}
			// Controlling questions from database
			if (dbCheck(questions, answers)) {
				controller = true;
			} else {
				controller = false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return controller;
	}

	public boolean dbCheck(String[] q, String[] a) {

		String sql = "SELECT q_id, question, type FROM tbl_questions"; // Creating query to retrieve data from db
		boolean dbChecker = false;
		boolean control = false;
		try {
			connection = DriverManager.getConnection(jdbcURL, username, password); // Connecting db
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			int[] qId = new int[q.length];
			for (int i = 0; i < q.length; i++) { // Checking db with questions and getting question_id from db to use
													// them while inserting db
				while (resultSet.next()) {
					if (resultSet.getString("question").equals(q[i])) {
						qId[i] = resultSet.getInt("q_id");
						dbChecker = true;
						control = true;
						break;
					} else {
						dbChecker = false;
					}
				}
				if (!dbChecker) {
					control = false;
					break;
				}
			}

			if (dbChecker) {
				int testId = 1;
				String testIdSql = "SELECT MAX(test_id) FROM tbl_answers";
				resultSet = statement.executeQuery(testIdSql);// Getting max test_id from db
				if (resultSet.getInt("test_id") != 0) {
					testId = resultSet.getInt("test_id") + 1;
				}
				for (int i = 0; i < a.length; i++) {
					String insertSql = "INSERT INTO tbl_answers (test_id, answer, q_id) VALUES(" + testId + ", '" + a[i]
							+ "', " + qId[i] + ")";// Inserting data into table
					resultSet = statement.executeQuery(insertSql); // Executing instert query
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return control;
	}

}
