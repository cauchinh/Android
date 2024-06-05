package com.example.to_do_list;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lv_job;
    JobAdapter jobAdapter;
    JobSqlite jobSqlite = new JobSqlite(this);
    ArrayList<Job> arrayListJob = new ArrayList<>();
    Job selectJob = null;

    @Override
    protected void onStart() {
        super.onStart();
        jobSqlite.openDB();
    }

    @Override
    protected void onStop() {
        super.onStop();
        jobSqlite.closeDB();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initWidgets();
        loadData();
        registerForContextMenu(lv_job);

        lv_job.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectJob = jobAdapter.getItem(position);
                return false;
            }
        });
    }

    private void loadData() {
        Cursor cursor = jobSqlite.DisplayAll();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            arrayListJob.add(new Job(cursor.getInt(cursor.getColumnIndexOrThrow(JobSqlite.getID())),
                    cursor.getString(cursor.getColumnIndexOrThrow(JobSqlite.getNAME()))));
        }
        jobAdapter = new JobAdapter(MainActivity.this, R.layout.items, arrayListJob);
        lv_job.setAdapter(jobAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actionAdd) {
            actionAdd();
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionAdd() {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.layout_add_job);

        EditText edtName = dialog.findViewById(R.id.edtName);
        EditText edtAddId = dialog.findViewById(R.id.edtAddId);
        Button btnAdd = dialog.findViewById(R.id.btnAdd);
        Button btnExit = dialog.findViewById(R.id.btnThoat);
        
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAddId.getText().toString().isEmpty() || edtName.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Vui lòng điền đủ ô trống", Toast.LENGTH_SHORT).show();
                }else {
                    int id = Integer.parseInt(edtAddId.getText().toString());
                    String name = edtName.getText().toString();

                    if (jobSqlite.isIdExists(id)){
                        Toast.makeText(MainActivity.this, "ID đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                    else if (jobSqlite.isNameExists(name)){
                        Toast.makeText(MainActivity.this, "Tên công việc đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        long resultAdd = jobSqlite.insertData(id, name);
                        if (resultAdd == -1){
                            Toast.makeText(MainActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            arrayListJob.clear();
                            loadData();
                        }
                    }

                }

            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        assert info != null;
        int position = info.position;
        Job jd = arrayListJob.get(position);
        if (item.getItemId() == R.id.actionDelete){
            actionDelete(jd);
        }
        if (item.getItemId() == R.id.actionEdit){
            actionEdit(jd);
        }
        return super.onContextItemSelected(item);
    }

    private void actionEdit(Job jd) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.layout_edit_job);

        EditText edtJob = dialog.findViewById(R.id.edtJob);
        EditText edtId = dialog.findViewById(R.id.edtId);
        Button btnEdit = dialog.findViewById(R.id.btnEdit);
        Button btnExit = dialog.findViewById(R.id.btnExit);

        edtId.setText(String.valueOf(selectJob.getId()));
        edtJob.setText(selectJob.getNameJob());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = Integer.parseInt(edtId.getText().toString());
                String name = edtJob.getText().toString();
                if (jobSqlite.isNameExists(name)) {
                    Toast.makeText(MainActivity.this, "Tên công việc đã tồn tại", Toast.LENGTH_SHORT).show();
                }
                else{
                    long resultEdit = jobSqlite.Update(id, name);
                    if (resultEdit == -1){
                        Toast.makeText(MainActivity.this, "Chỉnh sửa thất bại", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Chinh sửa thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        arrayListJob.clear();
                        loadData();
                    }
                }

            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void actionDelete(Job jd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure to delete?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                jobSqlite.Delete(jd.getId());
                Toast.makeText(MainActivity.this, "Xoá thành công", Toast.LENGTH_SHORT).show();
                arrayListJob.clear();
                loadData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initWidgets() {
        lv_job = (ListView) findViewById(R.id.lv_job);
    }
    
}