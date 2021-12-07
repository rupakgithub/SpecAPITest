package com.testscripts;

import org.testng.annotations.Test;

import com.common.framework.lib.CommonFunctions;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import io.restassured.path.json.JsonPath;

import static com.mongodb.client.model.Filters.*;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

public class Tests {

	@Test
	public void f() {

		MongoClient mongoClient = MongoClients
				.create("mongodb://support:support123@hfsuatvumgdb01:27019/?authSource=admin");

		MongoDatabase db = mongoClient.getDatabase("RDM");
		MongoIterable<String> u = db.listCollectionNames();
		for (String a : u) {
			System.out.println(a);
		}
		
		

		MongoCollection<Document> collection= db.getCollection("Securities_Global");
		
		

		
		Document myDoc = collection.find(eq("InstrumentID","1")).first();
		
		String j = myDoc.toJson();
		System.out.println(myDoc.toJson());
		
		mongoClient.close();
		System.out.println("done da na done");
		
		String k = JsonPath.from(j).get("IsActiveStatus");
		System.err.println(k);
		
		/*CommonFunctions cf = new CommonFunctions();
		String expectedResponse = cf.fetchRequestDatafromJsonArray("C:\\Users\\rupak.b\\Desktop\\uy.json");
		System.out.println(expectedResponse);*/
		
		
		//LinkedHashMap<String,String> json = "C:\\Users\\rupak.b\\Desktop\\uy6.json";
		
		/*LinkedHashMap<String,String> jsonArray = JsonPath
				.from(expectedResponse)
				.get("[0]");
		System.out.println(jsonArray);*/
	}
}
