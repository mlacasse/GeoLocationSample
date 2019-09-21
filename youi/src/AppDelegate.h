#if defined(YI_IOS) || defined(YI_TVOS)

#ifndef _APP_DELEGATE_H
#define _APP_DELEGATE_H

#import <UIKit/UIKit.h>

#import <Apple/youilabsAppDelegate.h>
#import <CoreLocation/CoreLocation.h>

@interface AppDelegate : youilabsAppDelegate
-(CLLocationManager*)getLocationManager;
@end

#endif

#endif
