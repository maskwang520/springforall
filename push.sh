sudo bash ./clear.sh
git pull
mvn clean package
sudo docker build . -t springforall/mesh:latest
echo 'finish compile and build'
echo 'pushing'
echo 'springforall123' | sudo docker login --username=826655812@qq.com registry.cn-hangzhou.aliyuncs.com
sudo docker tag springforall/mesh:latest registry.cn-hangzhou.aliyuncs.com/springforall/springforall:latest
sudo docker push registry.cn-hangzhou.aliyuncs.com/springforall/springforall:latest
echo 'finish'
