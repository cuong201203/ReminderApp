package com.example.reminderapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reminderapp.R;
import com.example.reminderapp.dao.CategoryDAO;
import com.example.reminderapp.dao.ReminderDAO;
import com.example.reminderapp.entity.Reminder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReminderAdapter extends ArrayAdapter {

    Activity context=null;
    int layoutID;
    ArrayList<Reminder> list;
    ReminderDAO reminderDAO;

    public ReminderAdapter(@NonNull Activity context, int resource, @NonNull List<Reminder> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.list = new ArrayList<Reminder>(objects);
        this.reminderDAO = new ReminderDAO(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutID,null);
        if(list.size()>0&&position>=0){
            // Lấy đối tượng Reminder từ danh sách theo vị trí
             Reminder reminder = list.get(position);
            // Tham chiếu các thành phần giao diện từ layout
            final TextView txtReminderTitle = convertView.findViewById(R.id.txtReminderTitle);
            final TextView txtReminderDescription = convertView.findViewById(R.id.txtReminderDescription);
            final TextView txtReminderDate = convertView.findViewById(R.id.txtAddReminderDate);
            final TextView txtReminderTime = convertView.findViewById(R.id.txtAddReminderTime);
            final TextView txtReminderCategory = convertView.findViewById(R.id.txtReminderCategory);

            txtReminderTitle.setText(reminder.getTitle()+"");
            txtReminderDescription.setText(reminder.getDescription()+"");
            txtReminderDate.setText(reminder.getDate()+"");
            txtReminderTime.setText(reminder.getTime()+"");
//            txtReminderCategory.setText(reminder.getCategoryId()+"");
//
            String categoryName =reminderDAO.getCategoryNameById(reminder.getCategoryId());
            txtReminderCategory.setText(categoryName);

        }
        return convertView;
    }

    // Sắp xếp theo thời gian tạo mới nhất
    public void sortByDateTimeAscending() {
        Collections.sort(list, new Comparator<Reminder>() {
            @Override
            public int compare(Reminder reminder1, Reminder reminder2) {
                int dateCompare = reminder1.getDate().compareTo(reminder2.getDate());
                if (dateCompare == 0) {
                    return reminder1.getTime().compareTo(reminder2.getTime());
                }
                return dateCompare;
            }
        });
        notifyDataSetChanged();
    }

    // Sắp xếp theo thời gian tạo cũ nhất
    public void sortByDateTimeDescending() {
        Collections.sort(list, new Comparator<Reminder>() {
            @Override
            public int compare(Reminder reminder1, Reminder reminder2) {
                int dateCompare = reminder2.getDate().compareTo(reminder1.getDate()); // Reverse order for descending
                if (dateCompare == 0) {
                    return reminder2.getTime().compareTo(reminder1.getTime()); // Reverse order for descending
                }
                return dateCompare;
            }
        });
        notifyDataSetChanged();
    }
}
