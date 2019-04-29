package com.example.faceblock;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.faceblock.helper.SampleApp;
import com.example.faceblock.helper.StorageHelper;
import com.microsoft.projectoxford.face.FaceServiceClient;

import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    Button btnCamera;
    Button btnAddWL;

    String personGroupId;
    String personGroupName;

    public static Context App;


    class AddPersonGroupTask extends AsyncTask<String, String, String> {
        // Indicate the next step is to add person in this group, or finish editing this group.

        AddPersonGroupTask() { }

        @Override
        protected String doInBackground(String... params) {

            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Syncing with server to add person group...");

                // Start creating person group in server.
                faceServiceClient.createPersonGroup(
                        params[0],
                        getString(R.string.person_group_name),
                        "Don't block faces");

                return params[0];
            } catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }
        }


        @Override
        protected void onPostExecute(String result) {

            if (result != null) {

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App = getApplicationContext();
        setContentView(R.layout.activity_home);

        personGroupId = StorageHelper.getPersonGroupId(HomeActivity.App);
        personGroupName = StorageHelper.getPersonGroupName(HomeActivity.App);

        if(personGroupId.equals(" ")){
            createPersonGroup();
        }

        btnAddWL = findViewById(R.id.btnAddWL);
        btnCamera = findViewById(R.id.btnCamera);

        btnAddWL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddWLActivity.class);
                startActivity(intent);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, FaceTrackerActivity.class);
                startActivity(intent);
            }
        });
    }

    public void createPersonGroup() {
        personGroupId = UUID.randomUUID().toString();
        personGroupName = getString(R.string.person_group_name);

        StorageHelper.setPersonGroupId(personGroupId, App);
        StorageHelper.setPersonGroupName(personGroupName, App);

        new HomeActivity.AddPersonGroupTask().execute(personGroupId);
    }

    public static Context getContext(){
        return App;
    }
}
