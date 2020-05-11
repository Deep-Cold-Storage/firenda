package dev.bednarski.firenda

import androidx.lifecycle.LiveData


class MedicineRepository(private val MedicineDao: MedicineDao) {

    // Observed LiveData will notify the observer when the data has changed.
    val allMedicines: LiveData<List<Medicine>> = MedicineDao.getLiveMedicines()

    suspend fun get(id: Int) {
        MedicineDao.getMedicineById(id)
    }

    suspend fun insert(medicine: Medicine): Long {
        return MedicineDao.insertMedicine(medicine)
    }

    suspend fun toggle(id: Int) {
        MedicineDao.toggleMedicineById(id)
    }

    suspend fun reset() {
        MedicineDao.resetAllMedicines()
    }

    suspend fun delete(position: Int) {
        MedicineDao.deleteMedicineById(position)
    }
}