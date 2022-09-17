package com.kotlin.android.ktx.ext.io

import android.content.Context
import android.os.Build
import android.os.Environment
import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.e
import java.io.*
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.util.*
import kotlin.random.Random

/**
 *
 * Created on 2020/5/6.
 *
 * @author o.s
 */

/**
 * 通用安全关闭
 */
fun Closeable?.safeClose() {
    this?.apply {
        try {
            close()
        } catch (e: Throwable) {
        } finally {
        }
    }
}

/**
 * 遍历获取文件大小
 */
val File.size: Long
    get() {
        var totalSize = 0L
        if (isFile) {
            totalSize = length()
        } else if (isDirectory) {
            listFiles()?.forEach {
                it?.run {
                    totalSize += if (isFile) length() else size
                }
            }
        }
        return totalSize
    }

/**
 * 将设备安装为照相机时，图片和视频的传统位置。请注意，这主要是顶级公共目录的约定，因为此约定在其他地方没有意义。
 */
val Context.sdDir: File
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.getStorageDirectory()
    } else {
        Environment.getExternalStorageDirectory()
    }

/**
 * 将设备安装为照相机时，图片和视频的传统位置。请注意，这主要是顶级公共目录的约定，因为此约定在其他地方没有意义。
 */
val Context.photoDir: File?
    get() = getExternalFilesDir(Environment.DIRECTORY_DCIM)

/**
 * 放置用户可用图片的标准目录。请注意，这主要是顶级公共目录的约定，因为媒体扫描器将在任何目录中查找和收集图片。
 */
val Context.picDir: File?
    get() = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

/**
 * 放置用户拍摄的屏幕快照的标准目录。通常用作第二目录
 */
val Context.screenShotsDir: File?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getExternalFilesDir(Environment.DIRECTORY_SCREENSHOTS)
    } else {
        null
    }

/**
 * 照片文件名称生成规则
 */
fun generateFileName(): String {
    val sb = StringBuffer()
    val calendar = Calendar.getInstance()
    val millis = calendar.timeInMillis
    val dictionaries = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I",
        "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
        "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g",
        "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
        "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4",
        "5", "6", "7", "8", "9")
    sb.append("mtime")
    sb.append(millis)
    for (i in 0..4) {
        sb.append(dictionaries[Random.nextInt(dictionaries.size - 1)])
    }
    return sb.toString()
}

/**
 * 持久化对象
 */
fun saveObject(context: Context, name: String, obj: Any?) {
    var fos: FileOutputStream? = null
    var oos: ObjectOutputStream? = null
    try {
        fos = context.openFileOutput(name, Context.MODE_PRIVATE)
        oos = ObjectOutputStream(fos)
        oos.writeObject(obj)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        oos.safeClose()
        fos.safeClose()
    }
}

/**
 * 加载对象
 */
fun getObject(context: Context, name: String): Any? {
    var fis: FileInputStream? = null
    var ois: ObjectInputStream? = null
    return try {
        fis = context.openFileInput(name)
        ois = ObjectInputStream(fis)
        ois.readObject()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        ois.safeClose()
        fis.safeClose()
    }
}

/**
 * 根据文件路径 [filePath] 读取字符串
 */
fun getStringByFilePath(filePath: String): String? {
    val file = File(filePath)
    var fis: FileInputStream? = null
    return try {
        fis = FileInputStream(file)
        getStringByInputStream(fis)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    } finally {
        fis.safeClose()
    }
}

/**
 * 读取输入流中的字符串
 */
fun getStringByInputStream(ins: InputStream?): String? {
    if (ins == null) {
        return null
    }
    var isr: InputStreamReader? = null
    var reader: BufferedReader? = null
    val sb = StringBuffer()
    try {
        isr = InputStreamReader(ins, "utf-8")
        reader = BufferedReader(isr)
        var line: String? = reader.readLine()
        while (!line.isNullOrEmpty()) {
            sb.append(line)
            sb.append("\n")
            line = reader.readLine()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        reader.safeClose()
        isr.safeClose()
        ins.safeClose()
    }
    return sb.toString()
}

/**
 * 确保文件路径存在文件，并返回
 */
fun makeSDDir(filePath: String): File? {
    val file: File
    return try {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            file = File(filePath)
            if (!file.exists()) {
                file.mkdirs()
            }
            file
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 读取文件
 */
fun readFile(filePath: String, fileName: String = "/test.json"): String {
    var stream: FileInputStream? = null
    return try {
        val testFile = File(Environment.getExternalStorageDirectory(), "$filePath$fileName")
        stream = FileInputStream(testFile)
        val fc: FileChannel = stream.channel
        val bb: MappedByteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
        Charset.defaultCharset().decode(bb).toString()
    } catch (e: Exception) {
        e.d()
        ""
    } finally {
        stream?.safeClose()
    }
}