package com.mtime.util;

import com.mtime.base.bean.MBaseBean;
import com.mtime.common.utils.LogWriter;
import com.mtime.constant.FrameConstant;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created by cong.zhang on 17/2/13.
 */

public class UploadPicture {
    // 工具类，防止错误创建对象
    private UploadPicture() {
    }

    public static String uploadImage(String actionUrl, Map<String, String> params, UploadPicture.FormFileBean file) {
        final String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线
        try {
            URL url = new URL(actionUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 上传的表单参数部分
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
                sb.append("--");
                sb.append(BOUNDARY);
                sb.append("\r\n");
                sb.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
                sb.append(entry.getValue());
                sb.append("\r\n");
            }
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);// 不使用Cache
            conn.setRequestMethod("POST");
//        conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type", file.getContentType() + "; boundary=" + BOUNDARY);
            conn.setRequestProperty("Accept-Charset", "UTF-8,*");
            conn.setRequestProperty("Accept-Language", "zh-cn");
            conn.setRequestProperty("User-Agent", FrameConstant.UA_STR);
            conn.setRequestProperty("Cache-Control", "no-cache");
            // 设置连接主机超市（单位：毫秒）
            conn.setConnectTimeout(8000);
            // 设置从主机读取数据超时（单位：毫秒）
            conn.setReadTimeout(8000);
            // Map<String, List<String>> map1=conn.getRequestProperties();
            conn.connect();
            DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
            outStream.write(sb.toString().getBytes());// 发送表单字段数据
            // 上传的图片部分
            StringBuilder split = new StringBuilder();
            split.append("--");
            split.append(BOUNDARY);
            split.append("\r\n");
            split.append("Content-Disposition: form-data;name=\"").append(file.getFormname()).append("\";filename=\"")
                    .append(file.getFilname()).append("\"\r\n");
            split.append("Content-Type: ").append(file.getContentType()).append("\r\n\r\n");
            outStream.write(split.toString().getBytes());
            outStream.write(file.getData(), 0, file.getData().length);
            outStream.write("\r\n".getBytes());

            byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
            outStream.write(end_data);
            outStream.flush();
            outStream.close();
            int cah = conn.getResponseCode();

            if (cah != HttpURLConnection.HTTP_OK) {
                LogWriter.e("UploadPicture", "图片上传失败，请求响应码：" + cah);
                conn.disconnect();
                return "";
            }

            InputStream is = conn.getInputStream();
            String jsonVale = convertStreamToString(is);
            conn.disconnect();
            return jsonVale;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将输入流解析为String
     *
     * @param is
     * @return
     * @throws Exception
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                line = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        line = sb.toString();
        sb = null;
        return line;
    }
    
    public static class FormFileBean extends MBaseBean implements Serializable {
        
        private static final long serialVersionUID = -8575445524367209125L;
        /* 上传文件的数据 */
        private byte[] data;
        /* 文件名称 */
        private String filname;
        /* 表单字段名称 */
        private String formname;
        /* 内容类型 */
        private String contentType = "text/plain"; // 需要查阅相关的资料
        
        public FormFileBean(String filname, byte[] data, String formname,
                            String contentType) {
            this.data = data;
            this.filname = filname;
            this.formname = formname;
            if (contentType != null)
                this.contentType = contentType;
        }
        
        public byte[] getData() {
            return data;
        }
        
        public void setData(byte[] data) {
            this.data = data;
        }
        
        public String getFilname() {
            return filname;
        }
        
        public void setFilname(String filname) {
            this.filname = filname;
        }
        
        public String getFormname() {
            return formname;
        }
        
        public void setFormname(String formname) {
            this.formname = formname;
        }
        
        public String getContentType() {
            return contentType;
        }
        
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
        
    }
}
