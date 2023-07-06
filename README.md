# Update history
  editor : chris
  
  update : 2023/7/6

# KV260_Yocto_build
  Xilinx only provide a bsp and petalinux-tool to build its image. Now we will use yocto and bitbake to build an image. 
**NOTE: Because we need to comply and build image, we need a few capacities(300GB or more).**
## Hardware
* **Limit Suggest**:
RAM >=8GB
CPU >=4
DISK >=300

* **Me**:
RAM >=16GB
CPU >=6
DISK >=500
## Get Resources
  we need some layers to build this image. We will use repo to clone all of layers we needed.
  ### Install
```script=
## Get repo first
curl https://storage.googleapis.com/git-repo-downloads/repo > repo
chmod a+x repo
sudo mv repo /usr/local/bin/
## If it is correctly installed, you should see a Usage message when invoked with the help flag. 
repo --help
```
### Initialize a Repo client
Create an empty directory to hold your working files:
```script=
XILINX_SOURCES=/path/to/xilinx/sources
mkdir -p $XILINX_SOURCES
cd $XILINX_SOURCES
```
Tell Repo where to find the manifest:
```script=
repo init -u https://github.com/Xilinx/yocto-manifests.git -b rel-v2021.1
```
### Fetch all the repositories
```script=
repo sync
```
Now go turn on the coffee machine as this may take 20 minutes depending on your connection.
## Set environment
When you use repo sync to set up whole layers and files, we need to set up environment setting.
```script=
## In your $XILINX_SOURCES
source setupsdk
```
## Build
After setting up our env, we need to change local.conf first.
```script=
## Open local.conf
nano $XILINX_SOURCES/build/conf/local.conf
#MUST ADD 
MACHINE = "zynqmp-generic"
#OPTION ADD this can be faster but I did not check it.
EXTRA_IMAGEDEPENDS_remove = "qemu-helper-native virtual/boot-bin qemu-xilinx-native"
```
Let's built it.
```script=
# build
bitbake petalinux-image-minimal
```
**If it works, uou can see 'successful'**

**If it failed, you can bitbake again. It sometimes failed when your network was not well.**

**Note: If we copy files first, it may not install xsct tarball. You can try to annotate "USE_XSCT_TARBALL = 0" in plnxtool.conf. It will work for installing xsct.**
## Copy resource to our layer
We check the bitbake is work. Then we need to add some petalinux setting for this layers.
**NOTE: sources from https://github.com/NinoX-RD/KV260_Yocto_build**
```script=
cp unlocked-sign.inc $XILINX_SOURCES/build/conf
cp locked-sign.inc $XILINX_SOURCES/build/conf
cp plnxtool.conf $XILINX_SOURCES/build/conf
cp petalinuxbsp.conf $XILINX_SOURCES/build/conf
cp -rf plnx_workspace $XILINX_SOURCES
cp -rf project-spec $XILINX_SOURCES
```
We need to let our bitbake know what it should do. We need to add something in our local.conf.
```script=
## Copy them to local.conf
cp local.conf $XILINX_SOURCES/build/conf/local.conf
```
## Checking config file 
In plnxtool.conf, Path for dts files may be wrong, so check this file and modify.
```script=
## Check path
nano $XILINX_SOURCES/build/conf/plnxtool.conf
```
## Remove dependency
There are dropbear and openssh in this file. Here, we remove dropbear.
```script=
## find packagegroup petalinux-som.bb
nano $XILINX_SOURCES/sources/meta-petalinux/recipe-core/packagegroup/packagegroup-petalinux-som.bb
REMOVE packagegroup-core-ssh-dropbear

## find petalinux-image-common.inc
nano $XILINX_SOURCES/sources/meta-petalinux/recipe-core/image/petalinux-image-common.inc
REMOVE ssh-server-dropbear 
```
We can comply and build our layers.
```script=
bitbake petalinux-image-minimal
```
For me, I often got an error (fsbl-firmware & pmu-firmware do-configuration error), You can try those methods to solve it.
1. Waiting for finish without interrupt.

  I've encountered this error many times, and I don't actually know what's causing the failure. When I'm successful, I execute for a long time.
3. Change plnxtool.conf XILINX_SDK_TOOLCHAIN to their tools.

  In this case, we need to install petalinux-tool in their website 
  https://www.xilinx.com/products/design-tools/embedded-software/petalinux-sdk.html#licensing

### Debug
1. hashserve.sock: [Errno 2] No such file or directory

   https://community.toradex.com/t/yocto-reference-build-for-custom-application-development-failing-because-of-error-in-contacting-hash-equivalence-server/13832
 
## Package image
Here, we will write wic setting in rootfs.wks and put it in wic default place.
```
cp rootfs.wks $XILINX_SOURCES/core/script/lib/wic/canned-wks/ 
```
Because wic will load this default path "$XILINX_SOURCES/build/tmp/deploy/image/zynqmp-generic/", we need to copy file to the default path.
```script=
# copy file to default path
cp $XILINX_SOURCES/image/linux/ramdisk.cpio.gz.u-boot $XILINX_SOURCES/build/tmp/deploy/image/zynqmp-generic/

# create  default path
mkdir -p /home/chris/July/build/tmp/work/zynqmp_generic-xilinx-linux/petalinux-image-minimal/1.0-r0

# find rootfs folder
cd /home/chris/July/build/tmp/work/zynqmp_generic-xilinx-linux/petalinux-initramfs-image
find -name "rootfs"

# copy file to default path
cp -r rootfs /home/chris/July/build/tmp/work/zynqmp_generic-xilinx-linux/petalinux-image-minimal/1.0-r0/
# we use wic create will make a lot of files
mkdir wic
cd wic
# create 
wic create rootfs -e petalinux-image-minimal
# check
wic ls $file.direct
# rename
mv $file.direct $file.wic
```
**NOTE: If we could not find wic command, run `source XILINX_SOURCES/setupsdk`.**


## Load SD_Card
Now, we will use balenaEtcher to do that.
```
$google balenaEtcher and download by yourself.
```
## Boot
put your sd-card in your kv-260.

* username: ninox

* password: ninox

**NOTE:We can set up an account in $XILINX_SOURCES/build/conf/petalinuxbsp.conf**
```
EXTRA_USERS_PARAMS = "useradd -P ${username} ${password};"
```

# Referance

* ROS 2 Humble in AMD KV260 with Yocto

  https://www.hackster.io/vmayoral/ros-2-humble-in-amd-kv260-with-yocto-4ae062
