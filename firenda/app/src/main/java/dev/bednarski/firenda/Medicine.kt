package dev.bednarski.firenda

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "medicines")
class Medicine(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "dosage") var dosage: String,
    @ColumnInfo(name = "dosage_unit") var dosageUnit: String,

    @ColumnInfo(name = "time_hour") var hour: String,
    @ColumnInfo(name = "time_minute") var minute: String,
    @ColumnInfo(name = "status") var status: Boolean
)