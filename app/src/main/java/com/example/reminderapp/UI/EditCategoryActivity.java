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

public class EditCategoryActivity extends AppCompatActivity {
    CategoryDAO categoryDAO = new CategoryDAO(this);
    EditText editEditCategoryTitle;
    Button btnEditCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_category);
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
        editEditCategoryTitle = findViewById(R.id.editEditCategoryTitle);
        btnEditCategory = findViewById(R.id.btnEditCategory);
    }

    public boolean validateInputs() {
        if (editEditCategoryTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Bạn chưa nhập tên danh mục!", Toast.LENGTH_SHORT).show();
            editEditCategoryTitle.requestFocus();
            return false;
        }
        return true;
    }

    public void doSomething() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int categoryId = bundle.getInt("categoryId");
            String categoryTitle = bundle.getString("categoryTitle");

            editEditCategoryTitle.setText(categoryTitle);

            btnEditCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newTitle = editEditCategoryTitle.getText().toString().trim();

                    if (newTitle.trim().equals("Mặc định")) {
                        Toast.makeText(EditCategoryActivity.this, "Tên danh mục này đã tồn tại!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (validateInputs()) {
                        if (!categoryDAO.isCategoryTitleExists(newTitle, categoryId)) {
                            Category updatedCategory = new Category(categoryId, newTitle);
                            categoryDAO.updateCategory(updatedCategory);

                            Toast.makeText(EditCategoryActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditCategoryActivity.this, "Tên danh mục này đã tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}