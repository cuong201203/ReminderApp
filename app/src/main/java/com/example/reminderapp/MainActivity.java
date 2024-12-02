package com.example.reminderapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.reminderapp.UI.AddReminderActivity;
import com.example.reminderapp.UI.EditReminderActivity;
import com.example.reminderapp.UI.ListCategoryActivity;
import com.example.reminderapp.adapter.CategoryAdapter;
import com.example.reminderapp.adapter.ReminderAdapter;
import com.example.reminderapp.dao.CategoryDAO;
import com.example.reminderapp.dao.ReminderDAO;
import com.example.reminderapp.entity.Category;
import com.example.reminderapp.entity.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ReminderDAO reminderDAO = new ReminderDAO(this);

    ListView lvReminders = null;
    ArrayList<Reminder> listReminder = new ArrayList<>();
    ReminderAdapter adapter;

    FloatingActionButton btnAddReminderNavigate;
    TextView txtNoResultsForReminder;
    CategoryDAO categoryDAO = new CategoryDAO(this);
    ArrayList<Category> listCategory = new ArrayList<>();
    ArrayAdapter<Category> adapterCategory;

    Spinner spinCategory;

    MenuItem searchItemReminder, deleteReminderItem;
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

        registerForContextMenu(lvReminders);
    }



    public void getWidgets() {
        spinCategory = findViewById(R.id.spinCategory);
        lvReminders = findViewById(R.id.lvReminder);
        btnAddReminderNavigate = findViewById(R.id.btnAddReminderNavigate);
        txtNoResultsForReminder = findViewById(R.id.txtNoResultsForReminder);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchItemReminder = menu.findItem(R.id.itemSearchReminder);
        SearchView searchView = (SearchView) searchItemReminder.getActionView();
        if (searchView != null) {
            // Thiết lập listener cho SearchView
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Gọi hàm xử lý khi người dùng nhấn nút "Search"
                    doSearch(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Gọi hàm xử lý khi văn bản thay đổi

                        doSearch(newText);
                    return false;
                }
            });

            // Thiết lập một số thuộc tính khác nếu cần
            searchView.setQueryHint("Search reminders..."); // Gợi ý trong ô tìm kiếm
            searchView.setIconifiedByDefault(false); // Không thu gọn SearchView ban đầu
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_reminder_item,menu);
        MenuItem editItem = menu.findItem(R.id.itemEditReminder);
        MenuItem deleteItem = menu.findItem(R.id.itemDeleteReminder);
        editItem.setVisible(true);
        deleteItem.setVisible(true);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Reminder reminderSelected = listReminder.get(position);

        if(itemId==R.id.itemEditReminder){
            doEditReminder(reminderSelected);
            return true;
        }
        if (itemId == R.id.itemDeleteReminder) {
            doDeleteReminder(reminderSelected.getId());
            return true;
        }


        return super.onContextItemSelected(item);
    }

    private void doDeleteReminder(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Xóa nhắc nhở");
        builder.setMessage("Bạn có chắc chắn muốn xóa nhắc nhở này?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reminderDAO.deleteReminder(id);
                loadData();
                Toast.makeText(MainActivity.this,"Xóa nhắc nhở thành công!",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    public void doSearch(String infor) {
        if (!infor.trim().isEmpty()) {
            listReminder = reminderDAO.searchReminder(infor.trim());

            adapter = new ReminderAdapter(MainActivity.this, R.layout.item_reminder, listReminder);
            lvReminders.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (listReminder.isEmpty()) {
                txtNoResultsForReminder.setText("Không có nhắc nhở: " + infor + "");
                txtNoResultsForReminder.setVisibility(View.VISIBLE);
            } else {
                txtNoResultsForReminder.setVisibility(View.GONE);
            }
        } else {
            txtNoResultsForReminder.setVisibility(View.GONE);
            loadData();
        }
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
            //sortByLatestCreated();
            Toast.makeText(this, "Thời gian tạo mới nhất", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.itemOldestCreatedTime) {
            //sortByOldestCreated();
            Toast.makeText(this, "Thời gian tạo cũ nhất", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.itemManageNotification) {
            Toast.makeText(this, "Quản lý thông báo", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

//    public void loadDataByCategory(Category c){
//        listReminder.clear();
//        listReminder.addAll(c.getListReminder());
//        adapter.notifyDataSetChanged();
//    }

    public void loadData(){
        //set adapter cho spinner danh mục
        listCategory = categoryDAO.getAllCategories();
        adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,listCategory);
        spinCategory.setAdapter(adapterCategory);
        adapterCategory.notifyDataSetChanged();
        //set adapter cho listview
        listReminder = reminderDAO.getAllReminders();
        adapter = new ReminderAdapter(MainActivity.this,R.layout.item_reminder,listReminder);
        lvReminders.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void doEditReminder(Reminder reminderSelected){

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
                doEditReminder(reminderSelected);
            }
        });

        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCategory = adapterView.getItemAtPosition(i).toString();
                int id = categoryDAO.getCategoryIdByTitle(selectedCategory);
                Log.d("AAA",id+"");
                // Lọc danh sách nhắc nhở theo danh mục
                ArrayList<Reminder> filteredReminders = reminderDAO.filterRemindersByCategory(id);

                // Cập nhật danh sách nhắc nhở và thông báo cho Adapter
                listReminder.clear();
                listReminder.addAll(filteredReminders);

//                String string ="";
//                for(Reminder reminder: listReminder){
//                    string += reminder.getTitle()+ " ";
//                }
//                Log.d("AAA",string);

                adapter = new ReminderAdapter(MainActivity.this, R.layout.item_reminder, listReminder);
                lvReminders.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}
