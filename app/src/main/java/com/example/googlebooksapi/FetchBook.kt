package com.example.googlebooksapi

import android.app.Activity
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class FetchBook(
    // Variables for the search input field, and results TextViews
    private var mBookInput: EditText, private var activity: MainActivity
) :
    AsyncTask<String?, Void?, String?>() {
    /**
     * Makes the Books API call off of the UI thread.
     *
     * @param params String array containing the search data.
     * @return Returns the JSON string from the Books API or
     * null if the connection failed.
     */
    protected override fun doInBackground(vararg params: String?): String? {

        // Get the search string
        val queryString = params[0]


        // Set up variables for the try block that need to be closed in the finally block.
        var urlConnection: HttpURLConnection? = null
        var reader: BufferedReader? = null
        var bookJSONString: String? = null

        // Attempt to query the Books API.
        try {
            // Base URI for the Books API.
            val BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?"
            val QUERY_PARAM = "q" // Parameter for the search string.
            val MAX_RESULTS = "maxResults" // Parameter that limits search results.
            val PRINT_TYPE = "printType" // Parameter to filter by print type.

            // Build up your query URI, limiting results to 10 items and printed books.
            val builtURI: Uri = Uri.parse(BOOK_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, queryString)
                .appendQueryParameter(MAX_RESULTS, "10")
                .appendQueryParameter(PRINT_TYPE, "books")
                .build()
            val requestURL = URL(builtURI.toString())

            // Open the network connection.
            urlConnection = requestURL.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            // Get the InputStream.
            val inputStream: InputStream = urlConnection.getInputStream()

            // Read the response string into a StringBuilder.
            val builder = StringBuilder()
            reader = BufferedReader(InputStreamReader(inputStream))
            var line: String

            reader.forEachLine {
                builder.append(it + "\n");
                Log.d("linha", it)
            }/*
            while ((reader.readLine()!=null)) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // but it does make debugging a *lot* easier if you print out the completed buffer for debugging.


                if(reader.readLine() != null){
                    line = reader.readLine()
                    builder.append(line + "\n");
                    Log.d("linha", line)
                }
                else{
                    break;
                }

            }
            */
            if (builder.length == 0) {
                // Stream was empty.  No point in parsing.
                // return null;
                return null
            }
            bookJSONString = builder.toString()
            // Catch errors.
        } catch (e: IOException) {
            e.printStackTrace()

            // Close the connections.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect()
            }
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        // Return the raw response.


        return bookJSONString
    }

    /**
     * Handles the results on the UI thread. Gets the information from
     * the JSON and updates the Views.
     *
     * @param s Result from the doInBackground method containing the raw JSON response,
     * or null if it failed.
     */
    override fun onPostExecute(s: String?) {
        super.onPostExecute(s)

        try {
            // Convert the response into a JSON object.
            val s1 = s?.substring(s.indexOf("{"), s.lastIndexOf("}"))

            val jsonObject = JSONObject(s.toString())

            // Get the JSONArray of book items.
            val itemsArray = jsonObject.getJSONArray("items")

            // Initialize iterator and results fields.
            var id:String? = null;
            var title: String? = null
            var authors: String? = null
            var thumbnail: String? = "null"
            var publisher: String? = "null"
            var publisherDate: String? = "null"
            var pageCount:Int?
            var saleability:String?
            var language:String?
            // Look for results in the items array, exiting when both the title and author
            // are found or when all items have been checked.
            //while (i < itemsArray.length() || title == null) {
            for (i in 0 until itemsArray.length()) {
                // Get the current item information.
                val book = itemsArray.getJSONObject(i)
                val volumeInfo = book.getJSONObject("volumeInfo")

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    id = book.getString("id")
                    title = volumeInfo.getString("title")
                    //Log.d("entrouuuuuu5555555",title)
                    authors = volumeInfo.getString("authors")
                    //Log.d("entrouuuuuu5555555",authors)
                    thumbnail = volumeInfo.getJSONObject("imageLinks").getString("thumbnail")
                    //Log.d("entrouuuuuu5555555",thumbnail)
                    publisher = volumeInfo.getString("publisher")
                    //Log.d("entrouuuuuu5555555",publisher)
                    publisherDate = volumeInfo.getString("publishedDate")
                    //Log.d("entrouuuuuu5555555",publisherDate)
                    pageCount = volumeInfo.getInt("pageCount")
                    //Log.d("entrouuuuuu5555555",pageCount.toString())
                    language = volumeInfo.getString("language")
                    //Log.d("entrouuuuuu5555555",language)


                    thumbnail=thumbnail.replace("http:","https:")



                    Log.d("entrouuuuuu5555555","---------")
                    /*
                    Log.d("entrouuuuuu5555555",title)
                    Log.d("entrouuuuuu5555555",authors)
                    Log.d("entrouuuuuu5555555",thumbnail)
                    Log.d("entrouuuuuu5555555",publisher)
                    Log.d("entrouuuuuu5555555",publisherDate)
                    Log.d("entrouuuuuu5555555","---------")
                    */
                    if (title != null ) {
                        mBookInput.setText("")
                        val book = Book(id,authors,title,thumbnail,publisher,publisherDate,pageCount,language)
                        activity.resultedBooks.add(book);

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Move to the next item.

            }
            activity.mAdapterBooks.notifyDataSetChanged()

            // If both are found, display the result.

        } catch (e: Exception) {
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results.

            e.printStackTrace()
        }
    }

    companion object {
        // Class name for Log tag
        private val LOG_TAG = FetchBook::class.java.simpleName
    }

    // Constructor providing a reference to the views in MainActivity
    init {
        mBookInput = mBookInput
        activity = activity
    }
}