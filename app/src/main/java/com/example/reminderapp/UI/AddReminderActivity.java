package com.example.reminderapp.UI;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.example.reminderapp.adapter.CategoryAdapter;
import com.example.reminderapp.dao.CategoryDAO;
import com.example.reminderapp.dao.ReminderDAO;
import com.example.reminderapp.entity.Category;
import com.example.reminderapp.entity.Reminder;

import java.util.ArrayList;
import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {
    ReminderDAO reminderDAO = new ReminderDAO(this);
    EditText editAddReminderTitle, editAddReminderDescription;
    TextView txtAddReminderDate,txtAddReminderTime;
    Button btnAddReminderDate,btnAddReminderTime,btnAddReminder;
    Spinner spinCategory;
    Calendar cal;

//
    CategoryDAO categoryDAO = new CategoryDAO(this);
    ArrayList<Category> categories = new ArrayList<Category>();
    ArrayAdapter<Category> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_reminder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        getWidget();
        setUpData();
        setUpCategorySpinner();
        doSomeThing();


    }



    public void getWidget() {
        editAddReminderTitle = findViewById(R.id.editAddReminderTitle);
        editAddReminderDescription = findViewById(R.id.editAddReminderDescription);

        txtAddReminderDate = findViewById(R.id.txtAddReminderDate);

        txtAddReminderTime = findViewById(R.id.txtAddReminderTime);

        btnAddReminderDate = findViewById(R.id.btnAddReminderDate);
        btnAddReminderTime = findViewById(R.id.btnAddReminderTime);
        btnAddReminder = findViewById(R.id.btnAddReminder);

        spinCategory = findViewById(R.id.spinCategory);
    }
    public void setUpCategorySpinner() {
        categories = categoryDAO.getAllCategories();
        adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_list_item_1,categories);
        spinCategory.setAdapter(adapter);
    }
    public boolean validateInputs() {
        if (editAddReminderTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Bạn chưa nhập tiêu đề nhắc nhở", Toast.LENGTH_SHORT).show();
            editAddReminderTitle.requestFocus();
            return false;
        }
        return true;
    }
    public void setUpData(){
        cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String formattedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
        txtAddReminderDate.setText(formattedDate);
    }
    public void doSomeThing() {
        //Xử lý button Date chọn ngày
        btnAddReminderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        txtAddReminderDate.setText(formattedDate);
                    }
                };
                DatePickerDialog date = new DatePickerDialog(AddReminderActivity.this,callback,year,month,day);
                date.setTitle("Chọn ngày");
                date.show();
            }
        });

        //Xử lý button chọn giờ
        btnAddReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY); // Lấy giờ hiện tại (24h format)
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        txtAddReminderTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                };
                TimePickerDialog time = new TimePickerDialog(AddReminderActivity.this,callback,hour,minute,true);
                time.setTitle("Chọn giờ");
                time.show();
            }
        });

        //Xử lý button thêm
        btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputs()){

                        String title = editAddReminderTitle.getText().toString();
                        String description = editAddReminderDescription.getText().toString();
                        String date = txtAddReminderDate.getText().toString();
                        String time = txtAddReminderTime.getText().toString();

                        Category category = (Category) spinCategory.getSelectedItem();
                        int categoryId = category.getId();
                        Reminder reminder = new Reminder(title, description, date,time,categoryId);
                    try{
                        reminderDAO.addReminder(reminder);
                        Toast.makeText(AddReminderActivity.this,"Thêm nhắc nhở thành công",Toast.LENGTH_SHORT).show();
                        finish();
                    }catch (Exception e){
                        Toast.makeText(AddReminderActivity.this,"Thêm nhắc nhở thất bại",Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });
    }
}