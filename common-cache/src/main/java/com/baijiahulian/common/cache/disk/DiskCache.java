package com.baijiahulian.common.cache.disk;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据的disk缓存，支持key value和可选的meta
 */
public class DiskCache {

    private static final String TAG = DiskCache.class.getSimpleName();
    private static final String TIMEOUT_KEY = "timeout";
    private static final int VALUE_IDX = 0;
    private static final int METADATA_IDX = 1;
    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 Kb

    private static com.baijiahulian.common.cache.disk.DiskLruCache diskLruCache;
    private static int mAppVersion;

    public static boolean init(File dir, int appVersion, long maxSize) {
        try {
            open(dir, appVersion, maxSize);
        } catch (IOException e) {
            Log.e(TAG, "init disk cache error, e:" + e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    private DiskCache(File dir, int appVersion, long maxSize) throws IOException {
        mAppVersion = appVersion;
        diskLruCache = com.baijiahulian.common.cache.disk.DiskLruCache.open(dir, appVersion, 2, maxSize);
    }

    /**
     * 打开一个缓存
     *
     * @param dir 缓存数据文件夹
     * @param appVersion 版本，由用户控制，修改version后需要自行删除旧文件，否则打开不同版本的文件会抛异常
     * @param maxSize cache缓存所有数据最大字节数，0表示没有限制
     * @return 缓存实例
     * @throws IOException
     */
    public static synchronized DiskCache open(File dir, int appVersion, long maxSize) throws IOException {
        if (maxSize == 0) {
            maxSize = Long.MAX_VALUE;
        }
        return new DiskCache(dir, appVersion, maxSize);
    }

    /**
     * 关闭cache
     */
    public static void close() {
        try {
            diskLruCache.close();
        } catch (IOException e) {
            Log.e(TAG, "catch exception when close cache, e:" + e.getLocalizedMessage());
        }
        diskLruCache = null;
    }

    /**
     * 清空并重新打开
     */
    public static void clear() {
        File dir = diskLruCache.getDirectory();
        long maxSize = diskLruCache.getMaxSize();
        try {
            diskLruCache.delete();
        } catch (Exception e) {
            Log.e(TAG, "catch exception when delete dir:" + dir + " e:" + e.getLocalizedMessage());
        }
        try {
            diskLruCache = com.baijiahulian.common.cache.disk.DiskLruCache.open(dir, mAppVersion, 1, maxSize);
        } catch (Exception e) {
            Log.e(TAG, "catch exception when reopen dir:" + dir + " e:" + e.getLocalizedMessage());
        }
    }

    public static boolean delete(String key) {
        try {
            return diskLruCache.remove(toInternalKey(key));
        } catch (IOException e) {
            Log.e(TAG, "catch exception when remove key, e:" + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 检测是否超时
     * 
     * @return 是否超时，发生解析错误按未超时处理
     */
    private static boolean ifTimeout(Map<String, Serializable> meta) {
        try {
            if (meta == null || !meta.containsKey(TIMEOUT_KEY)) {
                return false;
            }
            String timeStr = (String) meta.get(TIMEOUT_KEY);
            long timeout = Long.parseLong(timeStr);
            Log.d(TAG, "read meta timeout:" + timeout + " current time:" + System.currentTimeMillis());
            return ((timeout - System.currentTimeMillis()) <= 0);
        } catch (NumberFormatException e) {
            Log.e(TAG, "catch io exception when parse long, e:" + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 查找数据
     *
     * @param key 用户使用的key
     * @return 数据或null
     */
    public static InputStream getInputStream(String key) {
        InputStreamEntry entry = getInputStreamWithMeta(key);
        if (entry == null) {
            return null;
        }
        return entry.getInputStream();
    }

    /**
     * 查找数据
     *
     * @param key 用户使用的key
     * @return 数据或null
     */
    public static InputStreamEntry getInputStreamWithMeta(String key) {
        com.baijiahulian.common.cache.disk.DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = diskLruCache.get(toInternalKey(key));
        } catch (IOException e) {
            Log.e(TAG, "catch io exception when get snapshot, e:" + e.getLocalizedMessage());
        }
        if (snapshot == null) {
            return null;
        }
        try {
            Map<String, Serializable> meta = readMetadata(snapshot);
            if (ifTimeout(meta)) {
                delete(key);
                Log.d(TAG, "timeout key:" + key);
                return null;
            }
            return new InputStreamEntry(snapshot, meta);
        } catch (IOException e) {
            Log.e(TAG, "catch io exception when new InputStreamEntry, e:" + e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 查找数据
     *
     * @param key 用户使用的key
     * @return 数据或null
     */
    public static String getString(String key) {
        try {
            StringEntry entry = getStringWithMeta(key);
            if (entry == null) {
                return null;
            }
            return entry.getString();
        } catch (Exception e) {
            Log.e(TAG, "catch exception when read string from cache, e:" + e.getLocalizedMessage());
            return "";
        }
    }

    /**
     * 查找数据
     *
     * @param key 用户使用的key
     * @return 数据或null
     */
    public static StringEntry getStringWithMeta(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        com.baijiahulian.common.cache.disk.DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = diskLruCache.get(toInternalKey(key));
        } catch (IOException e) {
            Log.e(TAG, "catch io exception when get snapshot when read, e:" + e.getLocalizedMessage());
        }
        if (snapshot == null) {
            return null;
        }
        try {
            Map<String, Serializable> meta = readMetadata(snapshot);
            if (ifTimeout(meta)) {
                delete(key);
                Log.d(TAG, "timeout key:" + key);
                return null;
            }
            return new StringEntry(snapshot.getString(VALUE_IDX), meta);
        } catch (IOException e) {
            Log.e(TAG, "catch io exception when read from snapshot, e;" + e.getLocalizedMessage());
            return null;
        } finally {
            snapshot.close();
        }
    }

    /**
     * 是否包含该数据
     *
     * @param key 用户使用的key
     * @return 是否包含该数据
     */
    public static boolean contains(String key) {
        com.baijiahulian.common.cache.disk.DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = diskLruCache.get(toInternalKey(key));
        } catch (IOException e) {
            Log.e(TAG, "catch io exception when get snapshot when read, e:" + e.getLocalizedMessage());
            return false;
        }
        if (snapshot == null) {
            return false;
        }
        try {
            Map<String, Serializable> meta = readMetadata(snapshot);
            if (ifTimeout(meta)) {
                delete(key);
                Log.d(TAG, "timeout key:" + key);
                return false;
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "catch io exception when read from snapshot, e;" + e.getLocalizedMessage());
            return false;
        } finally {
            snapshot.close();
        }
    }

    /**
     * 使用CacheOutputStream包装OutputStream
     *
     * @param key 用户的key
     * @return CacheOutputStream
     * @throws IOException
     */
    private static OutputStream openStream(String key) throws IOException {
        return openStream(key, new HashMap<String, Serializable>());
    }

    /**
     * 使用CacheOutputStream包装OutputStream
     *
     * @param key 用户的key
     * @param metadata 自定义的数据
     * @return CacheOutputStream
     * @throws IOException
     */
    private static OutputStream openStream(String key, Map<String, ? extends Serializable> metadata) throws IOException {
        com.baijiahulian.common.cache.disk.DiskLruCache.Editor editor = diskLruCache.edit(toInternalKey(key));
        try {
            writeMetadata(metadata, editor);
            BufferedOutputStream bos = new BufferedOutputStream(editor.newOutputStream(VALUE_IDX), DEFAULT_BUFFER_SIZE);
            return new CacheOutputStream(bos, editor);
        } catch (IOException e) {
            editor.abort();
            throw e;
        }
    }

    /**
     * 向cache中添加数据
     *
     * @param key 用户的key
     * @param is cache的数据
     * @return 是否成功
     */
    public static boolean put(String key, InputStream is) {
        return put(key, is, new HashMap<String, Serializable>());
    }

    /**
     * 向cache中添加数据，并设定超时时间
     *
     * @param key 用户的key
     * @param is cache的数据
     * @param timeout 超时时间 毫秒
     * @return 是否成功
     */
    public static boolean put(String key, InputStream is, long timeout) {
        Map<String, Serializable> meta = new HashMap<String, Serializable>();
        meta.put(TIMEOUT_KEY, String.valueOf(System.currentTimeMillis() + timeout));
        return put(key, is, meta);
    }

    /**
     * 向cache中添加数据
     *
     * @param key 用户的key
     * @param is cache的数据
     * @param annotations 用户自定义数据
     * @return 是否成功
     */
    public static boolean put(String key, InputStream is, Map<String, Serializable> annotations) {
        OutputStream os = null;
        try {
            os = openStream(key, annotations);
            Util.copyStream(is, os);
        } catch (IOException e) {
            Log.e(TAG, "catch io exception when write stream, e:" + e.getLocalizedMessage());
            return false;
        } finally {
            Util.closeQuietly(os);
        }
        return true;
    }

    /**
     * 向cache中添加数据
     *
     * @param key 用户的key
     * @param value cache的数据
     * @return 是否成功
     */
    public static boolean put(String key, String value) {
        return put(key, value, new HashMap<String, Serializable>());
    }

    /**
     * 向cache中添加数据
     *
     * @param key 用户的key
     * @param value cache的数据
     * @param timeout 超时时间 毫秒
     * @return 是否成功
     */
    public static boolean put(String key, String value, long timeout) {
        Map<String, Serializable> meta = new HashMap<String, Serializable>();
        meta.put(TIMEOUT_KEY, String.valueOf(System.currentTimeMillis() + timeout));
        return put(key, value, meta);
    }

    /**
     * 向cache中添加数据
     *
     * @param key 用户的key
     * @param value cache的数据
     * @param annotations 用户自定义数据
     * @return 是否成功
     */
    public static boolean put(String key, String value, Map<String, Serializable> annotations) {
        OutputStream cos = null;
        try {
            cos = openStream(key, annotations);
            cos.write(value.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "catch io exception when write string, e:" + e.getLocalizedMessage());
            return false;
        } finally {
            Util.closeQuietly(cos);
        }
        return true;
    }

    /**
     * 将字符串转成cache使用的key
     *
     * @param key 用户的key
     * @return cache使用的key
     */
    private static String toInternalKey(String key) {
        return md5(key);
    }

    private static String md5(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes("UTF-8"));
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }

    /**
     * 将自定义数据写入文件
     * 
     * @param metadata 自定义文件
     * @param editor cache编辑器
     * @throws IOException
     */
    private static void writeMetadata(Map<String, ? extends Serializable> metadata,
        com.baijiahulian.common.cache.disk.DiskLruCache.Editor editor) throws IOException {
        ObjectOutputStream oos = null;
        try {
            oos =
                new ObjectOutputStream(new BufferedOutputStream(editor.newOutputStream(METADATA_IDX),
                    DEFAULT_BUFFER_SIZE));
            oos.writeObject(metadata);
        } finally {
            Util.closeQuietly(oos);
        }
    }

    private static Map<String, Serializable> readMetadata(
        com.baijiahulian.common.cache.disk.DiskLruCache.Snapshot snapshot) throws IOException {
        ObjectInputStream ois = null;
        try {
            ois =
                new ObjectInputStream(new BufferedInputStream(snapshot.getInputStream(METADATA_IDX),
                    DEFAULT_BUFFER_SIZE));
            @SuppressWarnings("unchecked")
            Map<String, Serializable> annotations = (Map<String, Serializable>) ois.readObject();
            return annotations;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            Util.closeQuietly(ois);
        }
    }

    /**
     * 通过是否有异常来判断close时是否提交修改
     */
    private static class CacheOutputStream extends FilterOutputStream {

        private final com.baijiahulian.common.cache.disk.DiskLruCache.Editor editor;
        private boolean failed = false;

        private CacheOutputStream(OutputStream os, com.baijiahulian.common.cache.disk.DiskLruCache.Editor editor) {
            super(os);
            this.editor = editor;
        }

        @Override
        public void close() throws IOException {
            IOException closeException = null;
            try {
                super.close();
            } catch (IOException e) {
                closeException = e;
            }

            if (failed) {
                editor.abort();
            } else {
                editor.commit();
            }

            if (closeException != null)
                throw closeException;
        }

        @Override
        public void flush() throws IOException {
            try {
                super.flush();
            } catch (IOException e) {
                failed = true;
                throw e;
            }
        }

        @Override
        public void write(int oneByte) throws IOException {
            try {
                super.write(oneByte);
            } catch (IOException e) {
                failed = true;
                throw e;
            }
        }

        @Override
        public void write(byte[] buffer) throws IOException {
            try {
                super.write(buffer);
            } catch (IOException e) {
                failed = true;
                throw e;
            }
        }

        @Override
        public void write(byte[] buffer, int offset, int length) throws IOException {
            try {
                super.write(buffer, offset, length);
            } catch (IOException e) {
                failed = true;
                throw e;
            }
        }
    }

    public static class InputStreamEntry {
        private final com.baijiahulian.common.cache.disk.DiskLruCache.Snapshot snapshot;
        private final Map<String, Serializable> metadata;

        public InputStreamEntry(com.baijiahulian.common.cache.disk.DiskLruCache.Snapshot snapshot,
            Map<String, Serializable> metadata) {
            this.metadata = metadata;
            this.snapshot = snapshot;
        }

        public InputStream getInputStream() {
            return snapshot.getInputStream(VALUE_IDX);
        }

        public Map<String, Serializable> getMetadata() {
            return metadata;
        }

        public void close() {
            snapshot.close();
        }

    }

    public static class StringEntry {
        private final String string;
        private final Map<String, Serializable> metadata;

        public StringEntry(String string, Map<String, Serializable> metadata) {
            this.string = string;
            this.metadata = metadata;
        }

        public String getString() {
            return string;
        }

        public Map<String, Serializable> getMetadata() {
            return metadata;
        }
    }
}
