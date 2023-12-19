package com.example.mypage;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class AddPetActivity extends AppCompatActivity {
    Button saveAll;
    MainActivity.petDBHelper petDB;
    SQLiteDatabase sqlDB;
    EditText petName, petKind;
    RadioButton petF, petM;
    RadioButton dog, cat;
    RadioButton neuter, notNeuter;
    DatePicker petBDay;
    ImageButton imageButton;
    ImageView imgView;
    private ActivityResultLauncher<Intent> galleryLauncher;

    CheckBox[] allergy = new CheckBox[11];
    int userID = 1; // 나중에 userTable에서 가져와야 함.
    String name;
    String animal;
    String sex;
    String kind;
    String bDay;
    String allergies;
    String uri;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.change);
        saveAll = findViewById(R.id.btnSave);
        petName = findViewById(R.id.editPetName);
        petKind = findViewById(R.id.editPetKind);
        petF = findViewById(R.id.radioFemale);
        petM = findViewById(R.id.radioMale);
        petDB = new MainActivity.petDBHelper(this);
        imageButton = findViewById(R.id.btnImg);
        imgView = findViewById(R.id.imgView);

        allergy = new CheckBox[11];
        for (int i = 0; i < allergy.length; i++) {
            int checkBoxId = getResources().getIdentifier("ch" + (i + 1), "id", getPackageName());
            allergy[i] = findViewById(checkBoxId);
        }

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        uri = selectedImageUri.toString();
                        if (selectedImageUri != null) {
                            Glide.with(this).load(selectedImageUri).placeholder(R.drawable.dogicon).into(imgView);
                        }
                    }
                });

        imageButton.setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
        });

        saveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = petName.getText().toString();
                kind = petKind.getText().toString();
                if (petF.isChecked()) sex = "여자";
                else sex = "남자";
                dog = findViewById(R.id.radioDog);
                cat = findViewById(R.id.radioCat);
                if (dog.isChecked()) animal = "강아지";
                else animal = "고양이";
                neuter = findViewById(R.id.radioNeutered);
                notNeuter = findViewById(R.id.radioNotNeutered);
                if (neuter.isChecked()) sex = sex + ", " + "중성화 O";
                else sex = sex + ", " + "중성화 X";
                petBDay = findViewById(R.id.datePickerBirth);
                int year = petBDay.getYear();
                int month = petBDay.getMonth() + 1; // 월은 0부터 시작하므로 1을 더함
                int dayOfMonth = petBDay.getDayOfMonth();
                bDay = year + "년 " + month + "월 " + dayOfMonth + "일";
                allergies = " ";
                for (int i = 0; i < allergy.length; i++) {
                    if (allergy[i].isChecked()) allergies  = allergies + " " + allergy[i].getText().toString();
                }

                sqlDB = petDB.getWritableDatabase();
                sqlDB.execSQL("INSERT INTO petTable VALUES (1, " + userID + ", '" + animal + "', '" + name + "', '" +
                        sex + "', '" + kind + "', '" + bDay + "', '" + allergies + "', '" + uri + "');");
                sqlDB.close();
                Toast.makeText(getApplicationContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show();
                finish();

            }
        });

    }
}
