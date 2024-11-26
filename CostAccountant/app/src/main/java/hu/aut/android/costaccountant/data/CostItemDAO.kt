package hu.aut.android.costaccountant.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
/*
Itt az adatbázis műveletek találhatóak.
Új adattagkor (új ShippingItem adattag), nem szükséges módosítani itt.
 */
@Dao
interface CostItemDAO {

    //Az összes listázása
    @Query("SELECT * FROM costitem")
    fun findAllItems(): List<CostItem>

    //Egy elem beszúrása
    @Insert
    fun insertItem(item: CostItem): Long
    //Egy törlése
    @Delete
    fun deleteItem(item: CostItem)
    //Egy módosítása
    @Update
    fun updateItem(item: CostItem)

}
