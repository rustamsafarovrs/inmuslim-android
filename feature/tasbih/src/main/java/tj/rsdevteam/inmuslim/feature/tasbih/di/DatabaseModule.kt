package tj.rsdevteam.inmuslim.feature.tasbih.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihDao
import tj.rsdevteam.inmuslim.feature.tasbih.data.db.TasbihDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTasbihDatabase(@ApplicationContext context: Context): TasbihDatabase =
        Room.databaseBuilder(context, TasbihDatabase::class.java, "tasbih_db")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    listOf(
                        "سُبْحَانَ اللَّهِ",
                        "الْحَمْدُ لِلَّهِ",
                        "اللَّهُ أَكْبَرُ",
                        "أَسْتَغْفِرُ اللَّهَ",
                        "لَا إِلَهَ إِلَّا اللَّهُ",
                    ).forEach { db.execSQL("INSERT INTO tasbihs (name) VALUES ('$it')") }
                }
            })
            .build()

    @Provides
    @Singleton
    fun provideTasbihDao(db: TasbihDatabase): TasbihDao = db.tasbihDao()
}
