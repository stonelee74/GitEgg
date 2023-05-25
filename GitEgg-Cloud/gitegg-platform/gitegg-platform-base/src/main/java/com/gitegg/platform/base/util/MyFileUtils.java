package com.gitegg.platform.base.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;


/**
 * 文件操作相关工具类
 */
@Slf4j
public class MyFileUtils {

    /**
     * 取得文件扩展名
     *
     * @param fileName
     * @return
     */
    public static String getExt(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return null;
        }
    }

//
//    public static String removePrefix(String src, String prefix) {
//        if (src != null && src.startsWith(prefix)) {
//            return src.substring(prefix.length());
//        }
//        return src;
//    }


    /**
     * 保存文件
     *
     * @param filename
     * @param context
     * @return
     */
    public static File saveTextFile(String filename, String context, String charset) throws Exception {
        File tempFile = new File(filename);
        if (!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdirs();
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(tempFile);
            outputStream.write(context.getBytes(charset));
            return tempFile;
        } catch (Exception e) {
            throw new Exception("无法保存文件", e);
        } finally {
            try {
                if (outputStream != null) outputStream.close();
            } catch (Exception e) {
                log.error("无法关闭输出流", e);
            }
        }
    }


//    public static String removeRootPath(String src){
//        return removePrefix(src, PathKit.getWebRootPath());
//    }
//
//    public static String readString(File file) {
//        ByteArrayOutputStream baos = null;
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(file);
//            baos = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            for (int len = 0; (len = fis.read(buffer)) > 0;) {
//                baos.write(buffer, 0, len);
//            }
//            return new String(baos.toByteArray(),JFinal.me().getConstants().getEncoding());
//        } catch (Exception e) {
//        } finally {
//            close(fis, baos);
//        }
//        return null;
//    }
//
//    public static void writeString(File file, String string) {
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(file, false);
//            fos.write(string.getBytes(JFinal.me().getConstants().getEncoding()));
//        } catch (Exception e) {
//        } finally {
//            close(null, fos);
//        }
//    }

    private static void close(InputStream is, OutputStream os) {
        if (is != null)
            try {
                is.close();
            } catch (IOException e) {
            }
        if (os != null)
            try {
                os.close();
            } catch (IOException e) {
            }
    }
//
//    public static void unzip(String zipFilePath) throws IOException {
//        String targetPath = zipFilePath.substring(0, zipFilePath.lastIndexOf("."));
//        unzip(zipFilePath, targetPath);
//    }
//
//    public static void unzip(String zipFilePath, String targetPath) throws IOException {
//        ZipFile zipFile = new ZipFile(zipFilePath);
//        try {
//            Enumeration<?> entryEnum = zipFile.entries();
//            if (null != entryEnum) {
//                while (entryEnum.hasMoreElements()) {
//                    OutputStream os = null;
//                    InputStream is = null;
//                    try {
//                        ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
//                        if (!zipEntry.isDirectory()) {
//                            File targetFile = new File(targetPath + File.separator + zipEntry.getName());
//                            if (!targetFile.getParentFile().exists()) {
//                                targetFile.getParentFile().mkdirs();
//                            }
//                            os = new BufferedOutputStream(new FileOutputStream(targetFile));
//                            is = zipFile.getInputStream(zipEntry);
//                            byte[] buffer = new byte[4096];
//                            int readLen = 0;
//                            while ((readLen = is.read(buffer, 0, 4096)) > 0) {
//                                os.write(buffer, 0, readLen);
//                            }
//                        }
//                    } finally {
//                        if (is != null)
//                            is.close();
//                        if (os != null)
//                            os.close();
//                    }
//                }
//            }
//        } finally {
//            zipFile.close();
//        }
//    }

    /**
     * 取得文件内容
     *
     * @param filename
     * @return
     */
    public static String getContent(String filename) {
        return getContent(new File(filename));
    }

    public static String getContent(File file) {
        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int len = 0; (len = fis.read(buffer)) > 0; ) {
                baos.write(buffer, 0, len);
            }
            return new String(baos.toByteArray(), "UTF-8");
        } catch (Exception e) {
            return null;
        } finally {
            close(fis, baos);
        }
    }
//    public static String extensionName(String filename) {
//        if ((filename != null) && (filename.length() > 0)) {
//            int dot = filename.lastIndexOf('.');
//            if ((dot > -1) && (dot < (filename.length() - 1))) {
//                return filename.substring(dot + 1);
//            }
//        }
//        return filename;
//    }

    public static void main(String[] args) {
        System.out.println(MyFileUtils.getContent("E:/nginx.conf"));
    }
}
