package com.hfba.keepingnotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*
   Adapter του recyclerView ο οποιος χρειαζεται για την διασύνδεση των δεδομένων με τα views που υπάρχουν μεσα στο ResView
   τα δεδομένα που μας ενδιαφέρουν είναι μονο ο τίτλος, η περιγραφή (υποσύνολο του περιεχομένου του note) και το ποτε δημιουργήθηκε
   καθώς αυτα φαίνονται στις καρτες του resView.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.viewHolder>{
    private ArrayList<Note> MyNote_List;

    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongListener;
//interfaces για click λειτουργίες (διαγραφής και επιλογης)
    public interface  OnItemClickListener {
        void onItemClick(int position);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(View v,int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener longlistener) {mLongListener=longlistener;}



    public class viewHolder extends RecyclerView.ViewHolder{
        TextView CardTile;
        TextView CardDesc;
        TextView CardCreated;




        public viewHolder(@NonNull View itemView, OnItemClickListener listener,OnItemLongClickListener longlistener) {
            super(itemView);
            //binding layout to variables
            CardTile = itemView.findViewById(R.id.CardTitle);
            CardDesc = itemView.findViewById(R.id.CardDesc);
            CardCreated = itemView.findViewById(R.id.CardCreated);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getBindingAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                     }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(longlistener != null){
                        int position = getBindingAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            longlistener.onItemLongClick(view,position);




                        }
                    }
                    return false;
                }
            });
         }
    }

    public Adapter(ArrayList<Note> Note_List) {
        MyNote_List = Note_List;
    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        viewHolder view_holder = new viewHolder(v, mListener,mLongListener);
        return view_holder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Note currentNote = MyNote_List.get(position);
        //substring μεχρι 30 χαρακτηρες για την εμφανιση στην λιστα
        int n=30;
        String str=currentNote.getDescritpion();
        holder.CardTile.setText(currentNote.getTitle());
        holder.CardDesc.setText(str.substring(0,Math.min(str.length(), n)));
        holder.CardCreated.setText(currentNote.getCreatedTime());


    }

    @Override
    //επιστρέφει πόσα items θα εχει η λίστα μας
    public int getItemCount() {

        return MyNote_List.size();

    }


}
