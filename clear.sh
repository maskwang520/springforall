sudo docker rmi $(docker images -f "dangling=true" -q)
sudo docker rmi registry.cn-hangzhou.aliyuncs.com/springforall/springforall
