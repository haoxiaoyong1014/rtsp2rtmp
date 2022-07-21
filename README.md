**java实现RTSP到RTMP转换** 

* 海康摄像头rtsp转rtmp
* 大华摄像头rtsp转rtmp
* 网页直播

**项目包含的技术**
* Netty
* javaCV
* Spring
* bilibili的前端直播组件flv.js


**大致的原理和过程** 

* javaCV把rtsp装封装成rtmp推送给netty实现的rtmp服务器

* 前端页面请求netty中的视频数据展示

我们使用javaCV把RTSP转封装为RTMP，推送给Netty实现的RTMP服务器。RTMP服务将转换的视频流转存起来。

在项目启动的时候不光开启了RTMP服务，还开启了以Netty实现的HTTP服务，这个服务主要是给前端访问RTMP的视频流使用，然后使用flv.js在页面上播放使用。

![image.png](https://upload-images.jianshu.io/upload_images/15181329-02f94ea9d00a8999.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)