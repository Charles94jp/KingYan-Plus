# KingYan-Plus

spring boot2.6.7 + sa-token + vue3 + ts + mybatis + log4j2 + fastjson2 + 国密的CMS管理系统



## 开发

1. 根据sql目录的sql文件创建数据库，插入测试数据
2. 拷贝application.example.yml为application.yml，并配置数据库、配置国密SM2的密钥对
3. IDEA设置Mark Directory as Sources Roots。接着才可运行启动类
3. 前端先`npm i`再用WebStorm打开，如果先打开再安装依赖。则需要关闭项目后删除.idea文件夹，再打开。这样使用`ctrl+alt+l`格式化代码时，才符合eslint规范

5. 去掉chrome浏览器的跨域保护:

```
"C:\Program Files\Google\Chrome\Application\chrome.exe" --disable-site-isolation-trials --disable-web-security --user-data-dir="D:\chrome-temp"
```

这样启动chrome后，当一个独立的浏览器用，能解决本地调试时，前后端端口不一导致的跨域访问问题



