package gov.anzong.task;

import java.io.UnsupportedEncodingException;

import gov.anzong.bean.BiliBiliApiData;
import gov.anzong.bean.BiliBiliParseJson;
import gov.anzong.bean.DataBetter;
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

public class BiliBiliAidLoadTask extends AsyncTask<String, Integer, String> {

	final String origurl;
	String title = "";
	final FragmentActivity context;
	static final String dialogTag = "load";
	int errorcode = -1;// 0²ÎÊý´íÎó,1ÍøÂç´íÎó,2½âÎö´íÎó

	public BiliBiliAidLoadTask(String origurl, FragmentActivity context) {
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
			VideoActivity.openVideo(context, Uri.parse(result), title);
			this.onCancelled();
		} else {
			String errorinfo = "";
			switch (errorcode) {// 0²ÎÊý´íÎó,1ÍøÂç´íÎó,2½âÎö´íÎó
			case 0:
				errorinfo = "²ÎÊý´íÎó";
				break;
			case 1:
				errorinfo = "ÍøÂç´íÎó";
				break;
			case 2:
				errorinfo = "½âÎö´íÎó";
				break;
			case -1:
			default:
				errorinfo = "Î´Öª´íÎó";
				break;
			}
			Toast.makeText(context, errorinfo, Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(origurl));
			boolean isIntentSafe = context.getPackageManager()
					.queryIntentActivities(intent, 0).size() > 0;
			if (isIntentSafe) {
				Intent chooser = Intent.createChooser(intent, "³ö´íÁË,ÇëÑ¡ÔñÆäËû´ò¿ª·½Ê½:");
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
		String uri = params[0];
		if (StringUtil.isEmpty(uri)) {
			errorcode = 0;
			return null;
		}
		String id = StringUtil.getStringBetween(origurl, 0, "/av", "/").result;
		String apiurl = StringUtil.get_bilibili_apiurl_withid(id);
		String js = "";
		for (int i = 0; i < 3; i++) {
			js = HttpUtil.bilibiliGetHtml(apiurl);
			if (!StringUtil.isEmpty(js)) {
				i = 10;
			}
		}
		if (!StringUtil.isEmpty(js)) {
			try {
				BiliBiliApiData data = BiliBiliParseJson.parseRead(js);
				if (!StringUtil.isEmpty(data.cid)) {
					String html5cidurl = "http://www.bilibili.com/m/html5?cid="
							+ data.cid;
					String htmlString = HttpUtil.iosGetHtml(html5cidurl);
					String videourl = StringUtil.getStringBetween(htmlString,
							0, "\"src\":\"", "\"").result;
					if (StringUtil.isEmpty(data.title)) {
						title = "BiliBili";
					} else {
						title = data.title + " - BiliBili";
					}
					if (!StringUtil.isEmpty(videourl)) {
						return videourl;
					}
				}
			} catch (Exception e) {

			}
		}
		uri = FunctionUtil.uribase64(uri);
		js = "";
		for (int i = 0; i < 3; i++) {
			js = HttpUtil.iosGetHtml(uri);
			if (!StringUtil.isEmpty(js)) {
				i = 10;
			}
		}
		DataBetter result = FunctionUtil.dataselectbetterone(js, "BiliBili",
				context);
		errorcode = result.errorcode;
		if (errorcode >= 0) {
			return null;
		}
		title = result.title;
		return result.url;

	}

}
