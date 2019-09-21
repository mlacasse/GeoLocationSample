#include "GeoLocationModule.h"

#include <logging/YiLogger.h>
#include <youireact/YiReactNativeView.h>

#define LOG_TAG "GeoLocationModule"

using namespace yi::react;

YI_RN_INSTANTIATE_MODULE(GeoLocationModule);

#if !defined(YI_ANDROID) && !defined(YI_IOS) && !defined(YI_TVOS)
GeoLocationModule::GeoLocationModule()
{
  YI_LOGE(LOG_TAG, "React Native GeoLocationModule is not supported on this platform.");
}

YI_RN_DEFINE_EXPORT_METHOD(GeoLocationModule, get)(Callback successCallback, Callback failedCallback)
{
    folly::dynamic locationInfo = folly::dynamic::object;

    locationInfo["lat"] = ToDynamic(0);
    locationInfo["long"] = ToDynamic(0);
    locationInfo["alt"] = ToDynamic(0);

    successCallback({ ToDynamic(locationInfo) });
}
#endif
