/**
 *
 */
package com.news.cqunews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabbar;
    private ViewPager pager;
    private int tab_cnts;
    static final int MOVABLE=4;// movable when tabs_cnt<MOVABLE
    private List<Fragment> fragments;// all the fragments
    private String[] labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabbar=findViewById(R.id.tabbar);
        pager=findViewById(R.id.pager);
        //init operations
        initData();
        initPager();
        initTabLayout();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;// true:show the menu
    }

    /**
     * menu events
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.about_menu:
                AlertDialog about=new AlertDialog.Builder(this)
                        .setMessage("github: https://github.com/WindWaving/Today-s-List")
                        .setPositiveButton("Close",null)
                        .create();
                about.show();
                break;
            case R.id.update_menu:// start update service
                Intent intent=new Intent(MainActivity.this,UpdateService.class);
                startService(intent);
                break;
            case R.id.lang_menu:
                GetGlobals.NEWS_ARRAY=GetGlobals.ZH_NEWS.get(GetGlobals.CUR_LABEL);//NEWS_ARRAY: news list in current page
                Intent transIntent=new Intent(MainActivity.this,TranslateService.class);
                startService(transIntent);
                Toast.makeText(this,"Translating...,please wait a moment...",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * stop update service
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent=new Intent(MainActivity.this,UpdateService.class);
        stopService(intent);
    }

    /*
    * init data
    * */
    private void initData(){
        //init titles and numbers
        labels=new String[]{"综合新闻", "教学科研","招生就业","交流合作","校园生活","媒体重大"};
        tab_cnts=labels.length;
        //init fragments
        GetGlobals.NEWS_ARRAY=new JSONArray();
        fragments=new ArrayList<>();
        for(int i=0;i<tab_cnts;++i){
            fragments.add(NewsFragment.Instance(labels[i]));
        }
        //init some global variables
        GetGlobals.ZH_NEWS=new HashMap<String, JSONArray>();
        GetGlobals.EN_NEWS=new HashMap<String, JSONArray>();
        GetGlobals.PAGE_INIT=new HashMap<String, Boolean>();
        GetGlobals.LANG=new HashMap<String,String>();
        GetGlobals.CUR_LABEL="综合新闻";
        for(int i=0;i<tab_cnts;++i){
            GetGlobals.PAGE_INIT.put(labels[i],Boolean.FALSE);//not loaded yet
            GetGlobals.LANG.put(labels[i],"zh");
        }
    }

    /*
    * init viewPager
    * */
    private void initPager(){
        pager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
    }
    /*
    * init the tab bar
    */
    private void initTabLayout(){
        tabbar.setTabMode(tab_cnts<MOVABLE?TabLayout.MODE_FIXED:TabLayout.MODE_SCROLLABLE);// if the numbers>4,it's scrollable
        tabbar.setupWithViewPager(pager);// associate the tabbar with the viewpager
        tabbar.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                GetGlobals.CUR_LABEL=tab.getText().toString();
                System.out.println("current "+GetGlobals.CUR_LABEL);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    /*
    * adapter to bind fragments with viewPager
    * */
    private class MyPageAdapter extends FragmentPagerAdapter{


        public MyPageAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return labels.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return labels[position];
        }
    }

}

