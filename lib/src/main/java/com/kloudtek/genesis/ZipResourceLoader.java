package com.kloudtek.genesis;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourceLoader implements ResourceLoader {
    public static final String FILES = "files/";
    private File file;

    public ZipResourceLoader(File file) {
        this.file = file;
    }

    @Override
    public Set<String> listFiles() throws IOException {
        HashSet<String> files = new HashSet<>();
        try (ZipFile zip = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                String path = zipEntry.getName();
                if (!zipEntry.isDirectory() && path.startsWith(FILES)) {
                    files.add(path.substring(FILES.length()));
                }
            }
        }
        return files;
    }

    @Override
    public InputStream loadResource(String resourcePath) throws IOException {
        ZipFile zip = new ZipFile(file);
        ZipEntry entry = zip.getEntry(resourcePath);
        if (entry != null) {
            return new ZipStreamWrapper(zip, zip.getInputStream(entry));
        } else {
            return null;
        }
    }

    public class ZipStreamWrapper extends InputStream {
        private ZipFile zipFile;
        private InputStream is;

        public ZipStreamWrapper(ZipFile zipFile, InputStream is) {
            this.zipFile = zipFile;
            this.is = is;
        }

        @Override
        public int read(@NotNull byte[] b) throws IOException {
            return is.read(b);
        }

        @Override
        public int read(@NotNull byte[] b, int off, int len) throws IOException {
            return is.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            return is.skip(n);
        }

        @Override
        public int available() throws IOException {
            return is.available();
        }

        @Override
        public void close() throws IOException {
            is.close();
            zipFile.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
            is.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            is.reset();
        }

        @Override
        public boolean markSupported() {
            return is.markSupported();
        }

        @Override
        public int read() throws IOException {
            return is.read();
        }
    }
}
