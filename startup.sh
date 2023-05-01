mkdir -p ./docker/mysql/data
sudo chmod -R 777 ./docker
sudo chmod -R 777 ./docker/mysql/data

docker-compose up -d

echo "sleep 15s"
sleep 15s

#  계정 생성
docker exec -it coconut mysql -uroot -proot -e " \
CREATE USER 'coco'@'localhost' IDENTIFIED WITH mysql_native_password BY 'coco'; \
FLUSH PRIVILEGES;"

# 계정 생성 확인
docker exec -it coconut mysql -uroot -proot -Dmysql -e "select user,host from user;"
