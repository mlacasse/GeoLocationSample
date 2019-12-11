#import "LocationManagerDelegate.h"

#include "GeoLocationModule.h"

#include <logging/YiLogger.h>

#define LOG_TAG "LocationManagerDelegate"

using namespace yi::react;

@implementation LocationManagerDelegate

#pragma mark - Callbacks

// Note: This callback is required to be implemented when -requestLocation API is used.
- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray<CLLocation *> *)locations {
    YI_LOGD(LOG_TAG, "Location Services location updated");
    
    CLLocation* location = [locations lastObject];

    GeoLocationModule::UpdatedGPSCoordinates.Emit(location.coordinate.latitude,
                                                  location.coordinate.longitude,
                                                  location.altitude);
}

// Note: This callback is required to be implemented when -requestLocation API is used.
- (void)locationManager:(CLLocationManager *)manager didFailWithError:(nonnull NSError *)error
{
    YI_LOGD(LOG_TAG, "Location Services failed with error");
}

@end
