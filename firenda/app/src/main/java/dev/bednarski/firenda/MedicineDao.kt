package dev.bednarski.firenda

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MedicineDao {

    @Query("SELECT * FROM medicines ORDER BY hour")
    fun getAllMedicines(): LiveData<List<Medicine>>

    @Insert()
    fun insertMedicine(medicine: Medicine)

    @Query("DELETE FROM medicines WHERE id = :position")
    suspend fun deleteMedicineById(position: Int)

    @Update()
    fun updateMedicine(medicine: Medicine)
}