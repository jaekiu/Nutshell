/** Firebase Util methods.
 * @author: Jacqueline Zhang
 * @date: 03/05/2019
 */
package com.jackie.nutshell;

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

public class FirebaseUtils {
    /** Retrieves the Firebase Database.
     * @return: FirebaseDatabase object. */
    public static FirebaseDatabase getFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    /** Returns a Firebase Database Reference to the SOCIALS path.
     * @return: DatabaseReference object. */
    public static DatabaseReference getUsersDatabaseRef() {
        return FirebaseDatabase.getInstance().getReference("users");
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
