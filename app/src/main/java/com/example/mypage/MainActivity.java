package com.example.mypage;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class Pet implements Serializable {
    int userID;
    // int userID = userTable.userID; ->이거 커서로 가져와야 할듯?
    int id;
    String name;
    String animal;
    String sex;
    String kind;
    String bDay;
    String allergy;
    String uri;

    public int getUserID() {
        return userID;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAnimal() {
        return animal;
    }

    public String getSex() {
        return sex;
    }

    public String getKind() {
        return kind;
    }

    public String getbDay() {
        return bDay;
    }

    public String getAllergy() {
        return allergy;
    }
    public String getUri() {return uri;}

    public Pet (int userID, String name, String animal, String sex, String kind, String bDay, String allergy, String uri) {
        this.id = 1;
        this.userID = userID;
        this.name = name;
        this.animal = animal;
        this.sex = sex;
        this.kind = kind;
        this.bDay = bDay;
        this.allergy = allergy;
        this.uri = uri;
    }
}

public class MainActivity extends AppCompatActivity {

    Button addButton;
    Button changeButton;
    petDBHelper petDB;
    TextView mainName, mainSex, mainKind, mainBDay, mainAllergy;
    ImageView imgView;
    int userID = 1;
    // 나중에 바꿔야 함.
@Override
protected void onResume() {
    super.onResume();

    // 여기에 화면 갱신 또는 업데이트 관련 작업을 추가
    if (isDataExists()) {
        changeButton.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.INVISIBLE);

        Pet nPet = getSinglePet(userID);
        mainName.setText(nPet.getName());
        mainKind.setText(nPet.getKind());
        mainBDay.setText(nPet.getbDay());
        mainSex.setText(nPet.getSex());
        mainAllergy.setText(nPet.getAllergy());
        Glide.with(this).load(nPet.getUri()).placeholder(R.drawable.dogicon).into(imgView);

    } else {
        changeButton.setVisibility(View.INVISIBLE);
        addButton.setVisibility(View.VISIBLE);
    }
}


    private boolean isDataExists() {
        petDB = new petDBHelper(this);
        SQLiteDatabase db = petDB.getReadableDatabase();

        // COUNT 함수를 사용하여 데이터가 있는지 확인
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM petTable", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();

        return count > 0;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        petDB = new petDBHelper(this);
        mainName = findViewById(R.id.mainName);
        mainSex = findViewById(R.id.mainSex);
        mainKind = findViewById(R.id.mainKind);
        mainBDay = findViewById(R.id.mainBDay);
        mainAllergy = findViewById(R.id.mainAllergy);
        imgView = findViewById(R.id.imgViewMain);

        addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPetActivity.class);
                startActivity(intent);
            }
        });
        changeButton = (Button) findViewById(R.id.changeButton);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pet pet = getSinglePet(userID);
                // 조회한 정보를 다음 액티비티로 전달하는 Intent 생성
                Intent intent = new Intent(MainActivity.this, changePetActivity.class);
                intent.putExtra("petInfo", pet);
                startActivity(intent);

            }
        });

    }

    public Pet getSinglePet(int userID) {
        petDBHelper petDB = new petDBHelper(this);
        SQLiteDatabase db = petDB.getReadableDatabase();

        Pet userPet = null;

        String[] columns = {"petID", "userID", "animal", "petName", "petSex", "petKind", "petBDay", "petAllergy", "uri"};
        String selection = "userID = ?";
        String[] selectionArgs = {String.valueOf(userID)};

        Cursor cursor = db.query("petTable", columns, selection, selectionArgs, null, null, null, "1");

        if (cursor.moveToFirst()) {
            int petID = cursor.getInt(cursor.getColumnIndexOrThrow("petID"));
            String animal = cursor.getString(cursor.getColumnIndexOrThrow("animal"));
            String petName = cursor.getString(cursor.getColumnIndexOrThrow("petName"));
            String petSex = cursor.getString(cursor.getColumnIndexOrThrow("petSex"));
            String petKind = cursor.getString(cursor.getColumnIndexOrThrow("petKind"));
            String petBDay = cursor.getString(cursor.getColumnIndexOrThrow("petBDay"));
            String petAllergy = cursor.getString(cursor.getColumnIndexOrThrow("petAllergy"));
            String uri = cursor.getString(cursor.getColumnIndexOrThrow("uri"));

            userPet = new Pet(userID, petName, animal, petSex, petKind, petBDay, petAllergy, uri);
        }

        cursor.close();
        db.close();

        return userPet;
    }


    public static class petDBHelper extends SQLiteOpenHelper {
        public petDBHelper(Context context)
        {
            super(context, "petDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE petTable  (petID INTEGER, userID INTEGER, animal TEXT, petName TEXT, petSex TEXT, petKind TEXT, petBDay TEXT, petAllergy TEXT, uri TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            db.execSQL("DROP TABLE IF EXISTS petDB");
            onCreate(db);
        }

    }
}