package com.example.eatingcompanion.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eatingcompanion.R;
import com.example.eatingcompanion.YelpDetailResponse;
import com.example.eatingcompanion.YelpService;
import com.example.eatingcompanion.adapters.MessagesAdapter;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.Message;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class MessagesFragment extends Fragment {

    public static final String TAG = "MessagesFragment";
    public static final String BASE_URL = "https://api.yelp.com/v3/";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int GALLERY_ACTIVITY_REQUEST_CODE = 44;

    private TextView tvRestaurant;
    private TextView tvTime;
    private ImageView ivInfo;
    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<Message> allMessages;
    private EditText etMessage;
    private Button btnMessage;
    private ImageView ivAddMedia;
    private ImageView ivCamera;
    private ImageView ivGallery;
    private ImageView ivPhoto;
    private ImageView ivRemovePhoto;
    private File photoFile;
    private String photoFileName = "photo.jpg";

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvRestaurant = view.findViewById(R.id.tvRestaurant);
        tvTime = view.findViewById(R.id.tvTime);
        ivInfo = view.findViewById(R.id.ivInfo);
        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnMessage = view.findViewById(R.id.btnMessage);
        ivAddMedia = view.findViewById(R.id.ivAddMedia);
        ivCamera = view.findViewById(R.id.ivCamera);
        ivGallery = view.findViewById(R.id.ivGallery);
        ivPhoto = view.findViewById(R.id.ivPhoto);
        ivRemovePhoto = view.findViewById(R.id.ivRemovePhoto);
        allMessages = new ArrayList<>();
        adapter = new MessagesAdapter(getContext(), allMessages);
        rvMessages.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        rvMessages.setLayoutManager(llm);

        ivCamera.setVisibility(View.GONE);
        ivGallery.setVisibility(View.GONE);
        ivPhoto.setVisibility(View.GONE);
        ivRemovePhoto.setVisibility(View.GONE);

        String restaurantId = ((Chat) getArguments().getSerializable("chat")).getRestaurantId();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy, hh:mma", Locale.US);
        String date = sdf.format(((Chat) getArguments().getSerializable("chat")).getTime());
        tvTime.setText(date);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final YelpService yelpService = retrofit.create(YelpService.class);

        yelpService.getRestaurantDetail("Bearer " + getContext().getString(R.string.yelp_api_key), restaurantId).enqueue(new Callback<YelpDetailResponse>() {
            @Override
            public void onResponse(Call<YelpDetailResponse> call, Response<YelpDetailResponse> response) {
                Log.i(TAG, "onResponse " + response);
                if (response.body() == null) {
                    Log.e(TAG, "Did not receive valid response body from Yelp API");
                    return;
                }
                tvRestaurant.setText(response.body().getName());
            }

            @Override
            public void onFailure(Call<YelpDetailResponse> call, Throwable t) {
                Log.i(TAG, "onFailure query restaurants for nearby chats" + t);
            }
        });

        // query messages in the chat, look for the latest one
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_CHAT);
        query.whereEqualTo(Message.KEY_CHAT, (Chat) getArguments().getSerializable("chat"));
        query.addDescendingOrder(Message.KEY_CREATED_KEY);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying messages", e);
                    return;
                }
                allMessages.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageBody = etMessage.getText().toString();
                Message message = new Message();
                message.setUser(ParseUser.getCurrentUser());
                message.setChat((Chat) getArguments().getSerializable("chat"));
                message.setBody(messageBody);
                if (photoFile != null) {
                    ParseFile parseFile = new ParseFile(photoFile);
                    parseFile.saveInBackground();
                    message.setMedia(parseFile);
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.setACL(new ParseACL(currentUser));
                ParseACL acl = new ParseACL(currentUser);
                acl.setPublicReadAccess(false);
                acl.setPublicWriteAccess(false);
                acl.setRoleReadAccess("Administrator", true);
                acl.setRoleWriteAccess("Administrator", true);
                message.setACL(acl);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error saving message", e);
                        }
                    }
                });
                InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(etMessage.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                view.clearFocus();
                rvMessages.requestFocus();
                etMessage.setText("");
            }
        });


        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Message> liveQuery = ParseQuery.getQuery(Message.class);
        liveQuery.include(Message.KEY_CHAT);
        liveQuery.whereEqualTo(Message.KEY_CHAT, (Chat) getArguments().getSerializable("chat"));
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(liveQuery);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<Message>() {
            @Override
            public void onEvent(ParseQuery<Message> query, Message object) {
                // HANDLING create event
                allMessages.add(object);
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

        ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ivAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ivCamera.getVisibility() == View.GONE) {
                    ivCamera.setVisibility(View.VISIBLE);
                    ivGallery.setVisibility(View.VISIBLE);
                } else {
                    ivCamera.setVisibility(View.GONE);
                    ivGallery.setVisibility(View.GONE);
                }
            }
        });

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Create a File reference for future access
                photoFile = getPhotoFileUri(photoFileName);

                // wrap File object into a content provider
                // required for API >= 24
                // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
                Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.eatingcompanion", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                // So as long as the result is not null, it's safe to use the intent.
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    // Start the image capture intent to take photo
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });

        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_ACTIVITY_REQUEST_CODE);
            }
        });

        ivRemovePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivPhoto.setVisibility(View.GONE);
                photoFile = null;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPhoto.setImageBitmap(takenImage);
                ivPhoto.setVisibility(View.VISIBLE);
                ivRemovePhoto.setVisibility(View.VISIBLE);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                // Create a File reference for future access
                photoFile = getPhotoFileUri(photoFileName);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                    ivPhoto.setImageBitmap(bitmap);
                    ivPhoto.setVisibility(View.VISIBLE);
                    ivRemovePhoto.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.d(TAG, "Error occurred while creating the file");
                }

                try {
                    InputStream inputStream = null;
                    inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                    // Copying
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Picture wasn't uploaded!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    // Uri = uniform resource identifier
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}
