#if defined(YI_IOS) || defined(YI_TVOS)

#import "AppDelegate.h"

#include <logging/YiLoggerHelper.h>

@interface AppDelegate () <CLLocationManagerDelegate>
@end

@implementation AppDelegate

CLLocationManager *m_pLocationManager;

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(nonnull NSArray<CLLocation *> *)locations
{
    YI_UNUSED(manager);
    YI_UNUSED(locations);
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
