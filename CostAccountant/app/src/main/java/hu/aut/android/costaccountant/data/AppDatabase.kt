package hu.aut.android.costaccountant.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
/*
Elkészíti az adatbázist azaz a shopping.db-t. a ShoppingItem alapján lesz a tábla
 */
@Database(entities = arrayOf(CostItem::class), version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shoppingItemDao(): CostItemDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "shopping.db")
                        .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}