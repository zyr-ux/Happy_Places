#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_happyplaces_activities_AddHappyPlacesActivity_00024Keys_getApi(JNIEnv *env,jobject thiz) {
    return env->NewStringUTF("AIzaSyCiQigeJc-Wq5Clx6EMiESh08_w6VZwmCc"); //Api Key Here
}