package com.example.faceblock;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import android.content.SharedPreferences;

import com.example.faceblock.helper.SampleApp;

import com.example.faceblock.helper.StorageHelper;
import com.microsoft.projectoxford.face.FaceServiceClient;
//     import com.microsoft.projectoxford.face.samples.R;
    import com.example.faceblock.helper.LogHelper;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
//     import com.microsoft.projectoxford.face.samples.helper.SampleApp;
//     import com.microsoft.projectoxford.face.samples.helper.StorageHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.Context;


public class PersonGroupManager extends AppCompatActivity{
    String personGroupId;
    String personGroupName;
    String personId;

    public static final int PICK_IMAGE = 1;
    public static final int USE_CAMERA = 101;

    FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();

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

                personGroupExists = true;
                GridView gridView = (GridView) findViewById(R.id.gridView_persons);
                personGridViewAdapter = new PersonGridViewAdapter();
                gridView.setAdapter(personGridViewAdapter);

                setInfo("Success. Group " + result + " created");

                if (mAddPerson) {
                    addPerson();
                } else {
                    doneAndSave(false);
                }
            }
        }
    }

    class AddPersonTask extends AsyncTask<String, String, String> {
        // Indicate the next step is to add face in this person, or finish editing this person.

        AddPersonTask () {}

        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{
                publishProgress("Syncing with server to add person...");

                // Start the request to creating person.
                CreatePersonResult createPersonResult = faceServiceClient.createPersonInLargePersonGroup(
                        params[0],
                        "Name",
                        "Person Info");

                return createPersonResult.personId.toString();
            } catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                personId = result;

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wl);

        personGroupId = StorageHelper.getPersonGroupId(PersonGroupManager.this);
        personGroupName = StorageHelper.getPersonGroupName(PersonGroupManager.this);

        if(personGroupId.equals(" ")){
            createPersonGroup();
        }


    }

    public void onTakePictureClicked(View v) {

        EditText editTextPersonName = (EditText)findViewById(R.id.edit_person_name);
        String name = editTextPersonName.getText().toString();

        if(!isNameEmpty(name)){
            if(doesPersonExist()){

            }
            else {
                new AddPersonTask().execute(personGroupId);
            }
        }
    }
    public void onAddFromGalleryClicked(View v) {

        EditText editTextPersonName = (EditText)findViewById(R.id.edit_person_name);
        String name = editTextPersonName.getText().toString();

        if(!isNameEmpty(name)){
            if(doesPersonExist()){

            }
            else {
                new AddPersonTask().execute(personGroupId);
            }
        }
    }
    }
    public void onResetClicked(View v) {


    }

    public void createPersonGroup() {
        personGroupId = UUID.randomUUID().toString();
        personGroupName = getString(R.string.person_group_name);

        StorageHelper.setPersonGroupId(personGroupId, PersonGroupManager.this);
        StorageHelper.setPersonGroupName(personGroupName, PersonGroupManager.this);

        new AddPersonGroupTask(false).execute(personGroupId);
    }

    public boolean isNameEmpty(String newPersonName) {

        if (newPersonName.equals("")) {
            return true;
        }
        else {
            return false;
        }

    }

    public boolean doesPersonExist(){
        EditText editTextPersonName = (EditText)findViewById(R.id.edit_person_name);
        String newPersonName = editTextPersonName.getText().toString();
        Set<String> personNames = StorageHelper.getAllPersonNames(PersonGroupManager.this);
        for (String name: personNames){
            if(name.equals(newPersonName)){
                return true;
            }
        }
        return false;

    }
















}
