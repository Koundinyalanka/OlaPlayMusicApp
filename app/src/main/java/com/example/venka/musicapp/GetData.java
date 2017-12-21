package com.example.venka.musicapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GetData extends AppCompatActivity {
ProgressDialog pd;
JSONArray song;
    SqliteDB db=new SqliteDB(this);
AutoCompleteTextView actv;
TextView song1,url1,artists1,cover1;
    ListView songList;
    SongCustomAdapter songAdapter;
    TextView title,sample;
    ImageButton btn_prev;
    ImageButton btn_next;
    int pageCount ;

    /**
     * Using this increment value we can move the listview items
     */
    int increment = 0;

    /**
     * Here set the values, how the ListView to be display
     *
     * Be sure that you must set like this
     *
     * TOTAL_LIST_ITEMS > NUM_ITEMS_PAGE
     */


    public int NUM_ITEMS_PAGE   = 4;
    ArrayList<Song> songArray = new ArrayList<Song>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        //actionBar.hide(); // or even hide the actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ola);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_get_data);
        btn_prev     = (ImageButton) findViewById(R.id.prev);
        btn_next     = (ImageButton) findViewById(R.id.next);
        sample=(TextView) findViewById(R.id.sample);
        title    = (TextView)findViewById(R.id.title);
       actv=(AutoCompleteTextView)findViewById(R.id.textView1);
        new JsonTask().execute("http://starlord.hackerearth.com/studio");
        btn_prev.setEnabled(false);


    }
    private class JsonTask extends AsyncTask<String, String, JSONArray> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(GetData.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected JSONArray doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }
                //txtJson.setText(buffer.toString());

                return new JSONArray(buffer.toString());


            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(final JSONArray result) {

            System.out.println(result);
            try {
                //JSONObject jsonObject=new JSONObject(result);
               // song=jsonObject.getJSONArray("song");
                int g=result.length();
                final String[] array=new String [g];
                int TOTAL_LIST_ITEMS = result.length();
                int val = TOTAL_LIST_ITEMS%NUM_ITEMS_PAGE;
                val = val==0?0:1;
                pageCount = TOTAL_LIST_ITEMS/NUM_ITEMS_PAGE+val;
                loadList(0);
                for(int i=0;i<result.length();i++)
                {
                    int o=i;
                JSONObject c = result.getJSONObject(i);
                String song = c.getString("song");
                array[i]= c.getString("song");
                String url = c.getString("url");
                String artists = c.getString("artists");
                String cover = c.getString("cover_image");
                db.insertUser(i,song,url,artists,cover);
                songArray.add(new Song(o,song,artists,cover,url,g));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (getApplicationContext(), android.R.layout.select_dialog_item, array);

                actv.setThreshold(1);//will start working from first character
                actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                actv.setDropDownBackgroundResource(R.color.autocompletet_background_color);
                loadList(0);
                actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                        String selection = (String)parent.getItemAtPosition(position);
                        sample.setText(array[1]);
                        int i;
                        //s=db.getUserFromName(selection);
                        for(i=0;i<result.length();i++)
                        {
                            if(selection.charAt(0)==array[i].charAt(0)&&selection.charAt(1)==array[i].charAt(i)){

                                Intent intent=new Intent(getApplicationContext(),MusicActivity.class);
                                intent.putExtra("EXTRA_SESSION_ID", i+"");
                                intent.putExtra("total",result.length());
                            }
                            else
                            {
                                System.out.println("bla bla");
                            }
                        }

                        //TODO Do something with the selected text
                    }
                });
                btn_next.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        increment++;
                        loadList(increment);
                        CheckEnable(increment);
                    }
                });

                btn_prev.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        increment--;
                        loadList(increment);
                        CheckEnable(increment);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            //txtJson.setText(result);
        }
        private void CheckEnable(int increment)
        {
            Toast.makeText(getApplicationContext(),""+increment,Toast.LENGTH_LONG).show();
            if(increment+1 == pageCount)
            {
                btn_next.setEnabled(false);

            }
            else if(increment == 0)
            {
                btn_prev.setEnabled(false);
            }
            else
            {
                btn_prev.setEnabled(true);
                btn_next.setEnabled(true);
            }
        }
        private void loadList(int number) {
            ArrayList<Song> sort = new ArrayList<Song>();
            title.setText("Page "+(number+1)+" of "+pageCount);

            int start = number * NUM_ITEMS_PAGE;
            for(int i=start;i<(start)+NUM_ITEMS_PAGE;i++)
            {
                if(i<songArray.size())
                {
                    sort.add(songArray.get(i));
                }
                else
                {
                    break;
                }
            }
            songAdapter = new SongCustomAdapter(GetData.this, R.layout.layout_appinfo,
                    sort);
            songList = (ListView) findViewById(R.id.listView1);
            songList.setItemsCanFocus(true);
            songList.setAdapter(songAdapter);
        }
        }
    }

