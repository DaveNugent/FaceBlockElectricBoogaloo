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
import com.microsoft.projectoxford.face.contract.CreatePersonResult;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.Context;
import android.widget.Toast;


public class PersonGroupManager extends AppCompatActivity{
    String personGroupId;
    String personGroupName;
    String personName;
    String personId;

    EditText editTextPersonName;

    private static final int REQUEST_TAKE_PHOTO = 0;
    private static final int REQUEST_SELECT_IMAGE_IN_ALBUM = 1;

    // The URI of photo taken with camera
    public Uri mUriPhotoTaken;


    String imageUriStr;
    Bitmap bitmap;


    public static final int PICK_IMAGE = 1;
    public static final int USE_CAMERA = 101;

    FaceServiceClient faceServiceClient;

    //////////////////////////////////////////////////////////////////////////
    //Taken and modified from Cognitive-Face sample android studio project //
    ////////////////////////////////////////////////////////////////////////


     // Task taken from PersonGroupActivity to set up a person group in Azure

    class AddPersonGroupTask extends AsyncTask<String, String, String> {
        // Indicate the next step is to add person in this group, or finish editing this group.

        AddPersonGroupTask() { }

        @Override
        protected String doInBackground(String... params) {

            // Get an instance of face service client.
            try{


                // Start creating person group in server.
                faceServiceClient.createLargePersonGroup(
                        params[0],
                        getString(R.string.person_group_name),
                        "Don't block faces");

                return params[0];
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String result) {

            if (result != null) {

            }
        }
    }


     // Task taken from PersonActivity to add a person to the person group


    class AddPersonTask extends AsyncTask<String, String, String> {
        // Indicate the next step is to add face in this person, or finish editing this person.

        boolean takePhoto;

        AddPersonTask (boolean take) {takePhoto = take;}

        @Override
        protected String doInBackground(String... params) {
            // Get an instance of face service client.
            try{
                System.out.println("param[0] = " + params[0]);
                System.out.println("Creating Person");

                // Start the request to creating person.
                CreatePersonResult createPersonResult = faceServiceClient.createPersonInLargePersonGroup(
                        params[0],
                        "Name",
                        "Person Info");

                return createPersonResult.personId.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                System.out.println("Person Created");
                personId = result;
                System.out.println("PersonId = " + personId);
                StorageHelper.addPerson(personName, personId, HomeActivity.App);
                if(takePhoto){
                    takePhoto();
                }
                else {
                    selectImageInAlbum();
                }

            }
        }
    }


     // Methods bellow taken from SelectImageActivity to get face image to apply to person


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
        System.out.println("ActivityResult");
        switch (requestCode)
        {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    System.out.println("Result OK");
                    Uri imageUri;
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    if (photo == null){
                        System.out.println("Intent returned null");
                    } else {
                        System.out.println("Data returned");
                        imageUri = getImageUri(this, photo);
                        System.out.println("Uri: " + imageUri.toString());
                        imageUriStr = imageUri.toString();
                    }



                    Intent intent = new Intent(this, AddFaceToPersonActivity.class);
                    intent.putExtra("PersonId", personId);
                    intent.putExtra("PersonGroupId", personGroupId);
                    intent.putExtra("ImageUriStr", imageUriStr);
                    startActivity(intent);
                }
                else{
                    System.out.println("Result Not Ok");
                }
                    break;
            case REQUEST_SELECT_IMAGE_IN_ALBUM:
                if (resultCode == RESULT_OK) {
                    System.out.println("Result OK");
                    Uri imageUri;
                    imageUri = data.getData();
                    System.out.println("Uri: " + imageUri.toString());
                    imageUriStr = imageUri.toString();

                    Intent intent = new Intent(this, AddFaceToPersonActivity.class);
                    intent.putExtra("PersonId", personId);
                    intent.putExtra("PersonGroupId", personGroupId);
                    intent.putExtra("ImageUriStr", imageUriStr);
                    startActivity(intent);

                }else{
                    System.out.println("Result Not Ok");
                }



                break;
            default:
                System.out.println("Request has no type");
                break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // When the button of "Take a Photo with Camera" is pressed.
    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            // Save the photo taken to a temporary file.
 //           File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
//                File file = File.createTempFile("IMG_", ".jpg", storageDir);
//                mUriPhotoTaken = Uri.fromFile(file);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
//                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
   //             Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                System.out.println("Request take photo");
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            } catch (Exception e) {
                    e.printStackTrace();
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


    class TrainPersonGroupTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            // Get an instance of face service client.
            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            try{


                faceServiceClient.trainLargePersonGroup(params[0]);
                return params[0];
            } catch (Exception e) {

                return null;
            }
        }


        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                Toast toast = Toast.makeText(HomeActivity.App, "Training Complete!", Toast.LENGTH_SHORT);
                toast.show();
            }
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
        editTextPersonName = (EditText)findViewById(R.id.edit_person_name);
        faceServiceClient = SampleApp.getFaceServiceClient();

        System.out.println("personGroupId = " + personGroupId);


    }

    public void onTakePictureClicked(View v) {

        personName = editTextPersonName.getText().toString();
        System.out.println("Take Picture for: " + personName);

        if(!isNameEmpty(personName)){
            if(doesPersonExist(personName)){
                personId = StorageHelper.getPersonId(personName, HomeActivity.App);
                takePhoto();
            }
            else {
                System.out.println("Create new Person");
                new AddPersonTask(true).execute(personGroupId);
            }
        }
    }
    public void onAddFromGalleryClicked(View v) {


        personName = editTextPersonName.getText().toString();

        System.out.println("Get Picture for: " + personName);

        if(!isNameEmpty(personName)){
            if(doesPersonExist(personName)) {
                personId = StorageHelper.getPersonId(personName, HomeActivity.App);
                System.out.println("personId = " + personId);
                selectImageInAlbum();
            }
            else {
                System.out.println("Create new Person");
                new AddPersonTask(false).execute(personGroupId);
            }

        }
    }

    public void onResetClicked(View v) {
        createPersonGroup();
        StorageHelper.deleteAll(HomeActivity.App);
    }

    public void onTrainClicked(View v){
        new TrainPersonGroupTask().execute(personGroupId);
        Toast toast = Toast.makeText(HomeActivity.App, "Training Whitelist...", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void createPersonGroup() {
        personGroupId = UUID.randomUUID().toString();
        personGroupName = getString(R.string.person_group_name);

        System.out.println("PersonGroupId = /" + personGroupId + "/");

        StorageHelper.setPersonGroupId(personGroupId, HomeActivity.App);
        StorageHelper.setPersonGroupName(personGroupName, HomeActivity.App);

        new AddPersonGroupTask().execute(personGroupId);
    }

    public boolean isNameEmpty(String newPersonName) {

        if (newPersonName.equals("")) {
            System.out.println("Name Empty");
            Toast.makeText(this, "Please enter Name", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            Toast toast = Toast.makeText(HomeActivity.App, "Please Enter A Name", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

    }

    public boolean doesPersonExist(String newPersonName){

        Set<String> personNames = StorageHelper.getAllPersonNames(HomeActivity.App);
        for (String name: personNames){
            if(name.equals(newPersonName)){
                System.out.println(newPersonName + " does exist");
                return true;
            }
        }
        System.out.println(newPersonName + " doesn't exist");
        return false;

    }
















}
