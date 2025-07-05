package com.example.luxevistaresortapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.luxevistaresortapp.Data.database.AppExecutors;
import com.example.luxevistaresortapp.Data.database.CrudOperations;
import com.example.luxevistaresortapp.Data.database.Model.RoomAvailability;
import com.example.luxevistaresortapp.Data.database.Model.Rooms;
import com.example.luxevistaresortapp.Data.database.Model.User;
import com.example.luxevistaresortapp.MainUi.MainUiActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private CrudOperations crudOperations;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        context = getApplicationContext();
        TextView gradientText = findViewById(R.id.gradientText);
        TextView signUpText = findViewById(R.id.signUpText);
        TextView btnLogin = findViewById(R.id.btn_Login);

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);

        crudOperations = new CrudOperations(this);

        String text = gradientText.getText().toString();
        TextPaint paint = gradientText.getPaint();
        float width = paint.measureText(text);

        Shader textShader = new LinearGradient(
                0, 0, width, 0, // Gradient left to right
                new int[]{
                        Color.parseColor("#2121F4"), // Start color
                        Color.parseColor("#00E5FF")  // End color
                },
                null,
                Shader.TileMode.CLAMP
        );

        gradientText.getPaint().setShader(textShader);
        gradientText.invalidate(); // Force redraw


        signUpText.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Pattern.compile("^(.+)@(.+)$").matcher(email).matches()) {
                Toast.makeText(MainActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            AppExecutors.getInstance().diskIO().execute(() -> {
                User user = crudOperations.getUserByEmailAndPassword(email, password);

                runOnUiThread(() -> {
                    if (user != null) {
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Ensure any external deletions are complete before inserting
                        AppExecutors.getInstance().diskIO().execute(() -> {
                            // Perform any necessary deletions (if needed)
                            crudOperations.deleteAllRooms(); // This will log the stack trace
                            // Wait for deletion to complete, then call insertInitialRooms
                            AppExecutors.getInstance().mainThread().execute(() -> {
                                insertInitialRooms(() -> {
                                    Log.d("INIT", "Room initialization completed");
                                    startActivity(new Intent(MainActivity.this, MainUiActivity.class));
                                    finish();
                                });
                            });
                        });

                    } else {
                        Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }


    private void insertInitialRooms(Runnable onComplete) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                // Begin transaction
                Log.d("INIT", "Starting room initialization");
                crudOperations.beginTransaction();

                // 1) Clear both tables
                Log.d("INIT", "Clearing all rooms");
                crudOperations.deleteAllRoomsSync();
                Log.d("INIT", "Clearing all availabilities");
                crudOperations.clearAllAvailabilitiesSync();
                Log.d("INIT", "All tables cleared");

                List<String> nextFiveDates = new ArrayList<>();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                for (int offset = 0; offset < 5; offset++) {
                    nextFiveDates.add(fmt.format(cal.getTime()));
                    cal.add(Calendar.DATE, 1);
                }
                Log.d("INIT", "Prepared dates: " + nextFiveDates);

                // 3) Insert rooms & availabilities
                for (int i = 1; i <= 10; i++) {
                    List<String> imageUrls = new ArrayList<>();
                    for (int j = 1; j <= 3; j++) {
                        String resourceName = "room_" + i + "_" + j;
                        int resourceId =  context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
                        if (resourceId != 0) {
                            imageUrls.add(String.valueOf(resourceId)); // Store resource ID as string
                        } else {
                            Log.w("INIT", "Drawable resource not found: " + resourceName);
                            imageUrls.add(""); // Fallback for missing resources
                        }
                    }


                    String code = (i <= 3) ? "1" : (i <= 6) ? "2" : "3";
                    int price = (i <= 3) ? 150 + (i - 1) * 10
                            : (i <= 6) ? 200 + (i - 4) * 10
                            : 250 + (i - 7) * 10;

                    Rooms room = new Rooms("Room" + i, code, price, "Available", imageUrls);
                    long newRoomId = crudOperations.insertRoomSync(room);
                    if (newRoomId == -1) {
                        throw new RuntimeException("Failed to insert Room" + i);
                    }
                    room.setId((int) newRoomId);
                    Log.d("INIT", "Inserted " + room);

                    // Insert availabilities for this room ID
                    for (String date : nextFiveDates) {
                        RoomAvailability avail = new RoomAvailability((int) newRoomId, date);
                        long availId = crudOperations.insertAvailabilitySync(avail);
                        if (availId == -1) {
                            throw new RuntimeException("Failed to insert availability for Room" + i + " on " + date);
                        }
                        Log.d("INIT", "Inserted availability for Room ID=" + newRoomId + ", date=" + date);
                    }
                    Log.d("INIT", "Room ID=" + newRoomId + " has availability on " + nextFiveDates);
                }

                // Check all availability rows after insertion
                List<RoomAvailability> allAvailabilities = crudOperations.getAllAvailableRooms();
                Log.d("Available_CHECK", "Total availabilities after insert: " + allAvailabilities.size());
                for (RoomAvailability a : allAvailabilities) {
                    Log.d("Available_CHECK", "roomId=" + a.roomId + ", date=" + a.availableDate);
                }

                // Check all rooms after insertion
                List<Rooms> allRooms = crudOperations.getAllRooms();
                Log.d("Rooms_CHECK", "Total rooms after insert: " + allRooms.size());
                for (Rooms r : allRooms) {
                    Log.d("Rooms_CHECK", "Room: " + r);
                }

                // Mark transaction as successful
                crudOperations.setTransactionSuccessful();
                Log.d("INIT", "Transaction marked successful");
            } catch (Exception e) {
                Log.e("INIT", "Error during room initialization: " + e.getMessage(), e);
            } finally {
                // End transaction
                crudOperations.endTransaction();
                Log.d("INIT", "Transaction ended");

                // Post-transaction check to verify room persistence
                List<Rooms> allRoomsPostTransaction = crudOperations.getAllRooms();
                Log.d("Rooms_CHECK_POST", "Total rooms after transaction: " + allRoomsPostTransaction.size());
                if (allRoomsPostTransaction.isEmpty()) {
                    Log.e("Rooms_CHECK_POST", "Rooms deleted after transaction! Possible external deleteAllRooms call.");
                } else {
                    for (Rooms r : allRoomsPostTransaction) {
                        Log.d("Rooms_CHECK_POST", "Room persists: " + r);
                    }
                }

                // Execute callback on main thread
                if (onComplete != null) {
                    AppExecutors.getInstance().mainThread().execute(onComplete);
                }
            }
        });
    }

}

