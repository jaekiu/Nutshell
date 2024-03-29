/** Firebase Util methods.
 * @author: Jacqueline Zhang
 * @date: 03/05/2019
 */
package com.jackie.nutshell.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseUtils {
    /** Retrieves the Firebase Storage.
     * @return: FirebaseStorage object. */
    public static FirebaseStorage getFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    /** Returns a Firebase Database Reference to the USERS path.
     * @return: DatabaseReference object. */
    public static DatabaseReference getUsersDatabaseRef() {
        return FirebaseDatabase.getInstance().getReference("users");
    }

    /** Returns a Firebase Database Reference to the USERNAMES path.
     * @return: DatabaseReference object. */
    public static DatabaseReference getUsernamesDatabaseRef() {
        return FirebaseDatabase.getInstance().getReference("usernames");
    }

    /** Returns a Firebase Database Reference to the PROJECTS path.
     * @return: DatabaseReference object. */
    public static DatabaseReference getProjsDatabaseRef() {
        return FirebaseDatabase.getInstance().getReference("projects");
    }

    /** Retrieves the Firebase User for Authentication purposes.
     * @return: FirebaseUser object. */
    public static FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /** Retrieves the Firebase Auth.
     * @return: FirebaseAuth object. */
    public static FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

}
