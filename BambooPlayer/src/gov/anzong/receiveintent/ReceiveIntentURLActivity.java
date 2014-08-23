package gov.anzong.receiveintent;

import java.util.Locale;

import gov.anzong.bean.VideoUrlHandle;
import gov.anzong.task.AcfunVideoLoadTask;
import gov.anzong.task.BiliBiliAidLoadTask;
import gov.anzong.task.BiliBiliCidLoadTask;
import gov.anzong.task.LetvVideoLoadTask;
import gov.anzong.task.M1905VideoLoadTask;
import gov.anzong.task.NetEaseVideoLoadTask;
import gov.anzong.task.PPSVideoLoadTask;
import gov.anzong.task.SohuHandleLoadTask;
import gov.anzong.task.SohuTitleLoadTask;
import gov.anzong.task.TEDVideoLoadTask;
import gov.anzong.task.VideoRealAddLoadTask;
import gov.anzong.task.YoutubeLoadTask;
import gov.anzong.task.YouxiaVideoLoadTask;
import gov.anzong.util.StringUtil;
import gov.anzong.util.UrlHandleFunction;
import io.vov.vitamio.LibsChecker;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class ReceiveIntentURLActivity extends FragmentActivity {
	final static String TAG = ReceiveIntentURLActivity.class.getSimpleName();
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		Intent intent = getIntent();
		Uri intenturi = intent.getData();
		if(intenturi==null){
			finish();
		}
		if (StringUtil.isEmpty(intenturi.toString())) {
			finish();
		}
		String uri = intenturi.toString();
		String from = "";
		VideoUrlHandle result = UrlHandleFunction.HandleUrl(uri);
		from = result.from;
		if (result.needotherhandle) {
			switch (result.handlesite.toLowerCase(Locale.US)) {
			case "youtube":
				YoutubeLoadTask loaderyoutube = new YoutubeLoadTask(result.urladd, this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loaderyoutube.executeOnExecutor(
							YoutubeLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
				} else {
					loaderyoutube.execute(result.urladd);
				}
				break;
			case "acfun":
				AcfunVideoLoadTask loaderacfun = new AcfunVideoLoadTask(result.urladd, this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loaderacfun.executeOnExecutor(
							AcfunVideoLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
				} else {
					loaderacfun.execute(result.urladd);
				}
				break;
			case "sohuneedtitle":
				SohuTitleLoadTask loadersohutitle = new SohuTitleLoadTask(result.urladd,from, this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loadersohutitle.executeOnExecutor(
							SohuTitleLoadTask.THREAD_POOL_EXECUTOR, result.origurl);
				} else {
					loadersohutitle.execute(result.origurl);
				}
				break;
			case "sohuneedhandle":
				SohuHandleLoadTask loadersohuhandle = new SohuHandleLoadTask(result.urladd,from, this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loadersohuhandle.executeOnExecutor(
							SohuHandleLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
				} else {
					loadersohuhandle.execute(result.urladd);
				}
				break;
			case "youxia":
				YouxiaVideoLoadTask loaderyouxia = new YouxiaVideoLoadTask(result.urladd, this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loaderyouxia.executeOnExecutor(
							YouxiaVideoLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
				} else {
					loaderyouxia.execute(result.urladd);
				}
				break;
			case "ted":
				TEDVideoLoadTask loadermted = new TEDVideoLoadTask(result.urladd, this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loadermted.executeOnExecutor(
							TEDVideoLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
				} else {
					loadermted.execute(result.urladd);
				}
				break;
			case "pps":
				PPSVideoLoadTask loaderpps = new PPSVideoLoadTask(result.urladd, this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loaderpps.executeOnExecutor(
							PPSVideoLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
				} else {
					loaderpps.execute(result.urladd);
				}
				break;
			case "m1905":
				M1905VideoLoadTask loaderm1905 = new M1905VideoLoadTask(result.urladd, this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loaderm1905.executeOnExecutor(
							M1905VideoLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
				} else {
					loaderm1905.execute(result.urladd);
				}
				break;
			case "bilibili":
				BiliBiliCidLoadTask loaderbili = new BiliBiliCidLoadTask(result.urladd, this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loaderbili.executeOnExecutor(
							BiliBiliCidLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
				} else {
					loaderbili.execute(result.urladd);
				}
				break;
			case "bilibiliaid":
				BiliBiliAidLoadTask loaderbilia = new BiliBiliAidLoadTask(result.urladd, this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loaderbilia.executeOnExecutor(
							BiliBiliAidLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
				} else {
					loaderbilia.execute(result.urladd);
				}
				break;
			case "netease":
				NetEaseVideoLoadTask loadernetease = new NetEaseVideoLoadTask(result.urladd,result.from, this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loadernetease.executeOnExecutor(
							NetEaseVideoLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
				} else {
					loadernetease.execute(result.urladd);
				}
				break;
			case "letv":
				LetvVideoLoadTask loaderletv = new LetvVideoLoadTask(result.urladd,result.from, this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loaderletv.executeOnExecutor(
							LetvVideoLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
				} else {
					loaderletv.execute(result.urladd);
				}
				break;
			default:
				uri = result.urladd;
				VideoRealAddLoadTask loader = new VideoRealAddLoadTask(result.urladd, from,
						this);
				if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
					loader.executeOnExecutor(
							VideoRealAddLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
				} else {
					loader.execute(result.urladd);
				}
				break;
			}
		} else {
			uri = result.urladd;
			VideoRealAddLoadTask loader = new VideoRealAddLoadTask(result.urladd, from,
					this);
			if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
				loader.executeOnExecutor(
						VideoRealAddLoadTask.THREAD_POOL_EXECUTOR, result.urladd);
			} else {
				loader.execute(result.urladd);
			}
		}
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("TAG",String.valueOf(requestCode));
     if (requestCode == 123) {
    	 finish();
     }else{
         super.onActivityResult(requestCode, resultCode, data);
     }
    }
}
