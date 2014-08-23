package gov.anzong.util;

import gov.anzong.bean.DataBetter;
import gov.anzong.bean.M1905VideoDetialData;
import gov.anzong.bean.VideoDetialData;
import gov.anzong.bean.VideoParseJson;
import gov.anzong.bean.VideoTypeAdd;
import gov.anzong.fragment.ProgressDialogFragment;
import gov.anzong.mediaplayer.R;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.Display;

public class FunctionUtil {
	public static String uribase64(String uri) {
		uri = uri.replaceAll("://", ":##");
		uri = Base64.encodeToString(uri.getBytes(), Base64.NO_WRAP);
		uri = "http://api.flvxz.com/jsonp/purejson/url/" + uri;
		return uri;
	}

	public static String uriEncodeFlvcd(String uri, String format) {
		String newuri = "http://www.flvcd.com/parse.php?format=" + format
				+ "&kw=";
		try {
			uri = URLEncoder.encode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return newuri + uri;
	}

	public static String videotitle(String title, String sitename, String from) {
		if (!StringUtil.isEmpty(title)) {
			title += " - ";
		}
		if (sitename == null) {
			sitename = "";
		}
		if (!StringUtil.isEmpty(from)) {
			title += from;
		} else {
			title += sitename;
		}
		return title;
	}

	public static boolean isjsondataempty(VideoDetialData[] jsondata) {
		if (jsondata == null) {
			return true;
		}
		if (jsondata.length == 0) {
			return true;
		}
		return false;
	}

	public static boolean isjsondataempty(M1905VideoDetialData[] jsondata) {
		if (jsondata == null) {
			return true;
		}
		if (jsondata.length == 0) {
			return true;
		}
		return false;
	}

	private static int getNumCores() {
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				if (Pattern.matches("cpu[0-9]", pathname.getName())) {
					return true;
				}
				return false;
			}
		}

		try {
			// Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			// Return the number of cores (virtual CPU devices)
			return files.length;
		} catch (Exception e) {
			// Default to return 1 core
			return 1;
		}
	}

	public static int minpixel(FragmentActivity context) {
		Display mDisplay = context.getWindowManager().getDefaultDisplay();
		int W = mDisplay.getWidth();
		int H = mDisplay.getHeight();
		if (W > H) {
			return H;
		} else {
			return W;
		}
	}

	public static int getvideohdformodel(FragmentActivity context) {
		int hd = 0;
		int Cores = getNumCores();
		if (Cores > 1) {// 多核
			if (Cores > 2) {// 四核以上,看分辨率吧
				int minpixel = minpixel(context);
				if (minpixel >= 1080) {
					hd = 2;
				} else {
					hd = 1;
				}
			} else {// 双核看分辨率吧
				int minpixel = minpixel(context);
				if (minpixel >= 720) {
					hd = 1;
				}
			}
		}
		String networkmode = getNetworkClass(context);
		switch (networkmode) {
		case "2G":
			hd = 0;
			break;
		case "3G":
			if (hd == 2)
				hd = 1;
			break;
		default:
			break;
		}
		return hd;
	}

	public static boolean isInWifi(Context mContext) {
		ConnectivityManager conMan = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		return wifi == State.CONNECTED;
	}

	public static boolean isConnected(Context mContext) {
		ConnectivityManager conMan = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conMan.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	public static String getNetworkClass(Context mContext) {
		if (isInWifi(mContext)) {
			return "WIFI";
		}
		if (!isConnected(mContext)) {
			return "无网络";
		}
		TelephonyManager mTelephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		int networkType = mTelephonyManager.getNetworkType();
		switch (networkType) {
		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
		case TelephonyManager.NETWORK_TYPE_IDEN:
			return "2G";
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
		case TelephonyManager.NETWORK_TYPE_EHRPD:
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			return "3G";
		case TelephonyManager.NETWORK_TYPE_LTE:
			return "4G";
		default:
			return "未知网络";
		}
	}

	public static String videorealurl(VideoDetialData[] jsondata, String from,
			FragmentActivity context) {
		int hd = getvideohdformodel(context);
		VideoTypeAdd add = new VideoTypeAdd();
		String quality1 = null, quality2 = null;
		if (hd == 2) {
			switch (from) {
			case "优酷网":// http://v.youku.com/v_show/id_XNzM0MTI2NDM2.html
			case "土豆网":// http://www.tudou.com/albumplay/4Eo9FRy41pw/MTelMNxnomQ.html
				quality1 = "超清M3U8";
				quality2 = "高清M3U8";
				break;
			case "乐视网":// http://www.letv.com/ptv/vplay/20218053.html
				quality1 = "1080pM3U8";
				quality2 = "超清M3U8";
				break;
			case "56网":// http://www.56.com/u49/v_MTAwMjQ3OTY2.html
				quality1 = "super";
				quality2 = "normal";
				break;
			case "酷六网":// http://v.ku6.com/show/HW9JjdBGdZYzTD-ivTIE5A...html?from=my
				quality1 = "超清";
				quality2 = "高清";
				break;
			case "腾讯视频":// http://v.qq.com/cover/c/ch4jk2ygsu95qtb.html
				quality1 = "超清MP4";
				quality2 = "高清MP4";
				break;
			case "华数TV":// http://www.wasu.cn/Play/show/id/4231159
				break;
			case "PPS.tv":// http://v.pps.tv/play_3HC6NV.html
				quality1 = "1080PM3U8";
				quality2 = "超清M3U8";
				break;
			case "音悦台":// http://v.yinyuetai.com/video/2061385
				quality1 = "超清";
				quality2 = "高清";
				break;
			case "爱奇艺":// http://www.iqiyi.com/v_19rrmo1540.html
				quality1 = "1080P";
				quality2 = "720P";
				break;
			case "CNTV":// http://tv.cntv.cn/video/C30881/75b026061ee849d09bdc2875d69dfc11
				quality1 = "超清";
				quality2 = "高清";
				break;
			case "凤凰网":// http://v.ifeng.com/documentary/history/201111/21e5dc45-2330-4325-b0aa-3edfaef7caaf.shtml
				quality1 = "整段";
				break;
			case "新浪视频":
			case "新浪公开课":
				quality1 = "超清";
				quality1 = "高清";
				break;
			default:
				break;
			}
		}
		if (quality1 == null && hd == 2) {// WASU适配
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].quality == null) {
					if (jsondata[i].files != null) {
						if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		}
		if (!StringUtil.isEmpty(quality1) && hd == 2) {
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].quality.toLowerCase(Locale.CHINA).equals(
						quality1.toLowerCase(Locale.CHINA))) {
					if (jsondata[i].files != null) {
						if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		}
		if (!StringUtil.isEmpty(quality2) && hd == 2) {
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].quality.toLowerCase(Locale.CHINA).equals(
						quality2.toLowerCase(Locale.CHINA))) {
					if (jsondata[i].files != null) {
						if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		} else if (hd == 2) {
			hd = 1;
		}
		if (hd == 1) {
			switch (from) {
			case "优酷网":
			case "土豆网":
				quality1 = "高清M3U8";
				quality2 = "MP4格式M3U8";
				break;
			case "乐视网":
				quality1 = "高清M3U8";
				quality2 = "高清";
				break;
			case "56网":
				quality1 = "normal";
				quality2 = "clear";
			case "酷六网":
				quality1 = "高清";
				quality2 = "M3U8";
			case "腾讯视频":
				quality1 = "高清MP4";
				quality2 = "标清MP4";
				break;
			case "华数TV":
				break;
			case "PPS.tv":
				quality1 = "720PM3U8";
				quality2 = "高清M3U8";
				break;
			case "音悦台":
				quality1 = "高清";
				quality2 = "标清";
				break;
			case "爱奇艺":
				quality1 = "720P";
				quality2 = "高清";
				break;
			case "CNTV":
				quality1 = "高清";
				quality2 = "M3U8";
				break;
			case "凤凰网":
				quality1 = "整段";
				break;
			case "新浪视频":
			case "新浪公开课":
				quality1 = "高清";
				quality1 = "MP4";
				break;
			default:
				break;
			}
		}
		if (quality1 == null && hd == 1) {// WASU适配
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].quality == null) {
					if (jsondata[i].files != null) {
						if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		}
		if (!StringUtil.isEmpty(quality1) && hd == 1) {
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].quality.toLowerCase(Locale.CHINA).equals(
						quality1.toLowerCase(Locale.CHINA))) {
					if (jsondata[i].files != null) {
						if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		}
		if (!StringUtil.isEmpty(quality2) && hd == 1) {
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].quality.toLowerCase(Locale.CHINA).equals(
						quality2.toLowerCase(Locale.CHINA))) {
					if (jsondata[i].files != null) {
						if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		} else if (hd == 1) {
			hd = 0;
		}
		if (hd == 0) {
			switch (from) {
			case "优酷网":
			case "土豆网":
				quality1 = "MP4格式M3U8";
				quality2 = "手机标清";
				break;
			case "乐视网":
				quality1 = "标清M3U8";
				quality2 = "标清";
				break;
			case "56网":
				quality1 = "clear";
				quality2 = null;
			case "酷六网":
				quality1 = "M3U8";
				quality2 = "标清";
			case "腾讯视频":
				quality1 = "标清";
				quality2 = "标清MP4";
				break;
			case "华数TV":
				break;
			case "PPS.tv":
				quality1 = "流畅M3U8";
				quality2 = "高清M3U8";
				break;
			case "音悦台":
				quality1 = "标清";
				quality2 = null;
				break;
			case "爱奇艺":
				quality1 = "mp4";
				quality2 = "m3u8";
				break;
			case "CNTV":
				quality1 = "标清";
				quality2 = "M3U8";
				break;
			case "凤凰网":
				quality1 = "整段";
				break;
			case "新浪视频":
			case "新浪公开课":
				quality1 = "标清";
				quality1 = "MP4";
				break;
			default:
				break;
			}
		}
		if (quality1 == null && hd == 0) {// WASU适配
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].quality == null) {
					if (jsondata[i].files != null) {
						if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		}
		if (!StringUtil.isEmpty(quality1) && hd == 0) {
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].quality.toLowerCase(Locale.CHINA).equals(
						quality1.toLowerCase(Locale.CHINA))) {
					if (jsondata[i].files != null) {
						if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		}
		if (!StringUtil.isEmpty(quality2) && hd == 0) {
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].quality.toLowerCase(Locale.CHINA).equals(
						quality2.toLowerCase(Locale.CHINA))) {
					if (jsondata[i].files != null) {
						if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		}
		for (int i = 0; i < jsondata.length; i++) {
			if (jsondata[i].files != null) {
				if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
					switch (jsondata[i].files[0].ftype.toLowerCase(Locale.US)) {
					case "m3u8":
						add.m3u8add = jsondata[i].files[0].furl;
						break;
					case "mp4":
						add.mp4add = jsondata[i].files[0].furl;
						break;
					case "f4v":
						add.f4vadd = jsondata[i].files[0].furl;
						break;
					case "flv":
						add.flvadd = jsondata[i].files[0].furl;
						break;
					default:
						add.otheradd = jsondata[i].files[0].furl;
						break;
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		}
		for (int i = 0; i < jsondata.length; i++) {
			if (jsondata[i].files != null) {
				if (jsondata[i].files.length > 1) {// 必须有files且只有一个,然后随便选吧
					switch (jsondata[i].files[0].ftype.toLowerCase(Locale.US)) {
					case "m3u8":
						add.m3u8add = jsondata[i].files[0].furl;
						break;
					case "mp4":
						add.mp4add = jsondata[i].files[0].furl;
						break;
					case "f4v":
						add.f4vadd = jsondata[i].files[0].furl;
						break;
					case "flv":
						add.flvadd = jsondata[i].files[0].furl;
						break;
					default:
						add.otheradd = jsondata[i].files[0].furl;
						break;
					}
				}
			}
		}
		return add.rightadd();
	}

	public static DataBetter dataselectbetterone(String js, String from,
			FragmentActivity context) {
		DataBetter databetter = new DataBetter();
		if (StringUtil.isEmpty(js)) {
			databetter.errorcode = 1;
			return databetter;
		}
		try {
			VideoDetialData[] jsondata = VideoParseJson.parseRead(js);
			if (FunctionUtil.isjsondataempty(jsondata)) {
				databetter.errorcode = 2;
				return databetter;
			}
			String title = jsondata[0].title;
			String sitename = jsondata[0].site;
			title = FunctionUtil.videotitle(title, sitename, from);
			String url = videorealurl(jsondata, from, context);
			if (StringUtil.isEmpty(url)) {
				databetter.errorcode = 2;
				return databetter;
			} else {
				databetter.errorcode = -1;
				databetter.title = title;
				databetter.url = url;
				return databetter;
			}
		} catch (Exception e) {
			databetter.errorcode = 2;
			return databetter;
		}
	}

	public static String videorealurl(M1905VideoDetialData[] jsondata,
			FragmentActivity context) {

		int hd = 0;
		int Cores = getNumCores();
		if (Cores > 1) {// 多核
			if (Cores > 2) {// 四核以上,看分辨率吧
				int minpixel = minpixel(context);
				if (minpixel >= 1080) {
					hd = 2;
				} else {
					hd = 1;
				}
			} else {// 双核看分辨率吧
				int minpixel = minpixel(context);
				if (minpixel >= 720) {
					hd = 1;
				}
			}
		}
		VideoTypeAdd add = new VideoTypeAdd();
		String quality1 = null;
		if (hd == 2) {
			quality1 = "高清";
		}
		if (!StringUtil.isEmpty(quality1) && hd == 2) {
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].quality.toLowerCase(Locale.CHINA).equals(
						quality1.toLowerCase(Locale.CHINA))) {
					if (jsondata[i].files != null) {
						if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		} else if (hd == 2) {
			hd = 1;
		}
		if (hd == 1) {
			quality1 = "高清";
		}
		if (!StringUtil.isEmpty(quality1) && hd == 1) {
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].quality.toLowerCase(Locale.CHINA).equals(
						quality1.toLowerCase(Locale.CHINA))) {
					if (jsondata[i].files != null) {
						if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		} else if (hd == 1) {
			hd = 0;
		}
		if (hd == 0) {
			quality1 = "标清";
		}
		if (!StringUtil.isEmpty(quality1) && hd == 0) {
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].quality.toLowerCase(Locale.CHINA).equals(
						quality1.toLowerCase(Locale.CHINA))) {
					if (jsondata[i].files != null) {
						if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		}
		for (int i = 0; i < jsondata.length; i++) {
			if (jsondata[i].files != null) {
				if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
					switch (jsondata[i].files[0].ftype.toLowerCase(Locale.US)) {
					case "m3u8":
						add.m3u8add = jsondata[i].files[0].furl;
						break;
					case "mp4":
						add.mp4add = jsondata[i].files[0].furl;
						break;
					case "f4v":
						add.f4vadd = jsondata[i].files[0].furl;
						break;
					case "flv":
						add.flvadd = jsondata[i].files[0].furl;
						break;
					default:
						add.otheradd = jsondata[i].files[0].furl;
						break;
					}
				}
			}
		}
		return add.rightadd();
	}

	public static String videorealurlted(VideoDetialData[] jsondata,
			FragmentActivity context) {

		int hd = 0;
		int Cores = getNumCores();
		if (Cores > 1) {// 多核
			if (Cores > 2) {// 四核以上,看分辨率吧
				int minpixel = minpixel(context);
				if (minpixel >= 1080) {
					hd = 2;
				} else {
					hd = 1;
				}
			} else {// 双核看分辨率吧
				int minpixel = minpixel(context);
				if (minpixel >= 720) {
					hd = 1;
				}
			}
		}
		VideoTypeAdd add = new VideoTypeAdd();
		String quality1 = null;
		if (hd == 2) {
			quality1 = "480p.mp4";
		}
		if (!StringUtil.isEmpty(quality1) && hd == 2) {
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].files != null) {
					if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
						if (jsondata[i].files[0].ftype.toLowerCase(Locale.US)
								.indexOf(quality1) >= 0) {
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		} else if (hd == 2) {
			hd = 1;
		}
		if (hd == 1) {
			quality1 = "s.mp4";
		}
		if (!StringUtil.isEmpty(quality1) && hd == 1) {
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].files != null) {
					if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
						if (jsondata[i].files[0].ftype.toLowerCase(Locale.US)
								.indexOf(quality1) >= 0) {
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		} else if (hd == 1) {
			hd = 0;
		}
		if (hd == 0) {
			quality1 = "s-320k.mp4";
		}
		if (!StringUtil.isEmpty(quality1) && hd == 0) {
			for (int i = 0; i < jsondata.length; i++) {
				if (jsondata[i].files != null) {
					if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
						if (jsondata[i].files[0].ftype.toLowerCase(Locale.US)
								.indexOf(quality1) >= 0) {
							switch (jsondata[i].files[0].ftype
									.toLowerCase(Locale.US)) {
							case "m3u8":
								add.m3u8add = jsondata[i].files[0].furl;
								break;
							case "mp4":
								add.mp4add = jsondata[i].files[0].furl;
								break;
							case "f4v":
								add.f4vadd = jsondata[i].files[0].furl;
								break;
							case "flv":
								add.flvadd = jsondata[i].files[0].furl;
								break;
							default:
								add.otheradd = jsondata[i].files[0].furl;
								break;
							}
						}
					}
				}
			}
		}
		if (add.hasadd()) {
			return add.rightadd();
		}
		for (int i = 0; i < jsondata.length; i++) {
			if (jsondata[i].files != null) {
				if (jsondata[i].files.length == 1) {// 必须有files且只有一个,然后随便选吧
					switch (jsondata[i].files[0].ftype.toLowerCase(Locale.US)) {
					case "m3u8":
						add.m3u8add = jsondata[i].files[0].furl;
						break;
					case "mp4":
						add.mp4add = jsondata[i].files[0].furl;
						break;
					case "f4v":
						add.f4vadd = jsondata[i].files[0].furl;
						break;
					case "flv":
						add.flvadd = jsondata[i].files[0].furl;
						break;
					default:
						add.otheradd = jsondata[i].files[0].furl;
						break;
					}
				}
			}
		}
		return add.rightadd();
	}

	public static void createdialog(FragmentActivity context, String dialogTag) {
		ProgressDialogFragment pd = new ProgressDialogFragment();

		Bundle args = new Bundle();
		final String content = context.getResources().getString(
				R.string.load_please_wait);
		args.putString("content", content);
		pd.setArguments(args);
		pd.show(context.getSupportFragmentManager(), dialogTag);
	}

	public static void closedialog(FragmentActivity context, String dialogTag) {

		FragmentManager fm = context.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Fragment prev = fm.findFragmentByTag(dialogTag);
		if (prev != null) {
			ft.remove(prev);
		}
		try {
			ft.commit();
		} catch (Exception e) {
		}
	}

}
