package com.smartdoorlock.iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserHomeActivity extends AppCompatActivity {

    private FirestoreRecyclerAdapter<Device, DeviceHolder> adapter;

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

    FirestoreRecyclerAdapter<Device, DeviceHolder> getDevicesAdapter() {
        // Define the query that gets the data we need from FireStore
        Query query = FirebaseFirestore
                .getInstance()
                .collection("devices");

        // Convert the query into options object to assign the class for data mapping
        FirestoreRecyclerOptions<Device> options = new FirestoreRecyclerOptions.Builder<Device>()
                .setQuery(query, Device.class)
                .build();

        // Create the adapter with its essential methods and return it
        return new FirestoreRecyclerAdapter<Device, UserHomeActivity.DeviceHolder>(options) {
            @Override
            public void onBindViewHolder(@NonNull DeviceHolder holder, int position, @NonNull Device model) {
                // Bind the Device object to the DeviceHolder
                holder.mName.setText(model.getName());
                holder.mState.setChecked(model.getState());
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
}