package com.common.framework.lib;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Test {

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		/*Object obj = new JSONParser().parse(new FileReader("C:\\Rupak\\WorkSpace_new\\RDMAPI\\Testdata\\EMEA Pricing Run.json")); 
		JSONArray ja = (JSONArray) obj; 
		JSONObject jo = (JSONObject)ja.get(0);
		System.out.println(jo.get("Body"));*/
		/*CommonFunctions cf = new CommonFunctions();
		String request = cf.fetchRequestData("C:\\Rupak\\WorkSpace_new\\RDMAPI\\RequestFiles\\61050_request _invalid.txt");
		System.out.println(request);*/
		
		CommonFunctions cf = new CommonFunctions();
		//String a = cf.fetchRequestData("C:\\Users\\rupak.b\\Desktop\\jsontest\\A.json");
		//String b = cf.fetchRequestData("C:\\Users\\rupak.b\\Desktop\\jsontest\\B.json");
		
		String a = "[{\"Source\":\"ICE\",\"FileSeries\":\"A\",\"SEDOL\":\"BJMTK96\"}]";
		String b = "[{\"Source\":\"ICE\",\"FileSeries\":\"A\",\"SEDOL\":\"BJMTK96\"}]";
		String c = "{\"a\":[{\"Source\":\"ICE\",\"FileSeries\":1,\"isAdhoc\":false,\"SEDOL\":\"BJMTK96\"},{\"Source\":\"miss1\",\"FileSeries\":2,\"SEDOL\":\"BJMTKfghf96\"},{\"Source\":\"miss2\",\"FileSeries\":3,\"SEDOL\":\"BJMTKfghf96\"}]}";
		//boolean c = cf.compareJsons(a, b);
		String jj = cf.findJsonDiff(a, b);
		
		JsonObject obj = new JsonParser().parse(c).getAsJsonObject();
		String l = obj.get("a").toString();
		
		System.out.println(l);
		
	}
}
