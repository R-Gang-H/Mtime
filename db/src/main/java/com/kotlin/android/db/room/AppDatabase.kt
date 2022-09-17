package com.kotlin.android.db.room

import android.app.Application
import androidx.room.*
import com.kotlin.android.db.room.dao.UserDao
import com.kotlin.android.db.room.entity.User
import com.kotlin.android.ktx.ext.toDate
import java.util.*

/**
 * Database(数据库)
 *
 * [Database] 注解可以用来创建数据库的持有者。该注解定义了实体列表，该类的内容定义了数据库中的DAO列表。
 * 这也是访问底层连接的主要入口点。注解类应该是抽象的并且扩展自 [RoomDatabase]。
 * [Database] 对应的对象 [RoomDatabase] 必须添加 [Database] 注解，
 *
 * [Database] 包含的属性：
 * [Database.entities]：数据库相关的所有 [Entity] 实体类，他们会转化成数据库里面的表。
 * [Database.version]：数据库版本。
 * [Database.exportSchema]：默认true，也是建议传true，这样可以把Schema导出到一个文件夹里面。同时建议把这个文件夹上传到VCS。
 *
 * 每次创建 [Database] 实例都会产生比较大的开销，所以应该将 [Database] 设计成单例的，或者直接放在 [Application] 中创建。
 *
 * 两种方式获取 [Database] 对象的区别:
 * 1. [Room.databaseBuilder]：生成 [Database] 对象，并且创建一个存在文件系统中的数据库。
 * 2. [Room.inMemoryDatabaseBuilder]：生成 [Database] 对象，并且创建一个存在内存中的数据库。当应用退出的时候(应用进程关闭)数据库也消失。
 *
 * 创建 [RoomDatabase] 实例的时候，[RoomDatabase.Builder] 类里面主要方法的介绍：
 * 1. [RoomDatabase.Builder.openHelperFactory]: 默认值是FrameworkSQLiteOpenHelperFactory，设置数据库的factory。比如我们想改变数据库的存储路径可以通过这个函数来实现
 * 2. [RoomDatabase.Builder.addMigrations]: 设置数据库升级(迁移)的逻辑
 * 3. [RoomDatabase.Builder.allowMainThreadQueries]: 设置是否允许在主线程做查询操作
 * 4. [RoomDatabase.Builder.setJournalMode]: 设置数据库的日志模式
 * 5. [RoomDatabase.Builder.fallbackToDestructiveMigration]: 设置迁移数据库如果发生错误，将会重新创建数据库，而不是发生崩溃
 * 6. [RoomDatabase.Builder.fallbackToDestructiveMigrationFrom]: 设置从某个版本开始迁移数据库如果发生错误，将会重新创建数据库，而不是发生崩溃
 * 7. [RoomDatabase.Builder.addCallback]: 监听数据库，创建和打开的操作
 *
 * [RoomDatabase] 除了必须添加 [Database] 注解也可以添加 [TypeConverters] 注解。用于提供一个把自定义类转化为一个Room能够持久化的已知类型的.
 * 比如我们想持久化日期的实例，我们可以用如下代码写一个 [TypeConverter] 去存储相等的Unix时间戳在数据库中。
 *
 *
 * Created on 2020/6/10.
 *
 * @author o.s
 */
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

}

class Converters {

    @TypeConverter
    fun fromTimestamp(time: Long): Date {
        return time.toDate()
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}