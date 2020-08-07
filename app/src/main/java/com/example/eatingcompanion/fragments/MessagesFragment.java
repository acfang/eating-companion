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
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.eatingcompanion.MainActivity;
import com.example.eatingcompanion.MySingleton;
import com.example.eatingcompanion.R;
import com.example.eatingcompanion.YelpDetailResponse;
import com.example.eatingcompanion.YelpService;
import com.example.eatingcompanion.adapters.MessagesAdapter;
import com.example.eatingcompanion.databinding.FragmentMessagesBinding;
import com.example.eatingcompanion.models.Chat;
import com.example.eatingcompanion.models.Message;
import com.example.eatingcompanion.models.User;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cdflynn.android.library.crossview.CrossView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static com.parse.Parse.getApplicationContext;

public class MessagesFragment extends Fragment {

    public static final String TAG = "MessagesFragment";
    public static final String BASE_URL = "https://api.yelp.com/v3/";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int GALLERY_ACTIVITY_REQUEST_CODE = 44;

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAhrdBc5g:APA91bHgDfl-hpeJgcyaoSDy9MJJdn6B2UiuGMK1CiO01vuYmn6-hE45V3kvCb3lE3WjbMjp1TcoZG0Omi3krbNqkfeNkzitXFUxqMH6Otf1oBpkN408jFtfKhaFSgrv7roZ2h5m0rvI";
    final private String contentType = "application/json";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    private TextView tvRestaurant;
    private TextView tvTime;
    private ImageView ivInfo;
    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<Message> allMessages;
    private EditText etMessage;
    private Button btnMessage;
    //private ImageView ivAddMedia;
    private ImageView ivCamera;
    private ImageView ivGallery;
    private ImageView ivPhoto;
    private ImageView ivRemovePhoto;
    private File photoFile;
    private String photoFileName = "photo.jpg";
    private Chat chat;
    private SpinKitView spinKit;
    private CrossView crossView;

    FragmentMessagesBinding binding;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMessagesBinding.inflate(LayoutInflater.from(getContext()), container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvRestaurant = binding.tvRestaurant;
        tvTime = binding.tvTime;
        ivInfo = binding.ivInfo;
        rvMessages = binding.rvMessages;
        etMessage = binding.etMessage;
        btnMessage = binding.btnMessage;
        //ivAddMedia = binding.ivAddMedia;
        ivCamera = binding.ivCamera;
        ivGallery = binding.ivGallery;
        ivPhoto = binding.ivPhoto;
        ivRemovePhoto = binding.ivRemovePhoto;
        spinKit = binding.spinKit;
        crossView = binding.crossView;
        allMessages = new ArrayList<>();
        adapter = new MessagesAdapter(getContext(), allMessages);
        rvMessages.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        //llm.setReverseLayout(true);
        //llm.setStackFromEnd(true);
        rvMessages.setLayoutManager(llm);
        Sprite wanderingCubes = new WanderingCubes();
        spinKit.setIndeterminateDrawable(wanderingCubes);
        spinKit.setVisibility(View.VISIBLE);

        ivCamera.setVisibility(View.GONE);
        ivGallery.setVisibility(View.GONE);
        ivPhoto.setVisibility(View.GONE);
        ivRemovePhoto.setVisibility(View.GONE);

        if (getArguments() != null && getArguments().containsKey("id")) {
            // user arrived via deep link
            ParseQuery<Chat> chatQuery = ParseQuery.getQuery(Chat.class);
            chatQuery.include(Chat.KEY_ID);
            chatQuery.whereEqualTo(Chat.KEY_ID, getArguments().getString("id"));
            try {
                List results = chatQuery.find();
                chat = (Chat) results.get(0);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            chat = (Chat) getArguments().getSerializable("chat");
        }

        //String restaurantId = ((Chat) getArguments().getSerializable("chat")).getRestaurantId();
        String restaurantId = chat.getRestaurantId();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy, hh:mma", Locale.US);
        //String date = sdf.format(((Chat) getArguments().getSerializable("chat")).getTime());
        String date = sdf.format(chat.getTime());
        tvTime.setText(date);

//        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
//        query.findInBackground(new FindCallback<Message>() {
//            @Override
//            public void done(List<Message> objects, ParseException e) {
//                Log.i(TAG, "num objects " + objects.size());
//            }
//        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final YelpService yelpService = retrofit.create(YelpService.class);

        yelpService.getRestaurantDetail("Bearer " + getContext().getString(R.string.yelp_api_key), restaurantId).enqueue(new Callback<YelpDetailResponse>() {
            @Override
            public void onResponse(Call<YelpDetailResponse> call, retrofit2.Response<YelpDetailResponse> response) {
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
        //query.whereEqualTo(Message.KEY_CHAT, (Chat) getArguments().getSerializable("chat"));
        query.whereEqualTo(Message.KEY_CHAT, Chat.createWithoutData(Chat.class, chat.getObjectId()));
        //query.whereEqualTo(Message.KEY_CHAT, ParseObject.createWithoutData(Chat.class, ((Chat) getArguments().getSerializable("chat")).getObjectId()));
        query.addAscendingOrder(Message.KEY_CREATED_KEY);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error when querying messages", e);
                    return;
                }
                Log.i(TAG, "num messages: "+ objects.size());
                allMessages.addAll(objects);
                adapter.notifyDataSetChanged();
                spinKit.setVisibility(View.GONE);
            }
        });

//        List results = null;
//        try {
//            results = query.find();
//            Log.i(TAG, "num messages: " + results.size());
//            for (int i = 0; i < results.size(); i++) {
//                allMessages.add((Message) results.get(i));
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageBody = etMessage.getText().toString();
                Message message = new Message();
                message.setUser(ParseUser.getCurrentUser());
                //message.setChat((Chat) getArguments().getSerializable("chat"));
                message.setChat(chat);
                message.setBody(messageBody);
                if (photoFile != null) {
                    ParseFile parseFile = new ParseFile(photoFile);
                    parseFile.saveInBackground();
                    message.setMedia(parseFile);
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                currentUser.setACL(new ParseACL(currentUser));
                ParseACL acl = new ParseACL(currentUser);
                acl.setPublicReadAccess(true);
                acl.setPublicWriteAccess(true);
                acl.setRoleReadAccess("Administrator", true);
                acl.setRoleWriteAccess("Administrator", true);
                message.setACL(acl);
                try {
                    message.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(etMessage.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                view.clearFocus();
                rvMessages.requestFocus();
                etMessage.setText("");
                ivPhoto.setVisibility(View.GONE);
                photoFile = null;

                TOPIC = "/topics/userABC"; //topic must match with what the receiver subscribed to
                NOTIFICATION_TITLE = tvRestaurant.getText().toString() + ", " + tvTime.getText().toString();
                NOTIFICATION_MESSAGE = ((User)ParseUser.getCurrentUser()).getName() + ": " + messageBody;

                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();
                try {
                    notifcationBody.put("title", NOTIFICATION_TITLE);
                    notifcationBody.put("message", NOTIFICATION_MESSAGE);

                    notification.put("to", TOPIC);
                    notification.put("data", notifcationBody);
                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage() );
                }
                sendNotification(notification);
            }
        });


        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Message> liveQuery = ParseQuery.getQuery(Message.class);
        liveQuery.include(Message.KEY_CHAT);
        //liveQuery.whereEqualTo(Message.KEY_CHAT, (Chat) getArguments().getSerializable("chat"));
        liveQuery.whereEqualTo(Message.KEY_CHAT, chat);
        liveQuery.addDescendingOrder(Message.KEY_CREATED_KEY);
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
                adapter.notifyItemInserted(0);
                rvMessages.smoothScrollToPosition(0);
            }
        });

        ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showInfoDialog((Chat) getArguments().getSerializable("chat"), ((Chat) getArguments().getSerializable("chat")).getRestaurantId());
                showInfoDialog(chat, chat.getRestaurantId());
            }
        });

        crossView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crossView.toggle();
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

    private void showInfoDialog(Chat chat, String id) {
        FragmentManager fm = getFragmentManager();
        InfoDialogFragment infoDialogFragment = InfoDialogFragment.newInstance(chat, id);
        infoDialogFragment.show(fm, "fragment_info_dialog");
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
