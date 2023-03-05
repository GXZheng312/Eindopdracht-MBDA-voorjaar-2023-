package com.example.foodnutrition;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.Manifest;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.InputStream;

public class DetailFragment extends Fragment {

    Dish dish;
    public static final String DISH_PARCEL = "dish";
    private static final int PERMISSION_REQUEST_WRITE_CALENDAR = 1;

    TextView titleTextView;
    TextView instructionTextView;
    Button saveAgendaButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        titleTextView = view.findViewById(R.id.titleTextView);
        instructionTextView = view.findViewById(R.id.instructionsTextView);
        ImageButton shareButton = (ImageButton) view.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                shareRecipe(view);
            }
        });

        ImageButton backgroundChangeButton = (ImageButton) view.findViewById(R.id.backgroundButton);
        backgroundChangeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectAndChangeBackground(view);
            }
        });

        //Toolbar shareToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        MainActivity mainActivity = (MainActivity) getActivity();
//        mainActivity.setSupportActionBar(shareToolbar);
        saveAgendaButton = view.findViewById(R.id.saveAgendaButton);
//        imageView = view.findViewById(R.id.imageView);
        saveAgendaButton.setOnClickListener(view1 -> onSaveInAgenda(view1));

        Bundle argument = getArguments();
        if(argument != null) {
            Dish dish = argument.getParcelable(DISH_PARCEL, Dish.class);
            setDish(dish);
        }


        return view;
    }

    /**
     * Opens gallery to select a background image
     * @param view
     */
    public void selectAndChangeBackground(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, 42);



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 42 && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
//                String[] filePathColumn = { MediaStore.MediaColumns.DATA };
//
//                // Get the cursor
//
//                MainActivity mainActivity = (MainActivity) getActivity();
//
//                Cursor cursor = mainActivity.getContentResolver().query(selectedImage,
//                        filePathColumn, null, null, null);
//                // Move to first row
//                cursor.moveToFirst();
//
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                String imgpath = cursor.getString(columnIndex);
//                Log.d("path", imgpath);
//                cursor.close();
//
//                SharedPreferences sp;
//                sp=mainActivity.getSharedPreferences("setback", MODE_PRIVATE);
//                SharedPreferences.Editor edit=sp.edit();
//                edit.putString("imagepath",imgpath);
//                edit.commit();



                final InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                Bitmap myBitmap = BitmapFactory.decodeStream(imageStream);

//                ImageView myImage = getView().findViewById(R.id.backgroundButton);
//                myImage.setImageBitmap(myBitmap);

//                Bitmap myBitmap = BitmapFactory.decodeFile(imgpath);

                // get instructionsTextView
                TextView instructionsTextView = getView().findViewById(R.id.instructionsTextView);
                instructionsTextView.setBackground(new BitmapDrawable(getResources(), myBitmap));
//                myImage.setImageBitmap(myBitmap);
            }
            else {
                Toast.makeText(getContext(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void setDish(Dish dish) {
        this.dish = dish;
        DisplaySelectedDish();
    }

    public void onSaveInAgenda(View view){
        Log.d("my", "onSaveInAgenda: inside");
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.d("my", "onSaveInAgenda: requesting");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    PERMISSION_REQUEST_WRITE_CALENDAR);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_CALENDAR) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Calendar permission is approved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Calendar permission is denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void DisplaySelectedDish() {
        if (dish == null) {
            return;
        }
        titleTextView.setText(dish.getTitle());
        instructionTextView.setText(dish.getInstructions());
        instructionTextView.setText(Html.fromHtml(dish.getInstructions(), Html.FROM_HTML_MODE_COMPACT));

    }

    public void shareRecipe(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Here is the recipe to make " + dish.getTitle() + " ! " + Html.fromHtml(dish.getInstructions()));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Recipe for " + dish.getTitle());
        Intent shareIntent = Intent.createChooser(intent, "Share");
        startActivity(shareIntent);
    }
}
