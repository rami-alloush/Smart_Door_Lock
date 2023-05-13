package com.smartdoorlock.iot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UserHomeActivity extends AppCompatActivity {

    private static final String TAG = "UserHomeActivity";
    private FirebaseRecyclerAdapter<Device, DeviceHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // Assign the recyclerView to the adapter that gets the data
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = getDevicesAdapter();
        recyclerView.setAdapter(adapter);
    }

    FirebaseRecyclerAdapter<Device, DeviceHolder> getDevicesAdapter() {

        // Get current user to use in the query
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "User UID: " + userUid);

        // Define the query that gets the data we need from FireStore
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("devices")
                .orderByChild("active_user")
                .equalTo(userUid);

        // Convert the query into options object to assign the class for data mapping
        FirebaseRecyclerOptions<Device> options = new FirebaseRecyclerOptions.Builder<Device>()
                .setQuery(query, Device.class)
                .build();

        // Create the adapter with its essential methods and return it
        return new FirebaseRecyclerAdapter<Device, UserHomeActivity.DeviceHolder>(options) {
            @Override
            public void onBindViewHolder(@NonNull DeviceHolder holder, int position, @NonNull Device model) {
                // Bind the Device object to the DeviceHolder
                holder.mName.setText(model.getName());
                holder.mState.setChecked(model.getState());

                // Action for the Toggle Bottom to change the Device State
                holder.mState.setOnCheckedChangeListener((compoundButton, b) -> {
                    String key = adapter.getRef(position).getKey();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("devices");
                    databaseReference.child(key).child("state").setValue(b);
                });
            }

            @NonNull
            @Override
            public DeviceHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.single_device for each device
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_device, group, false);

                return new DeviceHolder(view);
            }
        };
    }

    static class DeviceHolder extends RecyclerView.ViewHolder {
        private final TextView mName;
        private final ToggleButton mState;

        DeviceHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.device_name);
            mState = view.findViewById(R.id.device_state);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void userSignOut() {
        try {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Menu Functions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem itemSignOut = menu.add(Menu.NONE, 10, Menu.NONE, getString(R.string.sign_out));
        itemSignOut.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 10) {
            userSignOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}