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
import com.example.reminderapp.dao.NotificationDAO;
import com.example.reminderapp.entity.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends ArrayAdapter {
    Activity context=null;
    int layoutID;
    ArrayList<Notification> list;
    NotificationDAO notificationDAO;


    public NotificationAdapter(@NonNull Activity context, int resource, @NonNull List<Notification> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.list = new ArrayList<Notification>(objects);
        this.notificationDAO = new NotificationDAO(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String status = "";
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutID,null);
        if(list.size()>0&&position>=0){

            // Lấy đối tượng Reminder từ danh sách theo vị trí
            Notification notification = list.get(position);
            // Tham chiếu các thành phần giao diện từ layout
            final TextView txtNotificationTitle = convertView.findViewById(R.id.txtNotificationTitle);
            final TextView txtNotificationDescription = convertView.findViewById(R.id.txtNotificationDescription);
            final TextView txtNotificationDate = convertView.findViewById(R.id.txtNotificationDate);
            final TextView txtNotificationTime = convertView.findViewById(R.id.txtNotificationTime);

            if(notification.getStatus() == 1){
                status = "[Nhắc lại] ";
            }
            txtNotificationTitle.setText(status + notification.getTitle());
            txtNotificationDescription.setText(notification.getContent()+"");
            txtNotificationDate.setText(notification.getDate()+"");
            txtNotificationTime.setText(notification.getTime()+"");

        }
        return convertView;
    }
}
