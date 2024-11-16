package com.example.reminderapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reminderapp.R;
import com.example.reminderapp.UI.ListCategoryActivity;
import com.example.reminderapp.dao.CategoryDAO;
import com.example.reminderapp.entity.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class CategoryAdapter extends ArrayAdapter {
    Activity context;
    int layoutID;
    ArrayList<Category> list = null;
    CategoryDAO categoryDAO;
    HashSet<Integer> selectedCategoryIds = new HashSet<>();

    public CategoryAdapter(@NonNull Activity context, int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.list = new ArrayList<>(objects);
        this.categoryDAO = new CategoryDAO(context);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutID, null);

        if (list.size() > 0 && position >= 0) {
            final TextView txtCategoryTitle = convertView.findViewById(R.id.txtCategoryTitle);
            final TextView txtTotalReminders = convertView.findViewById(R.id.txtTotalReminders);
            final CheckBox checkBox = convertView.findViewById(R.id.chkCategoryItem);

            Category category = list.get(position);
            txtCategoryTitle.setText(category.getTitle());

            if (category.getTitle().equals("Mặc định")) {
                checkBox.setVisibility(View.INVISIBLE);
            } else {
                checkBox.setChecked(selectedCategoryIds.contains(category.getId()));
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedCategoryIds.add(category.getId());
                    } else {
                        selectedCategoryIds.remove(category.getId());
                    }
                    ((ListCategoryActivity) context).updateSearchDeleteButtonVisibility(!selectedCategoryIds.isEmpty());
                    notifyDataSetChanged();
                });
            }

            int totalReminders = categoryDAO.getTotalReminders(category.getId());
            txtTotalReminders.setText("Nhắc nhở: " + totalReminders);
        }
        return convertView;
    }

    public HashSet<Integer> getSelectedCategoryIds() {
        return selectedCategoryIds;
    }

    private int compareCategoryWithDefault(Category c1, Category c2) {
        if (c1.getTitle().equals("Mặc định")) return -1;
        if (c2.getTitle().equals("Mặc định")) return 1;
        return 0; // Cả hai đều không phải "Mặc định"
    }

    public void sortCategoryTitleAZ() {
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                // Đặt danh mục "Mặc định" luôn ở đầu
                if (compareCategoryWithDefault(c1, c2) != 0) return compareCategoryWithDefault(c1, c2);

                return c1.getTitle().compareToIgnoreCase(c2.getTitle());
            }
        });
        notifyDataSetChanged();
    }

    public void sortCategoryTitleZA() {
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                if (compareCategoryWithDefault(c1, c2) != 0) return compareCategoryWithDefault(c1, c2);

                return c2.getTitle().compareToIgnoreCase(c1.getTitle());
            }
        });
        notifyDataSetChanged();
    }

    public void sortTotalRemindersDesc() {
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                int reminderCount1 = categoryDAO.getTotalReminders(c1.getId());
                int reminderCount2 = categoryDAO.getTotalReminders(c2.getId());

                if (compareCategoryWithDefault(c1, c2) != 0) return compareCategoryWithDefault(c1, c2);

                return Integer.compare(reminderCount2, reminderCount1);
            }
        });
        notifyDataSetChanged();
    }

    public void sortTotalRemindersAsc() {
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                int reminderCount1 = categoryDAO.getTotalReminders(c1.getId());
                int reminderCount2 = categoryDAO.getTotalReminders(c2.getId());

                if (compareCategoryWithDefault(c1, c2) != 0) return compareCategoryWithDefault(c1, c2);

                return Integer.compare(reminderCount1, reminderCount2);
            }
        });
        notifyDataSetChanged();
    }
}
