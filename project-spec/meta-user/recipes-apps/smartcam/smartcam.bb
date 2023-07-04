SUMMARY = "Example Smartcam application"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a9c5ded2ac97b4ce01aa0ace8f3a1755"

BRANCH = "2021.1"
SRC_URI = "git://github.com/Xilinx/smartcam.git;protocol=https;branch=${BRANCH}"
SRCREV = "f76f59951f36bc89cb35f081accdd5c380a4ca9b"

inherit cmake

# Dependency and runtime dependency
# DEPENDS specifies build-time dependencies
DEPENDS = "ivas-accel-libs glog gstreamer1.0-rtsp-server opencv"
#RDEPENDS specifies run-time dependencies
RDEPENDS_${PN} = " \
    gst-perf \
    gstreamer1.0-omx \
    gstreamer1.0-plugins-good-multifile \
    gstreamer1.0-plugins-good-rtpmanager \
    gstreamer1.0-plugins-good-udp \
    gstreamer1.0-plugins-good-video4linux2 \
    gstreamer1.0-python \
    gstreamer1.0-rtsp-server \
    ivas-accel-libs \
    ivas-gst \
    ivas-utils \
    libdrm \
    libdrm-tests \
    libdrm-kms\
    v4l-utils \
    alsa-utils \
    python3-core \
    glog \
    "

SOMAPP_INSTALL_PATH = "/"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release -DCMAKE_SYSROOT=${STAGING_DIR_HOST} -DCMAKE_INSTALL_PREFIX=${SOMAPP_INSTALL_PATH} "

S = "${WORKDIR}/git"

FILES_${PN} += " \
    /opt/xilinx \
    "

