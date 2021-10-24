package com.example.googlebooksapi


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapterBookList(
    private var books: ArrayList<Book>,
    private var activity: MainActivity,
    private var context: Context
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    init {


    }

    @RequiresApi(Build.VERSION_CODES.N)
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v){

        init {

            v.setOnClickListener {

                openCreateSubtaskPopUpView(position)

            }

        }




    }

    /**
     * Função que filtra a pesquisa por utilizadores, letra a letra
     * Excluindo contactos já adicionados
     */

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.book_view, parent, false)
        v.invalidate()
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  books.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.bookAuthor).text = books[position].author
        holder.itemView.findViewById<TextView>(R.id.bookTitle).text = books[position].title
        Glide.with(activity).load(books[position].thumbnail).into(holder.itemView.findViewById(R.id.bookImage))

    }


    @RequiresApi(Build.VERSION_CODES.N)                                                             //EDIT TEXT popup para ediçao da tarefa.
    fun openCreateSubtaskPopUpView(position: Int) {
        val builder = AlertDialog.Builder(context)

        val myview: View = activity.layoutInflater.inflate(R.layout.extended_book_view, null)
        //mudar a cor de fundo para a da tarefa
        //  myview.setBackgroundColor(Color.parseColor(tasks_colors[position]))
        myview.findViewById<TextView>(R.id.bookAuthor).text = books[position].author
        myview.findViewById<TextView>(R.id.bookTitle).text = books[position].title
        Glide.with(activity).load(books[position].thumbnail).into(myview.findViewById(R.id.bookImage))
        myview.findViewById<TextView>(R.id.bookLanguage).text = "Language: " +books[position].language
        myview.findViewById<TextView>(R.id.bookPageCount).text = "Num. of Pages: " + books[position].pageCount.toString()
        myview.findViewById<TextView>(R.id.bookPublishedDate).text = books[position].publisherDate
        builder.setView(myview)

        builder.setPositiveButton("Add to favorites",
            DialogInterface.OnClickListener { dialog, which ->
                val tinydb = TinyDB(context)
                val favBookTemp = ArrayList<String>();
                var favBooksIds = ArrayList<String>();
                favBookTemp.add(books[position].id.toString())
                favBookTemp.add(books[position].author.toString())
                favBookTemp.add(books[position].title.toString())
                favBookTemp.add(books[position].thumbnail.toString())
                favBookTemp.add(books[position].publisher.toString())
                favBookTemp.add(books[position].publisherDate.toString())
                favBookTemp.add(books[position].pageCount.toString())
                favBookTemp.add(books[position].language.toString())


                favBooksIds = tinydb.getListString("favoriteBooksIds")

                favBooksIds.add(books[position].id.toString())

                tinydb.putListString("favoriteBooksIds", favBooksIds);

                tinydb.putListString(books[position].id.toString(), favBookTemp);

                for(bookID in favBooksIds){
                    Log.d("entrounatiny",tinydb.getListString(bookID).toString())
                }
            })
        builder.setNegativeButton("Close",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()

                //    notifyDataSetChanged()
            })
        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#A8A8A8"))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#A8A8A8"))

    }
}



