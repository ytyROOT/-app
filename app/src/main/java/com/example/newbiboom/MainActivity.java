package com.example.newbiboom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ListView listview=null;
    private TextView textview=null;

    private static final String ROOT_PATH="/";
    private ArrayList<String> mfilename=null;//保存所有的文件名
    private ArrayList<String> mfilepath=null;//保存所有的文件路径


    private File FileCopy=null;
    private File FileRename=null;
    private File CurPath=null;

    int[] imgs={R.drawable.folder,R.drawable.file};
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //显示当前文件夹下的所有的文件或者文件夹
        showFileDir(ROOT_PATH);//显示根目录下的所有的文件或者文件夹




    }

    //显示当前文件夹下的所有的文件或者文件夹
    private void showFileDir(String path)
    {
        mfilepath=new ArrayList<String>();
        mfilename=new ArrayList<String>();
        File file=new File(path);
        File[] files=file.listFiles();
        if(!this.ROOT_PATH.equals(path)) //如果path不是根目录
        {
            mfilename.add("@1");
            mfilepath.add(this.ROOT_PATH);


            mfilename.add("@2");
            mfilepath.add(file.getParent());

        }
        //遍历files
        for(File f:files)
        {
            mfilename.add(f.getName());
            mfilepath.add(f.getPath());
        }


        listview=(ListView)findViewById(R.id.listView1);
        //装载适配器
        listview.setAdapter(new MyAdapter());
        //绑定列表项的单击事件 ===设置listview列表项的单击事件监听器
        listview.setOnItemClickListener(new OnItemClickListener() {

                                            @Override
                                            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                                                    long arg3) {
                                                // TODO Auto-generated method stub
                                                //先判断权限
                                                String path=mfilepath.get(position);
                                                File f=new File(path);
                                                Toast.makeText(MainActivity.this, "正在读取"+path, Toast.LENGTH_LONG).show();
                                                if(f.exists() && f.canRead())
                                                {
                                                    if(f.isDirectory())
                                                    {
                                                        Toast.makeText(MainActivity.this, "正在读取", Toast.LENGTH_LONG).show();
                                                        showFileDir(path);

                                                    }
                                                    else
                                                    {
                                                        //文件处理
                                                        filehandle(f);
                                                    }

                                                }
                                                else
                                                {
                                                    new AlertDialog.Builder(MainActivity.this).setTitle("警告")
                                                            .setMessage("你无权访问").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // TODO Auto-generated method stub

                                                        }
                                                    }).show();
                                                    ;


                                                }


                                            }


                                        }
        );

        CurPath=file;//保存当前路径
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void alert(Object message)//显示提示
    {
        final String Text=message.toString();

        MainActivity.this.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, Text,Toast.LENGTH_LONG).show();
            }



        });



    }


    //文件处理核心模块  用对话框来
    void filehandle(final File f)
    {
        DialogInterface.OnClickListener  listener=new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(which==0) //复制
                {
                    //文件复制模块
                    FileCopy=f;
                    alert("复制成功");
                }


                if(which==1)//删除
                {
                    new AlertDialog.Builder(MainActivity.this).setTitle("警告")
                            .setMessage("您确定要删除该文件吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    //这里才真正进行删除
                                    if(f.delete())
                                    {
                                        alert("删除成功");
                                        showFileDir(f.getParent());
                                    }
                                    else
                                    {
                                        alert("删除失败");

                                    }


                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                }
                            })


                            .show();

                }
                if(which==2)//重命名
                {
                    FileRename=f;
                    //显示新的活动窗口
                    Intent _intent=new Intent(MainActivity.this,newFileName.class);
MainActivity.this.startActivityForResult(_intent,100);
                }
                if(which==3)//黏贴
                {
                    //粘贴菜单项 执行粘贴代码
                         if(!CurPath.canWrite())
                        {
                            alert("该文件夹不能粘贴！");
                        }
                        else if(FileCopy!=null)
                        {
                            //文件粘贴
                            File TargetPath=new File(CurPath,FileCopy.getName());
                            int i=0;
                            while(TargetPath.exists())
                            {
                                i++;
                                TargetPath=new File(CurPath,"("+i+")"+ FileCopy.getName());


                            }
                            //CurPath复制到TargetPath
                            MainActivity.this.CopyFile(FileCopy, TargetPath);

                        }
                        else
                        {
                            alert("没有文件要粘贴");

                        }

                }

            }
        };

        String[] menu={"复制","删除","重命名","黏贴"};
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("请选择你要执行的操作")
                .setItems(menu, listener)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                }).show();



    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==Activity.RESULT_OK ) //获得新文件名并重命名
        {
            String val=data.getExtras().getString("newfilename");
            //重命名
            if(FileRename==null)
            {
                alert("文件无法重命名");
            }
            else
            {
                File newPath=new File(this.CurPath,val);
                FileRename.renameTo(newPath);
                this.showFileDir(CurPath.toString());
                alert("文件名成功修改");
            }
        }
    }




    class MyAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mfilename.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return   mfilename.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if(convertView==null)
            {
                //手动加载相关界面
                LayoutInflater inflater=(LayoutInflater)MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View itemView=(View)inflater.inflate(R.layout.items, null);
                TextView title=(TextView) itemView.findViewById(R.id.textView1);
                title.setText(mfilename.get(position).toString());
                convertView=itemView;
            }
            else
            {
                TextView title=(TextView) convertView.findViewById(R.id.textView1);
                title.setText(mfilename.get(position).toString());
            }

            //
            File file=new File(mfilepath.get(position).toString());
            if(mfilename.get(position).equals("@1"))  //文件夹
            {
                TextView title=(TextView) convertView.findViewById(R.id.textView1);
                title.setText("/");
                ImageView iv=(ImageView) convertView.findViewById(R.id.imageView1);
                iv.setImageResource(imgs[0]);
            }
            else if(mfilename.get(position).equals("@2"))  //文件夹
            {
                TextView title=(TextView) convertView.findViewById(R.id.textView1);
                title.setText("..");
                ImageView iv=(ImageView) convertView.findViewById(R.id.imageView1);
                iv.setImageResource(imgs[0]);
            }
            else
            {
                if(file.isDirectory()) //文件夹
                {
                    ImageView iv=(ImageView) convertView.findViewById(R.id.imageView1);
                    iv.setImageResource(imgs[0]);
                }
                else if(file.isFile()) //文件
                {
                    ImageView iv=(ImageView) convertView.findViewById(R.id.imageView1);
                    iv.setImageResource(imgs[1]);
                }
                else
                {
                    ImageView iv=(ImageView) convertView.findViewById(R.id.imageView1);
                    iv.setImageResource(imgs[1]);
                }


            }

            return convertView;





        }

    }

    FileChannel in;
    FileChannel out;
    FileInputStream inStream;
    FileOutputStream outStream;
    ByteBuffer buffer;
    public void CopyFile(File Source,File Target)
    {
        try {
            // 初始化
            alert(Source.getPath());
            alert(Target.getPath());
            inStream = new FileInputStream(Source);
            outStream = new FileOutputStream(Target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            // 缓冲区为4K
            buffer = ByteBuffer.allocate(4096);

            // 实例化一个进度条对话框
            final ProgressDialog progressDialog = new ProgressDialog(
                    MainActivity.this);
            // 设置标题
            progressDialog.setTitle("粘贴中...");
            // 设置进度条的风格
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // 设置点返回或按区域外不取消进度条
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // 创建线程
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        // 设置进度条的最大值
                        progressDialog.setMax((int) in.size());
                        // 循环读取写入
                        while (in.read(buffer) != -1) {
                            buffer.flip();
                            out.write(buffer);
                            buffer.clear();
                            // 设置进度
                            progressDialog.setProgress((int) out.size());
                        }
                        // 取消进度条
                        progressDialog.cancel();
                        alert("粘贴完成");
                        // 在UI线程中初始化文件列表 (不能在非UI线程操作界面)
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.this.showFileDir(CurPath.toString());
                            }
                        });
                    } catch (IOException e) {
                        alert("粘贴出错");
                        e.printStackTrace();
                    } finally {
                        // 关闭流和管道
                        try {
                            inStream.close();
                            in.close();
                            outStream.close();
                            out.close();
                        } catch (IOException e) {
                            alert("关闭流出错");
                            e.printStackTrace();
                        }
                    }
                }
            });
            t.start();

        } catch (FileNotFoundException e1) {
            alert("粘贴源不见了~");
            e1.printStackTrace();
        }

    }


    //添加菜单项选择事件监听器
    public  boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            //粘贴菜单项 执行粘贴代码
            case  R.id.pasteitem:
                if(!CurPath.canWrite())
                {
                    alert("该文件夹不能粘贴！");
                }
                else if(FileCopy!=null)
                {
                    //文件粘贴
                    File TargetPath=new File(CurPath,FileCopy.getName());
                    int i=0;
                    while(TargetPath.exists())
                    {
                        i++;
                        TargetPath=new File(CurPath,"("+i+")"+ FileCopy.getName());


                    }
                    //CurPath复制到TargetPath
                    MainActivity.this.CopyFile(FileCopy, TargetPath);

                }
                else
                {
                    alert("没有文件要粘贴");

                }
                break;
        }

        return true;
    }


}
