package gov.anzong.mediaplayer;

import io.vov.vitamio.LibsChecker;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ReceiveIntentFilterActivity extends Activity {
	public String title;
	public Uri uri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		uri = getIntent().getData();
		if (uri != null) {
			if (!isEmpty(uri.toString())) {
				VideoActivity.openVideo(this, uri, getname(uri));
				this.finish();
			} else {
				Toast.makeText(this, "视频地址错误", Toast.LENGTH_SHORT).show();
				try {
					android.os.Process.killProcess(android.os.Process.myPid());
				} catch (Exception e) {
				}
			}
		} else {
			Toast.makeText(this, "视频地址错误", Toast.LENGTH_SHORT).show();
			try {
				android.os.Process.killProcess(android.os.Process.myPid());
			} catch (Exception e) {
			}
		}
	}

	public static String getname(Uri uritmp) {
		String path = uritmp.toString().trim();
		int pos = path.lastIndexOf("/");
		int totallen = path.length();
		String title = "";
		if (pos > 0 && pos + 1 < totallen) {
			title = path.substring(pos + 1);
		}
		if (title.equals("")) {
			pos = path.lastIndexOf("\\");
			if (pos > 0 && pos + 1 < totallen) {
				title = path.substring(pos + 1);
			}
		}
		if (title.equals("")) {
			title = "未知";
		}
		return title;
	}

	/** 判断是否是 "" 或者 null */
	public static boolean isEmpty(String str) {
		if (str != null && !"".equals(str)) {
			return false;
		} else {
			return true;
		}
	}
}
