package com.ifbaiano.powermap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifbaiano.powermap.R;
import com.ifbaiano.powermap.model.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> userList;

    public UsersAdapter(ArrayList<User> users) {
        this.userList = users;
    }

    public void UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewUser;
        TextView nameUser;
        TextView emailUserItem;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewUser = itemView.findViewById(R.id.imageViewUser);
            nameUser = itemView.findViewById(R.id.nameUser);
            emailUserItem = itemView.findViewById(R.id.emailUserItem);
        }

        public void bind(User user) {
            nameUser.setText(user.getName());
            emailUserItem.setText(user.getEmail());
        }
    }
}
