package cn.yearcon.sportapi.Service;

import cn.yearcon.feibank.flowfc.testapi.RestRequestByCxf;
import cn.yearcon.sportapi.json.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 短信验证码
 *
 * @author itguang
 * @create 2018-01-08 14:24
 **/
@Service
public class SmsCodeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public JsonResult getCheckCode(String mobile){
        /*String row=stringRedisTemplate.opsForValue().get(mobile);
        if(row==null|"".equals(row)){
            stringRedisTemplate.opsForValue().set(mobile,"1",60*60*24,TimeUnit.SECONDS);
        }else{
            Integer i=Integer.parseInt(row);
            if(i>5){
                return new JsonResult(0,"一天只能发送5次验证码");
            }else {
                i++;
                stringRedisTemplate.opsForValue().set(mobile,i.toString(),60*60*24,TimeUnit.SECONDS);
            }
        }*/
        String checkcode=generateCode();
        if(mobile==null ||"".equals(mobile)){
            return new JsonResult(0,"请输入手机号");
        }
        try {
            RestRequestByCxf.openCard(mobile,checkcode);
            System.out.println("短信验证码设置缓存30分钟");
            stringRedisTemplate.opsForValue().set("smscodeMobile="+mobile,checkcode,60*30, TimeUnit.SECONDS);
            return new JsonResult(1,checkcode);
        } catch (IOException e) {
            e.printStackTrace();
            return  new JsonResult(0,"一天只能发送5次验证码");
        }
    }

    /**
     * 随机生成6位字符串
     * @return
     */
    public String generateCode(){
        int a=0;
        StringBuffer str=new StringBuffer();
        for(int i=0;i<6;i++){
            a=(int) (Math.random()*10);
            str.append(a);
        }
        return str.toString();
    }

    /**
     * 发送短信给区域负责人
     * @param mobile
     * @return
     */
    public JsonResult sendAreaUser(String mobile,String content){
        if(mobile==null ||"".equals(mobile)){
            return new JsonResult(0,"请输入手机号");
        }
        try {
            RestRequestByCxf.sendAreaUser(mobile,content);
            return new JsonResult(1,"发送成功");
        }catch (IOException e){
            e.printStackTrace();
            return new JsonResult(0,e.getMessage());
        }

    }


}
