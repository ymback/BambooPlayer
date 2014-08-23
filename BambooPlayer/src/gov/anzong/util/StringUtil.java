package gov.anzong.util;

import java.util.Locale;

import gov.anzong.bean.StringFindResult;

public class StringUtil {

	/** 判断是否是 "" 或者 null */
	public static boolean isEmpty(String str) {
		if (str != null && !"".equals(str)) {
			return false;
		} else {
			return true;
		}
	}
	public static StringFindResult getStringBetween(String data,
			int begPosition, String startStr, String endStr) {
		StringFindResult ret = new StringFindResult();
		do {
			if (isEmpty(data) || begPosition < 0
					|| data.length() <= begPosition || isEmpty(startStr)
					|| isEmpty(startStr))
				break;

			int start = data.indexOf(startStr, begPosition);
			if (start == -1)
				break;

			start += startStr.length();
			int end = data.indexOf(endStr, start);
			if (end == -1)
				end = data.length();
			ret.result = data.substring(start, end);
			ret.position = end + endStr.length();

		} while (false);

		return ret;
	}
	

	public static String get_bilibili_apiurl_withid(String id)
	 {
		String base_url="http://api.bilibili.com/view?";
		String app_key="43fd790e02107193";
		String otherpa="appkey="+app_key+"&id="+id+"&page=1&platform=android&type=json";
	 	String app_secret="3c787076f6cc255d493a60077bf904ec";
	 	String sign="&sign="+MD5Util.MD5(otherpa+app_secret).toLowerCase(Locale.US);
	 	return base_url+otherpa+sign;
	 }
}
