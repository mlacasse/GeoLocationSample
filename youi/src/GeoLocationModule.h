#ifndef _GEO_LOCATION_MODULE_H_
#define _GEO_LOCATION_MODULE_H_

#include <signal/YiSignalHandler.h>
#include <youireact/NativeModule.h>
#include <youireact/modules/EventEmitter.h>

namespace yi
{
namespace react
{
class YI_RN_MODULE(GeoLocationModule, EventEmitterModule)
{
public:
    static CYISignal<double, double, double> UpdatedGPSCoordinates;

    YI_RN_EXPORT_NAME(GeoLocation);

    GeoLocationModule();
    virtual ~GeoLocationModule();

    YI_RN_EXPORT_METHOD(get)(Callback successCallback, Callback failedCallback);
private:

    virtual void StartObserving() override;
    virtual void StopObserving() override;

    void OnUpdatedGPSCoordinates(double latitude, double longitude, double altitude);
};

} // namespace react
} // namespace yi

#endif
