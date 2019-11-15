#if defined(YI_ANDROID)
#include "GeoLocationModule.h"

#include <logging/YiLogger.h>
#include <youireact/YiReactNativeView.h>

#include <jni.h>

#define LOG_TAG "GeoLocationModule"

using namespace yi::react;

extern JavaVM *cachedJVM;
extern jobject cachedActivity;

extern "C"
{
  JNIEXPORT void JNICALL Java_tv_youi_app_AppActivity_nativeUpdatedGPSCoordinates(JNIEnv *env, jobject thiz, jdouble latitude, jdouble longitude, jdouble altitude);
}

void GeoLocationModule::StartObserving()
{
    UpdatedGPSCoordinates.Connect(*this, &GeoLocationModule::OnUpdatedGPSCoordinates);
}

void GeoLocationModule::StopObserving()
{
    UpdatedGPSCoordinates.DisconnectFromAllSignals();
}

YI_RN_DEFINE_EXPORT_METHOD(GeoLocationModule, get)(Callback successCallback, Callback failedCallback)
{
     JNIEnv *pEnv = NULL;

     cachedJVM->GetEnv(reinterpret_cast<void **>(&pEnv), JNI_VERSION_1_6);

     if (pEnv)
     {
         jclass localDeviceClass = pEnv->FindClass("tv/youi/app/AppActivity");
         jclass _class = (jclass) pEnv->NewGlobalRef(localDeviceClass);

         if (_class)
         {
             jmethodID _get = pEnv->GetMethodID(_class, "_get", "()[D");

             if (_get)
             {
                 auto location = (jdoubleArray) pEnv->CallObjectMethod(cachedActivity, _get);

                 if (location)
                 {
                     jsize size = pEnv->GetArrayLength(location);

                     std::vector<double> input(size);

                     pEnv->GetDoubleArrayRegion(location, 0, size, &input[0]);

                     folly::dynamic locationInfo = folly::dynamic::object;

                     if (size == 3)  // Expect 3 items in this array; lat, long, and alt.
                     {
                         locationInfo["lat"] = ToDynamic(input[0]);
                         locationInfo["long"] = ToDynamic(input[1]);
                         locationInfo["alt"] = ToDynamic(input[2]);

                         successCallback({ToDynamic(locationInfo)});
                     }
                     else
                     {
                         folly::dynamic errorInfo = folly::dynamic::object;

                         errorInfo["message"] = ToDynamic("Location Services currently not available.");

                         failedCallback({ToDynamic(errorInfo)});
                     }
                 }
                 else
                 {
                     folly::dynamic errorInfo = folly::dynamic::object;

                     errorInfo["message"] = ToDynamic("Location Services currently not available.");

                     failedCallback({ToDynamic(errorInfo)});
                 }
             }
         }
     }
}

JNIEXPORT void JNICALL Java_tv_youi_app_AppActivity_nativeUpdatedGPSCoordinates(JNIEnv *env, jobject, jdouble latitude, jdouble longitude, jdouble altitude)
{
    YI_UNUSED(env);

    GeoLocationModule::UpdatedGPSCoordinates.Emit(latitude, longitude, altitude);
}
#endif
