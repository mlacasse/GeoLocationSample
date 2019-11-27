import React, { PureComponent } from 'react';
import { View, Text, AppState, NativeModules, NativeEventEmitter } from 'react-native';

const { GeoLocation } = NativeModules;

class AppComponent extends PureComponent {
  constructor(props) {
    super(props);

    this.state = {
      location: null,
    };

    // 0 = Landscape
    // 1 = Portrait
    // 2 = Auto
    // 3 = LandscapeRight
    // 4 = LandscapeLeft
    // 5 = PortraitUpright
    // 6 = AutoUpright

    this.locationChangeEvent = new NativeEventEmitter(GeoLocation);

    NativeModules.OrientationLock.setRotationMode(6);
  }

  componentDidMount = () => {
    GeoLocation.get()
      .then(location => this.setState({ location }))
      .catch(() => this.setState({ location: null }));

    this.locationChangeEvent.addListener('locationDidChange', this.handleLocationChange);

    AppState.addEventListener('change', this.handleAppStateChange);
  };

  componentWillUnmount = () => {
    this.locationChangeEvent.removeListener('locationDidChange', this.handleLocationChange);

    AppState.removeEventListener('change', this.handleAppStateChange);
  };

  handleLocationChange = event => {
    const { location } = event;

    if (location) {
      this.setState({ location });
    }
  };

  handleAppStateChange = async newAppState => {
    console.log('handleAppStateChange', newAppState);

    if (newAppState === 'active') {
      this.setState({ location });
    }
  };

  render = () => {
    const { location } = this.state;

    const style = {
      flex: 1,
      flexDirection: 'column',
      justifyContent: 'center',
      alignItems: 'center',
      backgroundColor: location ? 'green' : 'red',
    };

    if (location) {
      const { lat, long, alt } = location;

      return (
        <View style={style}>
          <Text style={{ fontSize: 36, color: 'white', }}>Location Services: {location ? 'ON' : 'OFF'}</Text>
          <Text style={{ fontSize: 28, color: 'white', }}>Latitude: {lat.toFixed(3)}</Text>
          <Text style={{ fontSize: 28, color: 'white', }}>Longitude: {long.toFixed(3)}</Text>
          <Text style={{ fontSize: 28, color: 'white', }}>Altitude: {alt}</Text>
        </View>
      );
    }

    return (
      <View style={style}>
        <Text style={{ fontSize: 72, color: 'white', }}>Nope.</Text>
      </View>
    );
  };
};

export default AppComponent;
