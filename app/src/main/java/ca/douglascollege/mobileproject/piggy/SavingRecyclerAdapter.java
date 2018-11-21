package ca.douglascollege.mobileproject.piggy;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SavingRecyclerAdapter extends RecyclerView.Adapter<SavingRecyclerAdapter.SavingEventHolder> {

    private ArrayList<SavingRecyclerView> _savingList;
    private OnSavingItemClickListener _Listener;

    // this method set recycler list item click listener
    public interface OnSavingItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnSavingItemClickListener listener){
        _Listener = listener;
    }

    public static class SavingEventHolder extends RecyclerView.ViewHolder{

        public ImageView savingImg;
        public TextView textView1;
        public TextView textView2;
        public ImageView deleteImg;

        // Saving Event Card
        public SavingEventHolder(@NonNull View itemView, final OnSavingItemClickListener listener) {
            super(itemView);
            // get image and text from savings_for_event_card
            savingImg = itemView.findViewById(R.id.savingImg);
            textView1 = itemView.findViewById(R.id.txtSample);
            textView2 = itemView.findViewById(R.id.txtSample2);
            deleteImg = itemView.findViewById(R.id.imgDelete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }

                }
            });
        }
    }

    public SavingRecyclerAdapter(ArrayList<SavingRecyclerView> savingList){
        _savingList = savingList;
    }
    @NonNull
    @Override
    public SavingEventHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.savings_for_event_card, viewGroup, false);
        SavingEventHolder savingEventHolder = new SavingEventHolder(view, _Listener);
        return savingEventHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SavingEventHolder savingEventHolder, int i) {
        // get selected position and item
        // i is the position
        SavingRecyclerView currentItem = _savingList.get(i);
        savingEventHolder.savingImg.setImageResource(currentItem.getImageResource());
        savingEventHolder.textView1.setText(currentItem.getText1());
        savingEventHolder.textView2.setText(currentItem.getText2());
    }

    @Override
    public int getItemCount() {
        return _savingList.size();
    }
}
