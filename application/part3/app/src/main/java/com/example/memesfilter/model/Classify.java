package com.example.memesfilter.model;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Classify extends AppCompatActivity {

    // presets for rgb conversion
    private static final int RESULTS_TO_SHOW = 3;

    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 1.0f;

    public static final BlockingQueue<Bitmap> images = new ArrayBlockingQueue<>(20);
    public static final BlockingQueue<float[]> results = new ArrayBlockingQueue<>(20);

    // options for model interpreter
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();
    // tflite graph
    private Interpreter tflite;
    // holds all the possible labels for model
    private List<String> labelList;

    private AssetManager assetsManager;

    // input image dimensions for the Inception Model
    private int DIM_IMG_SIZE_X;
    private int DIM_IMG_SIZE_Y;

    private int DIM_PIXEL_SIZE = 3;


    public Classify(AssetManager assetManager) {
        this.assetsManager = assetManager;

        //initilize graph and labels
        try{
            tflite = new Interpreter(loadModelFile(), tfliteOptions);
            labelList = FileUtil.loadLabels(assetManager.open("dict.txt"));

            int[] imageShape = tflite.getInputTensor(0).shape();

            DIM_IMG_SIZE_X = imageShape[1];
            DIM_IMG_SIZE_Y = imageShape[2];

        } catch (Exception ex){
            ex.printStackTrace();
        }

        ByteBuffer.allocateDirect(DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);

    }

    public String classify(Bitmap bitmap) {

        // Creates the input tensor.
        int probabilityTensorIndex = 0;
        int imageTensorIndex = 0;

        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();
        int[] probabilityShape =
                tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        // Creates the input tensor.
        TensorImage inputImageBuffer = new TensorImage(imageDataType);

        // Creates the output tensor and its processor.
        TensorBuffer outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);

        // Creates the post processor for the output probability.
        TensorProcessor probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

        loadImage(bitmap, inputImageBuffer);
        tflite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());

        Map<String, Float> labeledProbability =
                new TensorLabel(this.labelList, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();


        Map.Entry<String, Float> maxEntry = null;

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }

        return maxEntry.getKey();

    }

    // loads tflite grapg from file
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = assetsManager.openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);


    }

    // converts bitmap to byte array which is passed in the tflite graph
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {

        // holds the selected image data as bytes
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1 * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.put((byte) ((val >> 16) & 0xFF));
                byteBuffer.put((byte) ((val >> 8) & 0xFF));
                byteBuffer.put((byte) ((val) & 0xFF));
            }
        }
        return byteBuffer;
    }

    /** Loads input image, and applies preprocessing. */
    private TensorImage loadImage(final Bitmap bitmap, TensorImage inputImageBuffer) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeOp(DIM_IMG_SIZE_X, DIM_IMG_SIZE_X, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }


    protected TensorOperator getPostprocessNormalizeOp() {
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }
}
