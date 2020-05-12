package com.dd.testshareroomapp2

import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val CHEESE_URI =
        Uri.parse("content://com.example.android.contentprovidersample.provider/cheeses")
    private val CHEESE_ID = "_id"
    private val CHEESE_NAME = "name"
    private val LOADER_CHEESES: Int = 1
    private var mCheeseAdapter: CheeseAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val list: RecyclerView = findViewById(R.id.lvContact)
        list.layoutManager = LinearLayoutManager(list.context)
        mCheeseAdapter = CheeseAdapter()
        list.adapter = mCheeseAdapter

        LoaderManager.getInstance(this).initLoader(LOADER_CHEESES, null, mLoaderCallbacks)
    }


    private val mLoaderCallbacks: LoaderManager.LoaderCallbacks<Cursor?> =
        object : LoaderManager.LoaderCallbacks<Cursor?> {
            override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
                return when (id) {
                    LOADER_CHEESES -> {
                        CursorLoader(
                            applicationContext,
                            CHEESE_URI,
                            null,
                            null,
                            null,
                            null
                        )
                    }
                    else -> throw IllegalArgumentException()
                }
            }

            override fun onLoadFinished(
                loader: Loader<Cursor?>,
                data: Cursor?
            ) {
                when (loader.getId()) {
                    LOADER_CHEESES -> mCheeseAdapter?.setCheeses(data)
                }
            }

            override fun onLoaderReset(loader: Loader<Cursor?>) {
                when (loader.getId()) {
                    LOADER_CHEESES -> mCheeseAdapter?.setCheeses(null)
                }
            }
        }


    fun onClickInsert(v: View?) {
        try {
            val cv = ContentValues()
            cv.put(CHEESE_ID, "777")
            cv.put(CHEESE_NAME, "name777")
            val newUri: Uri? = contentResolver.insert(CHEESE_URI, cv)
            Toast.makeText(
                applicationContext,
                "insert, result Uri : ${newUri.toString()}",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun onClickUpdate(v: View?) {
        val cv = ContentValues()
        cv.put(CHEESE_ID, "2")
        cv.put(CHEESE_NAME, "Second Item Changed")
        val uri: Uri = ContentUris.withAppendedId(CHEESE_URI, 2)
        val cnt = contentResolver.update(uri, cv, null, null)
        Toast.makeText(applicationContext, "item $cnt updated", Toast.LENGTH_LONG).show()
    }

    fun onClickDelete(v: View?) {
        val uri: Uri = ContentUris.withAppendedId(CHEESE_URI, 3)
        val cnt = contentResolver.delete(uri, null, null)
        Toast.makeText(applicationContext, "item $cnt deleted", Toast.LENGTH_LONG).show()
    }

    private class CheeseAdapter : RecyclerView.Adapter<CheeseAdapter.ViewHolder?>() {
        private var mCursor: Cursor? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (mCursor!!.moveToPosition(position)) {
                holder.mText.text =
                    mCursor!!.getString(mCursor!!.getColumnIndexOrThrow("name"))
            }
        }

        override fun getItemCount(): Int {
            return if (mCursor == null) 0 else mCursor!!.count
        }

        fun setCheeses(cursor: Cursor?) {
            mCursor = cursor
            notifyDataSetChanged()
        }

        internal class ViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    android.R.layout.simple_list_item_1, parent, false
                )
            ) {
            var mText: TextView

            init {
                mText = itemView.findViewById(android.R.id.text1)
            }
        }
    }
}
