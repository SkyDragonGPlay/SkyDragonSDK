package com.skydragon.gplay.paysdk.h5.persister;

import android.util.Log;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * package : com.skydragon.hybridsdk.persister
 * <p>
 * Description :
 *
 * @author Y.J.ZHOU
 * @date 2016/4/19 14:21.
 */
public class FilePersister {
    private String TAG = "FilePersister";
    private File mEntityFile;
    private final ReadWriteLock mFileLock;

    public FilePersister(File file) {
        mEntityFile = file;
        mFileLock = new ReentrantReadWriteLock();
    }

    public FilePersister(String fileName) {
        mEntityFile = getPersistFile(fileName);
        mFileLock = new ReentrantReadWriteLock();
    }

    private File getPersistFile(String fileName) {
        File file = null;
        if(fileName != null && !fileName.equals("")){
            file = new File(fileName);
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    public Object get(Object fallbacks) {

        if (mEntityFile == null)
            return null;

        Object content = fallbacks;
        InputStream is = null;

        try {
            boolean locked = mFileLock.readLock().tryLock();
            if (!locked)
                return null;

            is = new FileInputStream(mEntityFile);
            content = readString(is);
        } catch (Exception e) {
            Log.w(TAG, "Write content exception: " + e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mFileLock.readLock().unlock();
        }

        return content;
    }

    public boolean set(Object o) {
        boolean retValue = false;
        OutputStream out = null;
        String content = String.valueOf(o);
        try {
            mFileLock.writeLock().lock();
            out = new FileOutputStream(mEntityFile);
            writeString(out, content);
            retValue = true;
        } catch (Exception e) {
            Log.w(TAG, "Write content exception: " + e.getMessage());
        } finally {
            mFileLock.writeLock().unlock();
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return retValue;
    }

    private void writeLong(OutputStream os, long n) throws IOException {
        os.write((byte)(n >>> 0));
        os.write((byte)(n >>> 8));
        os.write((byte)(n >>> 16));
        os.write((byte)(n >>> 24));
        os.write((byte)(n >>> 32));
        os.write((byte)(n >>> 40));
        os.write((byte)(n >>> 48));
        os.write((byte)(n >>> 56));
    }

    private long readLong(InputStream is) throws IOException {
        long n = 0;
        n |= ((read(is) & 0xFFL) << 0);
        n |= ((read(is) & 0xFFL) << 8);
        n |= ((read(is) & 0xFFL) << 16);
        n |= ((read(is) & 0xFFL) << 24);
        n |= ((read(is) & 0xFFL) << 32);
        n |= ((read(is) & 0xFFL) << 40);
        n |= ((read(is) & 0xFFL) << 48);
        n |= ((read(is) & 0xFFL) << 56);
        return n;
    }

    private void writeString(OutputStream os, String s) throws IOException {
        byte[] b = s.getBytes("UTF-8");
        writeLong(os, b.length);
        os.write(b, 0, b.length);
    }

    private static byte[] streamToBytes(InputStream in, int length) throws IOException {
        byte[] bytes = new byte[length];
        int count;
        int pos = 0;
        while (pos < length && ((count = in.read(bytes, pos, length - pos)) != -1)) {
            pos += count;
        }
        if (pos != length) {
            throw new IOException("Expected " + length + " bytes, read " + pos + " bytes");
        }
        return bytes;
    }

    private String readString(InputStream is) throws IOException {
        int n = (int) readLong(is);
        byte[] b = streamToBytes(is, n);
        return new String(b, "UTF-8");
    }

    private int read(InputStream is) throws IOException {
        int b = is.read();
        if (b == -1) {
            throw new EOFException("Reach the end of the input stream");
        }
        return b;
    }
}
