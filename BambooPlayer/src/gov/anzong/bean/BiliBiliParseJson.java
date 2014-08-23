package gov.anzong.bean;

import com.google.gson.Gson;
public class BiliBiliParseJson {

	public static BiliBiliApiData parseRead(String json)
	{
		Gson gson = new Gson();
		return gson.fromJson(json, BiliBiliApiData.class);
	}
}
