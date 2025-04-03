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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import is.hbv501g.lootapp.adapter.ScanCardAdapter;
import is.hbv501g.lootapp.api.ApiClient;
import is.hbv501g.lootapp.api.ApiService;
import is.hbv501g.lootapp.models.Card;
import is.hbv501g.lootapp.models.InventoryCard;
import is.hbv501g.lootapp.models.api.AddCardRequest;
import is.hbv501g.lootapp.models.api.AddCardResponse;
import is.hbv501g.lootapp.models.api.SearchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private Button buttonScan;
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

    // Local scan list of InventoryCard items (each with its own quantity)
    private List<InventoryCard> scanList = new ArrayList<>();

    // The adapter that displays the scan list
    private ScanCardAdapter scanCardAdapter;

    private Executor cameraExecutor;
    private volatile boolean scanningEnabled = false; // Flag for single scan trigger

    // Retrofit API service
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        // Main scanner UI elements
        ImageButton buttonHome = findViewById(R.id.buttonHome);
        buttonScan = findViewById(R.id.buttonScan);
        previewView = findViewById(R.id.previewView);
        textViewRecognized = findViewById(R.id.textViewRecognized);
        scanAreaOverlay = findViewById(R.id.scanArea); // Your MaterialCardView overlay

        // Popup UI elements
        buttonList = findViewById(R.id.buttonList);
        popupScanList = findViewById(R.id.popupScanList);
        buttonClosePopup = findViewById(R.id.buttonClosePopup);
        recyclerScanList = findViewById(R.id.recyclerScanList);
        buttonDeleteAll = findViewById(R.id.buttonDeleteAll);
        buttonAddAll = findViewById(R.id.buttonAddAll);

        // Home button listener
        buttonHome.setOnClickListener(view -> {
            Intent intent = new Intent(ScannerActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Scan button listener
        buttonScan.setOnClickListener(view -> {
            scanningEnabled = true;
            Toast.makeText(ScannerActivity.this, "Scanned", Toast.LENGTH_SHORT).show();
        });

        // List button to show popup
        buttonList.setOnClickListener(view -> popupScanList.setVisibility(View.VISIBLE));

        // Close popup button
        buttonClosePopup.setOnClickListener(view -> popupScanList.setVisibility(View.GONE));

        // Delete All button: clear the scan list and update the adapter
        buttonDeleteAll.setOnClickListener(view -> {
            scanList.clear();
            scanCardAdapter.notifyDataSetChanged();
            Toast.makeText(ScannerActivity.this, "Scan list cleared", Toast.LENGTH_SHORT).show();
        });

        // Add All button:
        buttonAddAll.setOnClickListener(view -> {
            // FIXED: Iterate over InventoryCard objects, not Card
            for (InventoryCard invCard : scanList) {
                // If you need to add multiple copies, you can either:
                //  1) Call addCardToInventory multiple times, or
                //  2) Modify addCardToInventory to accept a quantity
                // For now, we'll call it once per quantity as a simple example
                int quantity = invCard.getQuantity();
                for (int i = 0; i < quantity; i++) {
                    addCardToInventory(invCard.getCard());
                }
            }
            // Clear the list after we've added them
            scanList.clear();
            scanCardAdapter.notifyDataSetChanged();
            Toast.makeText(ScannerActivity.this, "Cards added to inventory", Toast.LENGTH_SHORT).show();
        });

        // Initialize Retrofit API service
        apiService = ApiClient.getApiService();

        // Initialize the RecyclerView adapter and attach it
        scanCardAdapter = new ScanCardAdapter(scanList, this);
        recyclerScanList.setLayoutManager(new LinearLayoutManager(this));
        recyclerScanList.setAdapter(scanCardAdapter);

        // Use the main executor for CameraX tasks
        cameraExecutor = ContextCompat.getMainExecutor(this);

        // Check camera permissions and start the camera
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
        // Set up the Preview use case
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Set up the ImageAnalysis use case
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
            @ExperimentalGetImage
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                if (!scanningEnabled) {
                    imageProxy.close();
                    return;
                }

                if (imageProxy.getImage() != null) {
                    Bitmap fullBitmap = imageProxyToBitmap(imageProxy);
                    if (fullBitmap == null) {
                        imageProxy.close();
                        return;
                    }

                    int[] previewLocation = new int[2];
                    previewView.getLocationOnScreen(previewLocation);

                    int[] overlayLocation = new int[2];
                    scanAreaOverlay.getLocationOnScreen(overlayLocation);

                    int relativeLeft = overlayLocation[0] - previewLocation[0];
                    int relativeTop = overlayLocation[1] - previewLocation[1];
                    int overlayWidth = scanAreaOverlay.getWidth();
                    int overlayHeight = scanAreaOverlay.getHeight();

                    int previewWidth = previewView.getWidth();
                    int previewHeight = previewView.getHeight();
                    int imageWidth = fullBitmap.getWidth();
                    int imageHeight = fullBitmap.getHeight();

                    float scaleX = (float) imageWidth / previewWidth;
                    float scaleY = (float) imageHeight / previewHeight;

                    int cropLeft = Math.round(relativeLeft * scaleX);
                    int cropTop = Math.round(relativeTop * scaleY);
                    int cropWidth = Math.round(overlayWidth * scaleX);
                    int cropHeight = Math.round(overlayHeight * scaleY);

                    cropLeft = Math.max(0, cropLeft);
                    cropTop = Math.max(0, cropTop);
                    if (cropLeft + cropWidth > imageWidth) {
                        cropWidth = imageWidth - cropLeft;
                    }
                    if (cropTop + cropHeight > imageHeight) {
                        cropHeight = imageHeight - cropTop;
                    }

                    Bitmap croppedBitmap = Bitmap.createBitmap(fullBitmap, cropLeft, cropTop, cropWidth, cropHeight);
                    InputImage croppedInputImage = InputImage.fromBitmap(croppedBitmap, 0);

                    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                    recognizer.process(croppedInputImage)
                            .addOnSuccessListener(visionText -> {
                                String recognizedText = visionText.getText();
                                textViewRecognized.setText(recognizedText);
                                Log.d("ScannerActivity", "Recognized text: " + recognizedText);
                                searchForCard(recognizedText);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ScannerActivity", "Text recognition failed: ", e);
                            })
                            .addOnCompleteListener(task -> {
                                scanningEnabled = false;
                                imageProxy.close();
                            });
                } else {
                    imageProxy.close();
                }
            }
        });

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private void searchForCard(String query) {
        apiService.searchCards(query, 1).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getCards().isEmpty()) {
                    Card foundCard = response.body().getCards().get(0);

                    // Check if we already have this card in our scan list
                    boolean cardExists = false;
                    for (int i = 0; i < scanList.size(); i++) {
                        InventoryCard invCard = scanList.get(i);
                        if (invCard.getCard().getId().equals(foundCard.getId())) {
                            // Increment quantity
                            invCard.setQuantity(invCard.getQuantity() + 1);
                            scanCardAdapter.notifyItemChanged(i);
                            cardExists = true;
                            break;
                        }
                    }
                    if (!cardExists) {
                        // Create a new InventoryCard with quantity = 1
                        InventoryCard newInvCard = new InventoryCard(foundCard, 1);
                        scanList.add(newInvCard);
                        scanCardAdapter.notifyItemInserted(scanList.size() - 1);
                    }

                    Toast.makeText(ScannerActivity.this, "Found and added: " + foundCard.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScannerActivity.this, "No card found for query: " + query, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Toast.makeText(ScannerActivity.this, "Card search failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * NOTE:
     *  If your backend supports adding multiple quantities of a card in one call,
     *  you could change `addCardToInventory` to accept a quantity. For now,
     *  we call it once per card in the "Add All" loop for demonstration.
     */
    private void addCardToInventory(Card card) {
        AddCardRequest request = new AddCardRequest(card.getId());
        apiService.addCardToInventory(request).enqueue(new Callback<AddCardResponse>() {
            @Override
            public void onResponse(Call<AddCardResponse> call, Response<AddCardResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ScannerActivity.this, "Card added to inventory: " + card.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScannerActivity.this, "Failed to add card: " + card.getName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddCardResponse> call, Throwable t) {
                Toast.makeText(ScannerActivity.this, "Error adding card: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

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
            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);
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
