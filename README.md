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


## Configuration

Application can be configured via YML files  contained in *src/main/resources*  Files are roughly structured around "prifles"

- application.yml  - basic configuration stuff active bay default
- application-dev.yml  -  development configuration,   mist likely to contain dev  profile  settings for logging and dev accounts and whatever
- application-pulse.yml -  pulse chain specific production settings. Keys for executor accounts and contract addresses ought to be  cconfigured here

Values can be  also passed through cimmand line when invoking containenr, like this:

````shell
sudo docker run -d -it bbkeeperbots --groundskeeper.bribe=35 --spring.profiles.active=pulse
````

Will activate pulsechain specific profile, and set bribe over base cas proce to 25%