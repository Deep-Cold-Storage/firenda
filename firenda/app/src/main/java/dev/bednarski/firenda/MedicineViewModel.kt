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
        val medicinesDao = AppDatabase.getDatabase(application).MedicineDao()
        repository = MedicineRepository(medicinesDao)
        allMedicines = repository.allMedicines
    }

    // Launching a new coroutine to insert the data in a non-blocking way
    fun insert(medicine: Medicine) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(medicine)
    }

    // Launching a new coroutine to insert the data in a non-blocking way
    fun delete(position: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(position)
    }
}