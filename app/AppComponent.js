import React, { PureComponent } from 'react';
import { View, Text, AppState, NativeModules } from 'react-native';

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

    NativeModules.OrientationLock.setRotationMode(6);
  }

  componentDidMount = () => {
    GeoLocation.get()
      .then(location => this.setState({ location }))
      .catch(() => this.setState({ location: null }));

    AppState.addEventListener('change', this.handleAppStateChange);
  }

  componentWillUnmount = () => {
    AppState.removeEventListener('change', this.handleAppStateChange);
  }

  handleAppStateChange = newAppState => {
    if (newAppState === 'active') {
      GeoLocation.get()
        .then(location => this.setState({ location }))
        .catch(() => this.setState({ location: null }));
    }
  }

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
      const { lat, long } = location;

      return (
        <View style={style}>
          <Text style={{ fontSize: 36, color: 'white', }}>Location Services: {location ? 'ON' : 'OFF'}</Text>
          <Text style={{ fontSize: 28, color: 'white', }}>Lat: {lat}</Text>
          <Text style={{ fontSize: 28, color: 'white', }}>Long: {long}</Text>
        </View>
      );
    }

    return (
      <View style={style}>
        <Text style={{ fontSize: 72, color: 'white', }}>Nope.</Text>
      </View>
    );
  }
};

export default AppComponent;
