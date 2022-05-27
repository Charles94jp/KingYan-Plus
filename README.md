# KingYan-Plus

spring boot2.6.7 + sa-token + vue3 + ts + mybatis + log4j2 + fastjson2 + 国密的CMS管理系统



## 开发

1. 根据sql目录的sql文件创建数据库，插入测试数据
2. 拷贝application.example.yml为application.yml，并配置数据库、配置国密SM2的密钥对
3. IDEA设置Mark Directory as Sources Roots。接着才可运行启动类
3. 前端先`npm i`再用WebStorm打开，如果先打开再安装依赖。则需要关闭项目后删除.idea文件夹，再打开。这样使用`ctrl+alt+l`格式化代码时，才符合eslint规范

5. 去掉chrome浏览器的跨域保护，以及SameSite防护，方便本地调试:

```
"C:\Program Files\Google\Chrome\Application\chrome.exe" --disable-site-isolation-trials --disable-web-security --disable-features=SameSiteByDefaultCookies --user-data-dir="D:\chrome-temp"
```

这样启动chrome后，当一个独立的浏览器用，能解决本地调试时，前后端端口不一导致的跨域访问问题



## 部署说明

使用nginx部署，将前后端部署到同源同域，即ip、端口相同。后端使用前缀，这样能在路由时区分前后端



### 导入数据库

1. `mysql -u -p`进入数据库
2. 创建数据库`kingyan_plus`，选择数据库
3. `sursce xxx.sql`执行sql脚本



### 部署后端

1. maven打包，上传至服务器
2. 创建文件`./config/application.yml`，配置数据库信息，配置一个前缀，方便nginx路由

```yaml
server:
  servlet:
    context-path: '/kingyan-plus-api'
```

3. 启动后端

```shell
nohup java -jar kingyan-plus-api-0.0.1-SNAPSHOT.jar >> kingyan-plus.log &
```

4. 配置nginx

```nginx
location ^~ /kingyan-plus-api/ {
    proxy_pass http://127.0.0.1:8080/kingyan-plus-api/;
}
```



### 部署前端

1. `npm run build`构建
2. 将dist目录中的所有文件打包，上传到服务器再解压到nginx目录中

```shell
unzip dist.zip -d /usr/local/nginx/kingyan-plus
```

3. nginx配置

```nginx
location /kingyan-plus {
    alias kingyan-plus;
    index index.html;
    default_type 'text/html; charset=UTF-8';
}
```



配置nginx后记得重启

```shell
/usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx.conf
/usr/local/nginx/sbin/nginx -s reload
```

