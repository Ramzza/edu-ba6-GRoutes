package edu.ubbcluj.cs.groutes.generalhelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import edu.ubbcluj.cs.groutes.activities.LoginTabActivity;
import edu.ubbcluj.cs.groutes.R;

public class DatabaseAssist {
    private static final String TAG = "grm.DatabaseAssist";
    private static DatabaseAssist sInstance;

    private FirebaseFirestore mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;

    private ProgressDialog progress;

    public static DatabaseAssist getInstance() {
        if (sInstance == null) {
            sInstance = new DatabaseAssist();
        }

        return sInstance;
    }

    private DatabaseAssist() {
        this.mFirebaseAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseFirestore.getInstance();
    }

    public FirebaseUser getUser() {
        return mUser;
    }

    public void setUser(FirebaseUser pUser) {
        this.mUser = pUser;
    }

    public void doRegister(final LoginTabActivity pContext, String pEmail, String pPassword) {
        showLoadingSpinner(pContext);
        try {
            mFirebaseAuth.createUserWithEmailAndPassword(pEmail, pPassword)
                    .addOnCompleteListener(pContext, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i(TAG, "createUserWithEmail:success");
                                setUser(mFirebaseAuth.getCurrentUser());
                                sendVerificationMail();
                                dismissLoadingSpinner();
                                pContext.updateUI();
                            }
                        }
                    });
        } catch (Exception err) {
            dismissLoadingSpinner();
            pContext.showMessage(err.getMessage());
        }
    }

    private void sendVerificationMail() {
        try {
            mUser.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                            } else {
                                Log.w(TAG, "Email not sent.");
                            }
                        }
                    });
        } catch (Exception err) {
            //
        }
    }

    public void doPasswordReset(final LoginTabActivity pContext, String emailAddress) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        showLoadingSpinner(pContext);

        try {
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dismissLoadingSpinner();
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                pContext.showMessage("Email sent.");
                            } else {
                                // If sign in fails, display a message to the user
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                pContext.showMessage(task.getException().getMessage());
                            }
                        }
                    });
        } catch (Exception err) {
            dismissLoadingSpinner();
            pContext.showMessage(err.getMessage());
        }
    }

    public void doLogin(final LoginTabActivity pContext, String pEmail, String pPassword) {
        showLoadingSpinner(pContext);
        try {


            mFirebaseAuth.signInWithEmailAndPassword(pEmail, pPassword)
                    .addOnCompleteListener(pContext, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i(TAG, "loginUserWithEmail:success");
                                setUser(mFirebaseAuth.getCurrentUser());

                                if (mUser.isEmailVerified()) {
                                    pContext.updateUI();
                                } else {
                                    pContext.showMessage("Email address not verified!");
                                }


                            } else {
                                // If sign in fails, display a message to the user
                                Log.w(TAG, "loginUserWithEmail:failure", task.getException());
                                pContext.showMessage(task.getException().getMessage());
                            }

                            dismissLoadingSpinner();
                        }
                    });
        } catch (Exception err) {
            pContext.showMessage(err.getMessage());
            dismissLoadingSpinner();
        }
    }

    public void getDocuments(CacheManager pContext, String pQueryParameter) {
        fetchData(pContext, pQueryParameter, true);
    }

    public void getCollectionSize(CacheManager pContext, String pQueryParameter) {
        fetchData(pContext, pQueryParameter, false);
    }

    private void fetchData(final CacheManager pContext, final String pQueryParameter, final boolean pProcess) {
        String queryPath = "/users/" + mUser.getUid() + "/" + pQueryParameter;

        try {
            mDatabase.collection(queryPath)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                pContext.handleQueryResult(task.getResult(), pQueryParameter, pProcess);
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDocument(String pPath, String pDocument, Object pData) {
        mDatabase.collection(pPath).document(pDocument)
                .set(pData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void deleteDocument(String pPath, String docId) {
        try {
            String path = "/users/" + mUser.getUid() + pPath;
            mDatabase.collection(path).document(docId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
        } catch (Exception err) {
            Log.e(TAG, err.getStackTrace().toString());
        }
    }

    private void showLoadingSpinner(Activity pContext) {
  //      pContext.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        progress = new ProgressDialog(pContext);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private void dismissLoadingSpinner() {
        progress.dismiss();
    }
}
