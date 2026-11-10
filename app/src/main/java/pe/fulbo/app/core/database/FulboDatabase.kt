package pe.fulbo.app.core.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Caché local. Agregar entidades por feature y subir `version` con su migración. */
@Database(entities = [CanchaEntity::class], version = 1, exportSchema = true)
abstract class FulboDatabase : RoomDatabase() {
    abstract fun canchaDao(): CanchaDao
}

@Entity(tableName = "canchas")
data class CanchaEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val distrito: String,
    val superficie: String,
    val formato: String,
    val techada: Boolean,
    val rating: Double,
    val precioHora: Double,
    val distanciaMetros: Int,
    val proximoLibre: String?,
    val lat: Double,
    val lng: Double,
    val actualizadoEn: Long = System.currentTimeMillis(),
)

@Dao
interface CanchaDao {
    @Query("SELECT * FROM canchas ORDER BY distanciaMetros")
    fun observarTodas(): Flow<List<CanchaEntity>>

    @Upsert
    suspend fun guardar(canchas: List<CanchaEntity>)

    @Query("DELETE FROM canchas")
    suspend fun borrarTodas()
}
