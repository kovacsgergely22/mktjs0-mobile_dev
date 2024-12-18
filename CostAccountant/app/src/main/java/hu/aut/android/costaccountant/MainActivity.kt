package hu.aut.android.costaccountant

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import hu.aut.android.costaccountant.adapter.CostAccountAdapter
import kotlinx.android.synthetic.main.activity_main.*
import android.preference.PreferenceManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import hu.aut.android.costaccountant.data.AppDatabase
import hu.aut.android.costaccountant.data.CostItem
import hu.aut.android.costaccountant.touch.ShoppingTouchHelperCallback
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt




/*
Ez a fő, main.
Itt nem szükséges módosítani a Shopping Item-hez ha új adattagot adunk.
 */
class MainActivity : AppCompatActivity(), CostItemDialog.CostItemHandler {
    companion object {
        val KEY_FIRST = "KEY_FIRST"
        val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
    }

    private lateinit var adapter: CostAccountAdapter
    /*
    Alkalmazás create-kor hívódik meg
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activity-main.xml-t hozza be
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        //Új elem hozzáadásakor hívódik meg, a rózsaszín levél ikonos gomb eseménykezelője
        //A ShoppingTimeDialog-ot hívja meg (jeleníti meg)
        ikon.setOnClickListener { view ->
            CostItemDialog().show(supportFragmentManager, "TAG_ITEM")
        }

        initRecyclerView()
            /*Új elem felvitelének vizsgálata, akkor hívódik meg, a dialógus címét állítja*/
        if (isFirstRun()) {
            MaterialTapTargetPrompt.Builder(this@MainActivity)
                    .setTarget(findViewById<View>(R.id.ikon))
                    .setPrimaryText("New Cost Item")
                    .setSecondaryText("Tap here to create new cost item")
                    .show()
        }


        saveThatItWasStarted()
    }

    private fun isFirstRun(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                KEY_FIRST, true
        )
    }

    private fun saveThatItWasStarted() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        sp.edit()
                .putBoolean(KEY_FIRST, false)
                .apply()
    }




    private fun initRecyclerView() {
        val dbThread = Thread {
            //Lekéri az összes Shopping Item-et.
            val items = AppDatabase.getInstance(this).costItemDao().findAllItems()

            runOnUiThread{
                adapter = CostAccountAdapter(this, items)
                recyclerCost.adapter = adapter

                val callback = ShoppingTouchHelperCallback(adapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerCost)
            }
        }
        dbThread.start()
    }
    /*Edit dialógus megnyitása*/
    fun showEditItemDialog(itemToEdit: CostItem) {
        val editDialog = CostItemDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM_TO_EDIT, itemToEdit)
        editDialog.arguments = bundle

        editDialog.show(supportFragmentManager, "TAG_ITEM_EDIT")
    }

/*Új Shopping Item-kor beszúrjuk a DB-be a DAO segítségével*/
    override fun shoppingItemCreated(item: CostItem) {
        val dbThread = Thread {
            val id = AppDatabase.getInstance(this@MainActivity).costItemDao().insertItem(item)

            item.itemId = id

            runOnUiThread{
                adapter.addItem(item)
            }
        }
        dbThread.start()
    }
/*Update-or módosítjuk a Shopping Item-et a DAO segítségével*/
    override fun shoppingItemUpdated(item: CostItem) {
        adapter.updateItem(item)

        val dbThread = Thread {
            AppDatabase.getInstance(this@MainActivity).costItemDao().updateItem(item)

            runOnUiThread { adapter.updateItem(item) }
        }
        dbThread.start()
    }
}
