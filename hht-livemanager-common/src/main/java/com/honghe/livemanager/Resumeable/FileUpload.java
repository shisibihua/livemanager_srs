package com.honghe.livemanager.Resumeable;

import com.honghe.livemanager.common.util.ParamUtil;
import com.honghe.livemanager.common.util.PathUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传
 *
 * @Author libing
 * @Date: 2018-09-10 16:49
 * @Mender:
 */
public class FileUpload{

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int resumableChunkNumber = this.getResumableChunkNumber(request);
        ResumableInfo info = this.getResumableInfo(request);
        if (info.uploadedChunks.contains(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber))) {
            response.getWriter().print("Uploaded.");
        } else {
            response.setStatus(404);
        }
    }

    /**
     * 分片上传文件
     * @param request
     * @param response
     * @param pathType  地址
     * @return
     * @throws IOException
     * @throws ServletException
     */
    public String doPost(HttpServletRequest request, HttpServletResponse response,PathUtil.PathType pathType) throws IOException, ServletException {
        //存储后的文件名称
        String returnStr;
        int resumableChunkNumber = getResumableChunkNumber(request);

        ResumableInfo info = getResumableInfo(request);

        RandomAccessFile raf = new RandomAccessFile(info.resumableFilePath, "rw");

        //Seek to position
        raf.seek((resumableChunkNumber - 1) * (long) info.resumableChunkSize);

        //得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        //上传时生成的临时文件保存目录
        String tempPath = PathUtil.getPath(pathType) + "/temp";
        File tmpFile = new File(tempPath);
        if (!tmpFile.exists()) {
            //创建临时目录
            tmpFile.mkdir();
        }
        try {
            //Enumeration<String> stringEnumeration = request.getHeaderNames();
            FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(request.getServletContext());
            //使用Apache文件上传组件处理文件上传步骤：
            //1、创建一个DiskFileItemFactory工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            //设置工厂的缓冲区的大小，当上传的文件大小超过缓冲区的大小时，就会生成一个临时文件存放到指定的临时目录当中。
            factory.setSizeThreshold(1024 * 1024);//设置缓冲区的大小为1M，如果不指定，那么缓冲区的大小默认是10KB
            //设置上传时生成的临时文件的保存目录
            factory.setRepository(tmpFile);
            factory.setFileCleaningTracker(fileCleaningTracker);
            //2、创建一个文件上传解析器
            ServletFileUpload upload = new ServletFileUpload(factory);
            //监听文件上传进度
            upload.setProgressListener(new ProgressListener() {
                public void update(long pBytesRead, long pContentLength, int arg2) {
//                    System.out.println("文件大小为：" + pContentLength + ",当前已处理：" + pBytesRead);
                }
            });
            //解决上传文件名的中文乱码
            upload.setHeaderEncoding("UTF-8");
            //3、判断提交上来的数据是否是上传表单的数据
            //TODO 放开
            if (!ServletFileUpload.isMultipartContent(request)) {
//                System.out.println("文件上传类型错误，不是isMultipart");
                return null;
            }

            //设置上传单个文件的大小的最大值，目前是设置为1024*1024字节，也就是1MB
            //BigDecimal bigDecimal = new BigDecimal(1024 * 1024).multiply(new BigDecimal(maxSize));
            //upload.setFileSizeMax(bigDecimal.longValue());
            //设置上传文件总量的最大值，最大值=同时上传的多个文件的大小的最大值的和，目前设置为10MB
            //upload.setSizeMax(bigDecimal.longValue());
            //4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
            List<FileItem> list = upload.parseRequest(request);
            for (FileItem item : list) {
                //如果fileitem中封装的是普通输入项的数据
                if (item.isFormField()) {
                    String name = item.getFieldName();
                    //解决普通输入项的数据的中文乱码问题
                    String value = item.getString("UTF-8");
                } else {//如果fileitem中封装的是上传文件
                    //得到上传的文件名称，
                    InputStream in = item.getInputStream();
                    //创建一个缓冲区
                    byte buffer[] = new byte[1024];
                    //判断输入流中的数据是否已经读完的标识
                    int len = 0;
                    //循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
                    while ((len = in.read(buffer)) > 0) {
                        //使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                        raf.write(buffer, 0, len);
                    }
                    //关闭输入流
                    in.close();
                    raf.close();
                    //删除处理文件上传时生成的临时文件
                    item.delete();
                }
            }


            //Mark as uploaded.
            info.uploadedChunks.add(new ResumableInfo.ResumableChunkNumber(resumableChunkNumber));
            if (info.checkIfUploadFinished()) { //Check if all chunks uploaded, and change filename
                ResumableInfoStorage.getInstance().remove(info);
                //增加md5校验
//            String fileMd5Util = MD5Util.getFileMD5String(new File(info.resumableFilePath));
//            if(fileMd5Util.equals(info.resumableFileMd5String)){
//                  returnStr = info.resumableFilename;
//            }
                returnStr = info.resumableFilename;

            } else {
                returnStr = info.resumableFilename;
            }
            response.setContentType("text/html;charset=utf-8");
            return returnStr;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;

    }




    private int getResumableChunkNumber(HttpServletRequest request) {
        return ParamUtil.toInt(request.getParameter("resumableChunkNumber"), -1);
    }

    private ResumableInfo getResumableInfo(HttpServletRequest request) throws ServletException {
        String upload_dir = PathUtil.getPath(PathUtil.PathType.UPLOAD);
        int resumableChunkSize = ParamUtil.toInt(request.getParameter("resumableChunkSize"), -1);
        long resumableTotalSize = ParamUtil.toLong(request.getParameter("resumableTotalSize"), -1);
        String resumableIdentifier = request.getParameter("resumableIdentifier");
        String resumableFilename = request.getParameter("resumableFilename");
        String resumableRelativePath = request.getParameter("resumableRelativePath");
        String resumableFileMd5String = request.getParameter("resumableFileMd5String");
        int resumableTotalChunks = ParamUtil.toInt(request.getParameter("resumableTotalChunks"), 1);
        //Here we add a ".temp" to every upload file to indicate NON-FINISHED
        resumableFilename = UUID.randomUUID().toString() + "." + this.getExtensionName(resumableFilename);
        String resumableFilePath = new File(upload_dir, resumableFilename).getAbsolutePath() + ".temp";
        ResumableInfoStorage storage = ResumableInfoStorage.getInstance();
        ResumableInfo info = storage.get(resumableChunkSize, resumableTotalSize,
                resumableIdentifier, resumableFilename, resumableRelativePath, resumableFilePath, resumableFileMd5String, resumableTotalChunks);
        if (!info.vaild()) {
            storage.remove(info);
            throw new ServletException("Invalid request params.");
        }
        return info;
    }

    private String getExtensionName(String filename) {
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf(46);
            if (dot > -1 && dot < filename.length() - 1) {
                return filename.substring(dot + 1).toLowerCase();
            }
        }
        return filename;
    }


}
