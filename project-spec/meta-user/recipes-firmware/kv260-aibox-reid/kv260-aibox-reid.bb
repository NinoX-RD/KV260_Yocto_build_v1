#
# This file is the user-firmware recipe.
#

SUMMARY = "Simple user-firmware to use fpgamanager class"
SECTION = "PETALINUX/apps"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit fpgamanager_custom
FPGA_MNGR_RECONFIG_ENABLE = "1"

SRC_URI = "file://kv260-aibox-reid.bit \
           file://kv260-aibox-reid.dtsi \
           file://kv260-aibox-reid.xclbin \
           file://shell.json \
           "

S = "${WORKDIR}"
