package com.example.luxevistaresortapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
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
import com.example.luxevistaresortapp.Data.database.Model.User;
import com.example.luxevistaresortapp.MainUi.MainUiActivity;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private CrudOperations crudOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        TextView gradientText = findViewById(R.id.gradientText);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        TextView btnCreateAccount = findViewById(R.id.btnCreateAccount);

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



        btnCreateAccount.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Pattern.compile("^(.+)@(.+)$").matcher(email).matches()) {
                Toast.makeText(RegisterActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }


            AppExecutors.getInstance().diskIO().execute(() -> {
                User existingUser = crudOperations.getUserByEmail(email);
                runOnUiThread(() -> {
                    if (existingUser != null) {
                        Toast.makeText(RegisterActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        User user = new User(name, email, password);
                        crudOperations.insertUserData(user);
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    }
                });
            });


        });
    }

}
