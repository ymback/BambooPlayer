package gov.anzong.task;

import gov.anzong.bean.DataBetter;
import gov.anzong.bean.M1905VideoDetialData;
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
import android.util.Log;
import android.widget.Toast;

public class TEDVideoLoadTask extends AsyncTask<String, Integer, String> {

	final String origurl;
	String title = "";
	final FragmentActivity context;
	static final String dialogTag = "load";
	int errorcode = -1;// 0²ÎÊý´íÎó,1ÍøÂç´íÎó,2½âÎö´íÎó

	public TEDVideoLoadTask(String origurl, FragmentActivity context) {
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
		uri = FunctionUtil.uribase64(uri);
		String js = "";
		for (int i = 0; i < 3; i++) {
			js = HttpUtil.iosGetHtml(uri);
			if (!StringUtil.isEmpty(js)) {
				i = 10;
			}
		}
		if (StringUtil.isEmpty(js)) {
			errorcode = 1;
			return null;
		}
		try {
			VideoDetialData[] jsondata = VideoParseJson.parseRead(js);
			if (FunctionUtil.isjsondataempty(jsondata)) {
				errorcode = 2;
				return null;
			}
			title = jsondata[0].title;
			String sitename = jsondata[0].site;
			title = FunctionUtil.videotitle(title, sitename, "TED");
			String url = FunctionUtil.videorealurlted(jsondata, context);
			if (StringUtil.isEmpty(url)) {
				errorcode = 2;
				return null;
			} else {
				return url;
			}
		} catch (Exception e) {
			errorcode = 2;
			return null;
		}
	}

}
