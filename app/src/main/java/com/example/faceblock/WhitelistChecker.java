package com.example.faceblock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.TrainingStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class WhitelistChecker extends AsyncTask<UUID, String, IdentifyResult[]> {


    String mPersonGroupId; //= getPersongroupID

    private boolean mSucceed = true;
    boolean detected;

    @Override
    protected IdentifyResult[] doInBackground(UUID... params) {

        FaceServiceClient faceServiceClient = FaceServiceHelper.getFaceServiceClient();
        //FIXME
        //mPersonGroupId = StorageHelper.getPersonGroupId()

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

    public void setIdentificationResult(IdentifyResult[] identifyResults) {
        List<IdentifyResult> mIdentifyResults = Arrays.asList(identifyResults);
    }

    //Checks through Azure API to see if it is on whitelist or not
    //some method that takes a bitmap as an input param (cut out face from Dave's stuff)
    //returns a boolean based on if this face is in the list or not
    public boolean isOnWhiteList(Bitmap faceThumbnail) {


        boolean isOnList = false;
        //compares this face to the persongroup we have access to

        isOnList = detect(faceThumbnail);

        return isOnList;

    }

    private boolean detect(Bitmap faceThumbnail) {
        boolean isOnList = false;

        //We need to put the bitmap into an input stream for detection
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        faceThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        //Starts background task to detect faces in the image
        //isOnList =
        new DetectionTask().execute(inputStream);

        return isOnList;
    }

    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
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