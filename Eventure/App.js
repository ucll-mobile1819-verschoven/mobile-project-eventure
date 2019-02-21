import React, { Component } from 'react';
import { Alert, AppRegistry, StyleSheet, View, Text, TextInput, Button } from 'react-native';

export default class JustifyContentBasics extends Component {

    constructor(props){
        super(props);
        this.state = {text: ''};
    }
    render() {
        return (
            // Try setting `justifyContent` to `center`.
            // Try setting `flexDirection` to `row`.
            <View style={styles.main}>
                <View style={styles.title}>
                    <Text style={{
                        flex:3
                    }}>
                        Eventure
                    </Text>
                    <Button
                        style={{
                            flex:1,
                        }}
                        onPress={()=>{
                            Alert.alert('Menu Button')
                        }}
                    title ="menu"/>
                </View>
                <View style={styles.list}>

                </View>
                <View style={styles.button}>
                    <Button
                        onPress={()=>{
                            Alert.alert('add Button')
                        }}
                        title="New Event"/>
                </View>
            </View>


        );
    }
};

const styles = StyleSheet.create({
    main: {
        flex: 1,
        backgroundColor: 'skyblue',
        flexDirection: 'column'
    },

    title: {
        flex: 1,
        backgroundColor: 'steelblue',
        flexDirection:'row',
    },

    list: {
        flex:4
    },

    button: {
        flex: 1
    }
})

// skip this line if using Create React Native App
AppRegistry.registerComponent('AwesomeProject', () => JustifyContentBasics);
