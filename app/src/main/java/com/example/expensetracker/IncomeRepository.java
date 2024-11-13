package com.example.expensetracker;


import com.example.expensetracker.Model.Data;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IncomeRepository {
    private final DatabaseReference incomeDatabase;


    public IncomeRepository(String uid) {
        incomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
    }

    public DatabaseReference getIncomeDatabase() {
        return incomeDatabase;
    }

    public void updateDataItem(Data data) {
        incomeDatabase.child(data.getId()).setValue(data);
    }

    public void deleteDataItem(String postKey) {
        incomeDatabase.child(postKey).removeValue();
    }
}
