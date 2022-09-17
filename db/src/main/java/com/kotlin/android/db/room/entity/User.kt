package com.kotlin.android.db.room.entity

import android.graphics.Bitmap
import androidx.room.*

/**
 * ********** 表名 **********
 * 默认情况下 [Entity] 类的名字就是表的名字(不区分大小写)。
 * 但是我们也可以通过 [Entity.tableName] 属性来自定义表名字：
 * @Entity(tableName = "users")
 *
 *
 * ********** 列名 **********
 * 默认情况下 [Entity] 类中字段的名字就是表中列的名字。
 * 我们也是可以通过 [ColumnInfo] 注解来自定义表中列的名字：
 * @ColumnInfo(name = "first_name")
 *
 *
 * ********** 主键 **********
 * 每个 [Entity] 都需要至少一个字段设置为主键。即使这个 [Entity] 只有一个字段也需要设置为主键。
 * 主键的方式有两种：
 * 1、通过 [Entity.primaryKeys] 属性来设置主键(primaryKeys是数组可以设置单个主键，也可以设置复合主键)。
 * @Entity(tableName = "users", primaryKeys = ["firstName", "lastName"])
 * 2、通过@PrimaryKey注解来设置主键。
 * @PrimaryKey(autoGenerate = true) (可以设置 [PrimaryKey.autoGenerate] 属性，设置一个自增的字段)
 *
 *
 * ********** 索引 **********
 * 数据库索引用于提高数据库表的数据访问速度的。数据库里面的索引有单列索引和组合索引。
 * Room里面可以通过 [Entity] 的 [Entity.indices] 属性来给表格添加索引。
 * 索引也是分两种的唯一索引和非唯一索引：
 * 唯一索引就想主键一样重复会报错的；可以通过的 [Index] 的 [Index.unique] 来设置是否唯一索引。
 * @Entity(
 *      tableName = "users",
 *      indices = [
 *          Index(
 *              "first_name",
 *              "last_name",
 *              unique = true
 *          )
 *      ]
 *  )
 *
 *
 * ********** 外键 **********
 * 因为SQLite是关系形数据库，表和表之间是有关系的。这也就是我们数据库中常说的外键约束(FOREIGN KEY约束)。
 * Room里面可以通过 [Entity.foreignKeys] 属性来设置外键。
 * 正常情况下，数据库里面的外键约束。子表外键于父表。当父表中某条记录子表有依赖的时候父表这条记录是不能删除的，删除会报错。
 * 一般大型的项目很少会采用外键的形式。一般都会通过程序依赖业务逻辑来保证的。
 * [ForeignKey] 属性介绍：
 * [ForeignKey.entity]：parent实体类(引用外键的表的实体)。
 * [ForeignKey.parentColumns]：parent外键列(要引用的外键列)。
 * [ForeignKey.childColumns]：child外键列(要关联的列)。\
 * [ForeignKey.onDelete]：默认 [ForeignKey.NO_ACTION]，当parent里面有删除操作的时候，child表可以做的 [ForeignKey.Action] 动作有：
 *      1. [ForeignKey.NO_ACTION]：当parent中的key有变化的时候child不做任何动作。
 *      2. [ForeignKey.RESTRICT]：当parent中的key有依赖的时候禁止对parent做动作，做动作就会报错。
 *      3. [ForeignKey.SET_NULL]：当paren中的key有变化的时候child中依赖的key会设置为NULL。
 *      4. [ForeignKey.SET_DEFAULT]：当parent中的key有变化的时候child中依赖的key会设置为默认值。
 *      5. [ForeignKey.CASCADE]：当parent中的key有变化的时候child中依赖的key会跟着变化。
 * [ForeignKey.onUpdate]：默认 [ForeignKey.NO_ACTION]，当parent里面有更新操作的时候，child表需要做的动作。[ForeignKey.Action] 动作方式和onDelete是一样的。
 * [ForeignKey.deferred]：默认值false，在事务完成之前，是否应该推迟外键约束。这个怎么理解，当我们启动一个事务插入很多数据的时候，事务还没完成之前。
 * 当parent引起key变化的时候。可以设置deferred为ture。让key立即改变。
 * @Entity(
 *   tableName = "books",
 *   foreignKeys = [
 *       ForeignKey(
 *           entity = User::class,
 *           parentColumns = ["id"],
 *           childColumns = ["user_id"]
 *       )]
 *   )
 *
 *
 * ********** 嵌套对象 **********
 * 有些情况下，你会需要将多个对象组合成一个对象。对象和对象之间是有嵌套关系的。
 * Room中你就可以使用 [Embedded] 注解来表示嵌入。然后，您可以像查看其他单个列一样查询嵌入字段.
 * [Embedded] 注解属性：
 * [Embedded.prefix]：如果实体具有多个相同类型的嵌入字段，则可以通过设置前缀属性来保持每个列的唯一性。
 * 然后Room会将提供的值添加到嵌入对象中每个列名的开头。
 *
 *
 * Created on 2020/6/10.
 *
 * @author o.s
 */
@Entity(tableName = "users")
data class User(

    /**
     * 每个Entity都需要至少一个字段设置为主键。即使这个Entity只有一个字段也需要设置为主键。
     *
     * Entity设置主键的方式有两种：
     * 1、通过@Entity的primaryKeys属性来设置主键(primaryKeys是数组可以设置单个主键，也可以设置复合主键)。
     *      primaryKeys = ["firstName", "lastName"]
     * 2、通过@PrimaryKey注解来设置主键。
     *
     * 可以设置@PrimaryKey的autoGenerate属性，设置一个自增的字段。
     */
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    /**
     * 默认情况下Entity类中字段的名字就是表中列的名字。我们也是可以通过@ColumnInfo注解来自定义表中列的名字。
     */
    @ColumnInfo(name = "first_name")
    var firstName: String,

    @ColumnInfo(name = "last_name")
    var lastName: String,

    var age: Int,

    @Ignore
    var pic: Bitmap?,

    @Embedded
    var address: Address
) {
    constructor(): this(0, "", "", 1, null, Address("", "", "", "100001"))
}

/**
 * 外键约束示例：
 */
@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"]
        )]
)
data class Book(

    @PrimaryKey
    @ColumnInfo(name = "book_id")
    var bookId: Int,

    var title: String,

    @ColumnInfo(name = "user_id")
    var userId: Int
)

/**
 * 嵌套对象示例：
 */
data class Address(
    var street: String,
    var state: String,
    var city: String,

    @ColumnInfo(name = "post_code")
    var postCode: String
)

data class Name(

    @ColumnInfo(name = "first_name")
    var firstName: String,

    @ColumnInfo(name = "last_name")
    var lastName: String
)