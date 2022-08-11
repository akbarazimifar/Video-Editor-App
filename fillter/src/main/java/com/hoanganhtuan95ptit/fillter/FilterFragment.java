package com.hoanganhtuan95ptit.fillter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hoanganhtuan95ptit.fillter.library.filter.FilterManager;
import com.hoanganhtuan95ptit.fillter.library.image.ImageEglSurface;
import com.hoanganhtuan95ptit.fillter.library.image.ImageRenderer;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Hoang Anh Tuan on 11/23/2017.
 */

public class FilterFragment extends Fragment implements FilterAdapter.OnItemFilterClickedListener, View.OnClickListener {

    private static final String INPUT_URL = "inputUrl";

    static FilterView filterView;
   // AVLoadingIndicatorView ivLoading;
    RecyclerView list;
    ImageView ivCancel;
    TextView tvTitle;
    ImageView ivCheck;
    LinearLayout controller;
    RelativeLayout rootFilter;
Bitmap bmp;

   /* public static FilterFragment create(String inputUrl, OnFilterListener onFilterListener) {
        FilterFragment fragment = new FilterFragment();
        fragment.setOnFilterListener(onFilterListener);
        Bundle bundle = new Bundle();
        bundle.putString(INPUT_URL, inputUrl);
        fragment.setArguments(bundle);
        return fragment;
    }*/
 public static FilterFragment create(Bitmap bitmap, OnFilterListener onFilterListener) {
        FilterFragment fragment = new FilterFragment();
        fragment.setOnFilterListener(onFilterListener);
     ByteArrayOutputStream stream=new ByteArrayOutputStream();
     bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
     byte[] byteArray=stream.toByteArray();

     Bundle b=new Bundle();
     b.putByteArray("image",byteArray);
     fragment.setArguments(b);
        return fragment;
    }/*public static FilterFragment create(Bitmap bitmap, View.OnClickListener onFilterListener) {
        FilterFragment fragment = new FilterFragment();
        fragment.setOnFilterListener((OnFilterListener) onFilterListener);
     ByteArrayOutputStream stream=new ByteArrayOutputStream();
     bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
     byte[] byteArray=stream.toByteArray();

     Bundle b=new Bundle();
     b.putByteArray("image",byteArray);
     fragment.setArguments(b);
        return fragment;
    }*/

    private String inputUrl;

    private OnFilterListener onFilterListener;

    public void setOnFilterListener(OnFilterListener onFilterListener) {
        this.onFilterListener = onFilterListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        mappingView(view);
        return view;
    }

    private void mappingView(View view) {
        filterView = view.findViewById(R.id.filterView);
       // ivLoading = view.findViewById(R.id.ivLoading);
        list = view.findViewById(R.id.list);
        ivCancel = view.findViewById(R.id.ivCancel);
        tvTitle = view.findViewById(R.id.tvTitle);
        ivCheck = view.findViewById(R.id.ivCheck);
        controller = view.findViewById(R.id.controller);
        rootFilter = view.findViewById(R.id.rootFilter);

        ivCancel.setOnClickListener(this);
        ivCheck.setOnClickListener(this);
        rootFilter.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        FilterAdapter filterAdapter = new FilterAdapter(getActivity());
        filterAdapter.setOnItemFilterClickedListener(this);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        list.setAdapter(filterAdapter);
        new StartSnapHelper().attachToRecyclerView(list);

        filterAdapter.add(new FilterModel(FilterManager.FilterType.Normal, R.drawable.filter11));
        filterAdapter.add(new FilterModel(FilterManager.FilterType.Filter1, R.drawable.filter1));
        filterAdapter.add(new FilterModel(FilterManager.FilterType.Filter2, R.drawable.filter2));
        filterAdapter.add(new FilterModel(FilterManager.FilterType.Filter3, R.drawable.filter3));
        filterAdapter.add(new FilterModel(FilterManager.FilterType.Filter4, R.drawable.filter4));
        filterAdapter.add(new FilterModel(FilterManager.FilterType.Filter5, R.drawable.filter5));
        filterAdapter.add(new FilterModel(FilterManager.FilterType.Filter6, R.drawable.filter6));
        filterAdapter.add(new FilterModel(FilterManager.FilterType.Filter7, R.drawable.filter7));
        filterAdapter.add(new FilterModel(FilterManager.FilterType.Filter8, R.drawable.filter8));
        filterAdapter.add(new FilterModel(FilterManager.FilterType.Filter9, R.drawable.filter9));
        filterAdapter.add(new FilterModel(FilterManager.FilterType.Filter10, R.drawable.filter10));
        filterAdapter.add(new FilterModel(FilterManager.FilterType.Filter11, R.drawable.filter11));

      /*  if (getArguments() != null) {
            inputUrl = getArguments().getString(INPUT_URL);
            changeFiler(FilterManager.FilterType.Filter1);
        }*/
        if (getArguments() != null) {
           /* inputUrl = getArguments().getString(INPUT_URL);
            showImage();*/
            byte[] byteArray=getArguments().getByteArray("image");
            bmp= BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
           // filterView.filterbitmap(bmp,FilterManager.FilterType.Filter3);
            changeFiler(FilterManager.FilterType.Filter3);


        }

    }

    private void changeFiler(final FilterManager.FilterType filter) {
        filterView.filterbitmapdemo(bmp,filter);
       // filterView.setImageBitmap(bmp);
    }
public static void setimage(Bitmap bitmap)
{
    filterView.setImageBitmap(bitmap);
}

   /* private void hideLoading() {
        if (ivLoading != null)
            ivLoading.smoothToHide();
    }

    private void showLoading() {
        if (ivLoading != null)
            ivLoading.smoothToShow();
    }*/

   /* private void saveImage() {
        Observable.just(inputUrl)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String url) throws Exception {
                        return saveBitmap(url);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoading();
                    }

                    @Override
                    public void onNext(String url) {
                        if (onFilterListener != null)
                            onFilterListener.onFilterPhotoCompleted(url);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        back();
                    }
                });
    }

    private String saveBitmap(String inputUrl) {
        Bitmap bitmap = Utils.getBitmapSdcard(inputUrl);
        bitmap = Utils.scaleDown(bitmap);

        ImageEglSurface imageEglSurface = new ImageEglSurface(bitmap.getWidth(), bitmap.getHeight());
        imageEglSurface.setRenderer(filterView.getImageRenderer());
        filterView.getImageRenderer().changeFilter(filterView.getType());
        filterView.getImageRenderer().setImageBitmap(bitmap);
        imageEglSurface.drawFrame();
        bitmap = imageEglSurface.getBitmap();
        imageEglSurface.release();
        filterView.getImageRenderer().destroy();


        Utils.saveBitmap(inputUrl, bitmap);

        return inputUrl;
    }*/

    private void back() {
        getActivity().onBackPressed();
    }

    @Override
    public void onItemFilterClicked(FilterModel filterModel) {
       /* filterView.filterbitmap(bmp,filterModel.getType());
        filterView.setImageBitmap(bmp);*/
        changeFiler(filterModel.getType());
    }

    @Override
    public void onClick(View view) {
      /*  if (ivLoading.isShown())
            return;*/
        if (view.getId() == R.id.ivCancel) {
            back();
        } else if (view.getId() == R.id.ivCheck) {
           // filterView.savebitmap(bmp,filterView.getType());
            saveImage(bmp);
            //getActivity().onBackPressed();
        }
    }
    public  void saveImage(Bitmap bitmap)
    {
        ImageEglSurface imageEglSurface = new ImageEglSurface(bitmap.getWidth(), bitmap.getHeight());
        imageEglSurface.setRenderer(filterView.getImageRenderer());
        filterView.getImageRenderer().changeFilter(filterView.getType());
        filterView.getImageRenderer().setImageBitmap(bitmap);
        imageEglSurface.drawFrame();
        bitmap = imageEglSurface.getBitmap();
        imageEglSurface.release();
        filterView.getImageRenderer().destroy();
      //  FilterFragment.saveImage(bitmap);
        if (onFilterListener != null)

            onFilterListener.onFilterPhotoCompleted(bitmap);
        //iv_demo.setImageBitmap(bitmap);

        getActivity().onBackPressed();

    }
}
