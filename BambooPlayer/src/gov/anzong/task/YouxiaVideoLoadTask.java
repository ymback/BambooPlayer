package gov.anzong.task;

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

public class YouxiaVideoLoadTask extends AsyncTask<String, Integer, String> {

	final String origurl;
	String title = "";
	final FragmentActivity context;
	static final String dialogTag = "load";

	public YouxiaVideoLoadTask(String origurl, FragmentActivity context) {
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
			Toast.makeText(context, "解析错误", Toast.LENGTH_LONG).show();
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
		final String htmlStringstep1 = HttpUtil.iosGetHtml(uri, "utf-8");
		String scriptdata = StringUtil.getStringBetween(htmlStringstep1, 0,
				"<script>var Vedio=", ";</script>").result;
		if (StringUtil.isEmpty(scriptdata)) {
			return null;
		}
		title = StringUtil.getStringBetween(htmlStringstep1, 0, "<title>",
				"</title>").result;
		String iid = StringUtil.getStringBetween(scriptdata, 0, "\"vid\":\"",
				"\"").result;
		if (StringUtil.isEmpty(iid)) {
			return null;
		}
		if (scriptdata.indexOf("\"type\":\"tudou\"") >= 0) {
			String newurl = "http://so.v.ali213.net/plus/tudou.php?id=" + iid;
			final String htmlStringstep2 = HttpUtil.iosGetHtml(newurl);
			String add = StringUtil.getStringBetween(htmlStringstep2, 0,
					"\"url\":\"", "\"").result;
			add = add.replaceAll("\\/", "/");
			add = add.replaceAll("\\\\/", "/");
			add = add.replaceAll("&amp;", "&");
			if (StringUtil.isEmpty(add)) {
				return null;
			} else {
				return add;
			}
		} else if (scriptdata.indexOf("\"type\":\"youku\"") >= 0) {
			String NewURL = "http://v.youku.com/v_show/id_" + iid + ".html";
			NewURL = FunctionUtil.uribase64(NewURL);
			String js = HttpUtil.iosGetHtml(NewURL);
			DataBetter result = FunctionUtil.dataselectbetterone(js, "优酷网",
					context);
			if (StringUtil.isEmpty(title)) {
				title = result.title + " - 游侠网";
			}
			return result.url;
		} else if (scriptdata.indexOf("\"type\":\"qq\"") >= 0) {
			String newurl = "http://so.v.ali213.net/plus/qq.php?id=" + iid;
			final String htmlStringstep2 = HttpUtil.iosGetHtml(newurl);
			String add = StringUtil.getStringBetween(htmlStringstep2, 0,
					"\"url\":\"", "\"").result;
			add = add.replaceAll("\\/", "/");
			add = add.replaceAll("\\\\/", "/");
			add = add.replaceAll("&amp;", "&");
			if (StringUtil.isEmpty(add)) {
				return null;
			} else {
				return add;
			}
		} else if (scriptdata.indexOf("\"type\":\"v56\"") >= 0) {
			String NewURL = "http://www.56.com/u16/v_" + iid + ".html";
			NewURL = FunctionUtil.uribase64(NewURL);
			String js = HttpUtil.iosGetHtml(NewURL);
			DataBetter result = FunctionUtil.dataselectbetterone(js, "56网",
					context);
			if (StringUtil.isEmpty(title)) {
				title = result.title + " - 游侠网";
			}
			return result.url;
		} else {
			return null;
		}
	}

}
