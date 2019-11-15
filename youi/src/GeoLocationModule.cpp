#include "GeoLocationModule.h"

#include <logging/YiLogger.h>
#include <youireact/YiReactNativeView.h>

#define LOG_TAG "GeoLocationModule"

using namespace yi::react;

CYISignal<double, double, double> GeoLocationModule::UpdatedGPSCoordinates;

YI_RN_INSTANTIATE_MODULE(GeoLocationModule, EventEmitterModule);

#if !defined(YI_ANDROID) && !defined(YI_IOS) && !defined(YI_TVOS)
YI_RN_DEFINE_EXPORT_METHOD(GeoLocationModule, get)(Callback successCallback, Callback failedCallback)
{
    folly::dynamic errorInfo = folly::dynamic::object;
    
    errorInfo["message"] = ToDynamic( "Location Services currently not enabled." );
    
    failedCallback({ ToDynamic(errorInfo) });
}

void GeoLocationModule::StartObserving()
{
    YI_LOGD(LOG_TAG, "This does nothing.");
}

void GeoLocationModule::StopObserving()
{
    YI_LOGD(LOG_TAG, "This does nothing.");
}
#endif

GeoLocationModule::GeoLocationModule()
{
    SetSupportedEvents({"change"});
}

GeoLocationModule::~GeoLocationModule()
{
    StopObserving();
}

void GeoLocationModule::OnUpdatedGPSCoordinates(double latitude, double longitude, double altitude)
{
    folly::dynamic location = folly::dynamic::object;
    
    location["lat"] = ToDynamic(latitude);
    location["long"] = ToDynamic(longitude);
    location["alt"] = ToDynamic(altitude);

    EmitEvent("change", folly::dynamic::object("location", location));

    YI_LOGD(LOG_TAG, "OnUpdatedGPSCoordinates");
}
