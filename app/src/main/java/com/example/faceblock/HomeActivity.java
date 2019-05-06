package com.example.faceblock;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.faceblock.helper.SampleApp;
import com.example.faceblock.helper.StorageHelper;
import com.microsoft.projectoxford.face.FaceServiceClient;

import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    Button btnCamera;
    Button btnAddWL;

    String personGroupId;
    String personGroupName;

    public static Context App; //Context of app used for Shared Preferences


    /*
     * Task taken from PersonGroupActivity to set up a person group in Azure
     */
    class AddPersonGroupTask extends AsyncTask<String, String, String> {

        AddPersonGroupTask() { }

        @Override
        protected String doInBackground(String... params) {

            /* Get an instance of face service client. */
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();

            try{
               // System.out.println("creating personGroup");

                /* Start creating person group in server. */
                faceServiceClient.createLargePersonGroup(
                        params[0], //UUID String to be PersonGroupID
                        getString(R.string.person_group_name), //name of person group in Azure
                        "Don't block faces"); //Description of person group in Azure

                return params[0];
            } catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }
        }


        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                //System.out.println("Person Group created");

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

        //System.out.println("PersonGroupId = /" + personGroupId + "/");

        if(personGroupId.equals(" ")){
            createPersonGroup();
        }

        btnAddWL = findViewById(R.id.btnAddWL);
        btnCamera = findViewById(R.id.btnCamera);

        btnAddWL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PersonGroupManager.class);
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

    /* Method to create new personGroup when reset is pressed */
    public void createPersonGroup() {
        personGroupId = UUID.randomUUID().toString();
        personGroupName = getString(R.string.person_group_name);

        //System.out.println("persongroupid = " + personGroupId);

        StorageHelper.setPersonGroupId(personGroupId, App);
        StorageHelper.setPersonGroupName(personGroupName, App);

        new HomeActivity.AddPersonGroupTask().execute(personGroupId);
    }
}
