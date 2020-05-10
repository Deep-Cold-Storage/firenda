package dev.bednarski.firenda

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "medicines")
class Medicine(

    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "dosage") val dosage: String,
    @ColumnInfo(name = "dosage_unit") val dosageUnit: String,

    @ColumnInfo(name = "time_hour") val hour: String,
    @ColumnInfo(name = "time_minute") val minute: String,
    @ColumnInfo(name = "status") val status: Boolean
)