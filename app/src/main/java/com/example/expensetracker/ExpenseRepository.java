package com.example.expensetracker;

import com.example.expensetracker.Model.Data;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExpenseRepository {
    private final DatabaseReference expenseDatabase;


    public ExpenseRepository(String uid) {
        expenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
    }

    public DatabaseReference getExpenseDatabase() {
        return expenseDatabase;
    }

    public void updateDataItem(Data data) {
        expenseDatabase.child(data.getId()).setValue(data);
    }

    public void deleteDataItem(String postKey) {
        expenseDatabase.child(postKey).removeValue();
    }
}