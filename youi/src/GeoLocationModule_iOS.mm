#if defined(YI_IOS) || defined(YI_TVOS)
#include "GeoLocationModule.h"

#include <logging/YiLogger.h>
#include <youireact/YiReactNativeView.h>

#import <CoreLocation/CoreLocation.h>
#import <UIKit/UIKit.h>

#import "AppDelegate.h"

#define LOG_TAG "GeoLocationModule"

using namespace yi::react;

namespace {
    bool LocationServicesEnabled()
    {
        return [CLLocationManager locationServicesEnabled] &&
               [CLLocationManager authorizationStatus] != kCLAuthorizationStatusDenied &&
               [CLLocationManager authorizationStatus] != kCLAuthorizationStatusRestricted;
    }
}

YI_RN_DEFINE_EXPORT_METHOD(GeoLocationModule, get)(Callback successCallback, Callback failedCallback)
{
    AppDelegate *pApp = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    CLLocationManager *pLocationManager = [pApp getLocationManager];

    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined)
    {
        [pLocationManager requestWhenInUseAuthorization];
    }

    if (LocationServicesEnabled())
    {
        [pLocationManager requestLocation];

        folly::dynamic locationInfo = folly::dynamic::object;

        locationInfo["lat"] = ToDynamic(pLocationManager.location.coordinate.latitude);
        locationInfo["long"] = ToDynamic(pLocationManager.location.coordinate.longitude);
        locationInfo["alt"] = ToDynamic(pLocationManager.location.altitude);

        successCallback({ ToDynamic(locationInfo) });
    }
    else
    {
        folly::dynamic errorInfo = folly::dynamic::object;

        errorInfo["message"] = ToDynamic( "Location Services currently not available." );

        failedCallback({ ToDynamic(errorInfo) });
    }
}

void GeoLocationModule::StartObserving()
{
    UpdatedGPSCoordinates.Connect(*this, &GeoLocationModule::OnUpdatedGPSCoordinates);

    if (LocationServicesEnabled())
    {
        AppDelegate *pApp = (AppDelegate*)[[UIApplication sharedApplication] delegate];
        CLLocationManager *pLocationManager = [pApp getLocationManager];

        if ([CLLocationManager significantLocationChangeMonitoringAvailable])
        {
            [pLocationManager startMonitoringSignificantLocationChanges];

            YI_LOGD(LOG_TAG, "StartObserving");
        }
    }
}

void GeoLocationModule::StopObserving()
{
    if (LocationServicesEnabled())
    {
        AppDelegate *pApp = (AppDelegate*)[[UIApplication sharedApplication] delegate];
        CLLocationManager *pLocationManager = [pApp getLocationManager];

        if ([CLLocationManager significantLocationChangeMonitoringAvailable])
        {
            [pLocationManager stopMonitoringSignificantLocationChanges];

            YI_LOGD(LOG_TAG, "StopObserving");
        }
    }

    UpdatedGPSCoordinates.DisconnectFromAllSignals();
}
#endif
