package com.section39.facerecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button button;
    TextView textView;
    ImageView imageView;
    private  final  static  int REQUEST_IMAGE_CAPTURE=124;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
button= findViewById(R.id.camera);
textView = findViewById(R.id.text1);
imageView = findViewById(R.id.imageView);

button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        openFile();
    }
});
        Toast.makeText(this,"App started ",Toast.LENGTH_SHORT).show();
    }

    private void openFile() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);

        }else {
            Toast.makeText(this,"Failed! ",Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle= data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");

        faceDetectionProcess(bitmap);
        Toast.makeText(this,"Success! ",Toast.LENGTH_SHORT).show();

    }

    private void faceDetectionProcess(Bitmap bitmap) {
    textView.setText("Processing Image");
    final  StringBuilder builder = new StringBuilder();
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        InputImage image = InputImage.fromBitmap(bitmap,0);
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

// Real-time contour detection
//        FaceDetectorOptions realTimeOpts =
//                new FaceDetectorOptions.Builder()
//                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
//                        .build();
        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        // Task completed successfully

                                        for (Face face : faces) {
                                            Rect bounds = face.getBoundingBox();
                                            int id = 0;
                                            if(face.getTrackingId()!= null){

                                             id = face.getTrackingId();
                                            }
                                            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
                                            builder.append("id  [ "+ id +" ]");
                                            builder.append("rotY  [ "+ String.format("%.2f",rotY) +" ]\n");
                                            builder.append("rotZ  [ "+ String.format("%.2f",rotZ) +" ]\n");
                                        //smiling probability
                                        if(face.getSmilingProbability()>0){
                                            float smilingProbability = face.getSmilingProbability();
                                            builder.append("smiling  [ "+ String.format("%.2f",smilingProbability) +" ]\n");

                                        }
                                            //left Eye open

                                            if(face.getLeftEyeOpenProbability()>0){
                                                float getLeftEyeOpenProbability = face.getLeftEyeOpenProbability();
                                                builder.append("getLeftEyeOpenProbability  [ "+ String.format("%.2f",getLeftEyeOpenProbability) +" ]\n");

                                            }

                                            if(face.getRightEyeOpenProbability()>0){
                                                float getRightEyeOpenProbability = face.getRightEyeOpenProbability();
                                                builder.append("getRightEyeOpenProbability  [ "+ String.format("%.2f",getRightEyeOpenProbability) +" ]\n");

                                            }
                                        }
                                        showDetection("Face Detection" , builder , true);
                                            // ...
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        StringBuilder builder1 = new StringBuilder();
                                        builder1.append("sorry ! there is  an Error");
                                        showDetection("Face Detection" , builder1 , false);

                                        // ...
                                    }
                                });
    }

    private void showDetection(String title, StringBuilder builder, boolean success) {
        if(success){
            textView.setText(null);
            textView.setMovementMethod(new ScrollingMovementMethod());

            if(builder.length() != 0){
                textView.append(builder);
                if(title.substring(0,title.indexOf(' ')).equalsIgnoreCase("OCR")){
                    textView.append("\n(Hold the text to copy it!");
                }else {
                    textView.append("(Hold the text to copy it!");

                }
                textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager clipboardManager= (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(title,builder);
                        clipboardManager.setPrimaryClip(clip);
                        return true;
                    }
                });
            }else {
                textView.append(title.substring(0,title.indexOf(' '))+ " Failed to find anything !");
            }
        }else if(success ==false){
            textView.setText(null);
            textView.setMovementMethod(new ScrollingMovementMethod());
            textView.append(builder);
        }


    }

}
