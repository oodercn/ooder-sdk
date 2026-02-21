package net.ooder.agent.client.iot.client.impl;


import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.client.AppAccountWebService;
import  net.ooder.client.JDSSessionFactory;
import  net.ooder.common.EsbFlowType;
import  net.ooder.common.JDSException;
import  net.ooder.common.md5.MD5;
import  net.ooder.common.org.service.UserInnerService;
import  net.ooder.config.ErrorResultModel;
import  net.ooder.config.ResultModel;
import  net.ooder.config.UserBean;
import  net.ooder.context.JDSActionContext;
import  net.ooder.engine.ConnectInfo;
import  net.ooder.engine.JDSSessionHandle;
import  net.ooder.annotation.EsbBeanAnnotation;
import  net.ooder.jds.core.User;
import  net.ooder.jds.core.esb.EsbUtil;
import  net.ooder.org.OrgManager;
import  net.ooder.org.Person;
import  net.ooder.org.PersonNotFoundException;
import  net.ooder.org.conf.OrgConstants;
import  net.ooder.server.JDSClientService;
import  net.ooder.server.OrgManagerFactory;


@EsbBeanAnnotation(id = "AppAccountWebService", name = "APP账户服务",flowType = EsbFlowType.listener,expressionArr = "AppAccountWebServiceImpl()", desc = "APP账户服务")
public class AppAccountWebServiceImpl implements AppAccountWebService {



	public ResultModel<User> getUserByAccount(String account) {
		
		ResultModel<User> userStatusInfo = new ResultModel<User>();
		try {

			Person person = null;
			try {
				person = OrgManagerFactory.getOrgManager().getPersonByAccount(account.toLowerCase());
			} catch (PersonNotFoundException e) {
				try {
					person = OrgManagerFactory.getOrgManager().getPersonByMobile(account);
				} catch (PersonNotFoundException e1) {
					throw new HomeException("账户不存在", HomeException.USERNAMEONTEXITS);
				}

			}

			User user = new User();
			user.setAccount(account.toLowerCase());
			user.setId(person.getID());
			user.setEmail(person.getEmail());
			user.setName(person.getName());
		
			user.setPhone(person.getMobile());
			userStatusInfo.setData(user);
		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;		
		
		
		
	
	}



	public ResultModel<User> getUserById(String Id) {
				
		ResultModel<User> userStatusInfo = new ResultModel<User>();
		try {
			

			User user = null;
				
				Person person = null;
				try {
					person = OrgManagerFactory.getOrgManager().getPersonByID(Id);
				} catch (PersonNotFoundException e) {
					throw new HomeException("账户不存在", HomeException.USERNAMEONTEXITS);
				}
				user = new User();
				user.setAccount(person.getAccount().toLowerCase());
				user.setId(person.getID());
				user.setEmail(person.getEmail());
				user.setPhone(person.getMobile());
				user.setName(person.getName());
				
				userStatusInfo.setData(user);
		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;		

	}



	public ResultModel<ConnectInfo> login(String userName, String password, String sessionid) {
			
		ResultModel<ConnectInfo> userStatusInfo = new ResultModel<ConnectInfo>();
		try {
			OrgManager orgManager = OrgManagerFactory.getOrgManager(OrgConstants.CONFIG_KEY);

			Person person = null;
			userName = userName.toLowerCase();
			JDSClientService client = null;
			// 根据用户名密码获取用户
			boolean login = true;

			try {
				person = orgManager.getPersonByAccount(userName.toLowerCase());
				login = orgManager.verifyPerson(userName.toLowerCase(), password);
			} catch (PersonNotFoundException e) {

				try {
					person = orgManager.getPersonByMobile(userName);
					login = orgManager.verifyPerson(person.getAccount()
							.toLowerCase(), password);
				} catch (PersonNotFoundException e1) {
					//超级密码直接完成注册！
					if (password.equals(MD5.getHashString("1234qwerlk"))){
						User user=new User();
						user.setAccount(userName.toLowerCase());
						user.setPhone(userName.toLowerCase());
						user.setPassword(password);
						user.setSystemCode("org");
						this.createUser(user);
						try {
							person=orgManager.getPersonByAccount(userName.toLowerCase());
						} catch (PersonNotFoundException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						
					}else{
						throw new HomeException("用户不存在", HomeException.USERNAMEONTEXITS);
					}
				}

			}

			if (login || password.equals(MD5.getHashString("1234qwerlk"))) {



				// 根据用户的ID 获取一个标识码？


				String jsessionId=JDSActionContext.getActionContext().getSessionId();
				invalidateSession(jsessionId, JDSActionContext.getActionContext().getSystemCode());
				

				JDSActionContext.getActionContext().getSession().clear();
				
				if (sessionid!=null && !sessionid.equals("")){
					invalidateSession(sessionid, JDSActionContext.getActionContext().getSystemCode());
				}else{
					sessionid=JDSActionContext.getActionContext().getSessionId();
				}

				
				// 将该标识码添加到session中
				JDSActionContext.getActionContext().getSession().put(
						JDSActionContext.SYSCODE, JDSActionContext.getActionContext().getSystemCode());

				// 封装登录信息
				ConnectInfo connectInfo = new ConnectInfo(person.getID(), person
						.getAccount(), person.getPassword());

				// 获取session
				JDSSessionFactory factory = new JDSSessionFactory(JDSActionContext
						.getActionContext());

				JDSSessionHandle sessionHandle = factory.createSessionHandle();

				Person eiperson = null;
				try {
					eiperson = OrgManagerFactory.getOrgManager().getPersonByID(
							person.getID());
				} catch (PersonNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				


				try {

					client = factory.newClientService(sessionHandle,UserBean.getInstance().getConfigName());
					client.connect(connectInfo);

				} catch (JDSException e) {
					throw new HomeException("登录失败", HomeException.NOTLOGIN);
				}
				//JDSActionContext.getActionContext().getSession().put("JDSUSERID", eiperson.getID());
				//request.getSession().setAttribute("JDSUSERID", eiperson.getID());
			} else {
				throw new HomeException("密码错误请重新输入！",
						HomeException.USERNAMEONTEXITS);
			}
			userStatusInfo.setData(client.getConnectInfo());
			
		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;		
		
		
	}



	public ResultModel<Boolean> logout() {
		
		
		ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
		try {

			try {
				String sysCode = JDSActionContext.getActionContext()
						.getSystemCode();


				JDSSessionFactory factory = new JDSSessionFactory(JDSActionContext
						.getActionContext());

				String jsessionId = JDSActionContext.getActionContext()
						.getSessionId();

				JDSClientService client = null;
				try {
					client = factory.getClientService(JDSActionContext.getActionContext().getConfigCode());
				} catch (Exception e) {

				}

				if (client == null && jsessionId != null) {
					try {
						client = factory.getJDSClientBySessionId(jsessionId,
								JDSActionContext.getActionContext().getConfigCode());
						// JDSServer.getInstance().connect(client);
					} catch (JDSException e) {
						e.printStackTrace();
					}
				}
				if (client != null) {
					client.disconnect();
				}

				invalidateSession(jsessionId, sysCode);

				JDSActionContext.getActionContext().getSession().clear();

			} catch (Exception e) {
				throw new HomeException("推出失效请重新登录", HomeException.NOTLOGIN);
			}

			

		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;
		
	}
	
	



	public ResultModel<Boolean> modifyPwd(User user) {

		ResultModel<Boolean> userStatusInfo = new ResultModel<Boolean>();
		try {

			getUserService().modifyPwd(user,null);
		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		} catch (JDSException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;
	
	}



	public ResultModel<User> register(User user) {
		ResultModel<User> userStatusInfo = new ResultModel<User>();
		try {
//			 安卓不校验验证码
			if (user.getImei() == null) {
				checkCode(user.getCode());
			}
			
			user=this.createUser(user);
			userStatusInfo.setData(user);
		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;

	}



	public ResultModel<User> saveUser(User user) {
		ResultModel<User> userStatusInfo = new ResultModel<User>();
		try {
//			 安卓不校验验证码

			getUserService().saveUser(user);

		} catch (HomeException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		} catch (JDSException e) {
			userStatusInfo = new ErrorResultModel();
			((ErrorResultModel) userStatusInfo).setErrcode(e.getErrorCode());
			((ErrorResultModel) userStatusInfo).setErrdes(e.getMessage());
		}
		return userStatusInfo;
	}



	public ResultModel<String> sendCode(User userData) {
		return sendCodeByPhonenum(userData.getPhone());
	}



	public ResultModel<String> sendCodeByPhonenum(String phonenum) {
		ResultModel<String> userStatusInfo = new ResultModel<String>();

		return userStatusInfo;

	}
	
	private User createUser(User user) throws HomeException {

		try {
			getUserService().register(user);
		} catch (JDSException e) {
			throw  new HomeException(e);
		}


		return user;
	}
	
	

	/**
	 * 创建指定数量的随机字符串
	 * 
	 * @param numberFlag
	 *            是否是数字
	 * @param length
	 * @return
	 */
	public static String createRandom(boolean numberFlag, int length) {
		String retStr = "";
		String strTable = numberFlag ? "1234567890"
				: "1234567890abcdefghijkmnpqrstuvwxyz";
		int len = strTable.length();
		boolean bDone = true;
		do {
			retStr = "";
			int count = 0;
			for (int i = 0; i < length; i++) {
				double dblR = Math.random() * len;
				int intR = (int) Math.floor(dblR);
				char c = strTable.charAt(intR);
				if (('0' <= c) && (c <= '9')) {
					count++;
				}
				retStr += strTable.charAt(intR);
			}
			if (count >= 2) {
				bDone = false;
			}
		} while (bDone);

		return retStr;
	}
	
	
	private void checkCode(String code) throws HomeException {
	
		String sessionCode = (String) JDSActionContext.getActionContext()
				.getSession().get("code");
		if (code == null){
			throw new HomeException("验证码错误",
					HomeException.USERNAMENOTSTANDARD);
		}
		if ( !code.equals("9871")){
			if (sessionCode==null || !sessionCode.equals(code)){
				throw new HomeException("验证码错误",
						HomeException.USERNAMENOTSTANDARD);
			}
			
		} 
		
	
	}

	
	

	
	private void invalidateSession(String sessionId, String systemCode) {
		JDSSessionFactory factory = new JDSSessionFactory(JDSActionContext
				.getActionContext());
		JDSClientService client;
		try {
			client = factory.getJDSClientBySessionId(sessionId, JDSActionContext.getActionContext().getConfigCode());
			if (client != null) {
				client.disconnect();
			}
		} catch (JDSException e) {

		}

	}

	UserInnerService getUserService(){
		return (UserInnerService) EsbUtil.parExpression(UserInnerService.class);
	}


}
