# nginx-using
### ngnix.conf配置
```
#定义一个upstream块
upstream nginxtest{
    server www.back.com;
}
server {
    #监听80端口即当访问www.back.com的时候不需要使用端口
    listen       80;
    server_name  localhost;

    #charset koi8-r;

    #access_log  logs/host.access.log  main;
    #匹配以表达中结尾的文件的请求
    location ~ .*\.(html|htm|gif|jpg|jpeg|bmp|png|ico|txt|js|css)$ 
    {   
        root html;
        expires      7d; 
    }   	
    location / {
        root   html;
        index  index.html index.htm;
    }
    #当访问www.back.com/*的时候向上游服务器，即我写的springboot服务
    location ~ ^/? {
        proxy_pass http://nginxtest;
    }

    #error_page  404              /404.html;

    # redirect server error pages to the static page /50x.html
    #
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   html;
    }
}
```