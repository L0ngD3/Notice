package com.example.a13522.notice;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CameraAlbum extends AppCompatActivity {
public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO=2;
    private ImageView mPicture;
    private Uri mimageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_album);
       Button photo = (Button) findViewById(R.id.photo);
       Button choose= (Button) findViewById(R.id.choose);
        mPicture = (ImageView) findViewById(R.id.image);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.添加运行权限
                if (ContextCompat.checkSelfPermission(CameraAlbum.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CameraAlbum.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    //如果有权限调用openAlbum();
                    openAlbum();
                }
            }
        });


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建File对象，用于储存拍照后的照片
                File outImage = new File(getExternalCacheDir(),"image.jpg");
                try {
                    if (outImage.exists()){
                        outImage.delete();
                    }
                    outImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT>=24){//安卓7.0以上
                    mimageUri = FileProvider.getUriForFile(CameraAlbum.this,"com.example.a13522.file",outImage);
                }else {//低于7.0
                    mimageUri = Uri.fromFile(outImage);
                }
                //开启照相机
            Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,mimageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    //3.接收传入的图片的地址，和requestCode
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode==RESULT_OK){
                    try {
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(mimageUri));

                        mPicture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode==RESULT_OK){
                    //判断手机系统版本
                  //  if (Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                  //  }
                }
            default:break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    //功能：解析封装过的Uri，得到单个图片的地址
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            //如果document类型的uri，则通过document id处理
            String docId=DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
               String id= docId.split(":")[1];//解析出数字格式的id
               String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
              Uri contentUri=  ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
               imagePath= getImagePath(contentUri,null);
            }else if("content".equalsIgnoreCase(uri.getScheme())){
               imagePath= getImagePath(uri,null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())){
               imagePath= uri.getPath();
            }
            displayImage(imagePath);
        }
    }

//获取图片的地址，显示在Image控件上
    private void displayImage(String imagePath) {
        if (imagePath!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            mPicture.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"图片加载失败",Toast.LENGTH_SHORT).show();

        }
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        //通过uri和selection来获取真实的图片路径
      Cursor cursor= getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //2.打开相册
    public void openAlbum(){
        //设置意图打开相册
       Intent intent= new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");//获取图片 称为：图片过滤器
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册中选择的图片，参数2，可以理解为键值。传到onActivityResult（）方法中
    }


    //一下方法只是用来判断权限是否拒绝访问，并且给出提示
//    @Override
//    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
//        switch (requestCode){
//            case 1:
//                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                    openAlbum();
//                }else {
//                    Toast.makeText(this,"你拒绝了权限",Toast.LENGTH_SHORT).show();
//                }
//                break;
//            default:
//
//        }
//    }
}
