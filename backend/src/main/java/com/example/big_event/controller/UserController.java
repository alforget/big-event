package com.example.big_event.controller;

import com.example.big_event.pojo.PageBean;
import com.example.big_event.pojo.Result;
import com.example.big_event.pojo.User;
import com.example.big_event.service.UserService;
import com.example.big_event.utils.JwtUtil;
import com.example.big_event.utils.Md5Util;
import com.example.big_event.utils.ThreadLocalUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private JwtUtil jwtUtil;
    //密码加密
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //注册
    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username,@Pattern(regexp = "^\\S{5,16}$") String password){
            //查询用户
            User u = userService.findByUserName(username);
            if(u==null){//没有占用
                userService.register(username,password);
                return Result.success();
            }else{//占用
                return Result.error("用户名已被占用");
            }
    }
    //登录
    @PostMapping("/login")
    public Result login(@Pattern(regexp = "^\\S{5,16}$") String username,@Pattern(regexp = "^\\S{5,16}$") String password){
        //查询用户
        User u = userService.findByUserName(username);
        //判断用户是否存在
        if(u==null){
            return Result.error("用户名错误");
        }
        String store = u.getPassword();
        if(store == null){
            return Result.error("密码错误");
        }
        boolean matched = false;
        //判断现在是否为BCrypt加密
        if(store.startsWith("$2a$")||store.startsWith("$2y$")||store.startsWith("$2b$")){
            matched = passwordEncoder.matches(password,store);
        }else if(Md5Util.getMD5String(password).equals(store)){
            //兼容老MD5数据：登录成功后升级为BCrypt
            userService.updatePwd(password,u.getId());
            matched = true;
        }
        if(!matched){
            return Result.error("密码错误");
        }

        Map<String,Object> claims= new HashMap<>();
        claims.put("id",u.getId());
        claims.put("username",u.getUsername());
        //生成jwt令牌
        String token = jwtUtil.genToken(claims);
        //RedisKey
        String redisKey = "login:token:"+u.getId();
        //把token存储到Redis中，TTL与JWT保持一致
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(redisKey,token,12, TimeUnit.HOURS);
        //设置用户在线
        userService.updateOnlineStatus(u.getId(),true);
        return Result.success(token);
    }

    //查看用户信息
    @GetMapping("/userInfo")
    public Result<User> userInfo(/*@RequestHeader(name="Authorization")String token*/){
        //根据用户名查询
        /*Map<String, Object> map = JwtUtil.parseToken(token);
        String username = (String)map.get("username");*/
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User u = userService.findByUserName(username);
        return Result.success(u);
    }

    //更新用户数据
    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user){
        userService.update(user);
        return Result.success();
    }

    //更新用户头像
   @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }
    //更新密码
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params){
        //校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");
        if(!StringUtils.hasLength(oldPwd)||!StringUtils.hasLength(newPwd)||!StringUtils.hasLength(rePwd)){
            return Result.error("缺少必要的参数");
        }
        //原密码是否正确
        Map<String,Object> map = ThreadLocalUtil.get();
        User u= userService.findByUserName((String)map.get("username"));
        String stored = u.getPassword();
        boolean matched;
        if(stored != null && (stored.startsWith("$2a$")||stored.startsWith("$2y$")||stored.startsWith("$2b$"))){
            matched = passwordEncoder.matches(oldPwd,stored);
        }else{
            matched = stored != null && stored.equals(Md5Util.getMD5String(oldPwd));
        }
        if(!matched){
            return Result.error("原密码不正确");
        }
        //两次新密码是否一致
        if(!newPwd.equals(rePwd)){
            return Result.error("两次填写的新密码不一致");
        }
        userService.updatePwd(newPwd,u.getId());
        //密码修改后让该用户旧token立即失效
        stringRedisTemplate.delete("login:token:"+u.getId());
        return Result.success();
    }

    //退出登录：删除Redis中的token，并把在线状态置为false
    @PostMapping("/logout")
    public Result logout(){
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        stringRedisTemplate.delete("login:token:"+userId);
        userService.updateOnlineStatus(userId,false);
        return Result.success();
    }

    //按用户id强制用户下线 管理员独属方法
    @DeleteMapping("/deleteToken")
    public Result deleteToken(@RequestParam Integer userId){
        Map<String,Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User u = userService.findByUserName(username);
        if(u == null || !"admin".equals(u.getRole())){
            return Result.error("您不是管理员，不能强制下线用户");
        }
        if(u.getId().equals(userId)){
            return Result.error("不能强制下线自己");
        }
        User target = userService.findById(userId);
        if(target == null){
            return Result.error("用户不存在");
        }
        //删除redis中对应的token，并把在线状态置为false
        stringRedisTemplate.delete("login:token:"+userId);
        userService.updateOnlineStatus(userId,false);
        return Result.success();
    }


    //获取所有用户信息（管理员）
    @GetMapping("/list")
    public Result<PageBean<User>> allUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String,Object> map = ThreadLocalUtil.get();
        User current = userService.findByUserName((String) map.get("username"));
        if(current == null || !"admin".equals(current.getRole())){
            return Result.error("无权限访问");
        }
        return Result.success(userService.list(pageNum,pageSize));
    }
}
