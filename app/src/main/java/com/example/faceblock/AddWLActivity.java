package com.example.faceblock;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.faceblock.helper.LogHelper;
import com.example.faceblock.helper.SampleApp;
import com.example.faceblock.helper.StorageHelper;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class AddWLActivity extends AppCompatActivity {

    Button btnTakePicture;
    Button btnAddFromGallery;

    public static final int PICK_IMAGE = 1;
    public static final int USE_CAMERA = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wl);

        btnAddFromGallery = findViewById(R.id.btnAddFromGallery);
        btnTakePicture = findViewById(R.id.btnTakePicture);

        btnAddFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE);
            }
        });


        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, USE_CAMERA);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            Toast.makeText(AddWLActivity.this, "You picked an image", Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == USE_CAMERA){
            Toast.makeText(AddWLActivity.this, "You took a picture", Toast.LENGTH_SHORT).show();
        }
    }

    // Background task of adding a person group.
    class AddPersonGroupTask extends AsyncTask<String, String, String> {
        // Indicate the next step is to add person in this group, or finish editing this group.
        boolean mAddPerson;

        AddPersonGroupTask(boolean addPerson) {
            mAddPerson = addPerson;
        }

        @Override
        protected String doInBackground(String... params) {

            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{

                // Start creating person group in server.
                faceServiceClient.createLargePersonGroup(
                        params[0],
                        "user_provided_person_group_name",
                        "user_provided_person_group_description_data");

                return params[0];
            } catch (Exception e) {
//                publishProgress(e.getMessage());
//                addLog(e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            //setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            //setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //FIXME
//            progressDialog.dismiss();
//
//            if (result != null) {
//                addLog("Response: Success. Person group " + result + " created");
//
//                personGroupExists = true;
//                GridView gridView = (GridView) findViewById(R.id.gridView_persons);
//                personGridViewAdapter = new PersonGridViewAdapter();
//                gridView.setAdapter(personGridViewAdapter);
//
//                //setInfo("Success. Group " + result + " created");
//
//                if (mAddPerson) {
//                    addPerson();
//                } else {
//                    doneAndSave(false); //FIXME
//                }
//            }
        }
    }

    class TrainPersonGroupTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //addLog("Request: Training group " + params[0]);

            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                //publishProgress("Training person group...");

                faceServiceClient.trainLargePersonGroup(params[0]);
                return params[0];
            } catch (Exception e) {
//                publishProgress(e.getMessage());
//                addLog(e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            //setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            //setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //progressDialog.dismiss();

            if (result != null) {
                //addLog("Response: Success. Group " + result + " training completed");

                finish();
            }
        }
    }

    // Background task of face detection.
    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        private boolean mSucceed = true;

        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try {

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        false,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        null);
            } catch (Exception e) {
                mSucceed = false;
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            //setUiBeforeBackgroundTask(); FIXME
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            //setUiDuringBackgroundTask(progress[0]); FIXME
        }

        @Override
        protected void onPostExecute(Face[] faces) {
            if (mSucceed) {
//                addLog("Response: Success. Detected " + (faces == null ? 0 : faces.length)
//                        + " Face(s)");
                //FIXME
            }

            // Show the result on screen when detection is done.
            //setUiAfterDetection(faces, mSucceed);
        }
    }

    public void addPerson(View view) {
        if (!personGroupExists) {
            new AddPersonGroupTask(true).execute(personGroupId);
        } else {
            addPerson();
        }
    }

    //FIXME
    private void addPerson() {
        //FIXME
    }

    boolean addNewPersonGroup;
    boolean personGroupExists;
    String personGroupId;
    String oldPersonGroupName;



}
