import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.services.sheets.v4.*;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Arrays;
import java.util.Properties;

public class Grader {
	/* Google Sheets */ 
	private static String SPREADSHEET_ID;
	private static final Integer SHEET_ID = 0;
	private static final String APP_NAME = "CanvasGradePusher";
	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY);

	/* Canvas. Change these in config.properties for every assignment */ 
	private static String TOKEN;
	private static String courseID;
	private static String assignmentID;
	
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static HttpTransport HTTP_TRANSPORT;

	public static Credential authorize() throws IOException {
		try { 	
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		}
		catch(Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
		
		InputStream in = 
			Grader.class.getResourceAsStream("/client_secret.json");	
		GoogleClientSecrets clientSecrets = 
			GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .build();
		
		Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
		
		return credential;
	}

	public static Sheets getSheets() throws IOException {
		Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APP_NAME)
                .build();	
	}

	public static List<List<Object>> getData(Sheets service) throws IOException {
		String spreadSheetID = SPREADSHEET_ID;
		String range = "Sheet1";
		ValueRange response = service.spreadsheets()
								.values().get(spreadSheetID, range).execute();
		
		return response.getValues();
	}

	public static HttpURLConnection getConnection(URL url) throws IOException {
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setRequestMethod("POST");
		httpCon.setRequestProperty("Authorization", "Bearer " + TOKEN);
		return httpCon;
	}	

	public static String canvasBuildString(List<List<Object>> data) {
		String urlParameters = "";
		int idCol = -1;
		int totalCol = -1;
		int commentsCol = -1;
		int lateCol = -1;
		int i = -1;
		List<Object> header = data.get(0);
		for(Object o:header) {
			String headerString = (String) o;
			i++;
			if (headerString.contains("ID"))
				idCol = i;

			if (headerString.contains("Comment"))
				commentsCol = i;

			if (headerString.contains("Total"))
				totalCol = i;

			if (headerString.contains("Late"))
				lateCol = i;
		}

		if (idCol == -1 || commentsCol == -1 || totalCol == -1 || lateCol == -1) {
			System.err.println("Appropriate rows not found.");
			System.exit(1);
		}

		boolean firstStudent = true;
		for(List<Object> row : data) { 
			if (!firstStudent)
				urlParameters += "&";
			else
				firstStudent = false;
			
			System.out.println(row);
			String id = (String) row.get(idCol);
			String total = (String) row.get(totalCol);
			String comment = "";
			for(i = lateCol; i < header.size(); i++) {
				String cell = (String) row.get(i);
				if(i == (header.size() - 1))
					comment += header.get(i) + ":" + cell;
				else	
					comment += header.get(i) + ":" + cell + ", ";
				
				comment.replace('&', ' '); // '&' character breaks code
			}
			urlParameters += "grade_data[" + id + "][posted_grade]=" + total + "&grade_data[" + id + "][text_comment]=" + comment;
		}
		return urlParameters;
	}

	public static void main(String[] args) throws IOException {
		/* here we read config.properties */ 
		Properties prop = new Properties();
		InputStream inConfig = new FileInputStream("config.properties");
		prop.load(inConfig);
		SPREADSHEET_ID = prop.getProperty("SPREADSHEET_ID");
		TOKEN = prop.getProperty("CANVAS_TOKEN");
		courseID = prop.getProperty("COURSE_ID");
		assignmentID = prop.getProperty("ASSIGNMENT_ID");

		boolean push = args[0].equals("push");
		Sheets service = Grader.getSheets();
		List<List<Object>> data = getData(service);
		URL url = new URL("https://courseworks2.columbia.edu/api/v1/courses/" + courseID + "/assignments/" + assignmentID + "/submissions/update_grades");

		String urlParameters = canvasBuildString(data);
		HttpURLConnection httpCon = getConnection(url);
		
		if(!push) {
			System.out.println(urlParameters);
		}
		else{

		// Send post request
		httpCon.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(httpCon.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = httpCon.getResponseCode();

		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());
		}
	}
}
