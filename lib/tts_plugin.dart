/*
 * Copyright (C) 2017 granoeste.net http://granoeste.net/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import 'dart:async';
import 'package:flutter/services.dart';

class TtsPlugin {
  static const MethodChannel _channel = const MethodChannel('net.granoeste.flutter.plugin/tts_plugin');

  static Future speech(String text) => _channel.invokeMethod("speech", text);

  static Future stop() => _channel.invokeMethod("stop");

  static Future language(String language) => _channel.invokeMethod("language", language);

}

