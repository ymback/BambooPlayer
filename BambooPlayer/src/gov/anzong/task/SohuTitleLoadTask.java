package gov.anzong.task;

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

public class SohuTitleLoadTask extends AsyncTask<String, Integer, String> {

	final String origurl,from;
	String title="หับüสำฦต";
	final FragmentActivity context;
	static final String dialogTag = "load";

	public SohuTitleLoadTask(String origurl,String from, FragmentActivity context) {
		super();
		this.origurl = origurl;
		this.from=from;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		FunctionUtil.createdialog(context,dialogTag);
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		FunctionUtil.closedialog(context,dialogTag);
		if(result!=null){
			title=result;
		}
		VideoActivity.openVideo(context, Uri.parse(origurl), title);
		this.onCancelled();
		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled(String result) {
		this.onCancelled();
	}

	@Override
	protected void onCancelled() {
		if(context!=null){
			context.finish();
		}
	}

	@Override
	protected String doInBackground(String... params) {
		String uri = params[0];
		if(StringUtil.isEmpty(uri)){
			return null;
		}
		uri=FunctionUtil.uribase64(uri);
		String js="";
		for(int i=0;i<3;i++){
			js=HttpUtil.iosGetHtml(uri);
			if(!StringUtil.isEmpty(js)){
				i=10;
			}
		}
		if (StringUtil.isEmpty(js)) {
			return null;
		}
		try {
			VideoDetialData[] jsondata = VideoParseJson.parseRead(js);
			if (FunctionUtil.isjsondataempty(jsondata)) {
				return null;
			}
			String title2 = jsondata[0].title;
			String sitename = jsondata[0].site;
			title2 = FunctionUtil.videotitle(title2, sitename, from);
			return title2;
		} catch (Exception e) {
			return null;
		}
	}

}
