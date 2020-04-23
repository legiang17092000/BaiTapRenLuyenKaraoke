package legiang.com.baitaprenluyenkaraoke;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import legiang.adapter.BaiHatAdapter;
import legiang.com.model.BaiHat;

public class MainActivity extends AppCompatActivity {

   public  String DATABASE_NAME="Arirang.sqlite";
   public String DB_PATH_SUFFIX = "/databases/";
   public static SQLiteDatabase database=null;
   public static String TableName="ArirangSongList";

    ListView lvAll;
    BaiHatAdapter adapterAll;
    TabHost tabHost;

    ListView lvLove;
    BaiHatAdapter adapterLove;

    public static int selectedTab=0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        processCopy();
        setupTabHost();
        addControls();
        hienThiToanBoBaiHat();
        addEvents();
    }
    private void setupTabHost() {
        tabHost=findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tab1=tabHost.newTabSpec("tab1");
        tab1.setContent(R.id.tab1);
        tab1.setIndicator("All");
        tabHost.addTab(tab1);

        TabHost.TabSpec tab2=tabHost.newTabSpec("tab2");
        tab2.setContent(R.id.tab2);
        tab2.setIndicator("Love");
        tabHost.addTab(tab2);
    }

    private void addEvents() {
    tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String s) {
            if(s.equals("tab1"))
            {
                hienThiToanBoBaiHat();
                selectedTab=0;
            }
            else {
                hienThiToanBoBaiHatYeuThich();
                selectedTab=1;
            }
        }
    });

    }

    private void hienThiToanBoBaiHatYeuThich() {
        Cursor c=database.query(TableName,null,"YEUTHICH=?",new String[]{"1"},null,null,null);
        adapterLove.clear();
        while(c.moveToNext())
        {
            String ma=c.getString(0);
            String ten=c.getString(1);
            String casi=c.getString(3);
            int thich=c.getInt(5);
            BaiHat bh=new BaiHat(ma,ten,casi,thich);
            adapterLove.add(bh);
        }
        c.close();
    }

    private void hienThiToanBoBaiHat() {
        database=openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor c=database.query(TableName,null,null,null,null,null,null);
        adapterAll.clear();
        while(c.moveToNext())
        {
            String ma=c.getString(0);
            String ten=c.getString(1);
            String casi=c.getString(3);
            int thich=c.getInt(5);
            BaiHat bh=new BaiHat(ma,ten,casi,thich);
            adapterAll.add(bh);
        }
        c.close();
    }

    private void processCopy() {

        {
            try
            { File dbFile = getDatabasePath(DATABASE_NAME);
                if (!dbFile.exists())
                {
                    CopyDataBaseFromAsset();
                }

            }
            catch (Exception e)
            {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }
    public void CopyDataBaseFromAsset()
    {
        try {
            InputStream myInput;
            myInput = getAssets().open(DATABASE_NAME);
            String outFileName = getDatabasePath();
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void addControls() {
        lvAll=findViewById(R.id.lvAll);
        adapterAll=new BaiHatAdapter(MainActivity.this,R.layout.item);
        lvAll.setAdapter(adapterAll);

        lvLove=findViewById(R.id.lvLove);
        adapterLove=new BaiHatAdapter(MainActivity.this,R.layout.item);
        lvLove.setAdapter(adapterLove);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        MenuItem menuSearch=menu.findItem(R.id.menuSearch);
        SearchView searchView= (SearchView) menuSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                xuLyTimKiem(s);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    private void xuLyTimKiem(String s) {
        Cursor c=database.query(TableName,null,
                "MABH like ? or TENBH like ? or TACGIA like ?",
                new String[]{"%"+s+"%","%"+s+"%","%"+s+"%"},
                null,null,null);
        adapterAll.clear();
        while(c.moveToNext())
        {
            String ma=c.getString(0);
            String ten=c.getString(1);
            String casi=c.getString(3);
            int thich=c.getInt(5);
            BaiHat bh=new BaiHat(ma,ten,casi,thich);
            adapterAll.add(bh); //nhap email vo alo? mô rồi nhập đâu mà email gì á, email đăng nhập git giangntl18406@st.uel.edu.vn anh copy dán

        }
        c.close();

    }
}
