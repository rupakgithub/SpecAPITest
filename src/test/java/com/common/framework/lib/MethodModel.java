package com.common.framework.lib;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class MethodModel {

	String url;
	//Map<String, Object> body;
	Object body;
	Response response;

	public MethodModel(String url,Object body ){
		this.url = url;
		this.body = body;
	}

	public MethodModel setMethod(String methodType) {
		if(methodType.equalsIgnoreCase("Post")) {
			response = fetchPostResponse();
		}else if(methodType.equalsIgnoreCase("Get")) {

		}
		return this;
	}
	
	public Response getResponse() {
		return response;
	}

	private Response fetchPostResponse(){
		Response res = null;
		try {
			res = given().relaxedHTTPSValidation()
					.contentType(ContentType.JSON)
					.headers(Global.allheaders)
					.body(body)
					.post(url);

		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return res;
	}
}
