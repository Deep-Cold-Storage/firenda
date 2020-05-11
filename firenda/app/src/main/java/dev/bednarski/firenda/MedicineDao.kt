package dev.bednarski.firenda

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MedicineDao {

    @Query("SELECT * FROM medicines ORDER BY time_hour")
    fun getAllMedicines(): LiveData<List<Medicine>>

    @Query("SELECT * FROM medicines ORDER BY time_hour")
    fun getMedicines(): List<Medicine>

    @Query("SELECT * FROM medicines WHERE id = :id")
    fun getMedicineById(id: Int): LiveData<Medicine>

    @Insert()
    fun insertMedicine(medicine: Medicine): Long

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Int)

    @Query("UPDATE medicines SET status = NOT status WHERE id = :id")
    suspend fun toggleMedicineById(id: Int)

    @Query("UPDATE medicines SET status = 0")
    suspend fun resetAllMedicines()

    @Update()
    fun updateMedicine(medicine: Medicine)
}