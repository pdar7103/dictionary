package com.dworld.accounting.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;



import com.dworld.accounting.Utils.Constant;
import com.dworld.accounting.model.DictionaryModel;
import com.dworld.architecture.R;


public class DatabaseHelper extends SQLiteOpenHelper {

	Context mContex;
	public SQLiteDatabase mDataBase;
	private SQLiteDatabase myDataBase;
	public  final static String DB_NAME ="wordbook";
	public  final static int DB_VERSION =1;


	public static final String TBL_NAME_WORD = "wordbook";
	public static final String KEY_ID = "_id";
	public static final String KEY_ENGWORD = "langFullWord";
	public static final String KEY_GUJWORD = "entry";
	public static final String KEY_TYPE = "type";
	public static final String KEY_SYNONYM = "synonym";
	public static final String KEY_MEANING = "meaning";
	
	public static final String KEY_FAVWORD = "isFav";


    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();


	private final String DB_Internal = "accountingdictionary";

	public DatabaseHelper(Context mContex) {
		super(mContex, DB_NAME+"_"+mContex.getString(R.string.DB_NAME),null, DB_VERSION);
		 this.mContex=mContex;
	        boolean dbexist = checkdatabase();
	        if (dbexist) {
	              opendatabase(); 
	        } else {
	            System.out.println("Database doesn't exist");
	            try {
					createdatabase();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }


	}

	private static DatabaseHelper sInstance;
	public static synchronized DatabaseHelper getInstance(Context context){
		// Use the application context, which will ensure that you don't accidentally leak an Activity's context.
		if (sInstance == null) {
			sInstance = new DatabaseHelper(context.getApplicationContext());
		}
		return sInstance;
	}



    public void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        if(!dbexist) {
            this.getReadableDatabase();
            try {
                copydatabase();
            } catch(IOException e) {
                throw new Error("Error copying database");
            }
        }
    }   

    private boolean checkdatabase() {
		 String DB_PATH = "/data/data/"+mContex.getApplicationContext().getPackageName()+"/databases/";
        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DB_NAME+"_"+mContex.getString(R.string.DB_NAME);
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch(SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copydatabase() throws IOException {
		String DB_PATH = "/data/data/"+mContex.getApplicationContext().getPackageName()+"/databases/";
        //Open your local db as the input stream
        InputStream myinput = mContex.getAssets().open(DB_NAME+"_"+mContex.getString(R.string.DB_NAME));

        // Path to the just created empty db
        String outfilename = DB_PATH + DB_NAME+"_"+mContex.getString(R.string.DB_NAME);

        //Open the empty db as the output stream
        OutputStream myoutput = new FileOutputStream(outfilename);

        // transfer byte to inputfile to outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer))>0) {
            myoutput.write(buffer,0,length);
        }

        //Close the streams
        myoutput.flush();
        myoutput.close();
        myinput.close();
    }

    public void opendatabase() throws SQLException {
        //Open the database
		String DB_PATH = "/data/data/"+mContex.getApplicationContext().getPackageName()+"/databases/";
        String mypath = DB_PATH + DB_NAME+"_"+mContex.getString(R.string.DB_NAME);
        myDataBase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void close() {
		super.close();
		if (mDataBase != null) {
			mDataBase.close();
		}
	}

	 public ArrayList<DictionaryModel> getALLWordFromDB(String isFrom){
	        ArrayList<DictionaryModel> wordList = new ArrayList<DictionaryModel>();
	        try {
	            r.lock();
	        mDataBase= getReadableDatabase();
	        Cursor c= null;
	        //mDataBase.rawQuery("select * from "+DatabaseHelper.TBL_NAME_WORD, null);

	        if(isFrom.equalsIgnoreCase(Constant.FROM_HOME)){
	        	 c=  mDataBase.rawQuery("select * from "+DatabaseHelper.TBL_NAME_WORD, null);
	        }else if(isFrom.equalsIgnoreCase(Constant.FROM_FAV)){
	        	 c=  mDataBase.rawQuery("select * from "+DatabaseHelper.TBL_NAME_WORD+" where "+DatabaseHelper.KEY_FAVWORD +"= '1'", null);
	        }
	        

	        if((c != null) && c.getCount()>0) {
	            c.moveToFirst();
	            do {
	            	DictionaryModel model = new DictionaryModel();

					model.setId(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.KEY_ID)));
	                model.setEngword(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.KEY_ENGWORD)));
	               // model.setType(c.getString(c.getColumnIndex(DatabaseHelper.KEY_TYPE)));
	                model.setMeaning(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.KEY_GUJWORD)));
	               // model.setAsynconamus(c.getString(c.getColumnIndex(DatabaseHelper.KEY_SYNONYM)));
	                model.setFavouriteword(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.KEY_FAVWORD)));
	            
	                wordList.add(model);

	            } while (c.moveToNext());
	        }
	        }catch(Exception e){

				e.printStackTrace();
	        }finally {
	            r.unlock();
	        }
	        return wordList ;
	    }
	   public long Updatetbl (String tablename,ContentValues cv,String id) {
		   mDataBase = getWritableDatabase();
	        long row=0;
	        try {
	            w.lock();
	            row =	mDataBase.update(tablename,cv,KEY_ID+"="+id,null);
	        }catch(Exception e){

	        }finally {
	            w.unlock();
	        }
//	        Log.v("tbl ",row+"");
	        return row;


	    }


}
