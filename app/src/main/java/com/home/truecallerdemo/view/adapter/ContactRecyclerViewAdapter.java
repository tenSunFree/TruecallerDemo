package com.home.truecallerdemo.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.home.truecallerdemo.ContactPersonActivity;
import com.home.truecallerdemo.R;
import com.home.truecallerdemo.data.ContactPersonHomePageData;
import com.home.truecallerdemo.eventbus.ContactPersonShortMessageServiceEvent;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.MyViewHolder> {

    private List<ContactPersonHomePageData> dataList;

    public ContactRecyclerViewAdapter(List<ContactPersonHomePageData> dataList) {
        this.dataList = dataList;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_person_view_pager_saved_item_recycler_view_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, int position) {
        final ContactPersonHomePageData model = dataList.get(position);
        if (model != null) {
            if (model.isShowLabel()) {
                holder.labelNameTextView.setText(String.valueOf(model.getName().charAt(0)));
                holder.labelNameFrameLayout.setVisibility(View.VISIBLE);
            } else {
                holder.labelNameFrameLayout.setVisibility(View.GONE);
            }
            if (model.getName() != null) {
                holder.nameTextView.setText(model.getName());
            }
            holder.messageImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(ContactPersonActivity.CONTACT_PERSON_SHORT_MESSAGE_SERVICE_FRAGMENT);
                    EventBus.getDefault().post(new ContactPersonShortMessageServiceEvent(model.getName(), model.getNumber()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, labelNameTextView;
        ImageView messageImageView;
        FrameLayout labelNameFrameLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            labelNameTextView = itemView.findViewById(R.id.labelNameTextView);
            messageImageView = itemView.findViewById(R.id.messageImageView);
            labelNameFrameLayout = itemView.findViewById(R.id.labelNameFrameLayout);
        }
    }
}
