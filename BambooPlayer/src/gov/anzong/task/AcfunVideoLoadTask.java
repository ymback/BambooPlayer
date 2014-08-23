package gov.anzong.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Pattern;

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

public class AcfunVideoLoadTask extends AsyncTask<String, Integer, String> {

	final String origurl;
	String title = "";
	final FragmentActivity context;
	private boolean isnetworkerr;
	private boolean isdelected;
	static final String dialogTag = "load";
	static private final String TUDOU_START = "http://www.tudou.com/programs/view/";
	static private final String TUDOU_END = "/";
	static private final String YOUKU_START = "http://v.youku.com/v_show/id_";
	static private final String YOUKU_END = ".html";
	static private final String QQ_START = "http://v.qq.com/";
	static private final String SINA_START = "http://video.sina.com.cn/";
	static private final String SINAYOU_START = "http://you.video.sina.com.cn/";

	public AcfunVideoLoadTask(String origurl, FragmentActivity context) {
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

		String uri = params[0];
		uri = uri.replace("acfun.com", "acfun.tv");
		final String htmlStringstep1 = HttpUtil.iosGetHtml(uri);
		if (StringUtil.isEmpty(htmlStringstep1)) {
			this.isnetworkerr = true;
			return null;
		}
		String datavid = StringUtil.getStringBetween(htmlStringstep1, 0,
				"data-vid=\"", "\"").result;
		title = StringUtil.getStringBetween(htmlStringstep1, 0, "<title>",
				"</title>").result;
		String newurl = "", sourceType = "", sourceUrl = "";
		String htmlStringstep2 = "";
		if (StringUtil.isEmpty(datavid)) {
			if (htmlStringstep1.indexOf("该页面可能因为如下原因被删除") > 0) {
				this.isdelected = true;
				return null;
			}
			String playerdata = StringUtil.getStringBetween(htmlStringstep1, 0,
					"autocompletion_player(", ");").result;
			String list[] = new String[4];
			if (playerdata.indexOf(",") < 0) {
				datavid = playerdata;
			} else {
				playerdata = playerdata.replaceFirst("'", "");
				list = playerdata.split(",");
			}
			if (StringUtil.isEmpty(datavid) && StringUtil.isEmpty(list[0])) {
				return null;
			} else {
				if (!StringUtil.isEmpty(datavid)) {
					newurl = "http://www.acfun.tv/video/getVideo.aspx?id="
							+ datavid;
					htmlStringstep2 = HttpUtil.iosGetHtml(newurl);
					sourceType = StringUtil.getStringBetween(htmlStringstep2,
							0, "\"sourceType\":\"", "\"").result;
					sourceUrl = StringUtil.getStringBetween(htmlStringstep2, 0,
							"\"sourceUrl\":\"", "\"").result;
				} else {
					sourceUrl = list[0];
					sourceType = list[1];
				}
			}
		} else {
			newurl = "http://www.acfun.tv/video/getVideo.aspx?id=" + datavid;
			htmlStringstep2 = HttpUtil.iosGetHtml(newurl);
			sourceType = StringUtil.getStringBetween(htmlStringstep2, 0,
					"\"sourceType\":\"", "\"").result;
			sourceUrl = StringUtil.getStringBetween(htmlStringstep2, 0,
					"\"sourceUrl\":\"", "\"").result;
		}
		if (StringUtil.isEmpty(sourceType)) {
			return null;
		}
		if (sourceType.toLowerCase(Locale.US).equals("sina")) {
			if (StringUtil.isEmpty(sourceUrl)) {
				return null;
			}
			if ((sourceUrl.toLowerCase(Locale.US).startsWith(SINA_START) || sourceUrl
					.toLowerCase(Locale.US).startsWith(SINAYOU_START))
					&& StrTotalCount(sourceUrl, "/") >= 4) {
				String sinaurl = sourceUrl;
				sinaurl = FunctionUtil.uribase64(sinaurl);
				String js = HttpUtil.iosGetHtml(sinaurl);
				DataBetter result = FunctionUtil.dataselectbetterone(js, "土豆网",
						context);
				if (StringUtil.isEmpty(title)) {
					title = result.title + " - AcFun";
				}
				return result.url;
			} else {
				return null;
			}
		} else if (sourceType.toLowerCase(Locale.US).equals("youku")) {
			if (StringUtil.isEmpty(sourceUrl)) {
				return null;
			}
			if (sourceUrl.toLowerCase(Locale.US).startsWith(TUDOU_START)) {
				String id = StringUtil.getStringBetween(sourceUrl, 0,
						TUDOU_START, TUDOU_END).result;
				String tudouuri = "http://www.tudou.com/programs/view/" + id
						+ "/";
				tudouuri = FunctionUtil.uribase64(tudouuri);
				String js = HttpUtil.iosGetHtml(tudouuri);
				DataBetter result = FunctionUtil.dataselectbetterone(js, "土豆网",
						context);
				if (StringUtil.isEmpty(title)) {
					title = result.title + " - AcFun";
				}
				return result.url;
			} else if (sourceUrl.toLowerCase(Locale.US).startsWith(YOUKU_START)) {// 优酷,可以直接拿VID解析的
				String youkuurl = sourceUrl;
				youkuurl = FunctionUtil.uribase64(youkuurl);
				String js = HttpUtil.iosGetHtml(youkuurl);
				DataBetter result = FunctionUtil.dataselectbetterone(js, "优酷网",
						context);
				if (StringUtil.isEmpty(title)) {
					title = result.title + " - AcFun";
				}
				return result.url;
			} else {
				String NewURL = "http://v.youku.com/v_show/id_" + sourceUrl
						+ ".html";
				NewURL = FunctionUtil.uribase64(NewURL);
				String js = HttpUtil.iosGetHtml(NewURL);
				DataBetter result = FunctionUtil.dataselectbetterone(js, "优酷网",
						context);
				if (StringUtil.isEmpty(title)) {
					title = result.title + " - AcFun";
				}
				return result.url;
			}
		} else if (sourceType.toLowerCase(Locale.US).equals("tudou")) {
			if (StringUtil.isEmpty(sourceUrl)) {
				return null;
			}
			if (sourceUrl.toLowerCase(Locale.US).startsWith(TUDOU_START)) {
				String id = StringUtil.getStringBetween(sourceUrl, 0,
						TUDOU_START, TUDOU_END).result;
				String tudouuri = "http://www.tudou.com/programs/view/" + id
						+ "/";
				tudouuri = FunctionUtil.uribase64(tudouuri);
				String js = HttpUtil.iosGetHtml(tudouuri);
				DataBetter result = FunctionUtil.dataselectbetterone(js, "土豆网",
						context);
				if (StringUtil.isEmpty(title)) {
					title = result.title + " - AcFun";
				}
				return result.url;
			} else {
				return null;
			}
		} else if (sourceType.toLowerCase(Locale.US).equals("qq")) {
			if (StringUtil.isEmpty(sourceUrl)) {
				return null;
			}
			if (sourceUrl.toLowerCase(Locale.US).startsWith(QQ_START)) {
				String qqurl = sourceUrl;
				qqurl = FunctionUtil.uribase64(qqurl);
				String js = HttpUtil.iosGetHtml(qqurl);
				DataBetter result = FunctionUtil.dataselectbetterone(js,
						"腾讯视频", context);
				if (StringUtil.isEmpty(title)) {
					title = result.title + " - AcFun";
				}
				return result.url;
			}
		} else {
			return null;
		}
		return null;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	private int StrTotalCount(String str, String key) {
		int count = 0;
		int index = 0;
		while ((index = str.indexOf(key, index)) != -1) {
			index = index + key.length();
			count++;
		}
		return count;
	}
}
