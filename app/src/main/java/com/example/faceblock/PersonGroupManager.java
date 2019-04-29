package com.example.faceblock;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.Context;


public class PersonGroupManager extends AppCompatActivity{
    String personGroupId;
    String personGroupName;
    String personName;
    String personId;
    boolean personReady;
    boolean prevFaceAdded;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 1;

    // The URI of photo taken with camera
    private Uri mUriPhotoTaken;


    String imageUriStr;
    Bitmap bitmap;


    public static final int PICK_IMAGE = 1;
    public static final int USE_CAMERA = 101;

    FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();

    //////////////////////////////////////////////////////////////////////////
    //Taken and modified from Cognitive-Face sample android studio project //
    ////////////////////////////////////////////////////////////////////////

    /*
     * Task taken from PersonGroupActivity to set up a person group in Azure
     */
    class AddPersonGroupTask extends AsyncTask<String, String, String> {
        // Indicate the next step is to add person in this group, or finish editing this group.

        AddPersonGroupTask() { }

        @Override
        protected String doInBackground(String... params) {

            // Get an instance of face service client.
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

    /*
     * Task taken from PersonActivity to add a person to the person group
     */

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
                CreatePersonResult createPersonResult = faceServiceClient.createPerson(
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
                StorageHelper.addPerson(personName, personId, HomeActivity.App);
                personReady = true;

            }
        }
    }

    /*
     * Methods bellow taken from SelectImageActivity to get face image to apply to person
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ImageUri", mUriPhotoTaken);
    }

    // Recover the saved state when the activity is recreated.
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUriPhotoTaken = savedInstanceState.getParcelable("ImageUri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_TAKE_PHOTO:
            case REQUEST_SELECT_IMAGE_IN_ALBUM:
                if (resultCode == RESULT_OK) {
                    Uri imageUri;
                    if (data == null || data.getData() == null) {
                        imageUri = mUriPhotoTaken;
                    } else {
                        imageUri = data.getData();
                    }

                    imageUriStr = imageUri.toString();

                    /*

                    Intent intent = new Intent();
                    intent.setData(imageUri);
                    setResult(RESULT_OK, intent);
                    finish();

                    */
                }
                break;
            default:
                break;
        }
    }

    // When the button of "Take a Photo with Camera" is pressed.
    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            // Save the photo taken to a temporary file.
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File file = File.createTempFile("IMG_", ".jpg", storageDir);
                mUriPhotoTaken = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            } catch (IOException e) {

            }
        }
    }

    // When the button of "Select a Photo in Album" is pressed.
    public void selectImageInAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM);
        }
    }
















    /////////////////////////////////////////////////////////
    //////////////////// Custom Methods ////////////////////
    ///////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wl);

        personGroupId = StorageHelper.getPersonGroupId(HomeActivity.App);
        personGroupName = StorageHelper.getPersonGroupName(HomeActivity.App);

        if(personGroupId.equals(" ")){
            createPersonGroup();
        }


    }

    public void onTakePictureClicked(View v) {

        EditText editTextPersonName = (EditText)findViewById(R.id.edit_person_name);
        personName = editTextPersonName.getText().toString();
        personReady = false;

        if(!isNameEmpty(personName)){
            if(doesPersonExist(personName)){
                personId = StorageHelper.getPersonId(personName, HomeActivity.App);
            }
            else {
                new AddPersonTask().execute(personGroupId);
            }

            takePhoto();
        }
    }
    public void onAddFromGalleryClicked(View v) {

        EditText editTextPersonName = (EditText)findViewById(R.id.edit_person_name);
        personName = editTextPersonName.getText().toString();
        personReady = false;

        if(!isNameEmpty(personName)){
            if(doesPersonExist(personName)) {
                personId = StorageHelper.getPersonId(personName, HomeActivity.App);
            }
            else {
                new AddPersonTask().execute(personGroupId);
            }

            selectImageInAlbum();

        }
    }

    public void onResetClicked(View v) {

        createPersonGroup();
        StorageHelper.deleteAll(HomeActivity.App);
    }

    public void createPersonGroup() {
        personGroupId = UUID.randomUUID().toString();
        personGroupName = getString(R.string.person_group_name);

        StorageHelper.setPersonGroupId(personGroupId, HomeActivity.App);
        StorageHelper.setPersonGroupName(personGroupName, HomeActivity.App);

        new AddPersonGroupTask().execute(personGroupId);
    }

    public boolean isNameEmpty(String newPersonName) {

        if (newPersonName.equals("")) {
            return true;
        }
        else {
            return false;
        }

    }

    public boolean doesPersonExist(String newPersonName){

        Set<String> personNames = StorageHelper.getAllPersonNames(HomeActivity.App);
        for (String name: personNames){
            if(name.equals(newPersonName)){
                return true;
            }
        }
        return false;

    }
















}
