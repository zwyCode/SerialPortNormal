LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

TARGET_PLATFORM := android-19
LOCAL_MODULE    := serial_port
LOCAL_SRC_FILES := serial_port.cpp
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)