#!/bin/bash
###########
echo "$ nginxReloader.sh"

echo "$ sed -i 's/USER_TOKEN/$1/g' /etc/nginx/conf.d/code-server.conf"
sed -i "s/USER_TOKEN/$1/g" /etc/nginx/conf.d/code-server.conf

echo "$ sed -i 's/CATALOG_URL_EXT/$2/g' /etc/nginx/conf.d/code-server.conf"
sed -i "s/CATALOG_URL_EXT/$2/g" /etc/nginx/conf.d/code-server.conf

echo "$ sed -i 's/CATALOG_URL/$3/g' /etc/nginx/conf.d/code-server.conf"
sed -i "s/CATALOG_URL/$3/g" /etc/nginx/conf.d/code-server.conf

echo "$ nginx"
nginx

echo "$ check nginx configuration change"
while true
do
 inotifywait --exclude .swp -e create -e modify -e delete -e move /etc/nginx/conf.d
 nginx -t
 if [ $? -eq 0 ]
 then
  echo "Detected Nginx Configuration Change"
  echo "Executing: nginx -s reload"
  nginx -s reload

 fi
done