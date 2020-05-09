package dev.bednarski.firenda

import androidx.lifecycle.LiveData


// Declares the DAO as a private property in the constructor.
class MedicineRepository(private val MedicineDao: MedicineDao) {

    // Observed LiveData will notify the observer when the data has changed.
    val allMedicines: LiveData<List<Medicine>> = MedicineDao.getAllMedicines()

    suspend fun insert(medicine: Medicine) {
        MedicineDao.insertMedicine(medicine)
    }

    suspend fun delete(position: Int) {
        MedicineDao.deleteMedicineById(position)
    }
}