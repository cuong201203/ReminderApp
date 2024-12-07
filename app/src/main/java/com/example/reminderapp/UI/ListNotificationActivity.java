package com.example.reminderapp.UI;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.reminderapp.R;
import com.example.reminderapp.adapter.NotificationAdapter;
import com.example.reminderapp.dao.NotificationDAO;
import com.example.reminderapp.entity.Notification;

import java.util.ArrayList;
import java.util.List;

public class ListNotificationActivity extends AppCompatActivity {

    NotificationDAO notificationDAO = new NotificationDAO(this);

    ListView lvNotification = null;
    ArrayList<Notification> listNotification = new ArrayList<>();
    NotificationAdapter adapter;
    ArrayAdapter<String> listTime=null;
    ArrayList<String> time = new ArrayList<>(List.of("Tất cả", "Hôm nay", "Tuần này", "Tháng này"));

    Spinner spinNotification;
    TextView txtNoResult;
    MenuItem searchItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_notification);

        notificationDAO.deleteAllNotifications();
        notificationDAO.insertNotifications();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getWidgets();
        loadSpinner();
        loadData();
        doClickListenerEvent();

        registerForContextMenu(lvNotification);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        searchItem = menu.findItem(R.id.itemSearchNotification);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                v.requestLayout();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                doSearch(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.itemDeleteNotifications) {
            doDeleteAllNotification();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_notification_item,menu);
        MenuItem deleteItem = menu.findItem(R.id.itemDeleteNotification);
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Notification notificationSelected = listNotification.get(position);

        if (itemId == R.id.itemDeleteNotification) {
            doDeleteNotification(notificationSelected.getId());
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void getWidgets() {
        spinNotification = findViewById(R.id.spinNotification);
        lvNotification = findViewById(R.id.lvNotification);
        txtNoResult = findViewById(R.id.txtNoResult);
    }
    public void loadSpinner(){
        listTime = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, time);
        listTime.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinNotification.setAdapter(listTime);
    }

    public void doDeleteAllNotification(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ListNotificationActivity.this);
        builder.setTitle("Xóa thông báo");
        builder.setMessage("Bạn có chắc chắn muốn xóa tất cả thông báo?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                notificationDAO.deleteAllNotifications();
                loadData();
                Toast.makeText(ListNotificationActivity.this,"Xóa thành công!",Toast.LENGTH_SHORT).show();
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

    public void doDeleteNotification(int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(ListNotificationActivity.this);
        builder.setTitle("Xóa thông báo");
        builder.setMessage("Bạn có chắc chắn muốn xóa thông báo này?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                notificationDAO.deleteNotification(id);
                loadData();
                Toast.makeText(ListNotificationActivity.this,"Xóa thành công!",Toast.LENGTH_SHORT).show();
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

    public void loadData(){
        //set adapter cho listview
        listNotification = notificationDAO.getAllNotifications();
        adapter = new NotificationAdapter(ListNotificationActivity.this,R.layout.item_notification,listNotification);
        lvNotification.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    public void doClickListenerEvent(){
        spinNotification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filterNotifications(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void doSearch(String infor) {
        if (!infor.trim().isEmpty()) {
            listNotification = notificationDAO.searchNotification(infor.trim());

            adapter = new NotificationAdapter(ListNotificationActivity.this, R.layout.item_notification, listNotification);
            lvNotification.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            if (listNotification.isEmpty()) {
                txtNoResult.setVisibility(View.VISIBLE);
                txtNoResult.setText("Không có thông báo: " + infor);
            } else {
                txtNoResult.setVisibility(View.GONE);
            }
        } else {
            txtNoResult.setVisibility(View.GONE);
            loadData();
        }
    }
    public void filterNotifications(int filterOption) {
        ArrayList<Notification> filteredNotifications = new ArrayList<>();

        // Lọc thông báo theo lựa chọn
        switch (filterOption) {
            case 0:
                filteredNotifications = notificationDAO.getAllNotifications();
                break;
            case 1:
                filteredNotifications = notificationDAO.getNotificationsByDate();
                break;
            case 2:
                filteredNotifications = notificationDAO.getNotificationsByWeek();
                break;
            case 3:
                filteredNotifications = notificationDAO.getNotificationsByMonth();
                break;
            default:
                break;
        }

        // Cập nhật dữ liệu cho ListView
        adapter = new NotificationAdapter(ListNotificationActivity.this, R.layout.item_notification, filteredNotifications);
        lvNotification.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //Kiểm tra xem có thông báo nào hay không
        if (filteredNotifications.isEmpty()) {
            txtNoResult.setText("Không có thông báo phù hợp");
            txtNoResult.setVisibility(View.VISIBLE);
        } else {
            txtNoResult.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

}