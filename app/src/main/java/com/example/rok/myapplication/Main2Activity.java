package com.example.rok.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {
    LinearLayout l1, l2;
    DatePicker d1;
    EditText e1;
    ListView list;
    Button b1;
    TextView t1;
    ArrayList<String> data = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    String filename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        init();
        listup();
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Main2Activity.this);
                dlg.setTitle("삭제확인").setMessage("정말이에요").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String path = getExternalPath();

                        File file = new File(path + "mydiary/" + data.get(position).toString());
                        file.delete();
                        data.remove(position);

                        adapter.notifyDataSetChanged();
                        listup();
                    }
                }).setNegativeButton("NO",null).show();

                return false;
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                b1.setText("수정");

                l2.setVisibility(View.VISIBLE);
                l1.setVisibility(View.INVISIBLE);
                String path = getExternalPath();
                try {
                    BufferedReader br = new BufferedReader(
                            new FileReader(path + "mydiary/" + data.get(position).toString()));
                    String readStr = "";
                    String str = null;
                    while ((str = br.readLine()) != null)
                        readStr += str + "\n";

                    br.close();

                    e1.setText(readStr.substring(0, readStr.length()-1));
                    filename = path + "mydiary/" + data.get(position).toString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String year  = data.get(position).toString().substring(0,4);
                String month = data.get(position).toString().substring(5,7);
                String date = data.get(position).toString().substring(8,10);

                d1.updateDate(Integer.parseInt(year),Integer.parseInt(month)-1,Integer.parseInt(date));
                b1.setText("수정");
                listup();
            }

        });


    }

    public void onClick(View view) {
        int day2 = d1.getDayOfMonth();
        int month2 = d1.getMonth() + 1;
        final String month;
        final String day;
        if (month2 < 10) {
            month = "0" + month2;
        } else {
            month = String.valueOf(month2);
        }
        if (day2 < 10) {
            day = "0" + day2;
        } else {
            day = String.valueOf(day2);
        }
        switch (view.getId()) {

            case R.id.btn1:
                b1.setText("저장");
                l2.setVisibility(View.VISIBLE);
                l1.setVisibility(View.INVISIBLE);
                Calendar calendar = Calendar.getInstance();

                int nowyear = calendar.get(Calendar.YEAR);
                int nowmonth = calendar.get(Calendar.MONTH);
                int nowdate = calendar.get(Calendar.DATE);

                d1.init(nowyear, nowmonth, nowdate, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int month4 = monthOfYear+1;
                        String month3;
                        String day3;
                        if (monthOfYear < 10) {
                            month3 = "0" + month4;
                        } else {
                            month3 = String.valueOf(month4);
                        }
                        if (dayOfMonth < 10) {
                            day3 = "0" + dayOfMonth;
                        } else {
                            day3 = String.valueOf(dayOfMonth);
                        }
                        String decider = year + "-" + month3 + "-" + day3 + ".memo";
                        if(data.contains(decider)){
                            b1.setText("수정");
                            Toast.makeText(getApplicationContext(),"수정모드입니다",Toast.LENGTH_SHORT).show();
                            String path = getExternalPath();
                            BufferedReader br = null;
                            try {
                                br = new BufferedReader(
                                        new FileReader(path + "mydiary/" + decider));
                                String readStr = "";
                                String str = null;
                                while ((str = br.readLine()) != null)
                                    readStr += str + "\n";
                                br.close();
                                e1.setText(readStr.substring(0, readStr.length()-1));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
//

                break;
            case R.id.btnsave:
                if(b1.getText().toString().matches("저장")) {

                    try {
                        String path = getExternalPath();
                        BufferedWriter bw = new BufferedWriter(new FileWriter(path + "mydiary/" + d1.getYear() + "-" + month + "-" + day + ".memo", false));
                        bw.write(e1.getText().toString());
                        bw.newLine();
                        bw.close();
                        Toast.makeText(getApplicationContext(), "save complete", Toast.LENGTH_SHORT).show();
                        e1.setText("");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    l1.setVisibility(View.VISIBLE);
                    l2.setVisibility(View.INVISIBLE);
                    if(data.contains(d1.getYear() + "-" + month + "-" + day + ".memo")){
                        listup();
                    }
                    else{
                        listup();
                    }
                }
                else{
                    AlertDialog.Builder dlg = new AlertDialog.Builder(Main2Activity.this);
                    dlg.setTitle("수정확인").setMessage("덮어씨우시겠습니까?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String path = getExternalPath();

                                File file = new File(filename);
                                file.delete();
                                BufferedWriter bw = new BufferedWriter(new FileWriter(path + "mydiary/" + d1.getYear() + "-" + month + "-" + day + ".memo", false));
                                bw.write(e1.getText().toString());
                                bw.newLine();
                                bw.close();
                                listup();
                                Toast.makeText(getApplicationContext(), "save complete", Toast.LENGTH_SHORT).show();
                                e1.setText("");
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            l1.setVisibility(View.VISIBLE);
                            l2.setVisibility(View.INVISIBLE);
                            if(data.contains(d1.getYear() + "-" + month + "-" + day + ".memo")){

                                listup();
                            }
                            else{
                                listup();
                            }
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String path = getExternalPath();

                                File file = new File(d1.getYear() + "-" + month + "-" + day + ".memo");
                                file.delete();
                                BufferedWriter bw = new BufferedWriter(new FileWriter(path + "mydiary/" + d1.getYear() + "-" + month + "-" + day + ".memo", false));
                                bw.write(e1.getText().toString());
                                bw.newLine();
                                bw.close();
                                listup();
                                Toast.makeText(getApplicationContext(), "save complete", Toast.LENGTH_SHORT).show();
                                e1.setText("");
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            l1.setVisibility(View.VISIBLE);
                            l2.setVisibility(View.INVISIBLE);
                            if(data.contains(d1.getYear() + "-" + month + "-" + day + ".memo")){

                                listup();
                            }
                            else{

                                listup();
                            }
                        }
                    }).show();
                }
                break;

            case R.id.btncancel:
                l1.setVisibility(View.VISIBLE);
                l2.setVisibility(View.INVISIBLE);
                e1.setText("");
                break;

        }
    }

    private void checkpermission() {

        int permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissioninfo == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "sd카드 쓰기권한있음", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "권한 설명", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    public String getExternalPath() {
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

        } else
            sdPath = getFilesDir() + ";" +
                    "";
        Toast.makeText(getApplicationContext(), sdPath, Toast.LENGTH_SHORT).show();
        return sdPath;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String str = null;
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                str = "Sdcard쓰기권한 승인";
            else str = "SD card 쓰기권한 거부";
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    Comparator<String> comparator = new Comparator<String>() {
        @Override
        public int compare(String s, String t1) {
            return s.compareTo(t1);
        }
    };
    public void listup(){
        String path = getExternalPath();
        File[] files = new File(path + "mydiary").listFiles();
        String str = "";
        data.removeAll(data);
        adapter.notifyDataSetChanged();
        for (File f : files) {
            data.add(f.getName().toString());
            adapter.notifyDataSetChanged();
        }
        Collections.sort(data,comparator);
        t1.setText("등록된 메모 개수: "+data.size());
    }
    void init(){
        t1 = (TextView)findViewById(R.id.tvCount);
        l1 = (LinearLayout) findViewById(R.id.linear1);
        l2 = (LinearLayout) findViewById(R.id.linear2);
        d1 = (DatePicker) findViewById(R.id.date);
        e1 = (EditText) findViewById(R.id.edit);
        b1 = (Button)findViewById(R.id.btnsave);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        list = (ListView) findViewById(R.id.listview);
        list.setAdapter(adapter);
        listup();
    }




}
