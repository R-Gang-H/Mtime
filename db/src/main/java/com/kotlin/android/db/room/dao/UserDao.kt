package com.kotlin.android.db.room.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import com.kotlin.android.db.room.entity.Book
import com.kotlin.android.db.room.entity.Name
import com.kotlin.android.db.room.entity.User

/**
 * DAO(数据访问对象)
 * 这个组件代表了作为DAO的类或者接口。DAO是Room的主要组件，负责定义访问数据库的方法。
 * Room使用过程中一般使用 [抽象] DAO类来定义数据库的CRUD操作。DAO可以是一个接口也可以是一个抽象类。
 * 如果它是一个抽象类，它可以有一个构造函数，它将RoomDatabase作为其唯一参数。Room在编译时创建每个DAO实。
 *
 * ********** Insert **********
 *  当DAO里面的某个方法添加了 [Insert] 注解。Room会生成一个实现，将所有参数插入到数据库中的一个单个事务。
 *  [Insert] 注解可以设置一个属性：
 *  [Insert.onConflict]：默认值是 [OnConflictStrategy.ABORT]，表示当插入有冲突的时候的处理策略。
 *  [OnConflictStrategy] 封装了Room解决冲突的相关策略：
 *      1. [OnConflictStrategy.REPLACE]：冲突策略是取代旧数据同时继续事务。
 *      2. [OnConflictStrategy.ROLLBACK]：【过时】冲突策略是回滚事务。
 *      3. [OnConflictStrategy.ABORT]：冲突策略是终止事务。
 *      4. [OnConflictStrategy.FAIL]：【过时】冲突策略是事务失败。
 *      5. [OnConflictStrategy.IGNORE]：冲突策略是忽略冲突。
 * 当 [Insert] 注解的方法只有一个参数的时候，这个方法也可以返回一个long，
 * 当 [Insert] 注解的方法有多个参数的时候则可以返回long[]或者r List<Long>。long都是表示插入的rowId。
 * @Insert(onConflict = OnConflictStrategy.REPLACE)
 *
 *
 * ********** Update **********
 * 当DAO里面的某个方法添加了 [Update] 注解。Room会把对应的参数信息更新到数据库里面去(会根据参数里面的primary key做更新操作)。
 * [Update] 和 [Insert] 一样也是可以设置 [Update.onConflict] 来表明冲突的时候的解决办法。
 * [Update] 注解的方法也可以返回int变量。表示更新了多少行。
 * @Update(onConflict = OnConflictStrategy.REPLACE)
 *
 *
 * ********** Delete **********
 * 当DAO里面的某个方法添加了 [Delete] 注解。Room会把对应的参数信息指定的行删除掉(通过参数里面的primary key找到要删除的行)。
 * [Delete] 对应的方法也是可以设置int返回值来表示删除了多少行。
 *
 *
 * ********** Query **********
 * [Query] 注解是DAO类中使用的主要注释。它允许您对数据库执行读/写操作。[Query] 在编译的时候会验证准确性，所以如果查询出现问题在编译的时候就会报错。
 * Room还会验证查询的返回值，如果返回对象中的字段名称与查询响应中的相应列名称不匹配的时候，Room会通过以下两种方式之一提醒您：
 *      1. 如果只有一些字段名称匹配，它会发出警告。
 *      2. 如果没有字段名称匹配，它会发生错误。
 * [Query.value]参数：查询语句，这也是我们查询操作最关键的部分。
 *      1. 简单的查询：返回结果可以是数组，也可以是List。
 *          @Query("SELECT * FROM users")
 *      2. 带参数的查询
 *          @Query("SELECT * FROM users WHERE first_name == :name")
 *          @Query("SELECT * FROM users WHERE age BETWEEN :minAge AND :maxAge")
 *          @Query("SELECT * FROM users WHERE first_name LIKE :search OR last_name LIKE :search")
 *      3. 查询返回列的子集
 *          @Query("SELECT first_name, last_name FROM users")
 *      4. 查询的时候传递一组参数
 *          @Query("SELECT first_name, last_name FROM users WHERE last_name IN (:names)")
 *          fun loadUsersFromNames(names: List<String>): List<Name>
 *      5. Observable的查询
 *          意思就是查询到结果的时候，UI能够自动更新。Room为了实现这一效果，查询的返回值的类型为  [LiveData]。
 *          @Query("SELECT first_name, last_name FROM users WHERE last_name IN (:names)")
 *          fun loadUsersFromNamesSync(names: List<String>): LiveData<List<Name>>
 *      6. 查询结果直接返回 [Cursor]
 *          @Query("SELECT * FROM users WHERE age > :minAge LIMIT 5")
 *          fun loadRawUsersOlderThan(minAge: Int): Cursor
 *      7. 多表查询
 *          @Query("SELECT * FROM books INNER JOIN users ON users.id = user_id WHERE users.first_name LIKE :userName")
 *          fun findBooksBorrowedByNameSync(userName: String): LiveData<List<Book>>
 *
 *
 * Created on 2020/6/10.
 *
 * @author o.s
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg user: User)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUser(vararg user: User): Int

    @Delete
    fun deleteUser(vararg user: User): Int

    /**
     * 1. 简单的查询：返回结果可以是数组，也可以是List。
     */
    @Query("SELECT * FROM users")
    fun loadUsers(): List<User>

    /**
     * 2. 带参数的查询
     */
    @Query("SELECT * FROM users WHERE first_name == :name")
    fun loadUsers(name: String): List<User>

    @Query("SELECT * FROM users WHERE age BETWEEN :minAge AND :maxAge")
    fun loadAllUsersBetweenAges(minAge: Int, maxAge: Int): List<User>

    @Query("SELECT * FROM users WHERE first_name LIKE :search OR last_name LIKE :search")
    fun findUserWithName(search: String): List<User>

    /**
     *  3. 查询返回列的子集
     */
    @Query("SELECT first_name, last_name FROM users")
    fun loadFullName(): List<Name>

    /**
     * 4. 查询的时候传递一组参数
     */
    @Query("SELECT first_name, last_name FROM users WHERE last_name IN (:names)")
    fun loadUsersFromNames(names: List<String>): List<Name>

    /**
     * 5. Observable的查询
     */
    @Query("SELECT first_name, last_name FROM users WHERE last_name IN (:names)")
    fun loadUsersFromNamesSync(names: List<String>): LiveData<List<Name>>

    /**
     * 6. 查询结果直接返回 [Cursor]
     */
    @Query("SELECT * FROM users WHERE age > :minAge LIMIT 5")
    fun loadRawUsersOlderThan(minAge: Int): Cursor

//    /**
//     * 7. 多表查询
//     */
//    @Query("SELECT * FROM books INNER JOIN users ON users.id = user_id WHERE users.first_name LIKE :userName")
//    fun findBooksBorrowedByNameSync(userName: String): LiveData<List<Book>>
}