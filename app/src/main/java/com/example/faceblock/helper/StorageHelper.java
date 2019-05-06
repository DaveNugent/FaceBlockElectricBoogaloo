//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Face-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.example.faceblock.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Defined several functions to manage local storage.
 */
public class StorageHelper {

    private static String mainPref = "PersonGroup";
    private static String personIdPref = "PersonIds";
    private static String facePref = "FaceIds";
    private static String uriPref = "FaceURIs";

    public static String getPersonGroupId(Context context) {
        SharedPreferences personGroup =
                context.getSharedPreferences(mainPref, Context.MODE_PRIVATE);
        return personGroup.getString("PersonGroupId", " ");
    }

    public static void setPersonGroupId(String personGroupId, Context context) {
        SharedPreferences personGroup =
                context.getSharedPreferences(mainPref, Context.MODE_PRIVATE);

        SharedPreferences.Editor personGroupEditor = personGroup.edit();
        personGroupEditor.putString("PersonGroupId", personGroupId);
        personGroupEditor.commit();

    }

    public static String getPersonGroupName(Context context) {
        SharedPreferences personGroup =
                context.getSharedPreferences(mainPref, Context.MODE_PRIVATE);
        return personGroup.getString("GroupName", "");
    }

    public static void setPersonGroupName(String personGroupName, Context context) {
        SharedPreferences personGroup =
                context.getSharedPreferences(mainPref, Context.MODE_PRIVATE);

        SharedPreferences.Editor personGroupEditor = personGroup.edit();
        personGroupEditor.putString("GroupName", personGroupName);
        personGroupEditor.commit();

    }


    public static Set<String> getAllPersonNames(Context context) {
        SharedPreferences personNameSet =
                context.getSharedPreferences(mainPref, Context.MODE_PRIVATE);
        return personNameSet.getStringSet("PersonNameSet", new HashSet<String>());
    }

    public static String getPersonId(String personName, Context context) {
        SharedPreferences personIdNameMap =
                context.getSharedPreferences(personIdPref, Context.MODE_PRIVATE);
        return personIdNameMap.getString(personName, " ");
    }

    public static void addPerson(String personName, String personId, Context context) {
        SharedPreferences personIdNameMap =
                context.getSharedPreferences(personIdPref, Context.MODE_PRIVATE);

        SharedPreferences.Editor personIdNameMapEditor = personIdNameMap.edit();
        personIdNameMapEditor.putString(personName, personId);
        personIdNameMapEditor.commit();

        Set<String> personNames = getAllPersonNames(context);
        Set<String> newPersonNames = new HashSet<>();
        for (String name: personNames) {
            newPersonNames.add(name);
        }
        newPersonNames.add(personName);
        SharedPreferences personNameSet =
                context.getSharedPreferences(mainPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor personNameSetEditor = personNameSet.edit();
        personNameSetEditor.putStringSet("PersonNameSet", newPersonNames);
        personNameSetEditor.commit();
    }

    public static Set<String> getAllFaceIds(String personId, Context context) {
        SharedPreferences faceIdSet =
                context.getSharedPreferences(facePref, Context.MODE_PRIVATE);
        return faceIdSet.getStringSet(personId, new HashSet<String>());
    }

    public static String getFaceUri(String faceId, Context context) {
        SharedPreferences faceIdUriMap =
                context.getSharedPreferences(uriPref, Context.MODE_PRIVATE);
        return faceIdUriMap.getString(faceId, "");
    }

    public static void setFaceUri(String faceIdToAdd, String faceUri, String personId, Context context) {
        SharedPreferences faceIdUriMap =
                context.getSharedPreferences(uriPref, Context.MODE_PRIVATE);

        SharedPreferences.Editor faceIdUriMapEditor = faceIdUriMap.edit();
        faceIdUriMapEditor.putString(faceIdToAdd, faceUri);
        faceIdUriMapEditor.commit();

        Set<String> faceIds = getAllFaceIds(personId, context);
        Set<String> newFaceIds = new HashSet<>();
        for (String faceId: faceIds) {
            newFaceIds.add(faceId);
        }
        newFaceIds.add(faceIdToAdd);
        SharedPreferences faceIdSet =
                context.getSharedPreferences(facePref, Context.MODE_PRIVATE);
        SharedPreferences.Editor faceIdSetEditor = faceIdSet.edit();
        faceIdSetEditor.putStringSet(personId, newFaceIds);
        faceIdSetEditor.commit();
    }

    public static void deleteAll(Context context){

        SharedPreferences mprefs = context.getSharedPreferences(mainPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit = mprefs.edit();
        mEdit.clear();
        mEdit.commit();

        mprefs = context.getSharedPreferences(personIdPref, Context.MODE_PRIVATE);
        mEdit = mprefs.edit();
        mEdit.clear();
        mEdit.commit();

        mprefs = context.getSharedPreferences(facePref, Context.MODE_PRIVATE);
        mEdit = mprefs.edit();
        mEdit.clear();
        mEdit.commit();

        mprefs = context.getSharedPreferences(uriPref, Context.MODE_PRIVATE);
        mEdit = mprefs.edit();
        mEdit.clear();
        mEdit.commit();

    }



}
