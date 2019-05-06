package com.example.faceblock;


import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.faceblock.helper.SampleApp;
import com.example.faceblock.helper.StorageHelper;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.TrainingStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WhitelistChecker {

    String mPersonGroupId;
    boolean detected;
    private volatile boolean onWhitelist;
    FaceGraphic mFaceGraphic;

    WhitelistChecker(FaceGraphic faceGraphic){
        this.mFaceGraphic = faceGraphic;
    }

    private class IdentificationTask extends AsyncTask<UUID, String, IdentifyResult[]>{


        private boolean mSucceed = true;
        private static final double CANDIDATE_THRESHOLD = 0.5;
        String mPersonGroupId;

        IdentificationTask(String personGroupId) {
            this.mPersonGroupId = personGroupId;
        }

        @Override
        protected IdentifyResult[] doInBackground(UUID... params) {

           // System.out.println("reached IdentifyResult before");

            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
            mPersonGroupId = StorageHelper.getPersonGroupId(HomeActivity.App);

            //System.out.println("reached IdentifyResult");

            try {
                //API call
                //Inputs
                //personGroupID
                //faceIDs
                //MaxNumOfCandidatesReturned
                TrainingStatus trainingStatus = faceServiceClient.getLargePersonGroupTrainingStatus(WhitelistChecker.this.mPersonGroupId);
                if (trainingStatus.status != TrainingStatus.Status.Succeeded) {
                    mSucceed = false;
                    return null;
                }

                return faceServiceClient.identityInLargePersonGroup(WhitelistChecker.this.mPersonGroupId, params, 1);
            } catch (Exception e) {
                e.printStackTrace();
                mSucceed = false;
                return null;
            }
        }

        @Override
        protected void onPreExecute()
        {
            //System.out.println("reached PreExecute");
        }

        @Override
        protected void onPostExecute(IdentifyResult[] result)
        {
           // System.out.println("reached PostExecute");

            afterIdentification(result, mSucceed);
        }


        public void afterIdentification(IdentifyResult[] result, boolean succeed)
        {
           // System.out.println("reached afterIdentification");
            if(succeed) {
                try {
                    if (result == null) {
                        //no match, return false
                        onWhitelist = false;
                    } else {
                        //evaluate confidence value of first (top) candidate
                        if (result[0].candidates.get(0).confidence > CANDIDATE_THRESHOLD) {
                            //match, return true
                            onWhitelist = true;
                            mFaceGraphic.setWhitelisted(true);
                            //System.out.println("set Whitelisted to true");
                        } else {
                            //not close enough to a match
                            onWhitelist = false;
                            //mFaceGraphic.setWhitelisted(false);
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            else
            {
                //System.out.println("failed mSucceed");
            }
        }

    }


    //Checks through Azure API to see if it is on whitelist or not
    //some method that takes a bitmap as an input param (cut out face from Dave's stuff)
    //returns a boolean based on if this face is in the list or not

    public boolean detect(Bitmap faceThumbnail) {

        //We need to put the bitmap into an input stream for detection
        detected = false;

        onWhitelist = true;

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        faceThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        //Starts background task to detect faces in the image
        new DetectionTask().execute(inputStream);

        return onWhitelist;

    }


    public void identify(Face[] faces) {
        mPersonGroupId = StorageHelper.getPersonGroupId(HomeActivity.App);
        if (mPersonGroupId != null) {
            //System.out.println("personGroup ID = " + mPersonGroupId.toString());
            List<UUID> faceIds = new ArrayList<>();
            for(Face face: faces){
                faceIds.add(face.faceId);
            }
            new IdentificationTask(mPersonGroupId).execute(
                    faceIds.toArray(new UUID[faceIds.size()]));
        }
    }


    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {

        @Override
        protected Face[] doInBackground(InputStream... params) {
            //Get an instance of face service client to detect faces in image

            FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();

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
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Face[] result) {
            if(result != null)
            {

                if(result.length == 0) {
                    detected = false;
                    //System.out.println("No faces detected!");
                }
                else
                {
                   // System.out.println("HOOOORAY FACES DETECTED!");
                    detected = true;
                    identify(result);
                }
            }
            else {
                detected = false;
            }
        }

        //if face is on whitelist, return true
        //else return false

    }
}