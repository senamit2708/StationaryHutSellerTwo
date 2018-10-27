package com.example.stationaryhutsellertwo.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.stationaryhutsellertwo.R;
import com.example.stationaryhutsellertwo.model.Product;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

//import static android.app.Activity.RESULT_OK;

public class ProductUpload extends Fragment {

    private static final String TAG = ProductUpload.class.getSimpleName();
    private final int PICK_IMAGE_REQUEST= 71;
    private Context context;

    Button btnUpload;
    Button btnUploadImage;
    Button btnChooseImage;
    EditText txtProductNumber;
    EditText txtProductName;
    EditText txtPrice;
    EditText txtQuantity;
    EditText txtMininumOrder;
    EditText txtBrand;
    EditText txtCategory;
    ImageView mImage;

    //private uri pathway
    private Uri uri;
    private String firebaseImageName;
    private Uri downloadImageUri;


    private String productNumber;
    private String productName;
    private String productPrice;
    private String productQuantity;
    private String minimumOrder;
    private String category;
    private String productBrand;
    private String imageUrl;

    private DatabaseReference mDatabase;
    private StorageReference storageReference;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_product_upload, container, false);
       context = container.getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnUpload = view.findViewById(R.id.btnUpload);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        btnChooseImage = view.findViewById(R.id.btnChooseImage);
        mImage = view.findViewById(R.id.imageProduct);
        txtProductNumber = view.findViewById(R.id.txtProductNumber);
        txtProductName = view.findViewById(R.id.txtProductName);
        txtPrice = view.findViewById(R.id.txtPrice);
        txtBrand = view.findViewById(R.id.txtBrand);
        txtCategory = view.findViewById(R.id.txtCategory);
        txtMininumOrder = view.findViewById(R.id.txtMininumOrder);
        txtQuantity = view.findViewById(R.id.txtQuantity);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProduct();
                Navigation.findNavController(view).navigate(R.id.action_productUpload_to_productDetails);
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {

        if (uri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("uploading....");
            progressDialog.show();
            final StorageReference ref = storageReference.child("product/images/" + UUID.randomUUID().toString());
            ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    firebaseImageName = taskSnapshot.getMetadata().getName();
                    Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show();
                    getUrlOfImage();

                }

                private void getUrlOfImage() {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadImageUri = uri;
                            Log.i(TAG, "the download link is " + uri);
                        }
                    });
                }
            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,
                                    "Upload Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");

                        }
                    })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            // Forward any exceptions
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            Log.i(TAG, "the download url is " + ref.getDownloadUrl());
                            return ref.getDownloadUrl();
                        }
                    });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK
                && data!= null && data.getData() != null){

            uri = data.getData();

            try {
                Bitmap bitmap =  MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);
                mImage.setImageBitmap(bitmap);
                Log.i(TAG, "the image is uploaded successfully");

            } catch (IOException e) {
                Log.e(TAG, "inside the exception block of image upload");
                e.printStackTrace();
            }

        }
    }


    private void submitProduct() {
        
        updateLocalVariable();
       int check = checkProductEntry();
       if (check==1){
           return;
       }
       setEditingEnable(false);
       String key = insertNewProduct();
       setEditingEnable(true);
        Toast.makeText(getActivity(), "product upload successful "+key, Toast.LENGTH_SHORT).show();

    }

    private String insertNewProduct() {
        String key = mDatabase.child("products").push().getKey();
        Product product = new Product(productName, productNumber,Integer.parseInt(productPrice),
                productBrand, Integer.parseInt(minimumOrder), Integer.parseInt(productQuantity),
                downloadImageUri.toString());
        Map<String, Object> productValues = product.toMap();
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put("/products/"+productNumber, productValues);
        mDatabase.updateChildren(childUpdate);
        return key;
    }

    private void setEditingEnable(boolean enable) {
        txtProductNumber.setEnabled(enable);
        txtProductName.setEnabled(enable);
        txtBrand.setEnabled(enable);
        txtPrice.setEnabled(enable);
        txtQuantity.setEnabled(enable);
        txtMininumOrder.setEnabled(enable);

        if (enable){
            btnUpload.setVisibility(View.VISIBLE);
        }else {
            btnUpload.setVisibility(View.INVISIBLE);
        }
    }

    private int checkProductEntry() {
        int check= 0;
        if (TextUtils.isEmpty(productNumber)){
            txtProductNumber.setText("REQUIRED");
            check=1;
        }
        if (TextUtils.isEmpty(productName)){
            txtProductName.setText("REQUIRED");
            check=1;
        }

        if (TextUtils.isEmpty(productPrice)){
            txtPrice.setText("REQUIRED");
            check=1;
        }
        if (TextUtils.isEmpty(minimumOrder)){
            txtMininumOrder.setText("REQUIRED");
            check=1;
        }
        if (TextUtils.isEmpty(productBrand)){
            txtBrand.setText("REQUIRED");
            check=1;
        }
       return check;
    }

    private void updateLocalVariable() {
        productNumber = txtProductNumber.getText().toString();
         productName = txtProductName.getText().toString();
        productPrice= txtPrice.getText().toString();
        productQuantity = txtQuantity.getText().toString();
        minimumOrder = txtMininumOrder.getText().toString();
         category = txtCategory.getText().toString();
         productBrand = txtBrand.getText().toString();
    }
}
