import React from 'react';
import { StyleSheet, Text, View } from 'react-native';

export default class App extends React.Component {
  render() {
    return (
      <View style={styles.container}>
          <View style={{flex: 1, backgroundColor: 'powderblue'}}>
            <Text>This is a Test</Text>
          </View>
          <View style={{flex: 3, backgroundColor: 'skyblue'}}>
              <Text>To see</Text>
          </View>
          <View style={{flex: 4, backgroundColor: 'steelblue'}}>
          <Text>how shit changes</Text>
      </View>
        <Text>Open up App.js to start working on your app!</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
