# written by wsq

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := malata_base android-support-v13 greendao

LOCAL_SRC_FILES    := $(call all-java-files-under, src)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res \
    packages/apps/malata_base/res
#it must be added here,the reason is unknown till now
LOCAL_AAPT_FLAGS := \
	--auto-add-overlay \
	--extra-packages com.malata.base
LOCAL_SDK_VERSION  := current

LOCAL_PACKAGE_NAME := HeartRate

LOCAL_PROGUARD_FLAG_FILES := proguard-project.txt

include $(BUILD_PACKAGE)

######################################
#Cause the greendao is defined in the MyLauncher app
#include $(CLEAR_VARS)

#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := greendao:libs/greendao-1.3.0-beta-1.jar

#include $(BUILD_MULTI_PREBUILT)