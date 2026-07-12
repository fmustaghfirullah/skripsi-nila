package com.faisal.bangunruang.ml;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.faisal.bangunruang.utils.ImageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.TensorInfo;

public class ShapeClassifier {

    private static final String TAG = "ShapeClassifier";
    private static final String MODEL_FILE = "model_6juli_6kelas.onnx";
    private static final int INPUT_SIZE = 224;

    private static final String[] LABELS_6 = {"balok", "bola", "kerucut", "kubus", "limas", "tabung"};
    private static final String[] LABELS_5 = {"balok", "bola", "kerucut", "kubus", "tabung"};

    private OrtEnvironment env;
    private OrtSession session;
    private boolean isInitialized = false;
    private boolean isNchw = false;
    private String[] activeLabels = LABELS_6;

    public ShapeClassifier(Context context) {
        try {
            env = OrtEnvironment.getEnvironment();
            byte[] modelBytes = loadModelFromAssets(context);
            session = env.createSession(modelBytes);
            detectModelConfig();
            isInitialized = true;
        } catch (OrtException | IOException e) {
            Log.e(TAG, "Failed to load model", e);
            isInitialized = false;
        }
    }

    private void detectModelConfig() {
        try {
            Map<String, NodeInfo> inputInfo = session.getInputInfo();
            for (NodeInfo node : inputInfo.values()) {
                TensorInfo ti = (TensorInfo) node.getInfo();
                long[] shape = ti.getShape();
                Log.d(TAG, "Model input shape: " + Arrays.toString(shape));
                if (shape.length == 4 && shape[1] == 3 && shape[2] >= 32) {
                    isNchw = true;
                }
            }

            Map<String, NodeInfo> outputInfo = session.getOutputInfo();
            for (NodeInfo node : outputInfo.values()) {
                TensorInfo ti = (TensorInfo) node.getInfo();
                long[] shape = ti.getShape();
                Log.d(TAG, "Model output shape: " + Arrays.toString(shape));
                if (shape.length == 2 && shape[1] > 0) {
                    int numClasses = (int) shape[1];
                    activeLabels = (numClasses <= 5) ? LABELS_5 : LABELS_6;
                    Log.d(TAG, "Detected " + numClasses + " output classes");
                }
            }
        } catch (OrtException | ClassCastException e) {
            Log.w(TAG, "Auto-detect failed, using 6-class NHWC default", e);
        }
        Log.d(TAG, "Config: NCHW=" + isNchw + ", labels=" + Arrays.toString(activeLabels));
    }

    private byte[] loadModelFromAssets(Context context) throws IOException {
        InputStream is = context.getAssets().open(MODEL_FILE);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();
        return buffer;
    }

    public ClassificationResult classify(Bitmap bitmap) {
        if (!isInitialized) {
            return new ClassificationResult("unknown", 0f);
        }

        try {
            FloatBuffer floatBuffer;
            long[] shape;

            if (isNchw) {
                floatBuffer = ImageUtils.preprocessForEdgeDetectionNCHW(
                        bitmap, INPUT_SIZE, INPUT_SIZE);
                shape = new long[]{1, 3, INPUT_SIZE, INPUT_SIZE};
            } else {
                floatBuffer = ImageUtils.preprocessForEdgeDetection(
                        bitmap, INPUT_SIZE, INPUT_SIZE);
                shape = new long[]{1, INPUT_SIZE, INPUT_SIZE, 3};
            }

            OnnxTensor inputTensor = OnnxTensor.createTensor(env, floatBuffer, shape);
            Map<String, OnnxTensor> inputs = Collections.singletonMap(
                    session.getInputNames().iterator().next(), inputTensor);

            OrtSession.Result result = session.run(inputs);

            float[][] output = (float[][]) result.get(0).getValue();
            float[] probabilities = output[0];

            int maxIndex = 0;
            float maxProb = probabilities[0];
            for (int i = 1; i < probabilities.length; i++) {
                if (probabilities[i] > maxProb) {
                    maxProb = probabilities[i];
                    maxIndex = i;
                }
            }

            inputTensor.close();
            result.close();

            String label = (maxIndex < activeLabels.length) ? activeLabels[maxIndex] : "unknown";

            StringBuilder sb = new StringBuilder("Probs: ");
            for (int i = 0; i < probabilities.length; i++) {
                String l = (i < activeLabels.length) ? activeLabels[i] : "idx" + i;
                sb.append(l).append("=").append(String.format("%.1f%% ", probabilities[i] * 100f));
            }
            Log.d(TAG, sb.toString());

            return new ClassificationResult(label, maxProb * 100f);
        } catch (OrtException e) {
            Log.e(TAG, "Classification failed", e);
            return new ClassificationResult("unknown", 0f);
        }
    }

    public boolean isReady() {
        return isInitialized;
    }

    public void close() {
        try {
            if (session != null) session.close();
            if (env != null) env.close();
        } catch (OrtException e) {
            e.printStackTrace();
        }
    }

    public static class ClassificationResult {
        private String label;
        private float confidence;

        public ClassificationResult(String label, float confidence) {
            this.label = label;
            this.confidence = confidence;
        }

        public String getLabel() { return label; }
        public float getConfidence() { return confidence; }
    }
}
