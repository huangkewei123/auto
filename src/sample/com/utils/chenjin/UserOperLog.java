package sample.com.utils.chenjin;


import java.io.Serializable;
import java.util.Date;

/**
 *  UserOperLog
 * @author jin_自动生成
 * @email 123@qq.com
 * @date 2021-01-15 14:28:38
 */
public class UserOperLog implements Serializable {

	private static final long serialVersionUID = 1L;


	/** 唯一编码 **/
	private String id;

	/** 功能模块 **/
	private String operModule;

	/** ukey码 或者用户名称**/
	private String ukeyCode;

	/** 时间 **/
	private Date createDate;

	/** 描述 对操作进行描述**/
	private String descr;

	/** 错误信息 **/
	private String errMsg;

	/** 操作ip **/
	private String operIp;


	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


	public String getOperModule() {
        return operModule;
    }

    public void setOperModule(String operModule) {
        this.operModule = operModule;
    }


	public String getUkeyCode() {
        return ukeyCode;
    }

    public void setUkeyCode(String ukeyCode) {
        this.ukeyCode = ukeyCode;
    }


	public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


	public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }


	public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }


	public String getOperIp() {
        return operIp;
    }

    public void setOperIp(String operIp) {
        this.operIp = operIp;
    }


	public UserOperLog() {
        super();
    }


	public UserOperLog(String id, String operModule, String ukeyCode, Date createDate, String descr, String errMsg, String operIp) {

		this.id = id;
		this.operModule = operModule;
		this.ukeyCode = ukeyCode;
		this.createDate = createDate;
		this.descr = descr;
		this.errMsg = errMsg;
		this.operIp = operIp;

	}

}
