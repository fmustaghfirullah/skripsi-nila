package com.faisal.bangunruang;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.faisal.bangunruang.databinding.ActivityScanBinding;
import com.faisal.bangunruang.ml.ShapeClassifier;
import com.faisal.bangunruang.utils.ImageUtils;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final float CONFIDENCE_THRESHOLD = 65f;
    private static final long ANALYSIS_INTERVAL_MS = 1000;

    private ActivityScanBinding binding;
    private ShapeClassifier classifier;
    private ImageCapture imageCapture;
    private ImageAnalysis imageAnalysis;
    private ExecutorService cameraExecutor;
    private long lastAnalysisTime = 0;
    private String detectedShapeName = null;
    private boolean isAnalyzing = true;

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        processImageFromUri(imageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        classifier = new ShapeClassifier(this);
        cameraExecutor = Executors.newSingleThreadExecutor();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnCamera.setOnClickListener(v -> captureImage());
        binding.btnGallery.setOnClickListener(v -> openGallery());

        binding.btnDetailLearn.setOnClickListener(v -> {
            if (detectedShapeName != null) {
                Intent intent = new Intent(this, LearnActivity.class);
                intent.putExtra("shape_id", detectedShapeName);
                startActivity(intent);
            }
        });

        binding.btnDetailChat.setOnClickListener(v -> {
            if (detectedShapeName != null) {
                Intent intent = new Intent(this, ChatbotActivity.class);
                intent.putExtra("shape_context", detectedShapeName);
                startActivity(intent);
            }
        });

        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Izin kamera diperlukan untuk fitur scan 📷", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeFrame);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void analyzeFrame(ImageProxy imageProxy) {
        if (!isAnalyzing || !classifier.isReady()) {
            imageProxy.close();
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastAnalysisTime < ANALYSIS_INTERVAL_MS) {
            imageProxy.close();
            return;
        }
        lastAnalysisTime = now;

        Bitmap bitmap = null;
        try {
            bitmap = ImageUtils.imageProxyToBitmap(imageProxy);
        } catch (Exception e) {
            // conversion failed
        } finally {
            imageProxy.close();
        }

        if (bitmap == null) return;

        try {
            ShapeClassifier.ClassificationResult result = classifier.classify(bitmap);
            bitmap.recycle();
            runOnUiThread(() -> updateDetectionOverlay(result));
        } catch (Exception e) {
            bitmap.recycle();
        }
    }

    private void updateDetectionOverlay(ShapeClassifier.ClassificationResult result) {
        if (result.getLabel().equals("limas") || result.getConfidence() < CONFIDENCE_THRESHOLD) {
            binding.cardDetection.setVisibility(View.GONE);
            binding.cardInstruction.setVisibility(View.VISIBLE);
            detectedShapeName = null;
            return;
        }

        detectedShapeName = result.getLabel();

        binding.cardDetection.setVisibility(View.VISIBLE);
        binding.cardInstruction.setVisibility(View.GONE);
        binding.tvDetectedName.setText(getDisplayName(result.getLabel()));
        binding.tvDetectedConfidence.setText(String.format("%.0f%%", result.getConfidence()));

        int icon = getShapeIconResource(result.getLabel());
        if (icon != 0) binding.ivDetectedShape.setImageResource(icon);

        if (result.getConfidence() >= 80) {
            binding.tvDetectedConfidence.setTextColor(getColor(R.color.confidence_high));
        } else {
            binding.tvDetectedConfidence.setTextColor(getColor(R.color.confidence_medium));
        }
    }

    private void captureImage() {
        if (imageCapture == null) return;

        isAnalyzing = false;
        binding.progressBar.setVisibility(View.VISIBLE);

        File photoFile = new File(getCacheDir(), "scan_" + System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        runOnUiThread(() -> classifyImage(bitmap));
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        runOnUiThread(() -> {
                            binding.progressBar.setVisibility(View.GONE);
                            isAnalyzing = true;
                            Toast.makeText(ScanActivity.this,
                                    "Gagal mengambil foto 😔", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void processImageFromUri(Uri uri) {
        isAnalyzing = false;
        binding.progressBar.setVisibility(View.VISIBLE);
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) inputStream.close();

            binding.previewView.setVisibility(View.GONE);
            binding.ivPreview.setVisibility(View.VISIBLE);
            binding.ivPreview.setImageBitmap(bitmap);
            binding.cardDetection.setVisibility(View.GONE);
            binding.cardInstruction.setVisibility(View.GONE);

            classifyImage(bitmap);
        } catch (IOException e) {
            binding.progressBar.setVisibility(View.GONE);
            isAnalyzing = true;
            Toast.makeText(this, "Gagal memuat gambar 😔", Toast.LENGTH_SHORT).show();
        }
    }

    private void classifyImage(Bitmap bitmap) {
        if (!classifier.isReady()) {
            binding.progressBar.setVisibility(View.GONE);
            isAnalyzing = true;
            Toast.makeText(this, "Model belum siap. Pastikan file model ada di folder assets.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        ShapeClassifier.ClassificationResult result = classifier.classify(bitmap);
        binding.progressBar.setVisibility(View.GONE);

        if (result.getLabel().equals("limas")) {
            isAnalyzing = true;
            Toast.makeText(this, "Bentuk tidak dikenali. Coba arahkan ke bangun ruang lain.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("shape_name", result.getLabel());
        intent.putExtra("confidence", result.getConfidence());
        startActivity(intent);
    }

    private String getDisplayName(String shapeId) {
        switch (shapeId.toLowerCase()) {
            case "kubus": return "Kubus";
            case "balok": return "Balok";
            case "kerucut": return "Kerucut";
            case "tabung": return "Tabung";
            case "bola": return "Bola";
            default: return shapeId;
        }
    }

    private int getShapeIconResource(String shapeId) {
        switch (shapeId.toLowerCase()) {
            case "kubus": return R.drawable.ic_shape_kubus;
            case "balok": return R.drawable.ic_shape_balok;
            case "kerucut": return R.drawable.ic_shape_kerucut;
            case "tabung": return R.drawable.ic_shape_tabung;
            case "bola": return R.drawable.ic_shape_bola;
            default: return 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAnalyzing = true;
        if (binding.ivPreview.getVisibility() == View.VISIBLE) {
            binding.ivPreview.setVisibility(View.GONE);
            binding.previewView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        if (classifier != null) {
            classifier.close();
        }
    }
}
