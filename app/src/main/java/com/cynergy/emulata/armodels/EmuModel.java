package com.cynergy.emulata.armodels;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.cynergy.emulata.R;
import com.cynergy.emulata.armodels.solarsystem.RotatingNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

public abstract class EmuModel {
    public ModelRenderable modelRenderable;

    public ViewRenderable infoTextRenderable;

    public Context context;

    // True once scene is loaded
    public boolean hasFinishedLoading = false;

    // True once the scene has been placed.
    public boolean hasPlacedScene = false;

    public Node infoCard;

    public String modelInfo;

    public Node modelVisual;

    public EmuModel(Context context, String infoText) {
        this.context = context;
        this.modelInfo = infoText;
    }

    public abstract Node createModel();

}
