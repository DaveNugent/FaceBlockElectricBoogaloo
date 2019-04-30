<<<<<<< HEAD
package com.example.faceblock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.example.faceblock.helper.StorageHelper;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.TrainingStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class WhitelistChecker extends AsyncTask<UUID, String, IdentifyResult[]> {

    //private class IdentificationTask extends AsyncTask<UUID, String, IdentifyResult[]>{}

    private OnTaskCompleteListener listener;
//    public WhitelistChecker(OnTaskCompleteListener listener){
//        this.listener = listener;
//    }

    String mPersonGroupId = StorageHelper.getPersonGroupId(HomeActivity.App);

    private boolean mSucceed = true;
    private static final double CANDIDATE_THRESHOLD = 0.5;

    boolean detected;

    @Override
    protected IdentifyResult[] doInBackground(UUID... params) {

        FaceServiceClient faceServiceClient = FaceServiceHelper.getFaceServiceClient();
        //mPersonGroupId = StorageHelper.getPersonGroupId(HomeActivity.App);

        try {
            //API call
            //Inputs
            //personGroupID
            //faceIDs
            //MaxNumOfCandidatesReturned
            TrainingStatus trainingStatus = faceServiceClient.getPersonGroupTrainingStatus(this.mPersonGroupId);
            if (trainingStatus.status != TrainingStatus.Status.Succeeded) {
                mSucceed = false;
                return null;
            }

            return faceServiceClient.identityInPersonGroup(this.mPersonGroupId, params, 1);
        } catch (Exception e) {
            mSucceed = false;
            return null;
        }
    }

    @Override
    protected void onPreExecute(){
        System.out.println("reached PreExecute");
    }

    @Override
    protected void onPostExecute(IdentifyResult[] result)
    {
        System.out.println("reached PostExecute");
        afterIdentification(result, mSucceed);
    }

    public void afterIdentification(IdentifyResult[] result, boolean succeed)
    {
        if(succeed)
        {
            if(result == null)
            {
                //no match, return false
                listener.onTaskComplete(false);
            }
            else
            {
                //evaluate confidence value of first (top) candidate
                if(result[0].candidates.get(0).confidence > CANDIDATE_THRESHOLD)
                {
                    //match, return true
                    listener.onTaskComplete(true);
                }
                else
                {
                    //not close enough to a match
                    listener.onTaskComplete(false);
                }
            }
        }
        else
        {
            System.out.println("failed mSucceed");
        }
    }


    public void setIdentificationResult(IdentifyResult[] identifyResults) {
        List<IdentifyResult> mIdentifyResults = Arrays.asList(identifyResults);
    }

    //Checks through Azure API to see if it is on whitelist or not
    //some method that takes a bitmap as an input param (cut out face from Dave's stuff)
    //returns a boolean based on if this face is in the list or not

    public void detect(Bitmap faceThumbnail) {

        //We need to put the bitmap into an input stream for detection
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        faceThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        //Starts background task to detect faces in the image
        //isOnList =
        new DetectionTask().execute(inputStream);

    }

    /*
    public void identify(View view) {
        if (detected && mPersonGroupId != null) {
            List<UUID> faceIds = new ArrayList<>();
            new IdentificationTask(mPersonGroupId).execute(
                    faceIds.toArray(new UUID[faceIds.size()]));
            )
        }
    }*/

    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {

        String mPersonGroupId = StorageHelper.getPersonGroupId(HomeActivity.App);

        @Override
        protected Face[] doInBackground(InputStream... params) {
            //Get an instance of face service client to detect faces in image

            //FIXME
            FaceServiceClient faceServiceClient = FaceServiceHelper.getFaceServiceClient();


            try {
                //API call to detect face
                //Inputs
                //params[0]:            stream of image to detect
                //b:                    whether to return face ID
                //b1:                   whether to return face landmark
                //faceAttributeTypes:   which face attributes to analyze
                return faceServiceClient.detect(params[0], true, false, null);

            } catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }
        }

        //if face is on whitelist, return true
        //else return false

    }
}
=======
package com.example.faceblock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.example.faceblock.helper.StorageHelper;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.TrainingStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class WhitelistChecker extends AsyncTask<UUID, String, IdentifyResult[]> {

    //private class IdentificationTask extends AsyncTask<UUID, String, IdentifyResult[]>{}

    private OnTaskCompleteListener listener;
//    public WhitelistChecker(OnTaskCompleteListener listener){
//        this.listener = listener;
//    }

    String mPersonGroupId = StorageHelper.getPersonGroupId(HomeActivity.App);

    private boolean mSucceed = true;
    private static final double CANDIDATE_THRESHOLD = 0.5;

    boolean detected;

    @Override
    protected IdentifyResult[] doInBackground(UUID... params) {

        FaceServiceClient faceServiceClient = FaceServiceHelper.getFaceServiceClient();
        //mPersonGroupId = StorageHelper.getPersonGroupId(HomeActivity.App);

        try {
            //API call
            //Inputs
            //personGroupID
            //faceIDs
            //MaxNumOfCandidatesReturned
            TrainingStatus trainingStatus = faceServiceClient.getPersonGroupTrainingStatus(this.mPersonGroupId);
            if (trainingStatus.status != TrainingStatus.Status.Succeeded) {
                mSucceed = false;
                return null;
            }

            return faceServiceClient.identityInPersonGroup(this.mPersonGroupId, params, 1);
        } catch (Exception e) {
            mSucceed = false;
            return null;
        }
    }

    @Override
    protected void onPreExecute(){
        System.out.println("reached PreExecute");
    }

    @Override
    protected void onPostExecute(IdentifyResult[] result)
    {
        System.out.println("reached PostExecute");
        afterIdentification(result, mSucceed);
    }

    public void afterIdentification(IdentifyResult[] result, boolean succeed)
    {
        if(succeed)
        {
            if(result == null)
            {
                //no match, return false
                listener.onTaskComplete(false);
            }
            else
            {
                //evaluate confidence value of first (top) candidate
                if(result[0].candidates.get(0).confidence > CANDIDATE_THRESHOLD)
                {
                    //match, return true
                    listener.onTaskComplete(true);
                }
                else
                {
                    //not close enough to a match
                    listener.onTaskComplete(false);
                }
            }
        }
        else
        {
            System.out.println("failed mSucceed");
        }
    }


    public void setIdentificationResult(IdentifyResult[] identifyResults) {
        List<IdentifyResult> mIdentifyResults = Arrays.asList(identifyResults);
    }

    //Checks through Azure API to see if it is on whitelist or not
    //some method that takes a bitmap as an input param (cut out face from Dave's stuff)
    //returns a boolean based on if this face is in the list or not

    public void detect(Bitmap faceThumbnail) {

        //We need to put the bitmap into an input stream for detection
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        faceThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        //Starts background task to detect faces in the image
        //isOnList =
        new DetectionTask().execute(inputStream);

    }

    public void identify(View view) {
        if (detected && mPersonGroupId != null) {
            List<UUID> faceIds = new ArrayList<>();
            new IdentificationTask(mPersonGroupId).execute(
                    faceIds.toArray(new UUID[faceIds.size()]));
            )
        }
    }

    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {

        String mPersonGroupId = StorageHelper.getPersonGroupId(HomeActivity.App);

        @Override
        protected Face[] doInBackground(InputStream... params) {
            //Get an instance of face service client to detect faces in image

            //FIXME
            FaceServiceClient faceServiceClient = FaceServiceHelper.getFaceServiceClient();


            try {
                //API call to detect face
                //Inputs
                //params[0]:            stream of image to detect
                //b:                    whether to return face ID
                //b1:                   whether to return face landmark
                //faceAttributeTypes:   which face attributes to analyze
                return faceServiceClient.detect(params[0], true, false, null);

            } catch (Exception e) {
                publishProgress(e.getMessage());
                return null;
            }
        }

        //if face is on whitelist, return true
        //else return false

    }
}
>>>>>>> 49a7d6bf0a15b585122b11597e43b42468681142
