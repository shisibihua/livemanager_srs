package com.honghe.livemanager.util;

import com.honghe.livemanager.common.util.*;
import com.honghe.livemanager.service.LiveService;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Excel工具
 */
public class ExcelTools {
    private static ExcelTools INSTANCE=null;
    /**
     * 导出锁定，防止导出过多而内存溢出
     */
    private static final Object EXPORT_LOCK = new Object();

    /**
     * 保存文件扩展名
     */
    public static final String EXCEL_EXT_NAME = ".xls";

    /**
     * 允许写入的最大行数
     */
    public static final int MAX_ROW = 65536;

    /**
     * 允许写入的最大sheet数
     */
    public static final int MAX_SHEET = 255;

    /**
     * 默认最大行数：10000行
     */
    public int default_row = 10000;
    private static final String LIVE_LIVING_TYPE="liveList";
    private static final String LIVE_STATISTIC_TYPE="liveStatistic";
    /**
     * 创建一个实例
     */
    public static ExcelTools getInstance() {
        if(null==INSTANCE){
            synchronized (ExcelTools.class){
                if(null==INSTANCE){
                    INSTANCE=new ExcelTools();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 修改默认最大导出行数，最大不允许超过65536
     */
    public ExcelTools setExportRow(int row) {
        if (row > MAX_ROW) {
            default_row = MAX_ROW;
        } else if (row > 0) {
            default_row = row;
        }
        return this;
    }

    private String exportExcelFile(String[] headers, List<Map<String,Object>> list, String filepath,
                                   Map<String,Object> params,String fileType) throws IOException {

        // 创建工作对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 创建sheet
        HSSFSheet sheet = workbook.createSheet();
        //查询时间范围表头
        HSSFRow tableTitle = sheet.createRow(0);
        HSSFCell cellTitle = tableTitle.createCell(0);
        // 生成一个样式
        HSSFCellStyle styleTitle = workbook.createCellStyle();
        // 设置表头样式
        cellTitle.setCellStyle(styleTitle);
        //创建字体样式
        HSSFFont titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 10);//设置字体大小
        styleTitle.setAlignment(HorizontalAlignment.CENTER);//左右居中

        //设置表头字体
        styleTitle.setFont(titleFont);
        // 设置表头内容
        String currentDate=params.get("currentDate")==null?"":params.get("currentDate").toString();
        String beginTime=params.get("beginTime")==null?"":params.get("beginTime").toString();
        String endTime=params.get("endTime")==null?"":params.get("endTime").toString();
        String searchTime="";
        if (!ParamUtil.isEmpty(beginTime) && !ParamUtil.isEmpty(endTime)) {
            searchTime=beginTime+" ~ "+endTime;
        }else{
            searchTime=currentDate;
        }
        cellTitle.setCellValue("查询时间范围:"+searchTime);
        // 合并单元格，合并标题的单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        // 在第二行创建
        HSSFRow lieRow = sheet.createRow(1);
        // 设置列名称
        HSSFFont cellFont = workbook.createFont();
        HSSFFont dataFont = workbook.createFont();
        cellFont.setFontHeightInPoints((short) 12);
        dataFont.setFontHeightInPoints((short) 10);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = lieRow.createCell(i);
            //创建格式
            HSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFont(cellFont);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);//左右居中
            //设置格式
            cell.setCellStyle(cellStyle);
            cell.setCellValue(headers[i]);
        }

        // 遍历数据集，写入数据,设置单元格样式，
        HSSFCellStyle dataStyle = workbook.createCellStyle();

        dataStyle.setFont(dataFont);
        HSSFDataFormat dataFormat = workbook.createDataFormat();
        dataStyle.setDataFormat(dataFormat.getFormat("@"));// 设置单元格格式为文本

        for (int i = 0; i < list.size(); i++) {
            // 每行的起始数+2
            HSSFRow dataRow = sheet.createRow(i + 2);
            // 取出一个数据
            Map<String,Object> map = list.get(i);
            switch (fileType){
                case LIVE_LIVING_TYPE:
                    writeLiveExcel(map,dataRow,dataStyle);
                    break;
                case LIVE_STATISTIC_TYPE:
                    writeLiveStatisticExcel(map,dataRow,dataStyle);
                    break;
            }
        }
        setSheetAutoWidth(fileType,headers.length,sheet);
        OutputStream stream = new FileOutputStream(filepath);
        workbook.write(stream);
        stream.close();
        return filepath;
    }

    /**
     * 设置excel表格宽度自适应
     * @param fileType        导出文件
     * @param headerLength    表头长度
     * @param sheet
     */
    private void setSheetAutoWidth(String fileType,int headerLength,HSSFSheet sheet){
        /**
         * 调整列宽为自动列宽
         * 用for循环添加 有多少列就将n改为多少
         */
        for (int i = 0; i < headerLength; i++) {
            if(LIVE_LIVING_TYPE.equals(fileType)){
                if(i!=6 && i!=7 && i!=8 && i!=9) {
                    sheet.autoSizeColumn((short) i);
                }
            }else {
                sheet.autoSizeColumn((short) i);
            }
        }
    }
    /**
     * 直播列表写入excel
     * @param map
     * @param dataRow
     * @param dataStyle
     */
    private void writeLiveExcel(Map<String,Object> map,HSSFRow dataRow,HSSFCellStyle dataStyle){
        //学校名称
        HSSFCell schoolCell = dataRow.createCell(0);
        schoolCell.setCellValue(map.get("name")==null?"":map.get("name").toString());
        schoolCell.setCellStyle(dataStyle);
        //直播名称
        HSSFCell titleCell = dataRow.createCell(1);
        titleCell.setCellValue(map.get("title")==null?"":map.get("title").toString());
        titleCell.setCellStyle(dataStyle);
        //主讲人
        HSSFCell speakerCell = dataRow.createCell(2);
        speakerCell.setCellValue(map.get("speakerName")==null?"":map.get("speakerName").toString());
        speakerCell.setCellStyle(dataStyle);
        //计划直播时间
        HSSFCell planTimeCell = dataRow.createCell(3);
        planTimeCell.setCellValue(getLivePlayTime(map.get("beginTime").toString(),map.get("endTime").toString()));
        planTimeCell.setCellStyle(dataStyle);
        //实际直播时间
        HSSFCell actualTimeCell = dataRow.createCell(4);
        String actualBeginTime="";
        String actualEndTime="";
        if(map.containsKey("actualBeginTime")){
            actualBeginTime=map.get("actualBeginTime").toString();
        }
        if(map.containsKey("actualEndTime")){
            actualEndTime=map.get("actualEndTime").toString();
        }
        actualTimeCell.setCellValue(getLivePlayTime(actualBeginTime,actualEndTime));
        actualTimeCell.setCellStyle(dataStyle);
        //推流设备ip
        HSSFCell pushClientIpCell = dataRow.createCell(5);
        pushClientIpCell.setCellValue(map.get("pushClientIp")==null?"":map.get("pushClientIp").toString());
        pushClientIpCell.setCellStyle(dataStyle);
        //推流地址
        Map<String,Object> res=new HashMap<>();
        LiveService liveService=(LiveService) SpringUtil.getBean(LiveService.class);
        Map<String,Object> streamUrlMap=liveService.getStreamUrl(res,map.get("streamCode").toString(),map.get("endTime").toString());
        HSSFCell pushStreamUrlCell = dataRow.createCell(6);
        pushStreamUrlCell.setCellValue(streamUrlMap.get("livePushUrl")==null?"":streamUrlMap.get("livePushUrl").toString());
        pushStreamUrlCell.setCellStyle(dataStyle);
        //播放地址
        HSSFCell rtmpPlayStreamUrlCell = dataRow.createCell(7);
        rtmpPlayStreamUrlCell.setCellValue(streamUrlMap.get("livePlayRtmpUrl")==null?"":streamUrlMap.get("livePlayRtmpUrl").toString());
        rtmpPlayStreamUrlCell.setCellStyle(dataStyle);

        HSSFCell flvPlayStreamUrlCell = dataRow.createCell(8);
        flvPlayStreamUrlCell.setCellValue(streamUrlMap.get("livePlayFlvUrl")==null?"":streamUrlMap.get("livePlayFlvUrl").toString());
        flvPlayStreamUrlCell.setCellStyle(dataStyle);

        HSSFCell hlsPlayStreamUrlCell = dataRow.createCell(9);
        hlsPlayStreamUrlCell.setCellValue(streamUrlMap.get("livePlayHlsUrl")==null?"":streamUrlMap.get("livePlayHlsUrl").toString());
        hlsPlayStreamUrlCell.setCellStyle(dataStyle);
        //推流帧率
        HSSFCell videoFrameRateCell = dataRow.createCell(10);
        videoFrameRateCell.setCellValue(streamUrlMap.get("videoFrameRate")==null?"":streamUrlMap.get("videoFrameRate").toString());
        videoFrameRateCell.setCellStyle(dataStyle);
        //推流码率
        HSSFCell bitRateCell = dataRow.createCell(11);
        bitRateCell.setCellValue(streamUrlMap.get("bitRate")==null?"":streamUrlMap.get("bitRate").toString());
        bitRateCell.setCellStyle(dataStyle);
        //宽带
        HSSFCell bandWidthCell = dataRow.createCell(12);
        bandWidthCell.setCellValue(streamUrlMap.get("bandWidth")==null?"":streamUrlMap.get("bandWidth").toString());
        bandWidthCell.setCellStyle(dataStyle);
        //流量
        HSSFCell trafficValueCell = dataRow.createCell(13);
        trafficValueCell.setCellValue(streamUrlMap.get("trafficValue")==null?"":streamUrlMap.get("trafficValue").toString());
        trafficValueCell.setCellStyle(dataStyle);
        //观看人数
        HSSFCell viewersNumberCell = dataRow.createCell(14);
        viewersNumberCell.setCellValue(streamUrlMap.get("viewersNumber")==null?"":streamUrlMap.get("viewersNumber").toString());
        viewersNumberCell.setCellStyle(dataStyle);
        //直播状态
        HSSFCell liveStatusCell = dataRow.createCell(15);
        String status=String.valueOf(map.get("status")==null?"":map.get("status"));
        liveStatusCell.setCellValue(convertLiveStatus(status));
        liveStatusCell.setCellStyle(dataStyle);
    }

    /**
     * 处理直播时间
     * @param beginTime
     * @param endTime
     * @return
     */
    private String getLivePlayTime(String beginTime,String endTime){
        String livePlayTime="";
        try {
            String actualBeginTime=DateUtil.formatShortTime(DateUtil.parseDatetime(beginTime));
            String actualEndTime=DateUtil.formatShortTime(DateUtil.parseDatetime(endTime));
            livePlayTime=actualBeginTime+" - "+actualEndTime;
        } catch (ParseException e) {
            livePlayTime="";
        }
        return livePlayTime;
    }

    /**
     * 转换直播状态
     * @param status
     * @return
     */
    private String convertLiveStatus(String status){
        String liveStatus="";
        switch (status){
            case "1":
                liveStatus="直播中";
                break;
            case "2":
                liveStatus="未开始";
                break;
            case "3":
                liveStatus="已结束";
                break;
            case "4":
                liveStatus="已关闭";
                break;
            default:
                liveStatus=status;
        }
        return liveStatus;
    }

    /**
     * 直播统计写入excel
     * @param map
     * @param dataRow
     * @param dataStyle
     */
    private void writeLiveStatisticExcel(Map<String,Object> map,HSSFRow dataRow,HSSFCellStyle dataStyle) {
        HSSFCell schoolNameCell = dataRow.createCell(0);
        schoolNameCell.setCellValue(map.get("schoolName")==null?"":map.get("schoolName").toString());
        schoolNameCell.setCellStyle(dataStyle);

        HSSFCell liveCountCell = dataRow.createCell(1);
        liveCountCell.setCellValue(map.get("liveCount")==null?0:Integer.parseInt(String.valueOf(map.get("liveCount"))));
        liveCountCell.setCellStyle(dataStyle);

        HSSFCell trafficValueCell = dataRow.createCell(2);
        trafficValueCell.setCellValue(map.get("trafficValue")==null?0.0:Float.parseFloat(String.valueOf(map.get("trafficValue"))));
        trafficValueCell.setCellStyle(dataStyle);

        HSSFCell viewersNumberCell = dataRow.createCell(3);
        viewersNumberCell.setCellValue(map.get("viewersNumber")==null?0:Integer.parseInt(String.valueOf(map.get("viewersNumber"))));
        viewersNumberCell.setCellStyle(dataStyle);

        HSSFCell screenShotPicCell = dataRow.createCell(4);
        screenShotPicCell.setCellValue(map.get("screenShotPic")==null?0:Integer.parseInt(String.valueOf(map.get("screenShotPic"))));
        screenShotPicCell.setCellStyle(dataStyle);
    }
    /**
     * 导出excel文件流，分文件导出
     *
     * @param headers  列名
     * @param list     行数据，必须按照列名的顺序排列
     * @param path     文件输出路径
     * @param fileName 文件名（不含扩展名）
     * @param fileType 文件类型
     * @throws IOException
     */
    private String[] exportTableByFile(String[] headers,List<Map<String,Object>> list, String path, String fileName,
                                       Map<String,Object> params,String fileType)
            throws IOException {
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        // 多文件导出锁定
        synchronized (EXPORT_LOCK) {
            // 文件路径
            String[] filepaths = null;

            // 如果大于默认行数。则分文件进行
            if (list.size() > default_row) {
                // 进行分文件筛选
                int page = list.size() / default_row;
                if (list.size() % default_row != 0) {// 如果有余数则加1页
                    page += 1;
                }

                // 创建文件数组
                filepaths = new String[page];

                for (int i = 0; i < page; i++) {
                    List<Map<String,Object>> templist = null;
                    if (i == page - 1) {
                        templist = list.subList(i * default_row,
                                list.size() - 1);
                    } else {
                        templist = list.subList(i * default_row, (i + 1)
                                * default_row - 1);
                    }
                    // 导出文件
                    String filepath = exportExcelFile(headers, templist,
                            path + fileName + "_" + (i + 1) + "-" + page + EXCEL_EXT_NAME,params,fileType);
                    // 将文件名写入数组
                    filepaths[i] = filepath;
                }
            } else {
                // 直接导出
                String filepath = exportExcelFile(headers, list, path + fileName + EXCEL_EXT_NAME,params,fileType);
                // 将文件名写入数组
                filepaths = new String[]{filepath};
            }
            // 返回文件数组
            return filepaths;
        }
    }

    /**
     * 导出excl表，返回文件路径
     * @return
     */
    public String exportExcel(String name,String[] headers,List<Map<String,Object>> excelList,
                              Map<String,Object> params,String fileType){

        String filePath = PathUtil.getPath(PathUtil.PathType.UPLOAD);
        try {
            filePath = exportTableByFile(headers, excelList, filePath, name,params,fileType)[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName=name+EXCEL_EXT_NAME;
        return fileName;
    }

    /**
     * 下载直播文件
     * @param fileName
     * @param res
     * @return
     */
    public boolean downLoadFile(String fileName,HttpServletResponse res){
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        boolean download=true;
        try {
            os = res.getOutputStream();
            String filePath=PathUtil.getPath(PathUtil.PathType.UPLOAD)+fileName;
            bis = new BufferedInputStream(new FileInputStream(new File(filePath)));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            download=false;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    ;
                }
            }
        }
        return download;
    }
}
