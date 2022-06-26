package com.hfba.keepingnotes;

import android.os.Parcel;
import android.os.Parcelable;

/*
κλάση note περιγράφει το περιεχόμενο του κάθε note αντικειμένου. Ετσι περιέχει τον τιτλο, το περιεχόμενο, την ημερομηνία δημιουργίας, εικόνες, αρχεία.
Για τους σκοπούς της επικοινωνίας μέσω των activities υλοποιεεί το parcelable το οποιο υλοποιει java serialization

 */
public class Note implements Parcelable {
    String title;
    String description;
    String createdTime;

    public Note(String title, String description, String createdTime) {
        this.title = title;
        this.description = description;
        this.createdTime = createdTime;
    }
//parcelable constructor
    protected Note(Parcel in) {
        title = in.readString();
        description = in.readString();
        createdTime = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };


//getters setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescritpion() {
        return description;
    }

    public void setDescritpion(String description) {
        this.description = description;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(createdTime);
    }
}
