package com.example.hp.storage;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button selectimage,uploadimage;
    private ImageView imageView;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private static final int GALAARY_INTENT=234;
    private Button tex;
    private ProgressBar mprogressbar;
    private EditText mEditTextFileName;

    private Uri filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectimage = (Button) findViewById(R.id.Choose);
        uploadimage=(Button)findViewById(R.id.uplod);

        imageView=(ImageView)findViewById(R.id.imageView1);
        mprogressbar=(ProgressBar)findViewById(R.id.progressBar2);
        mEditTextFileName=(EditText)findViewById(R.id.editText1);
        tex=(Button)findViewById(R.id.next);

        tex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ImageActivity.class));
            }
        });


        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(MainActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        selectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALAARY_INTENT);

            }
        });

       // Uri uri=Uri.fromFile(new File());
      //  StorageReference filepath=mstorage.child("photos").child(uri.getLastPathSegment());
      //  filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           // @Override
          //  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        //        Toast.makeText(MainActivity.this,"Upload done",Toast.LENGTH_LONG).show();
        //    }
     //   });
     //   filepath.putFile(uri).addOnFailureListener(new OnFailureListener() {
       //     @Override
         //   public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                // ...
           //     Toast.makeText(MainActivity.this,"Upload failed",Toast.LENGTH_LONG).show();
         //   }
       // });

    }
    @Override
         protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== GALAARY_INTENT && resultCode== RESULT_OK && data!=null && data.getData()!=null) {
            filepath=data.getData();
            Picasso.with(this).load(filepath).into(imageView);
        }

        }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (filepath != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(filepath));

            mUploadTask = fileReference.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mprogressbar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
                                    taskSnapshot.getDownloadUrl().toString());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mprogressbar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }




//        @Override
  //      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        super.onActivityResult(requestCode, resultCode, data);
      //      if(requestCode== GALAARY_INTENT && requestCode== RESULT_OK){
        //        Uri uri=data.getData();
          //      StorageReference filepath=mstorage.child("photos").child(uri.getLastPathSegment());
            //    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              //      @Override
                //    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  //      Toast.makeText(MainActivity.this,"Upload done",Toast.LENGTH_LONG).show();
   //                 }
     //           });
       //         filepath.putFile(uri).addOnFailureListener(new OnFailureListener() {
         //           @Override
           //         public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
             //           Toast.makeText(MainActivity.this,"Upload failed",Toast.LENGTH_LONG).show();
               //     }
             //   });

                // StorageReference filepath = mStorage.child("Photos").child(uriProfileImage.getLastPathSegment());
                //try {
                  //  Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                   // img.setImageBitmap(bitmap);


                    //final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
                    //if(uriProfileImage!=null){
                      //  progressBar.setVisibility(View.VISIBLE);
                        //profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           // @Override
                           // public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                          //      progressBar.setVisibility(View.GONE);
                            //    profileImageurl=taskSnapshot.getDownloadUrl().toString();
                           // }
                       // })
                         //       .addOnFailureListener(new OnFailureListener() {
                           //         @Override
                             //       public void onFailure(@NonNull Exception e) {
                               //         progressBar.setVisibility(View.GONE);
                                 //       Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                                   // }
                               // });{
                   //     }
                 //   }


               // } catch (IOException e) {
                 //   e.printStackTrace();
              //  }
            }



