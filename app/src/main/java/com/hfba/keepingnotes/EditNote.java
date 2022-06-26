package com.hfba.keepingnotes;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.google.android.material.button.MaterialButton;
import java.text.DateFormat;

public class EditNote extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView IV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note_view);
        //παραλαβη του αντικειμένου του item που επιλέχτηκε απο το mainactivity
        Note note=getIntent().getParcelableExtra("key");
        //αναφορα στο περιεχόμενο
        EditText titleIN = findViewById(R.id.TextTitleEdit);
        EditText descIN = findViewById(R.id.TextDescEdit);
        MaterialButton saveNote = findViewById(R.id.saveNoteBut);
        //να μπει και για αρχεια
        MaterialButton addPic = findViewById(R.id.addPicb);
        //εμφανιση περιεχομένου
        titleIN.setText(note.getTitle());
        descIN.setText(note.getDescritpion());

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //παιρνουμε τα δεδομενα
                Editable titleTxt = (Editable) titleIN.getText();
                Editable DescTxt = (Editable) descIN.getText();
                String title = titleTxt.toString();
                String description = DescTxt.toString();
                //με την system.currentTime παιρνουμε τον χρονο οπου δημιουργήθηκε το note και επειτα
                // το διαμορφώνουμε σε μορφή ημερομηνίας
                Long time = (System.currentTimeMillis());
                String formatedTime= DateFormat.getDateTimeInstance().format(time);
                Note myNewNote= new Note(title,description,formatedTime);
                //δημιουργία intent για αποστολή του object πισω στον πατερα - mainActivity
                Intent intent = new Intent();
                intent.putExtra("key", (Parcelable) myNewNote);
                setResult(RESULT_OK, intent);
                //closes the activity
                finish();
            }
        });

        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            IV.setImageURI(imageUri);
        }
    }
}