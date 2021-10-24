package com.example.googlebooksapi


import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class MainActivity : AppCompatActivity() {
    // Variables for the search input field, and results TextViews.
    private var mBookInput: EditText? = null


    var resultedBooks: ArrayList<Book> = ArrayList<Book>()
    lateinit var mAdapterBooks: RecyclerAdapterBookList
    lateinit var mRecyclerView: RecyclerView                        //Recyclers

    /**
     * Initializes the activity.
     *
     * @param savedInstanceState The current state data
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize all the view variables.
        mBookInput = findViewById<View>(R.id.bookInput) as EditText


        mRecyclerView = findViewById(R.id.recyclerBooks)!!
        mRecyclerView.setHasFixedSize(true)
        mAdapterBooks = RecyclerAdapterBookList(resultedBooks, this, this)
        mRecyclerView.adapter = mAdapterBooks


    }

    /**
     * Gets called when the user pushes the "Search Books" button
     *
     * @param view The view (Button) that was clicked.
     */
    fun searchBooks(view: View?) {
        // Get the search string from the input field.
        resultedBooks.clear()


        val queryString = mBookInput!!.text.toString()
        if (queryString != "") {

            // Hide the keyboard when the button is pushed.
            val inputManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )

            // Check the status of the network connection.
            val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo

            // If the network is active and the search field is not empty, start a FetchBook AsyncTask.
            if (networkInfo != null && networkInfo.isConnected && queryString.isNotEmpty()) {
                FetchBook(mBookInput!!, this).execute(queryString)
            }

        }
    }


    fun getFavorites(view: View?){
        resultedBooks.clear()
        val tinydb = TinyDB(this)
        val favBooksIds = tinydb.getListString("favoriteBooksIds")

        for(bookID in favBooksIds){
            val arrayListBookInfo = tinydb.getListString(bookID)
            val book = Book(arrayListBookInfo[0],arrayListBookInfo[1],arrayListBookInfo[2],arrayListBookInfo[3],arrayListBookInfo[4],arrayListBookInfo[5],arrayListBookInfo[5].toInt(),arrayListBookInfo[6])
            resultedBooks.add(book);
        }
        mAdapterBooks.notifyDataSetChanged()
    }


}