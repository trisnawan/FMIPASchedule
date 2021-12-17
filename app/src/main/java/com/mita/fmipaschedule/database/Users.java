package com.mita.fmipaschedule.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mita.fmipaschedule.Interface.DatabaseInterface;
import com.mita.fmipaschedule.app.AppHelper;
import com.mita.fmipaschedule.app.SessionManager;
import com.mita.fmipaschedule.model.ProgramModel;
import com.mita.fmipaschedule.model.UserModel;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Users {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseInterface databaseInterface;
    private FirebaseUser user;

    public Users(){
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setDatabaseInterface(DatabaseInterface databaseInterface) {
        this.databaseInterface = databaseInterface;
    }

    public void updatePassword(Context context, String oldPassword, String newPassword){
        AuthCredential credential = EmailAuthProvider.getCredential(getEmail(), oldPassword);
        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        syncUser(context);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        databaseInterface.onSuccess(false, e.getLocalizedMessage(), null);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                databaseInterface.onSuccess(false, e.getLocalizedMessage(), null);
            }
        });
    }

    public void updateAvatar(Context context, Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("users").child(getId() + new Date().getTime() + ".jpeg");
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata()!=null){
                    if (taskSnapshot.getMetadata().getReference()!=null){
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(uri)
                                        .build();
                                user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        syncUser(context);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        databaseInterface.onSuccess(false, e.getLocalizedMessage(), null);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                databaseInterface.onSuccess(false, "Failed!", null);
                            }
                        });
                    }else{
                        databaseInterface.onSuccess(false, "Failed!", null);
                    }
                }else{
                    databaseInterface.onSuccess(false, "Failed!", null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                databaseInterface.onSuccess(false, e.getLocalizedMessage(), null);
            }
        });
    }

    public void updateName(Context context, String name){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                syncUser(context);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                databaseInterface.onSuccess(false, e.getLocalizedMessage(), null);
            }
        });
    }

    public boolean isLogin(){
        return user != null;
    }

    public void login(Context context, String email, String password){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if (authResult.getUser()!=null){
                    user = authResult.getUser();
                    syncUser(context);
                }else{
                    databaseInterface.onSuccess(false, "Login Gagal!", null);
                }
            }
        }).addOnFailureListener(e -> databaseInterface.onSuccess(false, e.getLocalizedMessage(), null));
    }

    public void register(Context context, UserModel userModel, String password){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(userModel.getEmail(), password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if (authResult.getUser()!=null){
                    user = authResult.getUser();
                    userModel.setId(user.getUid());
                    // SET DISPLAY NAME
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(userModel.getName())
                            .build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                        // SAVE TO DATABASE
                        db.collection("users").document(user.getUid()).set(userModel).addOnCompleteListener(task1 -> {
                            syncUser(context);
                        });
                    });
                }else{
                    databaseInterface.onSuccess(false, "Register Gagal!", null);
                }
            }
        }).addOnFailureListener(e -> databaseInterface.onSuccess(false, e.getLocalizedMessage(), null));
    }

    public void getUser(String id){
        db.collection("users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                databaseInterface.onSuccess(true, null, documentSnapshot);
            }
        }).addOnFailureListener(e -> databaseInterface.onSuccess(false, e.getLocalizedMessage(), null));
    }

    public void setUser(UserModel userModel){
        db.collection("users").document(userModel.getId())
                .set(userModel)
                .addOnSuccessListener(unused -> {
                    if (databaseInterface!=null){
                        databaseInterface.onSuccess(true, null, null);
                    }
                })
                .addOnFailureListener(e -> {
                    if (databaseInterface!=null){
                        databaseInterface.onSuccess(false, e.getLocalizedMessage(), null);
                    }
                });
    }

    public void syncUser(Context context){
        Map<String, Object> us = new HashMap<>();
        us.put("name", getName());
        us.put("email", getEmail());
        us.put("phone", getPhone());
        us.put("avatar", getAvatar());
        db.collection("users").document(getId())
                .update(us)
                .addOnSuccessListener(unused -> {
                    db.collection("users").document(getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult()!=null){
                                UserModel um = task.getResult().toObject(UserModel.class);
                                if (um!=null) {
                                    um.setId(task.getResult().getId());
                                    SessionManager sm = new SessionManager(context);
                                    sm.setUser(um);
                                }
                            }
                            if (databaseInterface!=null){
                                databaseInterface.onSuccess(true, null, null);
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    if (databaseInterface!=null){
                        databaseInterface.onSuccess(false, e.getLocalizedMessage(), null);
                    }
                });
    }

    public String getId(){
        return user.getUid();
    }

    public String getAvatar(){
        if (user.getPhotoUrl()!=null){
            return user.getPhotoUrl().toString();
        }else{
            return AppHelper.getDefaultImage();
        }
    }

    public String getPhone(){
        return user.getPhoneNumber();
    }

    public String getName(){
        return user.getDisplayName();
    }

    public String getEmail(){
        return user.getEmail();
    }

    public String getTypeString(Context context){
        SessionManager sessionManager = new SessionManager(context);
        return sessionManager.getUserTypeString();
    }

    public Date getBirthDate(Context context){
        SessionManager sessionManager = new SessionManager(context);
        return new Date(sessionManager.getUser().getBirthdate());
    }

    public ProgramModel getProgram(Context context){
        SessionManager sessionManager = new SessionManager(context);
        return sessionManager.getProgram();
    }
}
