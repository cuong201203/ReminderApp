package com.example.reminderapp.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.reminderapp.adapter.CategoryAdapter;
import com.example.reminderapp.dao.CategoryDAO;
import com.example.reminderapp.entity.Category;
import com.example.reminderapp.MainActivity;
import com.example.reminderapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;

public class ListCategoryActivity extends AppCompatActivity {
    CategoryDAO categoryDAO = new CategoryDAO(this);
    ListView lvCategory;
    ArrayList<Category> listCategory = new ArrayList<>();
    CategoryAdapter adapter;
    FloatingActionButton btnAddCategoryNavigate;
    MenuItem searchItem, deleteCategoriesItem;
    TextView txtNoResultsForCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Thêm danh mục "Mặc định" nếu chưa có
        categoryDAO.addDefaultCategory();

        getWidgets();
        loadData();
        doClickListenerEvent();

        registerForContextMenu(lvCategory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        searchItem = menu.findItem(R.id.itemSearchCategory);
        deleteCategoriesItem = menu.findItem(R.id.itemDeleteCategories);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                doSearch(newText);
                boolean hasSelectedItems = !adapter.getSelectedCategoryIds().isEmpty();
                updateSearchDeleteButtonVisibility(hasSelectedItems);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.itemCategoryTitleAZ) {
            adapter.sortCategoryTitleAZ();
            return true;
        } else if (itemId == R.id.itemCategoryTitleZA) {
            adapter.sortCategoryTitleZA();
            return true;
        } else if (itemId == R.id.itemTotalRemindersDesc) {
            adapter.sortTotalRemindersDesc();
            return true;
        } else if (itemId == R.id.itemTotalRemindersAsc) {
            adapter.sortTotalRemindersAsc();
            return true;
        } else if (itemId == R.id.itemDeleteCategories) {
            deleteSelectedCategories();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateSearchDeleteButtonVisibility(boolean isVisible) {
        if (searchItem != null && deleteCategoriesItem != null) {
            searchItem.setVisible(!isVisible);
            deleteCategoriesItem.setVisible(isVisible);
        }
    }

    public void deleteSelectedCategories() {
        HashSet<Integer> selectedCategoryIds = adapter.getSelectedCategoryIds();
        if (!selectedCategoryIds.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ListCategoryActivity.this);
            builder.setTitle("Xóa các danh mục đã chọn");
            builder.setMessage("Bạn có chắc chắn muốn xóa các danh mục này?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (int categoryId : selectedCategoryIds) {
                        categoryDAO.deleteCategory(categoryId);
                    }
                    loadData();
                    updateSearchDeleteButtonVisibility(false);
                    if (searchItem != null && searchItem.isActionViewExpanded()) {
                        searchItem.collapseActionView();
                    }
                    Toast.makeText(ListCategoryActivity.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_category_item, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        Category selectedCategory = listCategory.get(position);

        // Nếu là danh mục "Mặc định", ẩn item sửa và xóa
        boolean isDefaultCategory = selectedCategory.getTitle().equals("Mặc định");
        MenuItem editItem = menu.findItem(R.id.itemEditCategory);
        MenuItem deleteItem = menu.findItem(R.id.itemDeleteCategory);
        editItem.setVisible(!isDefaultCategory);
        deleteItem.setVisible(!isDefaultCategory);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Category selectedCategory = listCategory.get(position);

        if (itemId == R.id.itemEditCategory) {
            editCategory(selectedCategory);
            return true;
        } else if (itemId == R.id.itemDeleteCategory) {
            deleteCategory(selectedCategory.getId());
            return true;
        } else if (itemId == R.id.itemShareCategory) {
            shareCategory(selectedCategory.getId(), selectedCategory.getTitle());
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại danh sách từ db mỗi khi quay lại màn hình này
        loadData();

        boolean hasSelectedItems = !adapter.getSelectedCategoryIds().isEmpty();
        updateSearchDeleteButtonVisibility(hasSelectedItems);

        // Đóng SearchView nếu nó đang mở
        if (searchItem != null && searchItem.isActionViewExpanded()) {
            searchItem.collapseActionView();
        }
    }

    public void getWidgets() {
        lvCategory = findViewById(R.id.lvCategory);
        btnAddCategoryNavigate = findViewById(R.id.btnAddCategoryNavigate);
        txtNoResultsForCategory = findViewById(R.id.txtNoResultsForCategory);

    }

    public void loadData() {
        listCategory = categoryDAO.getAllCategories();
        moveDefaultCategoryToTop();

        adapter = new CategoryAdapter(ListCategoryActivity.this, R.layout.item_category, listCategory);
        lvCategory.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void doClickListenerEvent() {
        btnAddCategoryNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListCategoryActivity.this, AddCategoryActivity.class);
                startActivity(intent);
            }
        });

        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListCategoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void editCategory(Category selectedCategory) {
        Intent intent = new Intent(ListCategoryActivity.this, EditCategoryActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("categoryId", selectedCategory.getId());
        bundle.putString("categoryTitle", selectedCategory.getTitle());
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public void deleteCategory(int categoryId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListCategoryActivity.this);
        builder.setTitle("Xóa danh mục");
        builder.setMessage("Bạn có chắc chắn muốn xóa danh mục này?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoryDAO.deleteCategory(categoryId);
                loadData();
                if (searchItem != null && searchItem.isActionViewExpanded()) {
                    searchItem.collapseActionView();
                }
                Toast.makeText(ListCategoryActivity.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    // Tìm và đưa danh mục "Mặc định" lên đầu danh sách
    private void moveDefaultCategoryToTop() {
        Category defaultCategory = null;
        for (Category category : listCategory) {
            if ("Mặc định".equals(category.getTitle())) {
                defaultCategory = category;
                break;
            }
        }
        if (defaultCategory != null) {
            listCategory.remove(defaultCategory);
            listCategory.add(0, defaultCategory);
        }
    }

    public void doSearch(String infor) {
        if (!infor.trim().isEmpty()) {
            listCategory = categoryDAO.searchCategory(infor.trim());

            moveDefaultCategoryToTop();

            adapter = new CategoryAdapter(ListCategoryActivity.this, R.layout.item_category, listCategory);
            lvCategory.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (listCategory.isEmpty()) {
                txtNoResultsForCategory.setText("Không có danh mục: " + infor + "");
                txtNoResultsForCategory.setVisibility(View.VISIBLE);
            } else {
                txtNoResultsForCategory.setVisibility(View.GONE);
            }
        } else {
            txtNoResultsForCategory.setVisibility(View.GONE);
            loadData();
        }
    }

    public void shareCategory(Integer categoryId, String categoryTitle) {
        ArrayList<String> reminders = categoryDAO.getRemindersByCategory(categoryId);

        StringBuilder strData = new StringBuilder("Danh mục: ").append(categoryTitle).append("\n\n");
        if (reminders.isEmpty()) {
            strData.append("Không có nhắc nhở nào trong danh mục này.");
        } else {
            strData.append("Danh sách nhắc nhở:\n\n");
            for (int i = 0; i < reminders.size(); i++) {
                strData.append(i + 1).append(". ").append(reminders.get(i)).append("\n\n");
            }
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, strData.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Ứng dụng nhắc nhở");
        startActivity(Intent.createChooser(shareIntent, "Chọn ứng dụng để chia sẻ:"));
    }
}