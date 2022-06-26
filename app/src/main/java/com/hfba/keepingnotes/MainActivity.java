package com.hfba.keepingnotes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.app.NotificationCompat;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView myRecyclerView;
    private Adapter myAdapter;
    private RecyclerView.LayoutManager myLayoutManager;
    public static Context context;
    private  ArrayList<Note> Note_List;
    private MaterialButton addNote;
    private int myEditPos;

    //αποθήκευση δεδομένων στην συσκευη σε μορφη json
    private void saveData() {
        SharedPreferences sharedPreferences= getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Note_List);
        editor.putString("Note List", json);
        editor.apply();
    }

    //φορτωση δεδομένων απο την συσκευη
    private void loadData(){
        SharedPreferences sharedPreferences= getSharedPreferences("shared preferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Note List",null);
        Type type =  new TypeToken<ArrayList<Note>>() {}.getType();
        Note_List=gson.fromJson(json,type);
        if(Note_List == null) {
            Note_List =  new ArrayList<>();
        }
    }

    //επιστροφή object απο το child activity NoteActivity και προσθήκη στην note arraylist
    ActivityResultLauncher<Intent> startForResultForNewItem =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result!= null && result.getResultCode() ==RESULT_OK)
                    {
                        if(result.getData() != null)
                        {
                            Note var= result.getData().getParcelableExtra("key");
                            Note_List.add(0,var);
                            myAdapter.notifyItemInserted(0);
                            saveData();
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> startForResultForNoteEdit =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result!= null && result.getResultCode() ==RESULT_OK)
                    {
                        if(result.getData() != null)
                        {
                            Note var= result.getData().getParcelableExtra("key");
                            Note_List.remove(myEditPos);
                            myAdapter.notifyItemRemoved(myEditPos);
                            Note_List.add(0,var);
                            myAdapter.notifyItemInserted(0);
                            saveData();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNoteList();
        buildRecycleView();

        //αναφορά στο add κουμπί
        addNote = findViewById(R.id.addButton);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                startForResultForNewItem.launch(intent);
            }
        });
       context= getApplicationContext();
       notifyThis("keepingNotes", "Your notes are here!");
    }

    private void buildRecycleView() {
        //init vars
        myRecyclerView =  findViewById(R.id.recyclerView);
        myRecyclerView.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(this);
        myAdapter = new Adapter(Note_List);
        myRecyclerView.setLayoutManager(myLayoutManager);
        myRecyclerView.setAdapter(myAdapter);

        //επιλογή item για προβολή και edit
        myAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(MainActivity.this, EditNote.class);
                intent.putExtra("key",(Parcelable) Note_List.get(position));
                myEditPos=position;
                startForResultForNoteEdit.launch(intent);
            }
        });

        //παρατεταμένη επιλογή item για διαγραφη
        myAdapter.setOnItemLongClickListener(new Adapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v,int position) {
                //δημιουργία μενου με τις επιλογες share/ delete
                PopupMenu menu = new PopupMenu(context,v);
                menu.getMenu().add("SHARE");
                menu.getMenu().add("DELETE");
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        //αν επιλεχτεί τότε αφαιρουμε απο την λιστα το item και ειδοποιούμε τον adapter
                        //για να ενημέρωσει το resView
                        if(menuItem.getTitle().equals("SHARE")){
                            Intent sendIntent = new Intent(Intent.ACTION_SEND);
                            sendIntent.setAction(Intent.ACTION_SEND);
                            String title= "Note's title: ";
                            title+= Note_List.get(position).getTitle();
                            sendIntent.putExtra(Intent.EXTRA_TITLE, title);
                            title+= "\n\n"+ Note_List.get(position).getDescritpion();
                            sendIntent.putExtra(Intent.EXTRA_TEXT, title);
                            sendIntent.setType("text/plain");
                            startActivity(Intent.createChooser(sendIntent, "Share note"));
                        }
                        if(menuItem.getTitle().equals("DELETE")){
                            Note_List.remove(position);
                            myAdapter.notifyItemRemoved(position);
                        }
                        return true;
                    }
                });
                menu.show();
            }
        });
    }
    //προσθήκη απλής ειδοποίησης με χρήση NotificationCompat που επιτρέπει ευκολότερο έλεγχο όλων των σημαιών, καθώς και βοήθεια στην κατασκευή των τυπικών διατάξεων ειδοποιήσεων.
    public void notifyThis(String title, String message) {
        NotificationCompat.Builder build = new NotificationCompat.Builder(this.context);
        build.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setTicker("{your tiny message}")
                .setContentTitle(title)
                .setContentText(message)
                .setContentInfo("INFO");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, build.build());
    }

    private void createNoteList() {
        loadData();
    }
}