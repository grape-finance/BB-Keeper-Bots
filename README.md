##  BB keeper bots



## docker image

### build

````shell
./gradlew bootBuildImage --imageName=bbkeeperbots
````


### download and ship

Sample invocations to download and ship docker image 
````shell
    docker save -o bbkeeperbots.tar bbkeeperbots
    gzip -f jarb.tar
    scp jarb.tar.gz  176.118.198.245:.
````

## run

Sample invocations to import and run docker image on the target host. 

````shell
sudo docker load -i bbkeeperbots.tar
sudo docker run -d -it bbkeeperbots  --spring.profiles.active=pulse
sudo docker container logs 04611514c444  --follow
 ````

