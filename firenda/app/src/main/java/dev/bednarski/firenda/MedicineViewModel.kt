package dev.bednarski.firenda

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MedicineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedicineRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allMedicines: LiveData<List<Medicine>>

    init {
        val medicinesDao = AppDatabase.getDatabase(application).medicineDao()
        repository = MedicineRepository(medicinesDao)
        allMedicines = repository.allMedicines
    }

    // Insert the data in a blocking way
    fun insert(medicine: Medicine): Long {
        return repository.insert(medicine)
    }

    // Launching a new coroutine to get the data in a non-blocking way
    fun get(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.get(id)
    }

    // Launching a new coroutine to toggle the data in a non-blocking way
    fun toggle(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.toggle(id)
    }

    fun reset() = viewModelScope.launch(Dispatchers.IO) {
        repository.reset()
    }

    // Launching a new coroutine to insert the data in a non-blocking way
    fun delete(position: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(position)
    }
}