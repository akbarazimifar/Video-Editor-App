package com.example.fullscreenvideocreator.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fullscreenvideocreator.Adepters.AdapterFont;
import com.example.fullscreenvideocreator.Models.ModelFont;
import com.example.fullscreenvideocreator.R;
import com.example.fullscreenvideocreator.StickerClasses.DrawableSticker;
import com.example.fullscreenvideocreator.Utills.Constance;

import java.util.ArrayList;
import java.util.Locale;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;

public class ActivityFont extends AppCompatActivity implements View.OnClickListener {

    private EditText et_text;
    private ImageView iv_color, iv_close, iv_done;
    private RecyclerView rv_font;
    LinearLayout ll_font_color;
    private AdapterFont fontAdapter;
    private ArrayList<ModelFont> list = new ArrayList<>();
    private String font_array[] = {"1", "6", "ardina_script", "beyondwonderland", "C", "coventry_garden_nf", "font3", "font6", "font10", "font16", "font20", "g", "h", "h2", "h3", "h6", "h7", "h8", "h15", "h18", "h19", "h20", "m", "o", "saman", "variane", "youmurdererbb"};

    AlertDialog dialog;
    Context context;
    int selectedcolour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font);

        context = ActivityFont.this;
        bindview();
        rv_font.setLayoutManager(new LinearLayoutManager(this));

        et_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return false;
            }
        });

        iv_color.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        iv_done.setOnClickListener(this);

        initView();

    }

    public void bindview() {
        et_text = (EditText) findViewById(R.id.et_text);
        ll_font_color = findViewById(R.id.ll_font_color);
        iv_color = (ImageView) findViewById(R.id.iv_color);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_done = (ImageView) findViewById(R.id.iv_done);
        rv_font = (RecyclerView) findViewById(R.id.rv_font);
    }

    @Override
    public void onClick(View view) {
        if (view == iv_color) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            showDailog();

        } else if (view == iv_close) {

            onBackPressed();

        } else if (view == iv_done) {
            String str = et_text.getText().toString();
            Bitmap b2 = createBitmapFromLayoutWithText(getApplicationContext(), et_text.getText().toString(), et_text.getCurrentTextColor(), 0, et_text.getTypeface());
            Drawable d = new BitmapDrawable(getResources(), b2);
            if (!str.equals("")) {
                // onBackPressed();
                nextActivity();
            } else {
                Toast.makeText(getApplicationContext(), "Text Is Empty", Toast.LENGTH_LONG).show();
            }

            DrawableSticker sticker = new DrawableSticker(d);

            Typeface face = Typeface.createFromAsset(getAssets(), Constance.FONT_EFFECT + ".ttf");

            if (Constance.COLOR == 0) {
                Constance.COLOR = getResources().getColor(R.color.colorPrimary);
            }

            Constance.TEXT_DRAWABLE = sticker;
        }
    }

    private void nextActivity() {

        Constance.FONT_FLAG = true;
        Constance.FONT_TEXT = et_text.getText().toString();
        finish();
        //   overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }


    private void initView() {

        for (int i = 0; i < font_array.length; i++) {
            ModelFont spinnerModel = new ModelFont();
            spinnerModel.setFont_name(font_array[i]);
            list.add(spinnerModel);
        }

        fontAdapter = new AdapterFont(context, list);
        rv_font.setAdapter(fontAdapter);

        fontAdapter.setEventListener(new AdapterFont.EventListener() {
            @Override
            public void onItemViewClicked(int position) {

                Constance.FONT_EFFECT = font_array[position].toString().toLowerCase();

                Typeface face = Typeface.createFromAsset(context.getAssets(), font_array[position].toLowerCase() + ".ttf");
                et_text.setTypeface(face);
            }
            
        });
    }

    public static Bitmap createBitmapFromLayoutWithText(Context context, String s, int color, int i, Typeface face) {

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.row_bitmap, null);

        TextView tv = (TextView) view.findViewById(R.id.tv_custom_text1);

        for (int j = 0; j < s.length(); j += 40) {
            if (s.length() >= 40) {
                if (j <= s.length() - 40) {
                    if (j == 0) {
                        String m = s.substring(0, 40);
                        tv.setText(m);
                    } else {
                        tv.append("\n");
                        String l = s.substring(j, j + 40);
                        tv.append(l);
                    }
                } else {
                    tv.append("\n");
                    String l = s.substring(j, s.length());
                    tv.append(l);
                }
            } else {
                tv.setText(s);
            }
        }
        tv.setTextColor(color);
        tv.setTypeface(face);
        view.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.draw(c);
        return bitmap;
    }

    public void showDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.dailog_colourpicker,
                        null);
        builder.setView(customLayout);
        TextView tv_colorcode, tv_choose, tv_cancel;
        ColorPickerView colorPicker;
        LinearLayout ll_colorbox;
        tv_choose = customLayout.findViewById(R.id.tv_choose);
        tv_cancel = customLayout.findViewById(R.id.tv_cancel);
        tv_colorcode = customLayout.findViewById(R.id.tv_colorcode);
        colorPicker = customLayout.findViewById(R.id.colorPicker);
        ll_colorbox = customLayout.findViewById(R.id.ll_colorbox);

        colorPicker.setEnabledBrightness(true);
        colorPicker.setEnabledAlpha(true);
        colorPicker.setOnlyUpdateOnTouchEventUp(false);

        // colorPicker.setInitialColor(0x7F313C93);
        tv_colorcode.setText(colorHex(0x7F313C93));
        ll_colorbox.setBackgroundColor(0x7F313C93);

        colorPicker.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                selectedcolour = color;
                tv_colorcode.setText(colorHex(color));
                ll_colorbox.setBackgroundColor(color);


            }
        });
        tv_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_text.setTextColor(selectedcolour);
                dialog.dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPicker.reset();
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();

    }

    private String colorHex(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format(Locale.getDefault(), "0x%02X%02X%02X%02X", a, r, g, b);
    }
}
