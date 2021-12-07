package com.common.framework.lib;

import static com.mongodb.client.model.Filters.eq;
import static com.qmetry.qaf.automation.core.ConfigurationManager.getBundle;
import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class CommonFunctions {

	public String executeDirectory = System.getProperty("user.dir");
	// public String confgXmlFile =
	// executeDirectory+"\\ExtentReportJars\\extent-config.xml";

	Utilities util = new Utilities();

	public boolean extentReportLogoCreator(String xmlFilepath, String logopath) {

		File file = new File(xmlFilepath);
		SAXBuilder builder = new SAXBuilder();
		try {
			String encoded = Base64.getEncoder().withoutPadding()
					.encodeToString(FileUtils.readFileToByteArray(new File(logopath)));
			String encodedString = "data:image/png;base64," + encoded;
			Document doc = builder.build(file);
			Element rootElement = doc.getRootElement();
			Element empElement = rootElement.getChild("configuration");
			Element detailElement = empElement.getChild("reportName");
			CDATA cdata = new CDATA("<img src='" + encodedString + "'/>");
			detailElement.setContent(cdata);
			XMLOutputter outXML = new XMLOutputter();
			outXML.output(doc, new FileWriter(Global.confgXmlFile));
			return true;

		} catch (Exception ex) {
			Messages.errorMsg = "[ERROR:: Exception raised while trying to create logo in Extent Report. " + ex + "]";
			return false;
		}

	}

	public Set<Entry<Object, Object>> readPropertyFile(String propFilePath) {

		Properties prop = new Properties();

		try {
			prop.load(new FileInputStream(propFilePath));
			Set<Entry<Object, Object>> entrySet = prop.entrySet();
			return entrySet;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public ExtentReports loadExtentSettings(ExtentHtmlReporter htmlReporter, ExtentReports extent, ExtentTest test,
			String propFilePath) {

		String date = util.now();
		String dateformat = date.replaceAll(":", "").replaceAll(" ", "_");
		htmlReporter = new ExtentHtmlReporter(Global.extentlog + "Report_" + dateformat + ".html");
		File configFile = new File(Global.confgXmlFile);
		htmlReporter.loadXMLConfig(configFile.toString());
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);

		extent.setSystemInfo("User Name", System.getProperty("user.name"));
		Set<Entry<Object, Object>> entrySet = readPropertyFile(propFilePath);

		for (Entry<Object, Object> entry : entrySet) {
			if (entry.getKey().toString().equalsIgnoreCase("Title")) {
				htmlReporter.config().setDocumentTitle(entry.getValue().toString());
			} else {
				extent.setSystemInfo(entry.getKey().toString(), entry.getValue().toString());
			}

		}

		// htmlReporter.config().setChartVisibilityOnOpen(true);
		// htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
		htmlReporter.config().setTheme(Theme.DARK);
		// htmlReporter.config().setReportName("hgdghfidu");
		return extent;
	}

	public boolean jsonValidation(String json) {

		try {
			final ObjectMapper mapper = new ObjectMapper();
			mapper.readTree(json);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public String getFlattenJson(String jsonResponse) {
		String flattenJson = "";
		try {
			flattenJson = JsonFlattener.flatten(jsonResponse.toString());
		} catch (Exception e) {
			return e.getMessage();
		}

		return flattenJson;
	}

	public String getcurrentdateandtime() {
		String str = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS");
			Date date = new Date();
			str = dateFormat.format(date);
			str = str.replace(" ", "").replaceAll("/", "").replaceAll(":", "");
		} catch (Exception e) {
		}
		return str;
	}

	public boolean fetchAccessToken() {
		try {
			Response response = given().relaxedHTTPSValidation().auth().preemptive()
					.basic(getBundle().getString("Clientid"), getBundle().getString("Client_Secret"))
					.formParam("grant_type", getBundle().getString("Grant_type"))
					.post(getBundle().getString("Access_Token_Url"));

			JSONObject jsonObject = new JSONObject(response.getBody().asString());
			Messages.accessCode = jsonObject.get("access_token").toString();
		} catch (Exception e) {
			e.printStackTrace();
			Messages.errorMsg = "[Error:: Exception raised in the method fetchAccessToken. Message --> "
					+ e.getMessage() + "]";
			return false;
		}

		return true;
	}

	public String getBodyfromJsonFile(String filePath) {

		JSONArray jsonArray = null;
		JSONObject jsonObject = null;
		Integer id = null;
		String flattenJson = "";
		ObjectMapper mapper = null;
		try {
			mapper = new ObjectMapper().configure(Feature.ALLOW_COMMENTS, true);
			jsonObject = mapper.readValue(new FileReader(filePath), JSONObject.class);
			flattenJson = jsonObject.toString();
		} catch (JsonParseException e1) {
			Messages.errorMsg = "Error raised when trying to read Body file path " + filePath;
			return e1.getMessage();
		} catch (JsonMappingException e2) {
			try {
				jsonArray = mapper.readValue(new FileReader(filePath), JSONArray.class);
				flattenJson = jsonArray.toString();
			} catch (Exception e1) {
				try {
					id = mapper.readValue(new FileReader(filePath), Integer.class);
					flattenJson = Integer.toString(id);
				} catch (Exception x) {
					return x.getMessage();
				}
			}
		} catch (FileNotFoundException e3) {
			Messages.errorMsg = "Error raised when trying to read Body file path " + filePath;
			return e3.getMessage();
		} catch (IOException e4) {
			Messages.errorMsg = "Error raised when trying to read Body file path " + filePath;
			return e4.getMessage();
		}
		return flattenJson;
	}

	public boolean compareJsons(String expected, String actual) {
		String errorMsg = "";
		try {
			JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
		} catch (AssertionError e) {
			errorMsg = e.getMessage();
			System.err.println(errorMsg);
			Messages.errorMsg = errorMsg;
			return false;
		}
		return true;
	}

	public String getFlattenJsonAsString(String filePath) {

		String flatjson;
		org.json.simple.JSONObject jo;
		try {
			Object obj = new JSONParser().parse(new FileReader(filePath));
			jo = (org.json.simple.JSONObject) obj;
			flatjson = getFlattenJson(jo.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return flatjson;
	}

	public void extentReportPassLogger(String label, String description) {
		try {
			Global.test.log(Status.INFO, MarkupHelper.createLabel(label, ExtentColor.GREEN))
					.pass("<pre>" + description + "</pre>");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void extentReportFailLogger(String label) {
		try {
			Global.test.log(Status.INFO, MarkupHelper.createLabel(label, ExtentColor.RED))
					.fail("<details>" + "<summary>" + "<b>" + "<font color=" + "red>" + "Exception Occured:Click to see"
							+ "</font>" + "</b >" + "</summary>" + "<pre>" + Messages.errorMsg + "</pre>"
							+ "</details>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void extentReportPassLogger(String label, String description, ExtentTest node) {
		try {
			node.log(Status.INFO, MarkupHelper.createLabel(label, ExtentColor.GREEN))
					.pass("<pre>" + description + "</pre>");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void extentReportFailLogger(String label, ExtentTest node) {
		try {

			node.log(Status.INFO, MarkupHelper.createLabel(label, ExtentColor.RED))
					.fail("<details>" + "<summary>" + "<b>" + "<font color=" + "red>" + "Exception Occured:Click to see"
							+ "</font>" + "</b >" + "</summary>" + "<pre>" + Messages.errorMsg + "</pre>"
							+ "</details>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showResponseBody(String label, String body, ExtentTest node) {
		try {

			node.log(Status.INFO, MarkupHelper.createLabel(label, ExtentColor.GREEN))
					.info("<details>" + "<summary>" + "<b>" + "<font color=" + "green>"
							+ "Request Parameters: Click to view" + "</font>" + "</b >" + "</summary>" + "<pre>" + body
							+ "</pre>" + "</details>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String fetchRequestDataFromJsonObject(String path) {
		String testdata = null;
		try {
			Object obj = new JSONParser().parse(new FileReader(path));

			org.json.simple.JSONObject jo = (org.json.simple.JSONObject) obj;
			testdata = jo.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return testdata;
	}

	public String fetchRequestDatafromJsonArray(String path) {
		String testdata = null;
		try {
			Object obj = new JSONParser().parse(new FileReader(path));

			org.json.simple.JSONArray jo = (org.json.simple.JSONArray) obj;
			testdata = jo.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return testdata;
	}

	public String returnPrettyString(String uglyString) {
		String notPretty = null;
		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(uglyString);
			notPretty = gson.toJson(je);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return notPretty;
	}

	/*
	 * Gson gson = new Gson(); Type type = new TypeToken<Map<String,
	 * Object>>(){}.getType(); Map<String, Object> myMap = gson.fromJson(request,
	 * type);
	 */

	public String findJsonDiff(String first, String second) {
		String diffJson = "";
		try {
			com.fasterxml.jackson.databind.ObjectMapper jackson = new com.fasterxml.jackson.databind.ObjectMapper();

			JsonNode beforeNode = jackson.readTree(first);
			JsonNode afterNode = jackson.readTree(second);
			EnumSet<DiffFlags> flags = DiffFlags.dontNormalizeOpIntoMoveAndCopy().clone();
			JsonNode patchNode = JsonDiff.asJson(beforeNode, afterNode, flags);
			diffJson = patchNode.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return diffJson;
	}

	public String fetchJsonStringByKey(String json, String key) {
		String value = "";
		try {
			JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
			value = obj.get(key).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return value;
	}

	public void doVerifyMissedData(String json) {

		Global.listOfExpiredSecurities = new ArrayList<String>();
		Global.listOfUnExpiredSecurities = new ArrayList<String>();
		JsonPath path = JsonPath.from(json);
		MongoCollection<org.bson.Document> collection = fetchFromMongoDb();
		try {
			Integer jsonLength = path.get("$.size()");
			for (int i = 0; i < jsonLength; i++) {
				if(!path.get("op["+i+"]").equals("add") && !path.get("op["+i+"]").equals("remove")) {
					continue;
				}
				String instrumentid = path.get("value["+i+"].InstrumentID");
				org.bson.Document myDoc = collection.find(eq("InstrumentID",instrumentid)).first();
				String isActiveStatus = JsonPath.from(myDoc.toJson()).get("IsActiveStatus");
				if(isActiveStatus == null) {
					Global.listOfUnExpiredSecurities.add(instrumentid);
					continue;
				}
				else if(isActiveStatus.equalsIgnoreCase("Expired")) {
					Global.listOfExpiredSecurities.add(instrumentid);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static MongoCollection<org.bson.Document> fetchFromMongoDb() {
		MongoCollection<org.bson.Document> collection;
		try {
			MongoClient mongoClient = MongoClients.create("mongodb://support:support123@hfsuatvumgdb01:27019/?authSource=admin");
			MongoDatabase db = mongoClient.getDatabase("RDM");
			collection= db.getCollection("Securities_Global");
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return collection;
	}
	
}
