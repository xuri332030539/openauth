package com.example.springboot.Ceshi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.springboot.dao.CeshiDao;
import com.example.springboot.po.LiuJiaPO;
import com.example.springboot.po.StudentPO;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.http.client.methods.HttpPost;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GetOutServiceImpl {
    @Autowired
    private CeshiDao ceshiDao;

    String nanyouUrl = "10.22.254.170:8088";
    String liujia = "10.22.244.19:8181";
    String local = "192.168.0.100";

    //获取宇视服务器token
    public String getLoginData() throws Exception{
        Map map = new HashMap();
        //南邮服务器
        map.put("Id", "admin");
        map.put("Pwd", "b23b0a91a9c831b7021cfd315704139e");
        String loginUrl = "http://10.22.254.170:8088/fastgate/user/login";
        HttpPost httpPost = new HttpPost(loginUrl);
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        final String CONTENT_TYPE_TEXT_JSON = "application/json";
        HttpClient httpclient = new DefaultHttpClient();
        StringEntity se = new StringEntity(JSONObject.toJSONString(map));
        se.setContentType(CONTENT_TYPE_TEXT_JSON);
        httpPost.setEntity(se);
        HttpResponse httpResponse = httpclient.execute(httpPost);
        HttpEntity entity = null;
        entity = httpResponse.getEntity();
        Header[] headers = httpResponse.getHeaders("Set-Cookie");
        String res = null;
        for (Header header : headers){
            HeaderElement[] elements = header.getElements();
            for(HeaderElement headerElement : elements){
                System.out.println(headerElement.getName()+"="+headerElement.getValue());
                res = headerElement.getName()+"="+headerElement.getValue();
            }
        }
        return res;
    }

    public void openauth() throws Exception {
        List<StudentPO> openauth = ceshiDao.openauth();
        for (StudentPO studentPO : openauth) {
            String personCode = studentPO.getPersonCode();
            Date startTime = studentPO.getStartTime();
            Date endTime = studentPO.getEndTime();
            Integer id = studentPO.getId();
            ceshiDao.afterOpenauth(new Date().toString(), id);
            String loginData = this.getLoginData();
//            String personCode = "10022";
//            Date startTime = new Date();
//            Date endTime = new Date();
            String getPerSonInfoUrl = "http://" + nanyouUrl + "/fastgate/personCode/" + personCode;
            HttpGet httpGet = new HttpGet(getPerSonInfoUrl);
            DefaultHttpClient client = new DefaultHttpClient();
            httpGet.setHeader("Cookie", loginData);
            HttpResponse httpResponse1 = client.execute(httpGet);
            HttpEntity entity = null;
            entity = httpResponse1.getEntity();
            String s = EntityUtils.toString(entity, "UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(s);
            JSONObject data = jsonObject.getJSONObject("data");
            System.out.println("宇视服务器中数据：" + data);
            if (data != null) {
                JSONArray pictures = data.getJSONArray("Pictures");
                JSONObject picJson = null;
                String picPath = "";
                if (pictures.size() != 0) {
                    picJson = pictures.getJSONObject(0);
                    picPath = picJson.getString("PersonPicturePath");
                }
                System.out.println("图片地址" + picPath);
                String name = data.getString("Name");
                System.out.println("姓名" + name);
                String Seqid = data.getString("Seqid");
                System.out.println("Seqid" + Seqid);
                String Depart = data.getString("DepartCode");
                System.out.println("Depart" + Depart);
                //put
                //测试服务器
                String url = "http://" + nanyouUrl + "/fastgate/person";
                HttpPut httpPut = new HttpPut(url);
                httpPut.setHeader("Cookie", loginData);
                MultipartEntity multipartEntity = new MultipartEntity();
                multipartEntity.addPart("Sex", new StringBody("1", Charset.forName("UTF-8")));
                multipartEntity.addPart("Name", new StringBody(name, Charset.forName("UTF-8")));
                multipartEntity.addPart("Code", new StringBody(personCode, Charset.forName("UTF-8")));
                multipartEntity.addPart("Seqid", new StringBody(Seqid, Charset.forName("UTF-8")));
                multipartEntity.addPart("ImageList", new StringBody(picPath));
                multipartEntity.addPart("Depart", new StringBody(Depart, Charset.forName("UTF-8")));
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                String startFormat = sdf.format(startTime);
                Calendar endDate = Calendar.getInstance();
                endDate.setTime(endTime);
                endDate.add(Calendar.DAY_OF_MONTH, 1);
                Date end = endDate.getTime();
                String endFormat = sdf.format(end);
                System.out.println(startTime);
                System.out.println(endTime);
                multipartEntity.addPart("StartTime", new StringBody(startFormat));
                multipartEntity.addPart("EndTime", new StringBody(endFormat));
                httpPut.setEntity(multipartEntity);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse2 = httpclient.execute(httpPut);
            }
          }
        }


        public void sendLiuJia () throws Exception {
            List<LiuJiaPO> liuJiaPOS = ceshiDao.sendLiuJia();
            String data = JSONObject.toJSONString(liuJiaPOS);
            String url = "http://" + liujia + "/facial/outSchool/saveXscxsq";
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            HttpClient httpclient = new DefaultHttpClient();
            StringEntity se = new StringEntity(data, "UTF-8");
            final String CONTENT_TYPE_TEXT_JSON = "application/json";
            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            httpPost.setEntity(se);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            for (LiuJiaPO liuJiaPO : liuJiaPOS) {
                ceshiDao.afterSendLiuJia(Integer.parseInt(liuJiaPO.getId()));
            }
        }
}

