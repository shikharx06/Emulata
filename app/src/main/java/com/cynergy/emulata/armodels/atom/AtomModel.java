package com.cynergy.emulata.armodels.atom;

import android.content.Context;
import android.net.Uri;

import com.cynergy.emulata.armodels.EmuModel;
import com.cynergy.emulata.armodels.solarsystem.DemoUtils;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AtomModel extends EmuModel {
    private ModelRenderable modelRenderable;

    public Context context;

    // True once scene is loaded
    public boolean hasFinishedLoading = false;

    // True once the scene has been placed.
    public boolean hasPlacedScene = false;

    public AtomModel(Context context, String modelInfo) {
        super(context, modelInfo);

        CompletableFuture<ModelRenderable> bohrStage =
                ModelRenderable.builder().setSource(context, Uri.parse("Thompson.sfb")).build();

        CompletableFuture.allOf(
                bohrStage
        ).handle(
                (notUsed, throwable) -> {
                    if (throwable != null) {
                        DemoUtils.displayError(context, "Unable to load renderable", throwable);
                        return null;
                    }

                    try {
                        modelRenderable = bohrStage.get();

                        // Everything finished loading successfully.
                        hasFinishedLoading = true;
                    }  catch (InterruptedException | ExecutionException ex) {
                        DemoUtils.displayError(context, "Unable to load renderable", ex);
                    }

                    return null;
                }
        );
    }

    public Node createModel(){
        return createAtomModel();
    }

    public Node createAtomModel() {
        Node base = new Node();

        Node atom = new Node();
        atom.setParent(base);
        atom.setLocalPosition(new Vector3(0.0f, 0.5f, 0.0f));

        Node bohrVisual = new Node();
        bohrVisual.setParent(atom);
        bohrVisual.setRenderable(modelRenderable);
        bohrVisual.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));

        return base;
    }
}
