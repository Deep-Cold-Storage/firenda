package dev.bednarski.firenda

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MedicineDao {
    @Insert()
    suspend fun insertMedicine(medicine: Medicine): Long

    @Query("SELECT * FROM medicines ORDER BY time_hour")
    fun getLiveMedicines(): LiveData<List<Medicine>>

    @Query("SELECT * FROM medicines ORDER BY time_hour")
    suspend fun getAllMedicines(): List<Medicine>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: Int): Medicine

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Int)

    @Query("UPDATE medicines SET status = NOT status WHERE id = :id")
    suspend fun toggleMedicineById(id: Int)

    @Query("UPDATE medicines SET status = 0")
    suspend fun resetAllMedicines()

}