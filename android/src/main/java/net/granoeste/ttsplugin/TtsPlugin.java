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

package net.granoeste.ttsplugin;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import java.util.Locale;
import java.util.UUID;

/**
 * TtsPlugin
 */
public class TtsPlugin implements MethodCallHandler {
    private static final String TAG = TtsPlugin.class.getSimpleName();

    private final Activity activity;
    private TextToSpeech tts;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel methodChannel = new MethodChannel(registrar.messenger(), "net.granoeste.flutter.plugin/tts_plugin");

        TtsPlugin ttsPlugin = new TtsPlugin(registrar.activity(), methodChannel);

        methodChannel.setMethodCallHandler(ttsPlugin);
    }

    private TtsPlugin(Activity activity, MethodChannel channel) {
        this.activity = activity;

        initTextToSpeech();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initTextToSpeech() {

        // TTS
        tts = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int listenerResult = tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Log.d(TAG, "progress on Start " + utteranceId);
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Log.d(TAG, "progress on Done " + utteranceId);
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.d(TAG, "progress on Error " + utteranceId);
                        }
                    });
                    if (listenerResult != TextToSpeech.SUCCESS) {
                        Log.e(TAG, "failed to add utterance progress listener");
                    }
                    for (Locale locale : tts.getAvailableLanguages()) {
                        Log.d(TAG, "availableLanguage:" + locale.toString());
                    }
                    int languageResult = tts.setLanguage(Locale.ENGLISH);
                    if (languageResult == TextToSpeech.LANG_MISSING_DATA
                            || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(TAG, "lang not supported" + Locale.ENGLISH);
                    }
                }
            }
        });
    }


    @SuppressLint("NewApi")
    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("speech")) {
            String speechText = call.arguments.toString();
            speechText(speechText);
            result.success(true);
        } else if (call.method.equals("stop")) {
            stopSpeech();
            result.success(true);
        } else if (call.method.equals("language")) {
            String language = call.arguments.toString();
            Log.d(TAG, "language:" + language);
            int languageResult = tts.setLanguage( new Locale(language));
            if (languageResult == TextToSpeech.LANG_MISSING_DATA
                    || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "lang not supported" + language);
                result.error("404","lang not supported", null);
            }

        } else {
            result.notImplemented();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void speechText(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (tts.isSpeaking()) {
                tts.stop();
            }
            String utteranceId = UUID.randomUUID().toString();
            Log.d(TAG, "speak:" + utteranceId);

            int queuingResult = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
            if (queuingResult == TextToSpeech.ERROR) {
                Log.d(TAG, "enqueue is error " + utteranceId);
            } else if (queuingResult == TextToSpeech.SUCCESS) {
                // NOP
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void stopSpeech() {
        if (tts.isSpeaking()) {
            tts.stop();
        }
    }

}
