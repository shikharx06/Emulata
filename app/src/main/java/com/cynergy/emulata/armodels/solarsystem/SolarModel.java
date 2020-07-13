package com.cynergy.emulata.armodels.solarsystem;

import android.content.Context;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.cynergy.emulata.R;
import com.cynergy.emulata.armodels.EmuModel;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SolarModel extends EmuModel {
    private ModelRenderable sunRenderable;
    private ModelRenderable mercuryRenderable;
    private ModelRenderable venusRenderable;
    private ModelRenderable earthRenderable;
    private ModelRenderable lunaRenderable;
    private ModelRenderable marsRenderable;
    private ModelRenderable jupiterRenderable;
    private ModelRenderable saturnRenderable;
    private ModelRenderable uranusRenderable;
    private ModelRenderable neptuneRenderable;
    private ViewRenderable solarControlsRenderable;

    public Context context;

    public final SolarSettings solarSettings = new SolarSettings();

    // True once scene is loaded
//    public boolean hasFinishedLoading = false;

    // True once the scene has been placed.
//    public boolean hasPlacedScene = false;

    // Astronomical units to meters ratio. Used for positioning the planets of the solar system.
    private static final float AU_TO_METERS = 0.5f;

    public SolarModel(Context context) {
        super(context, "");
        // Build all the planet models.
        this.context = context;
        CompletableFuture<ModelRenderable> sunStage =
                ModelRenderable.builder().setSource(context, Uri.parse("Sol.sfb")).build();
        CompletableFuture<ModelRenderable> mercuryStage =
                ModelRenderable.builder().setSource(context, Uri.parse("Mercury.sfb")).build();
        CompletableFuture<ModelRenderable> venusStage =
                ModelRenderable.builder().setSource(context, Uri.parse("Venus.sfb")).build();
        CompletableFuture<ModelRenderable> earthStage =
                ModelRenderable.builder().setSource(context, Uri.parse("Earth.sfb")).build();
        CompletableFuture<ModelRenderable> lunaStage =
                ModelRenderable.builder().setSource(context, Uri.parse("Luna.sfb")).build();
        CompletableFuture<ModelRenderable> marsStage =
                ModelRenderable.builder().setSource(context, Uri.parse("Mars.sfb")).build();
        CompletableFuture<ModelRenderable> jupiterStage =
                ModelRenderable.builder().setSource(context, Uri.parse("Jupiter.sfb")).build();
        CompletableFuture<ModelRenderable> saturnStage =
                ModelRenderable.builder().setSource(context, Uri.parse("Saturn.sfb")).build();
        CompletableFuture<ModelRenderable> uranusStage =
                ModelRenderable.builder().setSource(context, Uri.parse("Uranus.sfb")).build();
        CompletableFuture<ModelRenderable> neptuneStage =
                ModelRenderable.builder().setSource(context, Uri.parse("Neptune.sfb")).build();

        CompletableFuture<ViewRenderable> solarControlsStage =
                ViewRenderable.builder().setView(context, R.layout.solar_controls).build();

        CompletableFuture.allOf(
                sunStage,
                mercuryStage,
                venusStage,
                earthStage,
                lunaStage,
                marsStage,
                jupiterStage,
                saturnStage,
                uranusStage,
                neptuneStage,
                solarControlsStage)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                DemoUtils.displayError(context, "Unable to load renderable", throwable);
                                return null;
                            }

                            try {
                                sunRenderable = sunStage.get();
                                mercuryRenderable = mercuryStage.get();
                                venusRenderable = venusStage.get();
                                earthRenderable = earthStage.get();
                                lunaRenderable = lunaStage.get();
                                marsRenderable = marsStage.get();
                                jupiterRenderable = jupiterStage.get();
                                saturnRenderable = saturnStage.get();
                                uranusRenderable = uranusStage.get();
                                neptuneRenderable = neptuneStage.get();
                                solarControlsRenderable = solarControlsStage.get();

                                // Everything finished loading successfully.
                                hasFinishedLoading = true;

                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(context, "Unable to load renderable", ex);
                            }

                            return null;
                        });
    }


    public Node createSolarSystem() {
        Node base = new Node();

        Node sun = new Node();
        sun.setParent(base);
        sun.setLocalPosition(new Vector3(0.0f, 0.5f, 0.0f));

        Node sunVisual = new Node();
        sunVisual.setParent(sun);
        sunVisual.setRenderable(sunRenderable);
        sunVisual.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));

        Node solarControls = new Node();
        solarControls.setParent(sun);
        solarControls.setRenderable(solarControlsRenderable);
        solarControls.setLocalPosition(new Vector3(0.0f, 0.25f, 0.0f));

        View solarControlsView = solarControlsRenderable.getView();
        SeekBar orbitSpeedBar = solarControlsView.findViewById(R.id.orbitSpeedBar);
        orbitSpeedBar.setProgress((int) (solarSettings.getOrbitSpeedMultiplier() * 10.0f));
        orbitSpeedBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float ratio = (float) progress / (float) orbitSpeedBar.getMax();
                        solarSettings.setOrbitSpeedMultiplier(ratio * 10.0f);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        SeekBar rotationSpeedBar = solarControlsView.findViewById(R.id.rotationSpeedBar);
        rotationSpeedBar.setProgress((int) (solarSettings.getRotationSpeedMultiplier() * 10.0f));
        rotationSpeedBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float ratio = (float) progress / (float) rotationSpeedBar.getMax();
                        solarSettings.setRotationSpeedMultiplier(ratio * 10.0f);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        // Toggle the solar controls on and off by tapping the sun.
        sunVisual.setOnTapListener(
                (hitTestResult, motionEvent) -> solarControls.setEnabled(!solarControls.isEnabled()));

        createPlanet("Mercury", sun, 0.4f, 47f, mercuryRenderable, 0.019f, 0.03f);

        createPlanet("Venus", sun, 0.7f, 35f, venusRenderable, 0.0475f, 2.64f);

        Node earth = createPlanet("Earth", sun, 1.0f, 29f, earthRenderable, 0.05f, 23.4f);

        createPlanet("Moon", earth, 0.15f, 100f, lunaRenderable, 0.018f, 6.68f);

        createPlanet("Mars", sun, 1.5f, 24f, marsRenderable, 0.0265f, 25.19f);

        createPlanet("Jupiter", sun, 2.2f, 13f, jupiterRenderable, 0.16f, 3.13f);

        createPlanet("Saturn", sun, 3.5f, 9f, saturnRenderable, 0.1325f, 26.73f);

        createPlanet("Uranus", sun, 5.2f, 7f, uranusRenderable, 0.1f, 82.23f);

        createPlanet("Neptune", sun, 6.1f, 5f, neptuneRenderable, 0.074f, 28.32f);

        return base;
    }

    public Node createPlanet(
            String name,
            Node parent,
            float auFromParent,
            float orbitDegreesPerSecond,
            ModelRenderable renderable,
            float planetScale,
            float axisTilt) {
        // Orbit is a rotating node with no renderable positioned at the sun.
        // The planet is positioned relative to the orbit so that it appears to rotate around the sun.
        // This is done instead of making the sun rotate so each planet can orbit at its own speed.
        RotatingNode orbit = new RotatingNode(solarSettings, true, false, 0);
        orbit.setDegreesPerSecond(orbitDegreesPerSecond);
        orbit.setParent(parent);

        // Create the planet and position it relative to the sun.
        Planet planet =
                new Planet(
                        context, name, planetScale, orbitDegreesPerSecond, axisTilt, renderable, solarSettings);
        planet.setParent(orbit);
        planet.setLocalPosition(new Vector3(auFromParent * AU_TO_METERS, 0.0f, 0.0f));

        return planet;
    }

    @Override
    public Node createModel() {
        return createSolarSystem();
    }
}
