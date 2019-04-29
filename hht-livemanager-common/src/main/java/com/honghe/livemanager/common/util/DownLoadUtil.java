package com.honghe.livemanager.common.util;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Description 下载文件工具类
 * @Author libing
 * @Date: 2017-12-20 16:48
 * @Mender:
 */
public class DownLoadUtil {

    public static boolean getFile(String fileurl, HttpServletResponse response)
            throws IOException {
        //String basePath = session.getServletContext().getRealPath("/"); // 获取基本路径
        if (null!=fileurl && !fileurl.equals("")) {
            /* 第一步:根据文件路径获取文件 */
            File file = new File(fileurl);
            //判断目标文件所在的目录是否存在
            if (file.exists()) { // 文件存在
                /* 第二步：根据已存在的文件，创建文件输入流 */
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                /* 第三步：创建缓冲区，大小为流的最大字符数 */
                byte[] buffer = new byte[inputStream.available()]; // int available() 返回值为流中尚未读取的字节的数量
                /* 第四步：从文件输入流读字节流到缓冲区 */
                inputStream.read(buffer);
                /* 第五步： 关闭输入流 */
                inputStream.close();

                String fileName = file.getName();// 获取文件名
                response.reset();
                response.addHeader("Content-Disposition",
                        "attachment;filename=" + new String(fileName.getBytes("utf-8"), "iso8859-1"));
                response.addHeader("Content-Length", "" + file.length());

                /* 第六步：创建文件输出流 */
                OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                /* 第七步：把缓冲区的内容写入文件输出流 */
                outputStream.write(buffer);
                /* 第八步：刷空输出流，并输出所有被缓存的字节 */
                outputStream.flush();
                /* 第九步：关闭输出流 */
                outputStream.close();
            }
        } else {

            return false;
        }

        return true;
    }
}
