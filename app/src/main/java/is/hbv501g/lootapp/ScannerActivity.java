package is.hbv501g.lootapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class ScannerActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private Button buttonHome, buttonScan;
    private PreviewView previewView;
    private TextView textViewRecognized;
    // The overlay that defines the scan area.
    private View scanAreaOverlay;

    // Popup UI elements
    private ImageButton buttonList;
    private CardView popupScanList;
    private ImageButton buttonClosePopup;
    private RecyclerView recyclerScanList;
    private Button buttonDeleteAll, buttonAddAll;

    private Executor cameraExecutor;
    private volatile boolean scanningEnabled = false; // Flag for single scan trigger

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        // Main scanner UI elements.
        buttonHome = findViewById(R.id.buttonHome);
        buttonScan = findViewById(R.id.buttonScan);
        previewView = findViewById(R.id.previewView);
        textViewRecognized = findViewById(R.id.textViewRecognized);
        scanAreaOverlay = findViewById(R.id.scanArea); // Your MaterialCardView overlay

        // Popup UI elements.
        buttonList = findViewById(R.id.buttonList);
        popupScanList = findViewById(R.id.popupScanList);
        buttonClosePopup = findViewById(R.id.buttonClosePopup);
        recyclerScanList = findViewById(R.id.recyclerScanList);
        buttonDeleteAll = findViewById(R.id.buttonDeleteAll);
        buttonAddAll = findViewById(R.id.buttonAddAll);

        // Home button listener.
        buttonHome.setOnClickListener(view -> {
            Intent intent = new Intent(ScannerActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Scan button listener.
        buttonScan.setOnClickListener(view -> {
            scanningEnabled = true;
            Toast.makeText(ScannerActivity.this, "Scanning enabled", Toast.LENGTH_SHORT).show();
        });

        // List button to show popup.
        buttonList.setOnClickListener(view -> {
            popupScanList.setVisibility(View.VISIBLE);
        });

        // Close popup button.
        buttonClosePopup.setOnClickListener(view -> {
            popupScanList.setVisibility(View.GONE);
        });

        // Dummy Delete All button.
        buttonDeleteAll.setOnClickListener(view -> {
            Toast.makeText(ScannerActivity.this, "Delete All clicked (dummy)", Toast.LENGTH_SHORT).show();
        });

        // Dummy Add All button.
        buttonAddAll.setOnClickListener(view -> {
            Toast.makeText(ScannerActivity.this, "Add All clicked (dummy)", Toast.LENGTH_SHORT).show();
        });

        // Use the main executor for CameraX tasks.
        cameraExecutor = ContextCompat.getMainExecutor(this);

        // Check camera permissions and start the camera.
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e("ScannerActivity", "Error starting camera: ", e);
            }
        }, cameraExecutor);
    }

    private void bindCameraUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        // Set up the Preview use case to display camera preview.
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Set up the ImageAnalysis use case to process frames.
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
            @ExperimentalGetImage
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                // Only process the frame if scanning is enabled.
                if (!scanningEnabled) {
                    imageProxy.close();
                    return;
                }

                if (imageProxy.getImage() != null) {
                    // Convert ImageProxy to Bitmap.
                    Bitmap fullBitmap = imageProxyToBitmap(imageProxy);
                    if (fullBitmap == null) {
                        imageProxy.close();
                        return;
                    }

                    // Get the location of the PreviewView and overlay on screen.
                    int[] previewLocation = new int[2];
                    previewView.getLocationOnScreen(previewLocation);

                    int[] overlayLocation = new int[2];
                    scanAreaOverlay.getLocationOnScreen(overlayLocation);

                    // Calculate the overlay's position relative to the PreviewView.
                    int relativeLeft = overlayLocation[0] - previewLocation[0];
                    int relativeTop = overlayLocation[1] - previewLocation[1];
                    int overlayWidth = scanAreaOverlay.getWidth();
                    int overlayHeight = scanAreaOverlay.getHeight();

                    // Get dimensions for coordinate mapping.
                    int previewWidth = previewView.getWidth();
                    int previewHeight = previewView.getHeight();
                    int imageWidth = fullBitmap.getWidth();
                    int imageHeight = fullBitmap.getHeight();

                    // Calculate scaling factors.
                    float scaleX = (float) imageWidth / previewWidth;
                    float scaleY = (float) imageHeight / previewHeight;

                    // Map the overlay coordinates to the Bitmap's coordinate system.
                    int cropLeft = Math.round(relativeLeft * scaleX);
                    int cropTop = Math.round(relativeTop * scaleY);
                    int cropWidth = Math.round(overlayWidth * scaleX);
                    int cropHeight = Math.round(overlayHeight * scaleY);

                    // Ensure crop dimensions are within Bitmap bounds.
                    cropLeft = Math.max(0, cropLeft);
                    cropTop = Math.max(0, cropTop);
                    if (cropLeft + cropWidth > imageWidth) {
                        cropWidth = imageWidth - cropLeft;
                    }
                    if (cropTop + cropHeight > imageHeight) {
                        cropHeight = imageHeight - cropTop;
                    }

                    // Crop the Bitmap.
                    Bitmap croppedBitmap = Bitmap.createBitmap(fullBitmap, cropLeft, cropTop, cropWidth, cropHeight);

                    // Create InputImage from the cropped Bitmap.
                    InputImage croppedInputImage = InputImage.fromBitmap(croppedBitmap, 0);

                    // Process the cropped image using ML Kit.
                    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                    recognizer.process(croppedInputImage)
                            .addOnSuccessListener(visionText -> {
                                String recognizedText = visionText.getText();
                                textViewRecognized.setText(recognizedText);
                                Log.d("ScannerActivity", "Recognized text: " + recognizedText);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ScannerActivity", "Text recognition failed: ", e);
                            })
                            .addOnCompleteListener(task -> {
                                // Reset scanning flag so only one scan occurs per button press.
                                scanningEnabled = false;
                                imageProxy.close();
                            });
                } else {
                    imageProxy.close();
                }
            }
        });

        // Choose the camera (back-facing).
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Unbind use cases before rebinding.
        cameraProvider.unbindAll();

        // Bind use cases to the lifecycle.
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    /**
     * Converts an ImageProxy to a Bitmap.
     * Note: This is a basic conversion using YuvImage and may need adjustments.
     */
    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        try {
            ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();
            if (planes.length < 3) return null;
            ByteBuffer yBuffer = planes[0].getBuffer();
            ByteBuffer uBuffer = planes[1].getBuffer();
            ByteBuffer vBuffer = planes[2].getBuffer();

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            byte[] nv21 = new byte[ySize + uSize + vSize];

            // Copy Y data.
            yBuffer.get(nv21, 0, ySize);
            // Copy V data.
            vBuffer.get(nv21, ySize, vSize);
            // Copy U data.
            uBuffer.get(nv21, ySize + vSize, uSize);

            Bitmap bitmap = nv21ToBitmap(nv21, imageProxy.getWidth(), imageProxy.getHeight());
            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
            if (rotationDegrees != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotationDegrees);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            return bitmap;
        } catch (Exception e) {
            Log.e("ScannerActivity", "Error converting ImageProxy to Bitmap", e);
            return null;
        }
    }

    /**
     * Converts an NV21 byte array to a Bitmap using YuvImage.
     */
    private Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
        try {
            YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, out);
            byte[] imageBytes = out.toByteArray();
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception e) {
            Log.e("ScannerActivity", "Error converting NV21 to Bitmap", e);
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
