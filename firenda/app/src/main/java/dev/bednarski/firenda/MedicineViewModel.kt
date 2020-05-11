package dev.bednarski.firenda

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedicineRepository
    val allMedicines: LiveData<List<Medicine>>

    init {
        val medicinesDao = AppDatabase.getDatabase(application).medicineDao()
        repository = MedicineRepository(medicinesDao)
        allMedicines = repository.allMedicines
    }

    // Insert Medicine in a non-blocking way. Return ID
    suspend fun insert(medicine: Medicine): Long {
        return repository.insert(medicine)
    }

    // Launch a new coroutine to get Medicine in a non-blocking way.
    fun get(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.get(id)
    }

    // Launch a new coroutine to toggle status in Medicine in a non-blocking way.
    fun toggle(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.toggle(id)
    }

    // Launch a new coroutine to reset status in all Medicines in a non-blocking way.
    fun reset() = viewModelScope.launch(Dispatchers.IO) {
        repository.reset()
    }

    // Launch a new coroutine to delete Medicine in a non-blocking way.
    fun delete(position: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(position)
    }
}