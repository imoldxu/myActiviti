package com.x.flow.xflow.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.x.flow.xflow.context.ErrorCode;
import com.x.flow.xflow.context.HandleException;
import com.x.flow.xflow.entity.Role;
import com.x.flow.xflow.entity.User;
import com.x.flow.xflow.entity.UserRole;
import com.x.flow.xflow.mapper.RoleMapper;
import com.x.flow.xflow.mapper.UserMapper;
import com.x.flow.xflow.mapper.UserRoleMapper;

import tk.mybatis.mapper.entity.Example;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	IdentityService idService;
	@Autowired
	RoleMapper roleMapper;
	@Autowired
	UserMapper userMapper;
	@Autowired
	UserRoleMapper userRoleMapper;
	
//	public User loginByWxMiniprogram(String wxCode,String rawData, String signature, String encryptedData, String iv) {
//		
//		try {
//			JsonNode wx_session = WxMiniProgramUtil.getOauthInfobylittleApp(wxCode);
//			String sessionKey = wx_session.get("session_key").asText();
//			String openID = wx_session.get("openid").asText();
//			//String unionID = wx_session.get("unionid").asText();
//			
//			JsonNode userJson = WxMiniProgramUtil.getUserInfo(encryptedData, sessionKey, iv);
//			String nick = userJson.get("nickName").asText();
//			nick = WxMiniProgramUtil.converWxNick(nick);
//			String avatar = userJson.get("avatarUrl").asText();
//			avatar = WxMiniProgramUtil.convertAvatar(avatar);
//			String unionID = userJson.get("unionId").asText();
//			
//			User user = getUserByWxUnionID(unionID, openID, nick, avatar);	
//			
//			return user;
//		} catch (IOException e) {
//			throw new HandleException(ErrorCode.WX_NET_ERROR, e.getMessage());
//		}
//	}
	
//	public User loginByWx(String wxCode) throws IOException{
//
//		JsonNode wxOauthInfo = WxUtil.getOauthInfo(wxCode);
//		String accessToken = null;
//		accessToken = (String)redissonUtil.get("wechat_access_token");
//		
//		JsonNode wxUserInfo = WxUtil.getUserInfo2(accessToken, wxOauthInfo);
//		// 获得微信的数据
//		String unionID = wxUserInfo.get("unionid").asText();
//		String headerImgURL = wxUserInfo.get("headimgurl").asText();
//		headerImgURL = WxUtil.convertAvatar(headerImgURL);
//		String wxnick = wxUserInfo.get("nickname").asText();
//		String nick = WxUtil.converWxNick(wxnick);
//		// 获取微信账号对应的账号
//		User user = getUserByWxUnionID(unionID, "", nick ,headerImgURL);
//		
//		if(user.getIdcardtype() == User.TYPE_IDCARD){
//			String idcard = user.getIdcardnum();
//			if(idcard!=null && !idcard.isEmpty()){
//				try{
//					int age = IdCardUtil.getAge(idcard);
//					user.setAge(age);
//				}catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		JsonNode subscribeNode = wxUserInfo.get("subscribe");
//		if(subscribeNode!=null){
//			int subscribe = subscribeNode.intValue();
//			user.setSubscribe(subscribe);
//		}else{
//			user.setSubscribe(0);//视为没有关注
//		}
//		
//		return user;
//	}

	

//	private User getUserByWxUnionID(String unionID, String openid, String nick, String avatar) {
//		Example wxUserExample = new Example(User.class);
//		wxUserExample.createCriteria().andEqualTo("wxunionid", unionID);
//		wxUserExample.setOrderByClause("id asc");
//		User user = userMapper.selectOneByExample(wxUserExample);
//		if (user == null) {
//			// 微信用户未注册
//			user = new User();
//			user.setWxunionid(unionID);
//			user.setAvatar(avatar);
//			user.setNick(nick);
//			user.setWxminiopenid(openid);
//			user.setCreatetime(new Date());
//			userMapper.insertUseGeneratedKeys(user);
//		} else {
//			user.setWxminiopenid(openid);
//			user.setAvatar(avatar);
//			user.setNick(nick);
//			userMapper.updateByPrimaryKey(user);			
//		}
//		return user;
//	}

//	public void updateInfo(int uid, String name, String phone, int idcardtype, String idcardnum) {
//		
//		User user = userMapper.selectByPrimaryKey(uid);
//		if(user == null){
//			throw new HandleException(ErrorCode.NORMAL_ERROR, "系统异常,用户不存在");
//		}
//		if(idcardtype != User.TYPE_IDCARD && idcardtype != User.TYPE_JG){
//			throw new HandleException(ErrorCode.ARG_ERROR, "证件类型异常,请检查客户端");
//		}
//		if(idcardtype==User.TYPE_IDCARD){
//			if(!IdCardUtil.isIDCard(idcardnum)){
//				throw new HandleException(ErrorCode.NORMAL_ERROR, "身份证号有误,请检查");
//			}
//		}
//		if(!ValidDataUtil.isPhone(phone)){
//			throw new HandleException(ErrorCode.NORMAL_ERROR, "手机号有误,请检查"); 
//		}
//		
//		user.setName(name);
//		user.setPhone(phone);
//		user.setIdcardtype(idcardtype);
//		user.setIdcardnum(idcardnum);
//		
//		userMapper.updateByPrimaryKey(user);
//	}

	public User login(String phone, String password) {
		User user = userMapper.selectUserByPhone(phone);
		if(user == null){
			throw new HandleException("用户不存在");
		}else{
			if(user.getPassword().equals(password)){
				return user;
			}else{
				throw new HandleException("密码错误");
			}
		}
	}

	@Transactional
	public void register(String phone, String password, List<Integer> roleIds) {
		Example ex = new Example(User.class);
		ex.createCriteria().andEqualTo("phone", phone);
		User user = userMapper.selectOneByExample(ex);
		if(user != null){
			throw new HandleException("用户已存在");
		}else{
			user = new User();
			user.setPhone(phone);
			user.setPassword(password);
			userMapper.insertUseGeneratedKeys(user);
			
			final Integer uid = user.getId();
			
			org.activiti.engine.identity.User activitiUser = idService.newUser(uid.toString());
			idService.saveUser(activitiUser);
			
			List<UserRole> userRoles = roleIds.stream().map(roleId->{
				
				idService.createMembership(uid.toString(), roleId.toString());
				
				UserRole userRole = new UserRole();
				userRole.setRid(roleId);
				userRole.setUid(uid);
				return userRole;
			}).collect(Collectors.toList());
			
			userRoleMapper.insertList(userRoles);
		}
	}

	@Transactional
	public void updateRole(Integer uid, Set<Role> roles) {
	
		userRoleMapper.deleteAllRoleByUser(uid);
		
		List<Group> activitiGroups = idService.createGroupQuery().groupMember(uid.toString()).list();
		for(Group group : activitiGroups) {
			logger.debug("delete group from activit: {}", ToStringBuilder.reflectionToString(group));
			idService.deleteMembership(uid.toString(), group.getId());
		}
		
		List<UserRole> userRoleList = new ArrayList<UserRole>();
		roles.forEach(role->{
			idService.createMembership(uid.toString(), role.getId().toString());			
			UserRole userRole = new UserRole();
			userRole.setRid(role.getId());
			userRole.setUid(uid);
			userRoleList.add(userRole);
		});
		
		userRoleMapper.insertList(userRoleList);
	}
	
	@Transactional
	public void addRole(String name) {
		Example ex = new Example(Role.class);
		ex.createCriteria().andEqualTo("name", name);
		Role role = roleMapper.selectOneByExample(ex);
		if(role != null){
			throw new HandleException("角色已存在");
		}else{
			role = new Role();
			role.setName(name);
			roleMapper.insertUseGeneratedKeys(role);
			
			org.activiti.engine.identity.Group group = idService.newGroup(role.getId().toString());
			group.setName(name);
			idService.saveGroup(group);
		}
	}
	
//	public void getVerificationCode(String phone) {
//		Random r = new Random();
//		int number = r.nextInt(999999);
//		String code = String.format("%06d", number);
//		
//		redissonUtil.set("VERCODE_"+phone, code, 300000L);
//		System.out.println("vercode:"+code);
//		//TODO: 发送短信
//	}

//	public void verifyIDCard(int uid, String code, String name, String phone, int idcardtype, String idcardnum) {
//		String vercode = (String) redissonUtil.get("VERCODE_"+phone);
//		if(vercode==null){
//			throw new HandleException(ErrorCode.NORMAL_ERROR, "验证码已过期");
//		}
//		if(!vercode.equals(code)){
//			throw new HandleException(ErrorCode.NORMAL_ERROR, "验证码错误");
//		}
//		Example ex = new Example(User.class);
//		ex.createCriteria().andEqualTo("idcardnum", idcardnum);
//		User user = userMapper.selectOneByExample(ex);
//		if(user!=null) {
//			throw new HandleException(ErrorCode.NORMAL_ERROR, "该身份证号已被实名，不能绑定第二个账号");
//		}		
//		user = userMapper.selectByPrimaryKey(uid);
//		if(user.getIdcardnum()!=null){
//			throw new HandleException(ErrorCode.NORMAL_ERROR, "用户已实名认证，不可重复认证");
//		}
//		if(!IdCardUtil.isIDCard(idcardnum)){
//			throw new HandleException(ErrorCode.NORMAL_ERROR, "身份证号格式有误");
//		}
//		
//		//TODO 调用实名认证接口
//		
//		//FIXME:验证成功
//		user.setIdcardnum(idcardnum);
//		user.setName(name);
//		user.setPhone(phone);
//		String birthday = IdCardUtil.getBirthday(idcardnum);
//		user.setBirthday(birthday);
//		//String sex = IdCardUtil.getSex(idcardnum);
//		//user.setSex(sex);
//		userMapper.updateByPrimaryKey(user);
//	}

//	public User getUserById(Integer uid) {
//		User ret = userMapper.selectByPrimaryKey(uid);
//		if(ret==null){
//			throw new HandleException("内部数据错误");
//		}
//		return ret;
//	}

	public List<Role> listRole() {
		return roleMapper.selectAll();
	}

	@Transactional
	public User updateUser(User user) {
		
		userMapper.updateByPrimaryKey(user);
		
		Set<Role> roles = user.getRoles();
		
		updateRole(user.getId(), roles);
		
		return user;
	}

	public User getUserById(Integer uid) {
		User user = userMapper.selectUserById(uid);
		return user;
	}

	
}
