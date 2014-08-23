package gov.anzong.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import gov.anzong.bean.VideoDetialData;
import gov.anzong.bean.VideoParseJson;
import gov.anzong.fragment.ProgressDialogFragment;
import gov.anzong.mediaplayer.R;
import gov.anzong.mediaplayer.VideoActivity;
import gov.anzong.receiveintent.ReceiveIntentURLActivity;
import gov.anzong.util.FunctionUtil;
import gov.anzong.util.HttpUtil;
import gov.anzong.util.StringUtil;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.widget.Toast;

public class YoutubeLoadTask extends AsyncTask<String, Integer, String> {

	final String origurl;
	String title = "", site = "";
	final FragmentActivity context;
	static final String dialogTag = "load";

	public YoutubeLoadTask(String origurl, FragmentActivity context) {
		super();
		this.origurl = origurl;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		FunctionUtil.createdialog(context, dialogTag);
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		FunctionUtil.closedialog(context, dialogTag);
		if (result != null) {
			VideoActivity.openVideo(context, Uri.parse(result), "YouTube");
			this.onCancelled();
		} else {
			Toast.makeText(context, "网络或解析错误", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(origurl));
			boolean isIntentSafe = context.getPackageManager()
					.queryIntentActivities(intent, 0).size() > 0;
			if (isIntentSafe) {
				Intent chooser = Intent.createChooser(intent, "出错了,请选择其他打开方式:");
				context.startActivityForResult(chooser, 123);
			}
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled(String result) {
		this.onCancelled();
	}

	@Override
	protected void onCancelled() {
		if (context != null) {
			context.finish();
		}
	}

	@Override
	protected String doInBackground(String... params) {
		final String uri = params[0];
		final String htmlString = HttpUtil.iosGetHtml(uri);
		StringTokenizer st = null;
		if (StringUtil.isEmpty(htmlString))
			return null;
		else {
			if (htmlString.lastIndexOf("&") > 0) {
				st = new StringTokenizer(htmlString, "&");
			} else {
				return null;
			}
		}
		String data = null;
		if (st == null)
			return null;
		while (st.hasMoreTokens()) {
			data = st.nextToken();
			if (data.startsWith("url_encoded_fmt_stream_map=")) {
				break;
			}
		}
		if (data.length() > 28)
			data = data.substring(27);
		else
			return null;
		try {
			data = URLDecoder.decode(data, "UTF-8");
			data = URLDecoder.decode(data);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		String[] sta = data.split("quality=");
		String stb1080p = null, stb720p = null, stbmedium = null, stbsmall = null, stbother = null;
		int i = 0;
		while (i < sta.length) {
			if (sta[i].indexOf("video/mp4") > 0 && sta[i].indexOf("medium") < 0) {
				if (sta[i].indexOf("hd1080") >= 0) {
					stb1080p = sta[i];
				} else if (sta[i].indexOf("hd720") >= 0) {
					stb720p = sta[i];
				} else if (sta[i].indexOf("medium") >= 0) {
					stbmedium = sta[i];
				} else if (sta[i].indexOf("small") >= 0) {
					stbsmall = sta[i];
				} else {
					stbother = sta[i];
				}
				break;
			}
			i++;
		}
		String m3u8Url = null;
		if (StringUtil.isEmpty(stb1080p)) {
			m3u8Url = stb1080p;
		} else if (StringUtil.isEmpty(stb720p)) {
			m3u8Url = stb720p;
		} else if (StringUtil.isEmpty(stbmedium)) {
			m3u8Url = stbmedium;
		} else if (StringUtil.isEmpty(stbother)) {
			m3u8Url = stbother;
		} else if (StringUtil.isEmpty(stbsmall)) {
			m3u8Url = stbsmall;
		} else {
			return null;
		}
		m3u8Url = StringUtil.getStringBetween(m3u8Url, 0, "url=", ";").result;
		if (StringUtil.isEmpty(m3u8Url))
			return null;
		return m3u8Url;
	}

}
