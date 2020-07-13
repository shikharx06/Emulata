package com.cynergy.emulata.armodels.generic;

import android.content.Context;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cynergy.emulata.R;
import com.cynergy.emulata.armodels.EmuModel;
import com.cynergy.emulata.armodels.solarsystem.DemoUtils;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GenericModel extends EmuModel {
    TextToSpeech textToSpeech;
    public GenericModel(Context context, String modelUri, String modelInfo) {
        super(context, modelInfo);

        CompletableFuture<ModelRenderable> modelStage =
                ModelRenderable.builder().setSource(context, Uri.parse(modelUri)).build();

        // Build a renderable from a 2D View.
        CompletableFuture<ViewRenderable> modelTextStage =
                ViewRenderable.builder().setView(context, R.layout.model_text).build();

        CompletableFuture.allOf(
                modelStage,
                modelTextStage
        ).handle(
                (notUsed, throwable) -> {
                    if (throwable != null) {
                        DemoUtils.displayError(context, "Unable to load renderable", throwable);
                        return null;
                    }

                    try {
                        modelRenderable = modelStage.get();
                        infoTextRenderable = modelTextStage.get();
                        hasFinishedLoading = true;
                    }  catch (InterruptedException | ExecutionException ex) {
                        DemoUtils.displayError(context, "Unable to load renderable", ex);
                    }

                    return null;
                }
        );

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.ENGLISH);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(context, "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public Node createModel() {
        Node base = new Node();

        Node node = new Node();
        node.setParent(base);
        node.setLocalPosition(new Vector3(0.0f, 0.5f, 0.0f));

        this.modelVisual = new Node();
        modelVisual.setParent(node);
        modelVisual.setRenderable(modelRenderable);
        modelVisual.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));

        this.infoCard =  new Node();
        infoCard.setParent(node);
        infoCard.setRenderable(infoTextRenderable);
        infoCard.setEnabled(false);
        infoCard.setLocalPosition(new Vector3(0.0f, 0.25f, 0.0f));

        View infoCardView = infoTextRenderable.getView();
        TextView tv_info = infoCardView.findViewById(R.id.modelInfoCard);
        tv_info.setText(this.modelInfo.toString());

        modelVisual.setOnTapListener(
                (hitTestResult, motionEvent) -> {
                    this.infoCard.setEnabled(!this.infoCard.isEnabled());

                    String data = modelInfo;
                    Log.i("TTS", "button clicked: " + data);
                    int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null, "");

                    if (speechStatus == TextToSpeech.ERROR) {
                        Log.e("TTS", "Error in converting Text to Speech!");
                    }
                });

//        ViewRenderable.builder()
//                .setView(context, R.layout.model_text)
//                .build()
//                .thenAccept(
//                        (renderable) -> {
//                            infoCard.setRenderable(renderable);
//                            TextView textView = (TextView) renderable.getView();
//                            textView.setText(modelInfo);
//                        }
//                )
//                .exceptionally(
//                        (throwable) -> {
//                            throw new AssertionError("Could not load plane card view.", throwable);
//                        }
//                );

        return base;
    }
}
