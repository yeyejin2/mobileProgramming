package com.example.mypage;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class changePetActivity extends AppCompatActivity {
    Button saveAll;
    MainActivity.petDBHelper petDB;
    SQLiteDatabase sqlDB;
    EditText petName, petKind;
    RadioButton petF, petM;
    RadioButton dog, cat;
    RadioButton neuter, notNeuter;
    DatePicker petBDay;
    CheckBox[] allergy = new CheckBox[11];
    ImageView imgView;
    String name;
    String animal;
    String sex;
    String kind;
    String bDay;
    String allergies;
    String uri;
    int userID = 1; // 나중에 가져올 것.
    private ActivityResultLauncher<Intent> galleryLauncher;
    ImageButton imageButton;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.change);
        petName = findViewById(R.id.editPetName);
        petKind = findViewById(R.id.editPetKind);
        petF = findViewById(R.id.radioFemale);
        petM = findViewById(R.id.radioMale);
        petDB = new MainActivity.petDBHelper(this);
        dog = findViewById(R.id.radioDog);
        cat = findViewById(R.id.radioCat);
        neuter = findViewById(R.id.radioNeutered);
        notNeuter = findViewById(R.id.radioNotNeutered);
        petBDay = findViewById(R.id.datePickerBirth);
        imgView = findViewById(R.id.imgView);
        imageButton = findViewById(R.id.btnImg);

        allergy = new CheckBox[11];
        for (int i = 0; i < allergy.length; i++) {
            int checkBoxId = getResources().getIdentifier("ch" + (i + 1), "id", getPackageName());
            allergy[i] = findViewById(checkBoxId);
        }

        Intent intent = getIntent();
        if (intent!=null) {
            Pet receivedPet = (Pet) intent.getSerializableExtra("petInfo");

            if (receivedPet != null) {
                Glide.with(this).load(receivedPet.getUri()).placeholder(R.drawable.dogicon).into(imgView);
                petName.setText(receivedPet.getName());
                petKind.setText(receivedPet.getKind());
                animal = receivedPet.getAnimal();
                if (animal.equals("강아지")) dog.setChecked(true);
                else cat.setChecked(true);
                sex = receivedPet.getSex();
                if (sex.substring(0, 2).equals("여자")) petF.setChecked(true);
                else petM.setChecked(true);
                if (sex.substring(4, 9).equals("중성화 O")) neuter.setChecked(true);
                else notNeuter.setChecked(true);
                bDay = receivedPet.getbDay();
                String yearString = bDay.substring(0, bDay.indexOf("년"));

                String remainingString = bDay.substring(bDay.indexOf("년") + 2);
                String monthString = remainingString.substring(0, remainingString.indexOf("월"));

                String dayString = bDay.substring(bDay.indexOf("월") + 2, bDay.indexOf("일"));

                int year = Integer.parseInt(yearString);
                int month = Integer.parseInt(monthString);
                int dayOfMonth = Integer.parseInt(dayString);

                petBDay.updateDate(year, month - 1, dayOfMonth);
                allergies = receivedPet.getAllergy();
                String[] allergyArray = allergies.trim().split(" ");

                for (String allergyName : allergyArray) {
                    for (CheckBox checkbox : allergy) {
                        if (checkbox.getText().toString().equals(allergyName)) {
                            checkbox.setChecked(true);
                            break;
                        }
                    }
                }

            }
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

        saveAll = findViewById(R.id.btnSave);
        saveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = petName.getText().toString();
                kind = petKind.getText().toString();
                if (petF.isChecked()) sex = "여자";
                else sex = "남자";
                if (dog.isChecked()) animal = "강아지";
                else animal = "고양이";
                if (neuter.isChecked()) sex = sex + ", " + "중성화 O";
                else sex = sex + ", " + "중성화 X";
                int year = petBDay.getYear();
                int month = petBDay.getMonth() + 1; // 월은 0부터 시작하므로 1을 더함
                int dayOfMonth = petBDay.getDayOfMonth();
                bDay = year + "년 " + month + "월 " + dayOfMonth + "일";
                allergies = " ";
                for (int i = 0; i < allergy.length; i++) {
                    if (allergy[i].isChecked()) allergies  = allergies + " " + allergy[i].getText().toString();
                }

                sqlDB = petDB.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("animal", animal);
                values.put("petName", name);
                values.put("petSex", sex);
                values.put("petKind", kind);
                values.put("petBDay", bDay);
                values.put("petAllergy", allergies);
                if (uri != null) {
                    values.put("uri", uri);
                }

                String[] whereArgs = {String.valueOf(userID)};
                sqlDB.update("petTable", values, "userID=?", whereArgs);

                Toast.makeText(getApplicationContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}


