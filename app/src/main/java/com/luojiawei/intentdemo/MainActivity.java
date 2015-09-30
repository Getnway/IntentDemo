package com.luojiawei.intentdemo;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    final int PICK_CONTACT = 100;
    final int PICK_RESOURCE = 101;
    final int REQUEST_CODE = 102;
    Button mBtnA;
    Button mBtnB;
    Button mBtnHome;
    Button mBtnPhone;
    Button mBtnContact;
    Button mBtnPhoto;
    TextView mTextView;
    ImageView mImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnA = (Button)findViewById(R.id.btn_a);
        mBtnB = (Button)findViewById(R.id.btn_b);
        mBtnHome = (Button)findViewById(R.id.btn_home);
        mBtnPhone = (Button)findViewById(R.id.btn_phone);
        mBtnContact = (Button)findViewById(R.id.btn_contact);
        mBtnPhoto = (Button)findViewById(R.id.btn_photo);
        mTextView = (TextView)findViewById(R.id.text_view);
        mImgView = (ImageView)findViewById(R.id.img_view);

        //按钮监听
        mBtnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "message_from_MainActivity";//待传送信息
                //显式Intent
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AActivity.class);
                intent.putExtra("my_data", data);//第一个参数为键值
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        mBtnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐式Intent
                Intent intent = new Intent();
                //打开BActivity
                intent.setAction("com.luojiawei.intentdemo.action.B_ACTIVITY");
                intent.addCategory("android.intent.category.DEFAULT");
                startActivity(intent);

            }
        });
        mBtnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐式Intent
                Intent intent = new Intent();
                intent.setAction("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.HOME");
                startActivity(intent);
            }
        });
        mBtnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拨打电话10086
                Intent intent = new Intent();
                intent.setAction("android.intent.action.DIAL");
                intent.setData(Uri.parse("tel:10086"));
                startActivity(intent);
            }
        });
        mBtnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取联系人
                Intent intent = new Intent();
                intent.setAction("android.intent.action.GET_CONTENT");
                intent.setType("vnd.android.cursor.item/phone");
                startActivityForResult(intent, PICK_CONTACT);
            }
        });
        mBtnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择一张照片
                Intent intent = new Intent();
                intent.setAction("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                intent.addCategory("android.intent.category.OPENABLE");
                startActivityForResult(intent, PICK_RESOURCE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case REQUEST_CODE:  //处理AActivity返回的数据
                if(resultCode == Activity.RESULT_OK){
                    String returnData = data.getStringExtra("return_data");
                    mTextView.setText(returnData);
                }
                break;
            case PICK_CONTACT:  //显示联系人姓名和号码
                if(resultCode == Activity.RESULT_OK){
                    CursorLoader cursorLoader = new CursorLoader(MainActivity.this,data.getData(), null, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    if(cursor != null && cursor.moveToFirst()){
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                        String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.NUMBER));
                        mTextView.setText(name + " " + number);
                    }else{
                        mTextView.setText("请选择一个联系人");
                    }
                }
                break;
            case PICK_RESOURCE: //显示选择的照片
                if (resultCode == Activity.RESULT_OK) {
                    try{
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                        if(photo != null){
                            mImgView.setImageBitmap(photo);
                        }else{
                            mTextView.setText("请选择一张照片");
                        }
                    } catch (FileNotFoundException e) {
                        mTextView.setText("照片打开失败！请重试...");
                        e.printStackTrace();
                    } catch (IOException e) {
                        mTextView.setText("照片打开失败！请重试...");
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
