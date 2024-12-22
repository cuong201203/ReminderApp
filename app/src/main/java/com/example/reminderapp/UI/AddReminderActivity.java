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
    //Xử lý button date chọn ngày
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
                        // Tạo đối tượng Calendar cho ngày được chọn
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        // So sánh ngày được chọn với ngày hiện tại
                        if (selectedDate.getTimeInMillis() >= cal.getTimeInMillis()) {
                            String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                            txtAddReminderDate.setText(formattedDate);
                        } else {
                            Toast.makeText(AddReminderActivity.this, "Ngày được chọn không được nhỏ hơn ngày hiện tại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                DatePickerDialog date = new DatePickerDialog(AddReminderActivity.this, callback, year, month, day);
                date.setTitle("Chọn ngày");
                date.show();
            }
        });
        //Xử lý button date chọn giờ
        btnAddReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY); // Lấy giờ hiện tại (24h format)
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        // Lấy ngày hiện tại từ ô nhập "txtAddReminderDate"
                        String selectedDateText = txtAddReminderDate.getText().toString();

                        if (!selectedDateText.isEmpty()) {
                            // Parse ngày được chọn
                            Calendar selectedDate = Calendar.getInstance();
                            try {
                                String[] dateParts = selectedDateText.split("/");
                                int day = Integer.parseInt(dateParts[0]);
                                int month = Integer.parseInt(dateParts[1]) - 1; // Tháng bắt đầu từ 0
                                int year = Integer.parseInt(dateParts[2]);

                                selectedDate.set(year, month, day, hourOfDay, minute);

                                // Kiểm tra nếu ngày giờ được chọn >= ngày giờ hiện tại
                                if (selectedDate.getTimeInMillis() >= cal.getTimeInMillis()) {
                                    txtAddReminderTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                                } else {
                                    Toast.makeText(AddReminderActivity.this, "Giờ được chọn không được nhỏ hơn giờ hiện tại!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(AddReminderActivity.this, "Ngày không hợp lệ!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddReminderActivity.this, "Vui lòng chọn ngày trước!", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                TimePickerDialog time = new TimePickerDialog(AddReminderActivity.this, callback, hour, minute, true);
                time.setTitle("Chọn giờ");
                time.show();
            }
        });
        //Xử lý button thêm
        btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    String title = editAddReminderTitle.getText().toString();
                    String description = editAddReminderDescription.getText().toString();
                    String date = txtAddReminderDate.getText().toString();
                    String time = txtAddReminderTime.getText().toString();
                    Category category = (Category) spinCategory.getSelectedItem();
                    int categoryId = category.getId();
                    // Kiểm tra tính hợp lệ của thời gian nhập
                    if (isDateTimeValid(date, time)) {
                        Reminder reminder = new Reminder(title, description, date, time, categoryId);
                        try {
                            reminderDAO.addReminder(reminder);
                            Toast.makeText(AddReminderActivity.this, "Thêm nhắc nhở thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(AddReminderActivity.this, "Thêm nhắc nhở thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddReminderActivity.this, "Thời gian không được nhỏ hơn thời gian hiện tại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private boolean isDateTimeValid(String date, String time) {
        try {
            // Parse ngày và giờ từ chuỗi nhập vào
            String[] dateParts = date.split("/");
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Tháng bắt đầu từ 0
            int year = Integer.parseInt(dateParts[2]);

            String[] timeParts = time.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            // Tạo Calendar từ dữ liệu nhập
            Calendar inputDateTime = Calendar.getInstance();
            inputDateTime.set(year, month, day, hour, minute);
            // Lấy thời gian hiện tại
            Calendar currentDateTime = Calendar.getInstance();
            // So sánh thời gian nhập với thời gian hiện tại
            return inputDateTime.getTimeInMillis() >= currentDateTime.getTimeInMillis();
        } catch (Exception e) {
            return false; // Trả về false nếu dữ liệu không hợp lệ
        }
    }

}