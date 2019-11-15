#if defined(YI_IOS) || defined(YI_TVOS)

#include "GeoLocationModule.h"

#import "AppDelegate.h"

#include <logging/YiLoggerHelper.h>

using namespace yi::react;

@interface AppDelegate () <CLLocationManagerDelegate>
@end

@implementation AppDelegate

CLLocationManager *m_pLocationManager;

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(nonnull NSArray<CLLocation *> *)locations
{
    YI_UNUSED(manager);

    CLLocation* location = [locations lastObject];

    GeoLocationModule::UpdatedGPSCoordinates.Emit(location.coordinate.latitude,
                                                  location.coordinate.longitude,
                                                  location.altitude);
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(nonnull NSError *)error
{
    YI_UNUSED(manager);
    YI_UNUSED(error);
}

- (CLLocationManager*)getLocationManager
{
    if(!m_pLocationManager)
    {
        m_pLocationManager = [[CLLocationManager alloc] init];
        [m_pLocationManager setDelegate:self];
        [m_pLocationManager setDesiredAccuracy:kCLLocationAccuracyThreeKilometers];
    }
    
    return m_pLocationManager;
}

@end

#endif
