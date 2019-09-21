#ifndef _GEO_LOCATION_MODULE_H_
#define _GEO_LOCATION_MODULE_H_

#include <signal/YiSignalHandler.h>
#include <youireact/NativeModule.h>

namespace yi
{
namespace react
{
class YI_RN_MODULE(GeoLocationModule)
{
public:
    YI_RN_EXPORT_NAME(GeoLocation);
    GeoLocationModule();

    YI_RN_EXPORT_CONSTANT(enabled);
    YI_RN_EXPORT_METHOD(get)(Callback successCallback, Callback failedCallback);
};

} // namespace react
} // namespace yi

#endif
