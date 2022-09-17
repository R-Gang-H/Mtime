package com.kotlin.android.db

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kotlin.android.db.room.AppDatabase
import com.kotlin.android.ktx.ext.notNull

/**
 * DB Library
 *
 * 数据迁移(升级):
 *
 * 大部分情况下设计的数据库在版本的迭代过程中经常是会有变化的。比如突然某个表需要新加一个字段，需要新增一个表。
 * 这个时候我们又不想失去之前的数据。[Room] 里面以 [Migration] 类的形式提供可一个简化SQLite迁移的抽象层。
 * [Migration] 提供了从一个版本到另一个版本迁移的时候应该执行的操作。
 *
 * 当数据库里面表有变化的时候(不管你是新增了表，还是改变了某个表)有如下几个场景:
 *  1. 如果database的版本号不变。app操作数据库表的时候会直接crash掉。(错误的做法)
 *  2. 如果增加database的版本号。但是不提供Migration。app操作数据库表的时候会直接crash掉。（错误的做法）
 *  3. 如果增加database的版本号。同时启用 [RoomDatabase.Builder.fallbackToDestructiveMigration]。这个时候之前的数据会被清空掉。(不推荐的做法)
 *  4. 增加database的版本号，同时提供 [Migration]。这要是 [Room] 数据迁移的重点。(最正确的做法)
 *
 * 所以在数据库有变化的时候，我们任何时候都应该尽量提供 [Migration]。[Migration] 让我们可以自己去处理数据库从某个版本过渡到另一个版本的逻辑。
 *
 *
 *
 * 数据库信息的导出:
 *
 * [Room] 也允许你会将你数据库的表信息导出为一个json文件。你应该在版本控制系统中保存该文件，该文件代表了你的数据库表历史记录，
 * 这样允许 [Room] 创建旧版本的数据库用于测试。只需要在build.gradle文件中添加如下配置。编译的时候就会导出json文件。
 * android {
 *      ...
 *      defaultConfig {
 *          ...
 *          javaCompileOptions {
 *              annotationProcessorOptions {
 *                  arguments = ["room.schemaLocation":
 *                      "$projectDir/schemas".toString()]
 *                  }
 *              }
 *          }
 *      // 用于测试
 *      sourceSets {
 *          androidTest.assets.srcDirs += files("$projectDir/schemas".toString())
 *      }
 *  }
 *
 *
 * Created on 2020/6/10.
 *
 * @author o.s
 */

//
private var mDatabase: AppDatabase? = null

/**
 * 获取database
 */
fun Application.getAppDatabase(): AppDatabase {
    return mDatabase.notNull(notNull = {
        it
    }, isNull = {
        val database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "android-room-mtime-db")
//            .allowMainThreadQueries()
//            .addMigrations(MIGRATION_1_2)
            .build()
        mDatabase = database
        database
    })
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    /**
     * Should run the necessary migrations.
     *
     * This class cannot access any generated Dao in this method.
     *
     * This method is already called inside a transaction and that transaction might actually be a
     * composite transaction of all necessary `Migration`s.
     *
     * @param database The database instance
     */
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE users ADD COLUMN age integer")
    }

}