import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'dart:typed_data';
import 'dart:async';
import './element_response.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {

  static const platform = const MethodChannel('samples.flutter.dev/battery');

  String _batteryLevel;
  Uint8List _bytesData;



  Future<void> _getBatteryLevel() async {
    try {
//      _bytesData = await platform.invokeMethod('invokeFMCheck');
    final String d = await platform.invokeMethod('invokeFMCheck');
   ElementResponse elementResponse = ElementResponse.fromRawJson(d);

      _batteryLevel = elementResponse.message;
      _bytesData = elementResponse.imageByte;


      setState(() {});

    } on PlatformException catch (e) {
        print(e);
    }


  }

  @override
  Widget build(BuildContext context) {

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
          _batteryLevel != null ? Text(_batteryLevel): Container(),
            _bytesData != null ? Image.memory(_bytesData): Container()
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _getBatteryLevel,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
