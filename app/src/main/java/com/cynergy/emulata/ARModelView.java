package com.cynergy.emulata;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cynergy.emulata.armodels.EmuModel;
import com.cynergy.emulata.armodels.atom.AtomModel;
import com.cynergy.emulata.armodels.generic.GenericModel;
import com.cynergy.emulata.armodels.solarsystem.DemoUtils;
import com.cynergy.emulata.armodels.solarsystem.SolarModel;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ARModelView extends AppCompatActivity {

    private static final String JSON_URL = "https://simplifiedcoding.net/demos/view-flipper/heroes.php";

    private static final int RC_PERMISSIONS = 0x123;
    private boolean installRequested;

    private GestureDetector gestureDetector;
    private Snackbar loadingMessageSnackbar = null;

    private ArSceneView arSceneView;

    public EmuModel arModel;

    public String modelText = "";

    public String currentModel;

    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!DemoUtils.checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        Intent intent = getIntent();
        String modelToView = intent.getStringExtra("MODEL");

        if (modelToView == null) {
            return;
        }

        currentModel = modelToView;



        setContentView(R.layout.activity_armodel_view);
        arSceneView = findViewById(R.id.ar_scene_view);

        String modelReq = "atom";
        String subReq = "physics";

        if (currentModel.equals("Solar System")) {
            modelReq = "solar_system";
            subReq = "physics";
        } else if (currentModel.equals("Bohr Model")) {
            modelReq = "bohr_model";
            subReq = "chemistry";
        } else if (currentModel.equals("Rutherford Model")) {
            modelReq = "rutherford_model";
            subReq = "chemistry";
        } else if (currentModel.equals("Thompson Model")) {
            modelReq = "thompson_model";
            subReq = "chemistry";
        } else if (currentModel.equals("Plant Cell")) {
            modelReq = "plant_cell";
            subReq = "biology";
        } else if (currentModel.equals("Mitochondria")) {
            modelReq = "mitochondria";
            subReq = "biology";
        } else if (currentModel.equals("Methane")) {
            modelReq = "methane_structure";
            subReq = "chemistry";
        } else if (currentModel.equals("Ethane")) {
            modelReq = "ethane_structure";
            subReq = "chemistry";
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.43.167:8000/info?email=two@gmail.com&topic="+modelReq+"&subject="+subReq;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("DEBUG", response.toString());

                        try {
                            modelText = response.getString("info");
                             initModel(response.getString("info"));
                        } catch (Exception e) {
                            modelText = "";
                            initModel("");
                        }

//                        String data = "Hi i am Google";
//                        Log.i("TTS", "button clicked: " + data);
//                        int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null, "");
//
//                        if (speechStatus == TextToSpeech.ERROR) {
//                            Log.e("TTS", "Error in converting Text to Speech!");
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DEBUG", "That didn't work!"+error.toString());
                initModel(error.toString());
            }
        });

        queue.add(stringRequest);

//        initModel("");
    }

    public void initModel(String _text) {

        if (currentModel.equals("Solar System")) {
            arModel = new SolarModel(this);
        } else if (currentModel.equals("Bohr Model")) {
            arModel = new GenericModel(this, "Bohr.sfb", _text);
        } else if (currentModel.equals("Rutherford Model")) {
            arModel = new GenericModel(this, "Rutherford.sfb", _text);
        } else if (currentModel.equals("Thompson Model")) {
            arModel = new GenericModel(this, "Thompson.sfb", _text);
        } else if (currentModel.equals("Plant Cell")) {
            arModel = new GenericModel(this, "PlantCell.sfb", _text);
        } else if (currentModel.equals("Mitochondria")) {
            arModel = new GenericModel(this, "Mitochondria.sfb", _text);
        } else if (currentModel.equals("Methane")) {
            arModel = new GenericModel(this, "Methane.sfb", _text);
        } else if (currentModel.equals("Ethane")) {
            arModel = new GenericModel(this, "Ethane.sfb", _text);
        }

        // TODO: Give your model here
//        arModel = new GenericModel(this, "Ethane.sfb", _text);

        // Set up a tap gesture detector.
        gestureDetector =
                new GestureDetector(
                        this,
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                onSingleTap(e);
                                return true;
                            }

                            @Override
                            public boolean onDown(MotionEvent e) {
                                return true;
                            }
                        });

        // Set a touch listener on the Scene to listen for taps.
        arSceneView
                .getScene()
                .setOnTouchListener(
                        (HitTestResult hitTestResult, MotionEvent event) -> {
                            if (!arModel.hasPlacedScene) {
                                return gestureDetector.onTouchEvent(event);
                            }
                            return false;
                        });

        // Set an update listener on the Scene that will hide the loading message once a Plane is
        // detected.
        arSceneView
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            if (loadingMessageSnackbar == null) {
                                return;
                            }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                if (plane.getTrackingState() == TrackingState.TRACKING) {
                                    hideLoadingMessage();
                                }
                            }
                        });

        DemoUtils.requestCameraPermission(this, RC_PERMISSIONS);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (arSceneView == null) {
            return;
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = DemoUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = DemoUtils.hasCameraPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (arSceneView != null) {
            arSceneView.pause();
        }

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (arSceneView != null) {
            arSceneView.destroy();
        }

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!DemoUtils.hasCameraPermission(this)) {
            if (!DemoUtils.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                DemoUtils.launchPermissionSettings(this);
            } else {
                Toast.makeText(
                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void onSingleTap(MotionEvent tap) {
        if (!arModel.hasFinishedLoading) {
            // We can't do anything yet.
            return;
        }

        Frame frame = arSceneView.getArFrame();
        if (frame != null) {
            if (!arModel.hasPlacedScene && tryPlaceScene(tap, frame)) {
                arModel.hasPlacedScene = true;
            }
        }
    }


    private boolean tryPlaceScene(MotionEvent tap, Frame frame) {
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    // Create the Anchor.
                    Anchor anchor = hit.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arSceneView.getScene());
//                    Node solarSystem = arModel.createSolarSystem();
                    Node solarSystem = arModel.createModel();
                    anchorNode.addChild(solarSystem);
                    return true;
                }
            }
        }

        return false;
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ARModelView.this.findViewById(android.R.id.content),
                        R.string.plane_finding,
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }
}
