import 'dart:async';
import 'package:flutter/material.dart';
import 'package:tts_plugin/tts_plugin.dart';

void main() {
  runApp(new MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {

  final List LANGUAGES = [
    ['English', 'en'],
    ['Español', 'es'],
    ['Fançais', 'fr'],
    ['Italiano', 'it'],
    ['日本語', 'ja'],
  ];

  List language = [];

  @override
  initState() {
    super.initState();
    language = LANGUAGES[0];
  }

  Future _textToSpeech() async {
    final res = await TtsPlugin.speech(_controller.text);
  }

  Future _language(String language) async {
    final res = await TtsPlugin.language(language);
  }

  final TextEditingController _controller = new TextEditingController();

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: new Text('TTS Plugin example app'),
        ),
        body: new Padding(
          padding: const EdgeInsets.all(24.0),
          child: new Column(
            mainAxisAlignment: MainAxisAlignment.start,
            children: <Widget>[
              new ListTile(
                title: const Text('Language:'),
                trailing: new DropdownButton<List>(
                  value: LANGUAGES[0],
                  onChanged: (List newValue) {
                    setState(() {
                      language = newValue;
                      _language(language[1]);
                    });
                  },
                  items: LANGUAGES.map((List language) {
                    return new DropdownMenuItem<List>(
                      value: language,
                      child: new Text(language[0]),
                    );
                  }).toList(),
                ),
              ),
              const SizedBox(
                height: 16.0,
              ),
              new TextFormField(
                controller: _controller,
                decoration: const InputDecoration(
                  hintText: 'What do you speech?',
                  labelText: 'Text To Speech:',
                ),
              ),
            ],
          ),
        ),
        floatingActionButton: new FloatingActionButton(
          tooltip: 'Speech', // used by assistive technologies
          child: new Icon(Icons.play_arrow),
          onPressed: _textToSpeech,
        ),
      ),
    );
  }
}
