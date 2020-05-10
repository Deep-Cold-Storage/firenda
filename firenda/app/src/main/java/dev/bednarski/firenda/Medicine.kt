package dev.bednarski.firenda

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "medicines")
class Medicine(

    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "dosage") val dosage: String,
    @ColumnInfo(name = "dosageUnit") val dosageUnit: String,

    @ColumnInfo(name = "hour") val hour: String,
    @ColumnInfo(name = "minute") val minute: String,
    @ColumnInfo(name = "takenToday") val takenToday: Boolean
)