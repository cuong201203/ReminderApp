package com.example.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.reminderapp.UI.ListCategoryActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void getWidgets() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.itemManageCategory) {
            Intent intent = new Intent(MainActivity.this, ListCategoryActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.itemNewestReminderTIme) {
            Toast.makeText(this, "Thời gian nhắc nhở mới nhất", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.itemOldestReminderTime) {
            Toast.makeText(this, "Thời gian nhắc nhở cũ nhất", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.itemNewestCreatedTime) {
            Toast.makeText(this, "Thời gian tạo mới nhất", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.itemOldestCreatedTime) {
            Toast.makeText(this, "Thời gian tạo cũ nhất", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.itemManageNotification) {
            Toast.makeText(this, "Quản lý thông báo", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}