package com.example.reminderapp.UI;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.reminderapp.R;
import com.example.reminderapp.dao.CategoryDAO;
import com.example.reminderapp.dao.ReminderDAO;
import com.example.reminderapp.entity.Category;
import com.example.reminderapp.entity.Reminder;

import java.util.ArrayList;
import java.util.Calendar;

public class EditReminderActivity extends AppCompatActivity {
    ReminderDAO reminderDAO = new ReminderDAO(this);
    EditText editEditReminderTitle, editEditReminderDescription;
    TextView txtEditReminderDate,txtEditReminderTime;
    Button btnEditReminderDate,btnEditReminderTime,btnEditReminder;
    Spinner spinCategory;
    Calendar cal;

    CategoryDAO categoryDAO = new CategoryDAO(this);
    ArrayList<Category> categories = new ArrayList<Category>();
    ArrayAdapter<Category> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_reminder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getWidgets();
        setUpCategorySpinner();
        doSomeThing();

    }

    public void getWidgets() {
        editEditReminderTitle = findViewById(R.id.editEditReminderTitle);
        editEditReminderDescription = findViewById(R.id.editEditReminderDescription);
        txtEditReminderDate = findViewById(R.id.txtEditReminderDate);
        txtEditReminderTime = findViewById(R.id.txtEditReminderTime);

        btnEditReminderDate = findViewById(R.id.btnEditReminderDate);
        btnEditReminderTime = findViewById(R.id.btnEditReminderTime);
        btnEditReminder = findViewById(R.id.btnEditReminder);
        spinCategory = findViewById(R.id.spinCategory);
    }
    public boolean validateInputs() {
        if (editEditReminderTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Bạn chưa nhập tiêu đề nhắc nhở", Toast.LENGTH_SHORT).show();
            editEditReminderTitle.requestFocus();
            return false;
        }
        return true;
    }
    public void setUpCategorySpinner() {
        categories = categoryDAO.getAllCategories(); // Lấy danh mục từ DAO
        adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_list_item_1,categories);
        spinCategory.setAdapter(adapter);
    }
    public void doSomeThing(){
        btnEditReminderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        txtEditReminderDate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                };
                DatePickerDialog date = new DatePickerDialog(EditReminderActivity.this,callback,year,month,day);
                date.setTitle("Chọn ngày");
                date.show();
            }
        });
        btnEditReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY); // Lấy giờ hiện tại (24h format)
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        txtEditReminderTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                };
                TimePickerDialog time = new TimePickerDialog(EditReminderActivity.this,callback,hour,minute,true);
                time.setTitle("Chọn ngày");
                time.show();
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            int reminderId = bundle.getInt("reminderId");

            String reminderTitle = bundle.getString("reminderTitle");
            editEditReminderTitle.setText(reminderTitle);

            String reminderDescription = bundle.getString("reminderDescription");
            editEditReminderDescription.setText(reminderDescription);

            String reminderDate = bundle.getString("reminderDate");
            txtEditReminderDate.setText(reminderDate);

            String reminderTime = bundle.getString("reminderTime");
            txtEditReminderTime.setText(reminderTime);
            final int[] categoryId = {bundle.getInt("categoryId")};
            spinCategory.setSelection(categoryId[0] -1);

            btnEditReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateInputs()) {
                        try {

                            String title = editEditReminderTitle.getText().toString();
                            String description = editEditReminderDescription.getText().toString();
                            String date = txtEditReminderDate.getText().toString();
                            String time = txtEditReminderTime.getText().toString();
                            Category category = (Category) spinCategory.getSelectedItem();
                            int categoryId = category.getId();

                            Reminder reminder = new Reminder(reminderId,title, description, date, time, categoryId);
                            category.add(reminder);
                            reminderDAO.updateReminder(reminder);
                            Toast.makeText(EditReminderActivity.this, "Sửa nhắc nhở thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(EditReminderActivity.this, "Sửa nhắc nhở thất bại", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });

        }




    }
}