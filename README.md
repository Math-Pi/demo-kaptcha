一、引入Maven依赖
<!--验证码生成工具-->
<dependency>
   <groupId>com.github.penggle</groupId>
   <artifactId>kaptcha</artifactId>
   <version>2.3.2</version>
</dependency>
二、配置Kaptcha
package com.example.demo12kaptcha.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;
 
/**
 * kaptcha验证码生成器配置
 * @author lwf
 * @date 2022/3/15 12:53
 */
@Configuration
public class KaptchaConfig {
 
    /**
     * 配置生成图片的bean
     * @return
     */
    @Bean(name = "kaptchaProducer")
    public DefaultKaptcha getKaptchaBean() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "no");                             // 图片边框
        properties.setProperty("kaptcha.image.width", "150");                       // 图片宽
        properties.setProperty("kaptcha.image.height", "50");                       // 图片高

        properties.setProperty("kaptcha.textproducer.char.space", "4");             // 文字间隔
        properties.setProperty("kaptcha.textproducer.char.length", "4");            // 验证码长度
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");   // 文本集合，验证码值从此集合中获取
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");// 字体
        properties.setProperty("kaptcha.textproducer.font.color", "blue");          // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.size", "30");             // 字体大小
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
三、使用示例
3.1 调用
package com.example.demo12kaptcha.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
 
/**
 * 登录相关
 * @author CJM
 * @date 2022/3/15 14:23
 */
@Controller
public class CaptchaController {
 
    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private HttpServletRequest request;
    /**
     * 获取图形验证码
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        //设置页面不缓存
        response.setDateHeader("Expires", 0);
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setContentType("image/jpeg");
        //生成验证码文本
        String text = kaptchaProducer.createText();
        //获取session，并将验证码编码存放到session中
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, text);
        //生成图片验证码
        BufferedImage image = kaptchaProducer.createImage(text);
        //显示图片到客户端
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
    }
    @RequestMapping("/login")
    @ResponseBody
    public String login(String code){
        if (checkVerifyCode(request,code))
            return "验证码正确";
        return "验证码错误";
    }
    @RequestMapping("/")
    public String index(){
        return "index";
    }

    /**
     * 验证码校验
     * @param request
     * @param code
     * @return
     */
    public static boolean checkVerifyCode(HttpServletRequest request,String code) {
        //获取生成的验证码
        String verifyCodeExpected = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if(code == null ||!code.equals(verifyCodeExpected)) {
            return false;
        }
        return true;
    }
}
3.2 HTML
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>验证码测试</title>
</head>
<body>
<form action="/login" method="post">
验证码：<input type="text" name="code">
<img id="test"
     style="height: 32px;width: 96px;border-radius: 4px;"
     alt="如果看不清楚，请单击图片刷新！" title="点击刷新"
     class="pointer" src="/captcha" onclick="refreshCode()" >
<input type="submit" value="提交">
</form>
</body>
<script src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
<script type="text/javascript">
    let captcha;
    refreshCode = function () {
        captcha = "captcha?t=" + $.now();
        document.getElementById("test").setAttribute("src",captcha);
    }
</script>
</html>
四、Kaptcha 参数设置说明
Constant	描述	默认值	合法值
kaptcha.border	图片边框	yes	yes,no
kaptcha.border.color	边框颜色	black	r,g,b (and optional alpha)
或者 white,black,blue.
kaptcha.border.thickness	边框厚度	1	>0
kaptcha.image.width	图片宽度	200	
kaptcha.image.height	图片高度	50	
kaptcha.producer.impl	图片实现类	com.google.code.kaptcha
.impl.DefaultKaptcha	
kaptcha.textproducer.impl	文本实现类	com.google.code.kaptcha.text
.impl.DefaultTextCreator	
kaptcha.textproducer.char.string	文本集合，验证码值从此集合中获取	abcde2345678gfynmnpwx	
kaptcha.textproducer.char.length	验证码长度	5	
kaptcha.textproducer.font.names	字体	Arial, Courier	
kaptcha.textproducer.font.size	字体大小	40px	
kaptcha.textproducer.font.color	字体颜色	black	r,g,b  
或者 white,black,blue.
kaptcha.textproducer.char.space	文字间隔	2	
kaptcha.noise.impl	干扰实现类	com.google.code.kaptcha
.impl.DefaultNoise	
kaptcha.noise.color	干扰颜色	black	r,g,b
或者 white,black,blue.
kaptcha.obscurificator.impl	图片样式	com.google.code.kaptcha
.impl.WaterRipple	水纹 com.google.code.kaptcha
.impl.WaterRipple
鱼眼 com.google.code.kaptcha
.impl.FishEyeGimpy
阴影 com.google.code.kaptcha
.impl.ShadowGimpy
kaptcha.background.impl	背景实现类	com.google.code.kaptcha
.impl.DefaultBackground	
kaptcha.background.clear.from	背景颜色渐变，开始颜色	light grey	
kaptcha.background.clear.to	背景颜色渐变，结束颜色	white	
kaptcha.background.clear.to	文字渲染器	white	
kaptcha.word.impl	session key	KAPTCHA_SESSION_KEY	
kaptcha.session.key	session date	KAPTCHA_SESSION_DATE	
五、进一步优化
5.1 配置
配置验证码图片的样式
package com.example.demo12kaptcha.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;

import static com.google.code.kaptcha.Constants.*;
import static com.google.code.kaptcha.Constants.KAPTCHA_OBSCURIFICATOR_IMPL;

/**
 * kaptcha验证码生成器配置
 * @author lwf
 * @date 2022/3/15 12:53
 */
@Configuration
public class KaptchaConfig {
 
    /**
     * 配置生成图片的bean
     * @return
     */
    @Bean(name = "kaptchaProducer")
    public DefaultKaptcha getKaptchaBean() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "no");                             // 图片边框
        properties.setProperty("kaptcha.image.width", "150");                       // 图片宽
        properties.setProperty("kaptcha.image.height", "50");                       // 图片高

        properties.setProperty("kaptcha.textproducer.char.space", "4");             // 文字间隔
        properties.setProperty("kaptcha.textproducer.char.length", "4");            // 验证码长度
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789");   // 文本集合，验证码值从此集合中获取
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");// 字体
        properties.setProperty("kaptcha.textproducer.font.color", "blue");          // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.size", "30");             // 字体大小
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    /**
     * 算术型验证码
     * @return
     */
    @Bean(name = "captchaProducerMath")
    public DefaultKaptcha getKaptchaBeanMath()
    {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 是否有边框 默认为true 我们可以自己设置yes，no
        properties.setProperty(KAPTCHA_BORDER, "yes");
        // 边框颜色 默认为Color.BLACK
        properties.setProperty(KAPTCHA_BORDER_COLOR, "105,179,90");
        // 验证码文本字符颜色 默认为Color.BLACK
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "blue");
        // 验证码图片宽度 默认为200
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "160");
        // 验证码图片高度 默认为50
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "60");
        // 验证码文本字符大小 默认为40
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "35");
        // KAPTCHA_SESSION_KEY
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCodeMath");
        // 验证码文本生成器
        properties.setProperty(KAPTCHA_TEXTPRODUCER_IMPL, "com.example.demo12kaptcha.config.KaptchaTextCreator");
        // 验证码文本字符间距 默认为2
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "3");
        // 验证码文本字符长度 默认为5
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "6");
        // 验证码文本字体样式 默认为new Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier");
        // 验证码噪点颜色 默认为Color.BLACK
        properties.setProperty(KAPTCHA_NOISE_COLOR, "white");
        // 干扰实现类
        properties.setProperty(KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
        // 图片样式 水纹com.google.code.kaptcha.impl.WaterRipple 鱼眼com.google.code.kaptcha.impl.FishEyeGimpy 阴影com.google.code.kaptcha.impl.ShadowGimpy
        properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
5.2 自定义验证码生成器
用于生成算术型的验证码
package com.example.demo12kaptcha.config;

import java.util.Random;
import com.google.code.kaptcha.text.impl.DefaultTextCreator;

/**
 * 自定义验证码文本生成器
 * 
 * @author CJM
 */
public class KaptchaTextCreator extends DefaultTextCreator {
    private static final String[] CNUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(",");

    @Override
    public String getText() {
        Integer result = 0;
        Random random = new Random();
        int x = random.nextInt(10);
        int y = random.nextInt(10);
        StringBuilder suChinese = new StringBuilder();
        int randomoperands = (int) Math.round(Math.random() * 2);   //随机生成0、1、2三个数，并根据它来选择加减乘除
        System.out.println("randomoperands:"+randomoperands);
        if (randomoperands == 0) {
            result = x * y;
            suChinese.append(CNUMBERS[x]);
            suChinese.append("*");
            suChinese.append(CNUMBERS[y]);
        }
        else if (randomoperands == 1) {
            if (!(x == 0) && y % x == 0){    //除数不能为0并且y整除x
                result = y / x;
                suChinese.append(CNUMBERS[y]);
                suChinese.append("/");
                suChinese.append(CNUMBERS[x]);
            } else {
                result = x + y;
                suChinese.append(CNUMBERS[x]);
                suChinese.append("+");
                suChinese.append(CNUMBERS[y]);
            }
        } else if (randomoperands == 2) {
            if (x >= y) {
                result = x - y;
                suChinese.append(CNUMBERS[x]);
                suChinese.append("-");
                suChinese.append(CNUMBERS[y]);
            } else {
                result = y - x;
                suChinese.append(CNUMBERS[y]);
                suChinese.append("-");
                suChinese.append(CNUMBERS[x]);
            }
        } else {
            result = x + y;
            suChinese.append(CNUMBERS[x]);
            suChinese.append("+");
            suChinese.append(CNUMBERS[y]);
        }
        suChinese.append("=?@" + result);
        return suChinese.toString();
    }
}

5.3 控制类
利用@Resource(name="captchaProducerMath")调用指定的配置Bean
package com.example.demo12kaptcha.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
 
/**
 * 登录相关
 * @author CJM
 * @date 2022/3/15 14:23
 */
@Controller
public class CaptchaController {
 
    @Resource(name = "captchaProducerMath")
    private Producer kaptchaProducer;

    @Autowired
    private HttpServletRequest request;
    /**
     * 获取图形验证码
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        //设置页面不缓存
        response.setDateHeader("Expires", 0);
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setContentType("image/jpeg");
        //获得前端发送来的type="math"或"char"
        String type = request.getParameter("type");
        //码
        String capStr = null;
        //值
        String code = null;
        BufferedImage bi = null;
        if ("math".equals(type)) {
            //创建验证码中的内容
            String capText = kaptchaProducer.createText();
            //substring:截取字符串
            //lastIndexOf:返回参数从字符串右边开始的索引
            //capStr中是capText从0位到@位之前的数据
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            //输出码
            System.out.println(capStr);
            //获得上面剩余的字符
            //code中是@之后的数，一个随机数（就是验证码）
            code = capText.substring(capText.lastIndexOf("@") + 1);
            //输出值
            System.out.println(code);
            //创建验证码
            bi = kaptchaProducer.createImage(capStr);
        }else if ("char".equals(type)) {    //如果类型为字符型
            capStr = code = kaptchaProducer.createText();
            bi = kaptchaProducer.createImage(capStr);
        }
        //获取session，并将验证码编码存放到session中
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, code);
        //显示图片到客户端
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
    }
    @RequestMapping("/login")
    @ResponseBody
    public String login(String code){
        if (checkVerifyCode(request,code))
            return "验证码正确";
        return "验证码错误";
    }
    @RequestMapping("/")
    public String index(){
        return "index";
    }

    /**
     * 验证码校验
     * @param request
     * @param code
     * @return
     */
    public static boolean checkVerifyCode(HttpServletRequest request,String code) {
        //获取生成的验证码
        String verifyCodeExpected = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if(code == null ||!code.equals(verifyCodeExpected)) {
            return false;
        }
        return true;
    }
}
5.4 HTML
添加type=math参数
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>验证码测试</title>
</head>
<body>
<form action="/login" method="post">
验证码：<input type="text" name="code">
<img id="test"
     style="height: 32px;width: 96px;border-radius: 4px;"
     alt="如果看不清楚，请单击图片刷新！" title="点击刷新"
     class="pointer" src="/captcha?type=math" onclick="refreshCode()" >
<input type="submit" value="提交">
</form>
</body>
<script src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
<script type="text/javascript">
    let captcha;
    refreshCode = function () {
        captcha = "captcha?type=math&t=" + $.now();
        document.getElementById("test").setAttribute("src",captcha);
    }
</script>
</html>
六、参考
JAVA验证码（算术型和字符型）和生成和解析——生成     	https://www.freesion.com/article/5860234817/
Kaptcha      https://blog.csdn.net/qq_44496147/article/details/114768412
SpringBoot整合Kaptcha（图形验证码）	https://sunkuan.blog.csdn.net/article/details/108080646
SpringBoot 中使用 Kaptcha 验证码生成器	https://blog.csdn.net/liang_wf/article/details/102369824
