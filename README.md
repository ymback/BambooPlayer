BambooPlayer
============

一个轻量级、多格式、支持解析多个视频网站的绿色播放器,基于VitamioMediaPlayerDemo,可作为插件或库使用

怎么用?
==========

导入视频解码库 [VitamioBundle](https://github.com/yixia/VitamioBundle).<br>
作为库:<br>
1. 复制权限及Activity的说明(AndroidManifest.xml中)到你的项目<br>
2. 使用:

```
    Uri uri = Uri.parse("xxxx");
    VideoActivity.openVideo(this, uri, "video title");
```

作为插件(请在你的项目中使用如下代码)：<br>
1. 直接打开网络视频实际地址:

```
try{
    Intent mIntent = new Intent();
    ComponentName comp = new ComponentName("gov.anzong.mediaplayer","gov.anzong.receiveintent.ReceiveIntentActivity");
    mIntent.setComponent(comp);
    mIntent.putExtra("uri", "video url");//http://192.168.2.64/1.flv，不要转化为URI
    mIntent.putExtra("title", "video title");
    startActivity(mIntent);
}catch(Exception e){
	//TODO
}
```

2.使用播放器自动解析视频实际地址:

```
try{
    Intent mIntent = new Intent();
    ComponentName comp = new ComponentName("gov.anzong.mediaplayer","gov.anzong.receiveintent.ReceiveIntentURLActivity");
    mIntent.setComponent(comp);
	Uri uri = Uri.parse("xxxx");//http://v.youku.com/v_show/id_XNDkyNzI5ODA4.html
    mIntent.setData(uri);
    startActivity(mIntent);
}catch(Exception e){
	//TODO
}
```

视频网站解析说明
==========

一、支持列表

```
1.优酷
2.土豆
3.56
4.酷六
5.乐视
6.腾讯
7.华数
8.Youtube
9.CNTV
10.网易(包含公开课)
11.音悦台
12.PPS
13.新浪(包含公开课,有些地址的视频只能播放第一段,无解)
14.爱奇艺
15.游侠视频
16.搜狐
17.M1905
18.凤凰网
19.TED
20.AcFun(部分可能不能播放,大部分情况下都能播放,多P的视频只能播放第一P,无弹幕)
21.BiliBili(基本都可播放,多P的视频只能播放第一P,无弹幕)
```

二、说明

```
以上支持列表中的视频在大部分情况下均可使用,而且可以支持多项地址格式(包括SWF播放器及分享地址等),播放器将依照手机系统及网络情况选择最优的视频清晰度
标题信息将自动读取,有些情况下无法读取,也将显示正确的网站名
若你想提交新的以上支持站点的解析地址或者新的站点,请在Issue中提出,若能在Issue中顺带提出解析方式的,将优先予以加入,否则不确定是否会加,因为我没那么多空一个个写解析
```

# License

    Copyright 2014 Shiori Takei

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
