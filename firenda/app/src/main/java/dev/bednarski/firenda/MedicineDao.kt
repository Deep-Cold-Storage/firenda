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

    @Query("SELECT * FROM medicines WHERE id = :id")
    fun getMedicineById(id: Int): LiveData<List<Medicine>>

    @Insert()
    fun insertMedicine(medicine: Medicine)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Int)

    @Query("UPDATE medicines SET takenToday = NOT takenToday WHERE id = :id")
    suspend fun toggleMedicineById(id: Int)

    @Update()
    fun updateMedicine(medicine: Medicine)
}