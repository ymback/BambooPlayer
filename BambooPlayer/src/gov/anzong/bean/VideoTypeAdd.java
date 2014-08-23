package gov.anzong.bean;

import gov.anzong.util.StringUtil;

public class VideoTypeAdd {
	public String m3u8add;
	public String mp4add;
	public String f4vadd;
	public String flvadd;
	public String otheradd;
	public boolean hasadd(){
		if(StringUtil.isEmpty(this.m3u8add)
				&&StringUtil.isEmpty(this.mp4add)
				&&StringUtil.isEmpty(this.f4vadd)
				&&StringUtil.isEmpty(this.flvadd)
				&&StringUtil.isEmpty(this.otheradd)){
			return false;
		}else{
			return true;
		}
	}
	public String rightadd(){
		if (!StringUtil.isEmpty(m3u8add)) {
			return m3u8add;
		}
		if (!StringUtil.isEmpty(mp4add)) {
			return mp4add;
		}
		if (!StringUtil.isEmpty(f4vadd)) {
			return f4vadd;
		}
		if (!StringUtil.isEmpty(flvadd)) {
			return flvadd;
		}
		if (!StringUtil.isEmpty(otheradd)) {
			return otheradd;
		}
		return null;
	}
}
