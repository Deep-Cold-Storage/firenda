package dev.bednarski.firenda

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MedicineDao {

    @Query("SELECT * FROM medicines")
    fun getAllMedicines(): LiveData<List<Medicine>>

    @Insert()
    fun insertMedicine(medicine: Medicine)

    @Query("DELETE FROM medicines WHERE id")
    suspend fun deleteMedicineById()
}