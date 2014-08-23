package gov.anzong.bean;

import com.google.gson.Gson;
public class VideoParseJson {

	public static VideoDetialData[] parseRead(String json)
	{
		Gson gson = new Gson();
		return gson.fromJson(json, VideoDetialData[].class);
	}
	public static M1905VideoDetialData[] m1905parseRead(String json)
	{
		Gson gson = new Gson();
		return gson.fromJson(json, M1905VideoDetialData[].class);
	}
}
