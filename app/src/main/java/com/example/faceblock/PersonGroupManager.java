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

import com.microsoft.projectoxford.face.FaceServiceClient;
//     import com.microsoft.projectoxford.face.samples.R;
    import com.example.faceblock.helper.LogHelper;
//     import com.microsoft.projectoxford.face.samples.helper.SampleApp;
//     import com.microsoft.projectoxford.face.samples.helper.StorageHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.Context;


public class PersonGroupManager {


    String groupIdKey = getString(R.string.person_group_id);
    String groupNameKey = getString(R.string.group_name);
    SharedPreferences mPrefs = Context.getSharedPreferences( getString(R.string.preference_name), Context.MODE_PRIVATE);

    String personGroupId;
    String personGroupName;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        personGroupId = mPrefs.getString(groupIdKey, " ");
        personGroupName = mPrefs.getString(groupNameKey, " ");

        if(personGroupId == null){
            personGroupId = UUID.randomUUID().toString();
            personGroupName = getString(R.string.person_group_name);

            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.clear();

            mEditor.putString(groupIdKey, personGroupId);
            mEditor.putString(groupNameKey, personGroupId);
            mEditor.commit();

            new AddPersonGroupTask(false).execute(personGroupId);
        }






    }

    private String getString(int string){
        return Resources.getSystem().getString(string);
    }
















}
