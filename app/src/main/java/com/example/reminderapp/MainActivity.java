package com.example.reminderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.reminderapp.UI.AddReminderActivity;
import com.example.reminderapp.UI.EditReminderActivity;
import com.example.reminderapp.UI.ListCategoryActivity;
import com.example.reminderapp.adapter.ReminderAdapter;
import com.example.reminderapp.dao.ReminderDAO;
import com.example.reminderapp.entity.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ReminderDAO reminderDAO = new ReminderDAO(MainActivity.this);
    ListView lvReminders = null;
    ArrayList<Reminder> listReminder = new ArrayList<>();
    ReminderAdapter adapter;
    FloatingActionButton btnAddReminderNavigate;
    TextView txtNoResultsForReminder;
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
        getWidgets();
        loadData();
        doClickListenerEvent();
    }



    public void getWidgets() {
        lvReminders = findViewById(R.id.lvReminder);
        btnAddReminderNavigate = findViewById(R.id.btnAddReminderNavigate);
        txtNoResultsForReminder = findViewById(R.id.txtNoResultsForReminder);
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

    public void loadData(){
//        listReminder = reminderDAO.getAllReminders();
        listReminder.add(new Reminder(1, "Meeting", "Project discussion", "2024-11-20", "10:00 AM", 1));
        listReminder.add(new Reminder(2, "Meeting2", "Project discussion", "2024-11-22", "12:00 AM", 2));

        adapter = new ReminderAdapter(MainActivity.this,R.layout.item_reminder,listReminder);
        lvReminders.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void doClickListenerEvent() {
        btnAddReminderNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
                startActivity(intent);
            }
        });

        lvReminders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Reminder reminderSelected = listReminder.get(position);

                Intent intent = new Intent(MainActivity.this, EditReminderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("reminderId", reminderSelected.getId());
                bundle.putString("reminderTitle", reminderSelected.getTitle());
                bundle.putString("reminderDescription", reminderSelected.getDescription());
                bundle.putString("reminderDate", reminderSelected.getDate());
                bundle.putString("reminderTime", reminderSelected.getTime());
                bundle.putInt("categoryId", reminderSelected.getCategoryId());

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }
}
