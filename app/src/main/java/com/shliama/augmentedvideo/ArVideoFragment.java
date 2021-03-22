package com.shliama.augmentedvideo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

class ArVideoFragment1 extends ArFragment {
    private static final String TAG = ArVideoFragment1.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private ExternalTexture externalTexture;
    private ModelRenderable videoRenderable;
    private VideoAnchorNode videoAnchorNode;
    private final String TEST_VIDEO_1 = "test_video_1.mp4";
    private final String TEST_VIDEO_2 = "test_video_2.mp4";
    private final String TEST_VIDEO_3 = "test_video_3.mp4";
    private final String TEST_IMAGE_1 = "test_image_1.jpg";
    private final String TEST_IMAGE_2 = "test_image_2.jpg";
    private final String TEST_IMAGE_3 = "test_image_3.jpg";

    private AugmentedImage actieAugmentedImage = null;
    @Override
    public void onCreate(Bundle savedState){
        super.onCreate(savedState);
        this.mediaPlayer = new MediaPlayer();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Intrinsics.checkParameterIsNotNull(inflater, "inflater");
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.getPlaneDiscoveryController().hide();
        this.getPlaneDiscoveryController().setInstructionView(null);
        this.getArSceneView().getPlaneRenderer().setEnabled(false);
        this.getArSceneView().setLightEstimationEnabled(false);
        this.initializeSession();
//        this.createArScene();
        return view;
    }
    @Override
    public Config getSessionConfiguration(Session session){
        Config config = new Config(session);
        config.setFocusMode(Config.FocusMode.AUTO);
        config.setLightEstimationMode(Config.LightEstimationMode.DISABLED);
        setupAugmentedImageDatabase(config, session);
        return super.getSessionConfiguration(session);
    }
    private Bitmap loadAugmentedImageBitmap(String imageName){
        try (InputStream is = requireContext().getAssets().open(imageName)) {
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e(TAG, "IO exception loading augmented image bitmap.", e);
        }
        return null;
    }
    private void setupAugmentedImageDatabase(Config config, Session session){
            AugmentedImageDatabase augmentedImageDatabase;
            augmentedImageDatabase = new AugmentedImageDatabase(session);
            augmentedImageDatabase.addImage(TEST_VIDEO_1, loadAugmentedImageBitmap(TEST_IMAGE_1));
            augmentedImageDatabase.addImage(TEST_VIDEO_2, loadAugmentedImageBitmap(TEST_IMAGE_2));
            augmentedImageDatabase.addImage(TEST_VIDEO_3, loadAugmentedImageBitmap(TEST_IMAGE_3));
            config.setAugmentedImageDatabase(augmentedImageDatabase);
    }
    private void createArScene(){
        ExternalTexture externalTexture = new ExternalTexture();
        MediaPlayer var10000 = this.mediaPlayer;
        var10000.setSurface(externalTexture.getSurface());
        ModelRenderable.builder()
                .setSource(requireContext(), R.raw.augmented_video_model)
                .build()
                .thenAccept((Consumer)(new Consumer() {
                    public void accept(Object externalTexture) {
                        this.accept((ModelRenderable)externalTexture);
                    }

                    public final void accept(ModelRenderable renderable) {
                        ArVideoFragment1 var10000 = ArVideoFragment1.this;
                        Intrinsics.checkExpressionValueIsNotNull(renderable, "renderable");
                        var10000.videoRenderable = renderable;
                        renderable.setShadowCaster(false);
                        renderable.setShadowReceiver(false);
                        renderable.getMaterial().setExternalTexture("videoTexture", externalTexture);
                    }
                }));
        VideoAnchorNode videoAnchorNode =new VideoAnchorNode();
        ArSceneView arSceneView = this.getArSceneView();
        videoAnchorNode.setParent(arSceneView.getScene());
    }
    public void onUpdate(FrameTime framtime){
        ArSceneView arSceneView = this.getArSceneView();
        Frame frame = arSceneView.getArFrame();
        Collection <AugmentedImage> updateAugmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);
        Iterable filter = (Iterable)updateAugmentedImages;
        Iterator object = filter.iterator();
        Collection destination = (Collection)(new ArrayList());
        for(AugmentedImage img : updateAugmentedImages) {
            if (img.getTrackingMethod() != AugmentedImage.TrackingMethod.FULL_TRACKING) {
                if (isArVideoPlaying() && img.getIndex() == actieAugmentedImage.getIndex()) {
                    pauseArVideo();
                }
            }
        }
        while(object.hasNext()){
            Object element = object.next();
            AugmentedImage it = (AugmentedImage)element;
            if (it.getTrackingMethod() == AugmentedImage.TrackingMethod.FULL_TRACKING){
                destination.add(element);
                if (!isArVideoPlaying()){
                    resumeArVideo();
                }
            }
        }
        List fullTrackingImages = (List) destination;
        actieAugmentedImage = (AugmentedImage)CollectionsKt.firstOrNull(fullTrackingImages);

    }
    private boolean isArVideoPlaying(){
        return mediaPlayer.isPlaying();
    }
    private void pauseArVideo(){
        videoAnchorNode.setRenderable(null);
        mediaPlayer.pause();
    }
    private void resumeArVideo(){
        mediaPlayer.start();
//        fadeInVideo();
    }

}
