package com.example.reminderapp.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.reminderapp.entity.Category;

public class AddCategoryActivity extends AppCompatActivity {
    CategoryDAO categoryDAO = new CategoryDAO(this);
    EditText editAddCategoryTitle;
    Button btnAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
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
        doSomething();
    }

    public void getWidgets() {
        editAddCategoryTitle = findViewById(R.id.editAddCategoryTitle);
        btnAddCategory = findViewById(R.id.btnAddCategory);
    }

    public boolean validateInputs() {
        if (editAddCategoryTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Bạn chưa nhập tên danh mục!", Toast.LENGTH_SHORT).show();
            editAddCategoryTitle.requestFocus();
            return false;
        }
        return true;
    }

    public void doSomething() {
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editAddCategoryTitle.getText().toString().trim();

                if (title.trim().equals("Mặc định")) {
                    Toast.makeText(AddCategoryActivity.this, "Tên danh mục này đã tồn tại!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (validateInputs()) {
                    if (!categoryDAO.isCategoryTitleExists(title, null)) {
                        Category category = new Category(title);
                        categoryDAO.addCategory(category);
                        Toast.makeText(AddCategoryActivity.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddCategoryActivity.this, "Tên danh mục này đã tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}