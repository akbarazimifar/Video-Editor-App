package com.hoanganhtuan95ptit.crop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wang.avi.AVLoadingIndicatorView;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.model.ImageState;
import com.yalantis.ucrop.task.BitmapCropTask;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.UCropView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Hoang Anh Tuan on 11/15/2017.
 */

public class CropFragment extends Fragment implements TransformImageView.TransformImageListener,
        CropAdapter.OnItemCropClickedListener,
        View.OnClickListener {

    private static final String INPUT_URL = "inputUrl";
    private static final String SAMPLE_CROPPED_IMAGE_NAME ="AAAAAAA" ;

    UCropView ivCrop;
    AVLoadingIndicatorView ivLoading;
    RecyclerView list;
    ImageView ivCancel;
   static Context context;
    TextView tvTitle;
    ImageView ivCheck;
    LinearLayout controller;
    RelativeLayout rootCrop;
Bitmap bmp;

   /* public static CropFragment create(String inputUrl, OnCropListener onCropListener) {
        CropFragment fragment = new CropFragment();
        fragment.setOnCropListener(onCropListener);
        Log.d("aaaaaaa",""+inputUrl);
        Bundle bundle = new Bundle();
        bundle.putString(INPUT_URL, inputUrl);
        fragment.setArguments(bundle);
        return fragment;
    }*/
   public static CropFragment create(Bitmap bitmap, OnCropListener onCropListener) {
        CropFragment fragment = new CropFragment();
        fragment.setOnCropListener(onCropListener);
       Log.d("aaaa", "" + bitmap);
       ByteArrayOutputStream stream = new ByteArrayOutputStream();
       bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
       byte[] byteArray = stream.toByteArray();

       Bundle b = new Bundle();
       b.putByteArray("image", byteArray);
       fragment.setArguments(b);
        return fragment;
    }


    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;

    private OnCropListener onCropListener;

    public void setOnCropListener(OnCropListener onCropListener) {
        this.onCropListener = onCropListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crop, container, false);
      hideLoading();
       context=getContext();
        mappingView(view);
        return view;
    }

    private void mappingView(View view) {
        ivCrop = view.findViewById(R.id.ivCrop);
        ivLoading = view.findViewById(R.id.ivLoading);
        list = view.findViewById(R.id.list);
        ivCancel = view.findViewById(R.id.ivCancel);
        tvTitle = view.findViewById(R.id.tvTitle);
        ivCheck = view.findViewById(R.id.ivCheck);
        controller = view.findViewById(R.id.controller);
        rootCrop = view.findViewById(R.id.rootCrop);

        ivCancel.setOnClickListener(this);
        ivCheck.setOnClickListener(this);
        rootCrop.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        CropAdapter cropAdapter = new CropAdapter(getActivity());
        cropAdapter.setOnItemCropClickedListener(this);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setAdapter(cropAdapter);
        new StartSnapHelper().attachToRecyclerView(list);

        cropAdapter.add(new CropModel(R.drawable.pg_sdk_edit_crop_crop11_selected, "1:1", CropModel.Type.TYPE11));
        cropAdapter.add(new CropModel(R.drawable.pg_sdk_edit_crop_crop23_selected, "2:3", CropModel.Type.TYPE23));
        cropAdapter.add(new CropModel(R.drawable.pg_sdk_edit_crop_crop32_selected, "3:2", CropModel.Type.TYPE32));
        cropAdapter.add(new CropModel(R.drawable.pg_sdk_edit_crop_crop43_selected, "4:3", CropModel.Type.TYPE43));
        cropAdapter.add(new CropModel(R.drawable.pg_sdk_edit_crop_crop34_selected, "3:4", CropModel.Type.TYPE34));
        cropAdapter.add(new CropModel(R.drawable.pg_sdk_edit_crop_crop169_selected, "16:9", CropModel.Type.TYPE169));
        cropAdapter.add(new CropModel(R.drawable.pg_sdk_edit_crop_crop916_selected, "9:16", CropModel.Type.TYPE916));

        mGestureCropImageView = ivCrop.getCropImageView();
        mOverlayView = ivCrop.getOverlayView();

        mGestureCropImageView.setScaleEnabled(true);
        mGestureCropImageView.setRotateEnabled(true);
        mGestureCropImageView.setImageToWrapCropBounds(true);
        mGestureCropImageView.setTransformImageListener(this);
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;

        destinationFileName += ".jpg";
       // Uri.fromFile(new File(getActivity().getCacheDir(), destinationFileName));
      /*  try {
            if (getArguments() != null) {
                showLoading();
                String inputUrl = getArguments().getString(INPUT_URL);
                assert inputUrl != null;
                mGestureCropImageView.setImageUri(Uri.fromFile(new File(inputUrl)), Uri.fromFile(new File(getActivity().getCacheDir(), destinationFileName)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        try {
            if (getArguments() != null) {
                byte[] byteArray = getArguments().getByteArray("image");
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                mGestureCropImageView.setImageUri(getImageUri(context,bmp),  Uri.fromFile(new File(getActivity().getCacheDir(), destinationFileName)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,"CROP_" + Calendar.getInstance().getTime(),null);

        //String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void saveImage() {
        showLoading();
       // back();
        mGestureCropImageView.cropAndSaveImage(Bitmap.CompressFormat.PNG, 100, new BitmapCropCallback() {
            @Override
            public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                if (onCropListener != null)
                    onCropListener.onCropPhotoCompleted(resultUri.getPath());
                getActivity().onBackPressed();
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                back();
            }
        });
    }
    private void uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getActivity().getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);


              /*  if (onCropListener != null)
                    //onCropListener.onCropPhotoCompleted(resultUri.getPath());
                   onCropListener.onCropPhotoCompleted(image);
*/
            back();
            parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void back() {
        hideLoading();
        getActivity().onBackPressed();
    }


    @Override
    public void onLoadComplete() {
        hideLoading();
        changeCropType(1f / 1f);
    }

    @Override
    public void onLoadFailure(@NonNull Exception e) {

    }

    @Override
    public void onRotate(float currentAngle) {

    }

    @Override
    public void onScale(float currentScale) {

    }

    @Override
    public void onBrightness(float currentBrightness) {

    }

    @Override
    public void onContrast(float currentContrast) {

    }

    @Override
    public void onItemCropClicked(CropModel.Type type) {
        switch (type) {
            case TYPE11:
                changeCropType(1f / 1f);
                break;
            case TYPE23:
                changeCropType(2f / 3f);
                break;
            case TYPE32:
                changeCropType(3f / 2f);
                break;
            case TYPE43:
                changeCropType(4f / 3f);
                break;
            case TYPE34:
                changeCropType(3f / 4f);
                break;
            case TYPE169:
                changeCropType(16f / 9f);
                break;
            case TYPE916:
                changeCropType(9f / 16f);
                break;
        }
    }

    private void hideLoading() {
        if (ivLoading != null)
            ivLoading.smoothToHide();
    }

    private void showLoading() {
        if (ivLoading != null)
            ivLoading.smoothToShow();
    }

    private void changeCropType(float crop) {
        mOverlayView.setTargetAspectRatio(crop);
        mGestureCropImageView.setTargetAspectRatio(crop);
    }

    @Override
    public void onClick(View view) {
        if (ivLoading.isShown()) return;
        if (view.getId() == R.id.ivCancel) {
            back();
        } else if (view.getId() == R.id.ivCheck) {
            saveImage();
        }
    }
}
