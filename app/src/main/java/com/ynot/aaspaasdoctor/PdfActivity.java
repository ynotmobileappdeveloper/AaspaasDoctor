package com.ynot.aaspaasdoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.IOException;

public class PdfActivity extends AppCompatActivity {

    TextView subject,username,course;
    PDFView pdfView;
    String name;
    int i=0;
    String pdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        pdfView=findViewById(R.id.pdfView);
        Bundle bundle=getIntent().getExtras();
        pdf  =bundle.getString("pdf");
        DownloadFile downloadFile=new DownloadFile();
        downloadFile .execute(pdf,"Report"+"_"+name+i+".pdf");
        view();

    }




    public void view()
    {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Aaspaas/" +"Report"+"_"+(i)+".pdf");  // -> filename = maven.pdf
        Uri path = Uri.fromFile(pdfFile);

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try{
            startActivity(pdfIntent);
            pdfView.fromFile(pdfFile).load();
        }
        catch(ActivityNotFoundException e){
            Toast.makeText(PdfActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            i++;
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/studymeterial.pdf
            String fileName = strings[1];  // -> studymeterial.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "Vidyajayam");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onBackPressed();
    }
}